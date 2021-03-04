#!/bin/bash

execute() {
  pathname=$1
  echo running test $runTests "$pathname"
  stage1=true

	refOutput=${pathname/valid/backendTests}

	if [ ! -e ${refOutput/.wacc/.txt} ]; then
	  echo $'\n' | ./refCompile "-x" "$pathname" > ${refOutput/.wacc/.txt}
	  fi

	FILE_PATH=$(basename "$pathname")


  ./compile "$pathname" "-a" >/dev/null 2>&1
  arm-linux-gnueabi-gcc -o ${FILE_PATH/.wacc/} -mcpu=arm1176jzf-s -mtune=arm1176jzf-s ${FILE_PATH/.wacc/.s}
  echo $'\n' | qemu-arm -L /usr/arm-linux-gnueabi/ ${FILE_PATH/.wacc/} > output.txt
  ourExitCode="${PIPESTATUS[0]}"
  rm ${FILE_PATH/.wacc/.s}
  rm ${FILE_PATH/.wacc/}

  {
    correctFlag=true
    stage2=false
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
             read -r ourLine<output.txt;
             refLine=${refLine/0x*/addr}
             ourLine=${ourLine/0x*/addr}
              if [ "$refLine" != "$ourLine" ]; then
              correctFlag=false
              read -r refLine
              break
              fi
            fi
        fi
      done; }  < "${refOutput/.wacc/.txt}"

        #compare exit code
        if [ "$refLine" != "The exit code is ${ourExitCode}." ] ; then
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
echo =======================
echo SUMMARY
echo passed $passedTests / $runTests "$TESTS_TYPE" tests

