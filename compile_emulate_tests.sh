#!/bin/sh

run_test_in_dir() {
  for pathname in "$1"/*; do
        if [ -d "$pathname" ]; then
            run_tests "$pathname"
        else
            case "$pathname" in
                *.wacc)
                  echo running test $runTests "$pathname"
                  ./compile "$pathname" >/dev/null 2>&1
                  arm-linux-gnueabi-gcc -o FILENAME1 -mcpu=arm1176jzf-s -mtune=arm1176jzf-s ${pathname/.wacc/.s}
                  qemu-arm -L /usr/arm-linux-gnueabi/ ${pathname/.wacc/} > output.txt
                  output=${pathname/valid/backendtests}
                  refLine=$(head -n 1 ${output/.wacc/.txt})
                  flag=true
                  if [ "$refLine" -eq $? ]; then
                    { read -r;
                    while read -r refLine && $flag;
                    do
                      ourLine=$(read -r ${pathname/valid/backendtests});
                      if not[ "$refLine" -eq "$ourLine" ]; then
                        flag=false
                      fi
                    done; } < output.txt
                    if [ $flag ]; then
                      echo Passed test $runTests "$pathname"
                      runTests=$((runTests + 1))
                    fi
                  fi
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
                  ./compile "$pathname" >/dev/null 2>&1
                  arm-linux-gnueabi-gcc -o FILENAME1 -mcpu=arm1176jzf-s -mtune=arm1176jzf-s ${pathname/.wacc/.s}
                  qemu-arm -L /usr/arm-linux-gnueabi/ ${pathname/.wacc/}
                  output=${pathname/valid/backendtests}
                  refLine=$(head -n 1 ${output/.wacc/.txt})
                  flag=true
                  if [ "$refLine" -eq $? ]; then
                    { read -r;
                    while read -r refLine && $flag;
                    do
                      ourLine=$(read -r ${pathname/valid/backendtests});
                      if not[ "$refLine" -eq "$ourLine" ]; then
                        flag=false
                      fi
                    done; } < output.txt
                    if [ $flag ]; then
                      echo Passed test $runTests "$pathname"
                      runTests=$((runTests + 1))
                      fi
                  fi
            esac
        fi
    done
}

touch output.txt
run_tests "src/test/valid"
rm output.txt

git commit -m "Added expected output files for half of the tests, changed testing script to compare output with reference output files [IK]"