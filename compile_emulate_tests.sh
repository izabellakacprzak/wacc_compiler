#!/bin/bash

execute() {
  pathname=$1
  echo running test $runTests "$pathname"

	refOutput=${pathname/valid/backendTests}
  FILE_PATH=$(basename "$pathname")
  customInputPath="src/test/backendTests/testInput/${FILE_PATH/.wacc/.txt}"  # text file of custom user input

  customInput="\n"
  if [  -e "$customInputPath" ]; then # if custom user input exists, run the program with it
    read -r customInput<"$customInputPath";
  fi


	if [ ! -e ${refOutput/.wacc/.txt} ]; then  # check for the existence of a cached file
	  echo "$customInput" | ./refCompile "-x" "$pathname" > ${refOutput/.wacc/.txt} # get result from reference compiler
	fi


  ./compile "$pathname" "-a" >/dev/null 2>&1 # compile and emulate with local compiler
  arm-linux-gnueabi-gcc -o ${FILE_PATH/.wacc/} -mcpu=arm1176jzf-s -mtune=arm1176jzf-s ${FILE_PATH/.wacc/.s}
  echo "$customInput" | qemu-arm -L /usr/arm-linux-gnueabi/ ${FILE_PATH/.wacc/} > output1.txt
  ourExitCode=$?
  rm ${FILE_PATH/.wacc/.s}
  rm ${FILE_PATH/.wacc/}

  {
    sed 's/ *$//' output1.txt > output.txt # remove any extra spaces at the end of the line

    correctFlag=true
    stage1=true # everything up to the first line of ='s in the reference compiler's output
    stage2=false # everything from stage1 up to the second line of ='s - output from running the program
    readLines=1 # position in local compiler's output file
    while read -r refLine;
      do
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
             readLines=$(("$readLines"+1))

             refLine=$(sed 's/\b0x[^ ]*/#addr#/' <<< "$refLine") # substitute any addresses with #addr#
             ourLine=$(sed 's/\b0x[^ ]*/#addr#/' <<< "$ourLine")

              if [ "$refLine" != "$ourLine" ]; then
              correctFlag=false
              read -r refLine
              break
              fi
            fi
        fi
      done; }  < "${refOutput/.wacc/.txt}"

        if [ "$refLine" != "The exit code is ${ourExitCode}." ] ; then   #compare exit code
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

run_test_in_dir() {
  for pathname in "$1"/*; do
        if [ -d "$pathname" ]; then
            run_tests "$pathname"
        else
            case "$pathname" in
                *.wacc)
                  execute "$pathname"
            esac
        fi
    done
}

run_tests () {
  for pathname in $1; do
        if [ -d "$pathname" ]; then
            run_test_in_dir "$pathname"
        else
            case "$pathname" in
                *.wacc)
                  execute "$pathname"
            esac
        fi
    done
}

RED='\e[31m'
GREEN='\e[32m'
NC='\e[39m' # No Color
runTests=0
passedTests=0
touch output.txt
run_tests "src/test/valid"
rm output.txt
rm output1.txt
echo "======================="
echo SUMMARY
echo passed $passedTests / $runTests "$TESTS_TYPE" tests