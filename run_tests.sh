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
                    echo test $runTests passed
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
                    echo test $runTests passed
                  fi
                  runTests=$((runTests + 1))

            esac
        fi
    done
}

# valid tests variables
VALID_TESTS=src/test/valid_tests.txt
SUCCESS_CODE=0
runValidTests=0
passedValidTests=0

# syntax error tests variables
SYNTAX_ERROR_TESTS=src/test/syntax_error_tests.txt
SYNTAX_ERROR_CODE=100
runSyntaxErrorTests=0
passedSyntaxErrorTests=0

# sematic error tests variables
SEMANTIC_ERROR_TESTS=src/test/semantic_error_tests.txt
SEMANTIC_ERROR_CODE=200
runSemanticErrorTests=0
passedSemanticErrorTests=0

#echo Running valid tests...
#LINES=$(cat $VALID_TESTS)
#run_tests "$LINES" $SUCCESS_CODE
#echo passed $passedTests / $runTests valid tests
#runValidTests=$runTests
#passedValidTests=$passedTests

echo =======================
passedTests=0
runTests=0
echo Running syntax error tests...
LINES=$(cat $SYNTAX_ERROR_TESTS)
run_tests "$LINES" $SYNTAX_ERROR_CODE
echo passed $passedTests / $runTests syntax error tests
runSyntaxErrorTests=$runTests
passedSyntaxErrorTests=$passedTests


#echo =======================
#passedTests=0
#runTests=0
#echo Running semantic error tests...
#LINES=$(cat $SEMANTIC_ERROR_TESTS)
#run_tests "$LINES" $SEMANTIC_ERROR_CODE
#echo passed $passedTests / $runTests semantic error tests
#runSemanticErrorTests=$runTests
#passedSemanticErrorTests=$passedTests

echo =======================
echo SUMMARY
#echo passed $passedValidTests / $runValidTests valid tests
echo passed $passedSyntaxErrorTests / $runSyntaxErrorTests syntax error tests
#echo passed $passedSemanticErrorTests / $runSemanticErrorTests semantic error tests


if [ $passedSyntaxErrorTests -eq $runSyntaxErrorTests ]; then
  exit 0
else
  exit 1
fi
