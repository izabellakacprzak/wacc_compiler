#!/bin/sh
echo Running some tests...
passedTests=0
runTests=0
walk_dir () {
    for pathname in "$1"/*; do
        if [ -d "$pathname" ]; then
            walk_dir "$pathname"
        else
            case "$pathname" in
                *.wacc)
                     java -cp bin:lib/antlr-4.9.1-complete.jar src/Compiler.java "$pathname" >/dev/null 2>&1


                      if [ $? -eq 100 ]; then
                      passedTests=$((passedTests + 1))
                      echo test $runTests passed
                      fi
                      echo ran test $runTests "$pathname"
                      runTests=$((runTests + 1))

            esac
        fi
    done
}

DOWNLOADING_DIR=src/tests/invalid/syntaxErr

walk_dir "$DOWNLOADING_DIR"

#echo Running some tests...
#passedTests=0
#runTests=0
#for file in src/tests/invalid/sytaxErr/**/*.wacc; do
#  java -cp bin:lib/antlr-4.9.1-complete.jar src/Compiler.java file
#  echo $file
#  runTests=$((runTests + 1))
#  if [ ${?} -eq 100 ]; then
#  passedTests=$((passedTests + 1))
#  fi
#  echo Finished test...
#done
echo $passedTests '/' $runTests