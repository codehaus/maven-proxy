#!/bin/sh
echo Building maven-proxy


for module in core standalone webapp;
do
  cd $module
  maven build:snapshot
  RESULT=$?
  if [ $RESULT -ne "0" ]; then
    echo Exit value of $RESULT in module $module
    exit $RESULT
  fi
  cd ..
done

