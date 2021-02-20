#!/bin/sh

run_test_in_dir() {
  for pathname in "$1"/*; do
        if [ -d "$pathname" ]; then
            run_tests "$pathname" "$2"
        else
            case "$pathname" in
                *.wacc)
                  echo running test $runTests "$pathname"
                  ./compile "$pathname" >/dev/null 2>&1
                  wacc=".wacc"
                  assembly=".s"
                  echo ${pathname/$wacc/}
                  arm-linux-gnueabi-gcc -o FILENAME1 -mcpu=arm1176jzf-s -mtune=arm1176jzf-s ${pathname/$wacc/$assembly}
                  qemu-arm -L /usr/arm-linux-gnueabi/ ${pathname/$.wacc/}
                  runTests=$((runTests + 1))

            esac
        fi
    done
}

run_tests () {
  for pathname in $1; do
        if [ -d "$pathname" ]; then
            run_test_in_dir "$pathname" "$2"
        else
            case "$pathname" in
                *.wacc)
                  echo running test $runTests "$pathname"
                  ./compile "$pathname" >/dev/null 2>&1
                  wacc=".wacc"
                  assembly=".s"
                  arm-linux-gnueabi-gcc -o FILENAME1 -mcpu=arm1176jzf-s -mtune=arm1176jzf-s ${pathname/$wacc/$assembly}
                  qemu-arm -L /usr/arm-linux-gnueabi/ ${pathname/$wacc/}
                  runTests=$((runTests + 1))

            esac
        fi
    done
}

run_tests "src/test/valid"
