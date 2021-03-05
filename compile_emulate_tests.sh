#!/bin/bash

execute() {
  pathname=$1
  echo running test $runTests "$pathname"

  refOutput=${pathname/valid/backendTests}
  FILE_PATH=$(basename "$pathname")
  customInputPath="src/test/backendTests/testInput/${FILE_PATH/.wacc/.txt}" # text file of custom user input

  customInput="\n"
  if [ -e "$customInputPath" ]; then # if custom user input exists, run the program with it
    read -r customInput <"$customInputPath"
  fi

  if [ ! -e ${refOutput/.wacc/.txt} ]; then # check for the existence of a cached file
    echo "$customInput" | ./refCompile "-x" "$pathname" >${refOutput/.wacc/.txt} # get result from reference compiler
  fi

  ./compile "$pathname" "-a" >/dev/null 2>&1 # compile and emulate with local compiler
  arm-linux-gnueabi-gcc -o ${FILE_PATH/.wacc/} -mcpu=arm1176jzf-s -mtune=arm1176jzf-s ${FILE_PATH/.wacc/.s}
  echo "$customInput" | qemu-arm -L /usr/arm-linux-gnueabi/ ${FILE_PATH/.wacc/} >output1.txt
  ourExitCode=$?
  rm ${FILE_PATH/.wacc/.s}
  rm ${FILE_PATH/.wacc/}

  {
    sed 's/ *$//' output1.txt >output.txt # remove any extra spaces at the end of the line

    correctFlag=true
    stage1=true  # everything up to the first line of ='s in the reference compiler's output
    stage2=false # everything from stage1 up to the second line of ='s - output from running the program
    readLines=1  # position in local compiler's output file
    while read -r refLine; do
      if [ "$stage1" = true ] && [ "$refLine" = "===========================================================" ]; then
        stage1=false
        stage2=true
        read -r refLine
      fi

      if [ "$stage2" = true ] && [ "$refLine" = "===========================================================" ]; then
        read -r refLine
        break
      else
        if [ "$stage2" = true ]; then

          ourLine=$(sed "${readLines}q;d" output.txt)
          readLines=$(("$readLines" + 1))

          refLine=$(sed 's/\b0x[^ ]*/#addr#/g' <<<"$refLine") # substitute any addresses with #addr#
          ourLine=$(sed 's/\b0x[^ ]*/#addr#/g' <<<"$ourLine")

          if [ "$refLine" != "$ourLine" ]; then
            correctFlag=false
            read -r refLine
            break
          fi
        fi
      fi
    done
  } <"${refOutput/.wacc/.txt}"

  if [ "$refLine" != "The exit code is ${ourExitCode}." ]; then #compare exit code
    correctFlag=false
  fi

  if [ "$correctFlag" = true ]; then
    passedTests=$((passedTests + 1))
    echo -e "$GREEN"passed"$NC": test "$runTests" "$pathname"
  else
    echo -e "$RED"failed"$NC": test "$runTests" "$pathname"
  fi
  runTests=$((runTests + 1))
}

# goes through all progress files in the temp_progress directory
# and outputs appropriate summaries for each feature
check_progress() {
  for progressFile in temp_progress/*; do

    failedTests=$(($(grep "" -c "$progressFile") - 1)) # number of lines in file - 1
    numTests=$(head -n 1 "$progressFile")              # first line of progress file holds number of tests for this feature
    progress=$((100 * (numTests - failedTests) / numTests))
    name=$(sed 's:.*/::' <<<"$progressFile")

    echo "======================="

    errorMessage=$(pickFeatureMessage "$name")

    if [ "$progress" = 100 ]; then
      echo -e "$GREEN""$errorMessage""$NC"
    else
      echo -e "$RED""$errorMessage""$NC"
      echo -e "$RED""Failing tests for :""$NC"
      {
        read -r # read past first line as it indicates number of tests altogether
        while read -r currLine; do
          echo "$currLine" # print failing tests for this feature from progress file
        done
      } <"$progressFile"
    fi
  done
}

# picks an appropriate message for each implemented feature
pickFeatureMessage() {
  name=$1
  case $name in
  advanced | basic | expressions | array | function)
    echo "Progress for $name features: $progress%"
    ;;
  variables)
    echo "Progress for variable declarations: $progress%"
    ;;
  if | while)
    echo "Progress for $name statements: $progress%"
    ;;
  pairs)
    echo "Progress for pair features: $progress%"
    ;;
  sequence)
    echo "Progress for sequential composition: $progress%"
    ;;
  scope)
    echo "Progress for scoping: $progress%"
    ;;
  IO)
    echo "Progress for advanced $name : $progress%"
    ;;
  print | read)
    echo "Progress for IO:$name feature: $progress%"
    ;;
  runtimeErr)
    echo "Progress for runtime errors: $progress%"
    ;;
  arrayOutOfBounds | divideByZero | integerOverflow | nullDereference)
    echo "Progress for runtime errors feature: $name: $progress%"
    ;;
  *)
    echo "Progress for $name: $progress%"
    ;;
  esac
}

# runs a test whilst keeping track of the failing tests per feature
run_test_with_progress() {
  pathname=$1

  # finds the path of the directory the test is in
  baseDir=$(readlink -f "$pathname" | sed 's|\(.*\)/.*|\1|')

  # finds the name of the progress file which holds the progress for the specific feature
  progressFile="temp_progress/"$(readlink -f "$pathname" | sed 's|\(.*\)/.*|\1|' | sed 's:.*/::')

  if [ ! -e "$progressFile" ]; then
    (find "$baseDir" -maxdepth 1 -type f | wc -l) >"$progressFile" # creates a new file with the first line being set
    # to the number of tests implemented for this feature
  fi

  tempRunTests="$passedTests"
  execute "$pathname"

  if [ "$tempRunTests" == "$passedTests" ]; then
    echo "$pathname" >>"$progressFile" # put the path of the failing test in the progress file
  fi
}

run_test_in_dir() {
  for pathname in "$1"/*; do
    if [ -d "$pathname" ]; then
      run_tests "$pathname"
    else
      case "$pathname" in
      *.wacc)
        run_test_with_progress "$pathname"
        ;;
      esac
    fi
  done
}

run_tests() {
  for pathname in $1; do
    if [ -d "$pathname" ]; then
      run_test_in_dir "$pathname"
    else
      case "$pathname" in
      *.wacc)
        run_test_with_progress "$pathname"
        ;;
      esac
    fi
  done
}

RED='\e[31m'
GREEN='\e[32m'
NC='\e[39m' # No Color
TESTS_TO_RUN=$1
runTests=0
passedTests=0
mkdir "temp_progress"
LINES=$(cat $TESTS_TO_RUN)
run_tests "$LINES"
check_progress
echo "======================="
echo SUMMARY
echo passed $passedTests / $runTests "$TESTS_TYPE" tests
rm -r "temp_progress"
rm output.txt
rm output1.txt

if [ $passedTests -eq $runTests ]; then
  exit 0
else
  exit 1
fi
