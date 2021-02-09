#!/bin/sh
passedTests=0
runTests=0
run_tests () {
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
                    echo test $runTests passed
                  fi
                  runTests=$((runTests + 1))

            esac
        fi
    done
}

VALID_TESTS=src/test/valid
SYNTAX_ERROR_TESTS=src/test/invalid/syntaxErr
SUCCESS_CODE=0
SYNTAX_ERROR_CODE=100
runValidTests=0
runSyntaxErrorTests=0
passedValidTests=0
passedSyntaxErrorTests=0

echo Running valid tests...
run_tests "$VALID_TESTS" $SUCCESS_CODE
echo passed $passedTests / $runTests valid tests
runValidTests=$runTests
passedValidTests=$passedTests

echo =======================
passedTests=0
runTests=0
echo Running syntax error tests...
run_tests "$SYNTAX_ERROR_TESTS" $SYNTAX_ERROR_CODE
echo passed $passedTests / $runTests syntax error tests
runSyntaxErrorTests=$runTests
passedSyntaxErrorTests=$passedTests

echo =======================
echo SUMMARY
echo passed $passedValidTests / $runValidTests valid tests
echo passed $passedSyntaxErrorTests / $runSyntaxErrorTests syntax error tests

if [ $passedValidTests -eq $runValidTests ] && [ $passedSyntaxErrorTests -eq $runSyntaxErrorTests ]; then
  exit 0
else
  exit 1
fi