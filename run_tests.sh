#!/bin/sh

echo Running some tests...
for line in $(cat src/tests/test_to_run.txt); do
  cat $line | ./grun antlr.WACC program -tree
  echo Finished test...
done