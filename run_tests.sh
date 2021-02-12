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
                  if [ $? -eq $(($2)) ]; then
                    passedTests=$((passedTests + 1))
                    echo ${GREEN}passed${NC}: test $runTests "$pathname"
                  else
                    echo ${RED}failed${NC}: test $runTests "$pathname"
                  fi
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
                  if [ $? -eq $(($2)) ]; then
                    passedTests=$((passedTests + 1))
                    echo ${GREEN}passed${NC}: test $runTests "$pathname"
                  else
                    echo ${RED}failed${NC}: test $runTests "$pathname"
                  fi
                  runTests=$((runTests + 1))

            esac
        fi
    done
}



TESTS_TYPE=$1
TESTS_TO_RUN=$2
EXPECTED_CODE=$3
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color
echo =======================
passedTests=0
runTests=0
echo Running "$TESTS_TYPE" tests...
LINES=$(cat $TESTS_TO_RUN)
run_tests "$LINES" $EXPECTED_CODE

echo =======================
echo SUMMARY
echo passed $passedTests / $runTests "$TESTS_TYPE" tests


if [ $passedTests -eq $runTests ]; then
  exit 0
else
  exit 1
fi


