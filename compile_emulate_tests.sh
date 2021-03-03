#!/bin/bash

run_test_in_dir() {
  for pathname in "$1"/*; do
        if [ -d "$pathname" ]; then
            run_tests "$pathname"
        else
            case "$pathname" in
                *.wacc)
                  echo running test $runTests "$pathname"
                  flag=true

		              refOutput=${pathname/valid/backendTests}

		              if [ ! -e ${refOutput/.wacc/.txt} ]; then
		                echo $'\n' | ./refCompile "-x" "$pathname" > ${refOutput/.wacc/.txt}
		              fi

                  ./compile "$pathname" "-a">/dev/null 2>&1
                  arm-linux-gnueabi-gcc -o ${pathname/.wacc/} -mcpu=arm1176jzf-s -mtune=arm1176jzf-s ${pathname/.wacc/.s}
                  qemu-arm -L /usr/arm-linux-gnueabi/ ${pathname/.wacc/} > output.txt
                  ourExitCode=$?
                  rm ${pathname/.wacc/.s}
                  rm ${pathname/.wacc/}

                  { read -r;
                  correctFlag=true
                  while read -r refLine;
                    do
                     if "$flag" && [ "$refLine" = "===========================================================" ]; then
                        flag=false
                      fi
                      flag=true
                      { read -r;
                      while "$correctFlag" && "$flag" && read -r refLine;
                        do
                          if "$flag" && [ "$refLine" = "===========================================================" ]; then
                            flag=false
                            read -r refLine
                          fi
                          if [ ! $flag ]; then
                            #compare outputs
                            ourLine=$(read -r ${pathname/valid/backendTests});
                            if [ "$refLine" != "$ourLine" ]; then
                              correctFlag=false
                            fi
                          fi
                        done; } < ${refOutput/.wacc/.txt}

                      #compare exit code
                      if [ "$refLine" != "The exit code is ${ourExitCode}." ] ; then
                        correctFlag=false
                      fi
                    done; } < ${refOutput/.wacc/.txt}

                   if [ $correctFlag ]; then
                      passedTests=$((passedTests + 1))
                      echo -e "$GREEN"passed"$NC": test "$runTests" "$pathname"
                    else
                      echo -e "$RED"failed"$NC": test "$runTests" "$pathname"
                    fi
                    runTests=$((runTests + 1))
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
                  echo running test $runTests "$pathname"
                  flag=true

		              if not[ -e ${refOutput/.wacc/.txt} ]; then
		                ./refCompile "-x" "$pathname" > ${refOutput/.wacc/.txt}
		              fi

                  ./compile "$pathname" "-a">/dev/null 2>&1
                  arm-linux-gnueabi-gcc -o ${pathname/.wacc/} -mcpu=arm1176jzf-s -mtune=arm1176jzf-s ${pathname/.wacc/.s}
                  qemu-arm -L /usr/arm-linux-gnueabi/ ${pathname/.wacc/} > output.txt
                  ourExitCode=$?
                  rm ${pathname/.wacc/.s}
                  rm ${pathname/.wacc/}

                  { read -r;
                  correctFlag=true
                  while read -r refLine;
                    do
                      if "$flag" && [ "$refLine" = "===========================================================" ]; then
                        flag=false
                      fi
                      flag=true
                      { read -r;
                      while "$correctFlag" && "$flag" && read -r refLine;
                        do
                          if "$flag" && [ "$refLine" = "===========================================================" ]; then
                            flag=false
                            read -r refLine
                          fi
                          if [ ! $flag ]; then
                            #compare outputs
                            ourLine=$(read -r ${pathname/valid/backendTests});
                            if [ "$refLine" != "$ourLine" ]; then
                              correctFlag=false
                            fi
                          fi
                        done; } < ${refOutput/.wacc/.txt}

                      #compare exit code
                      if [ "$refLine" != "The exit code is ${ourExitCode}." ] ; then
                        correctFlag=false
                      fi
                    done; } < ${refOutput/.wacc/.txt}

                    if [ $correctFlag ]; then
                      passedTests=$((passedTests + 1))
                      echo -e "$GREEN"passed"$NC": test "$runTests" "$pathname"
                    else
                      echo -e "$RED"failed"$NC": test "$runTests" "$pathname"
                    fi
                    runTests=$((runTests + 1))
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
