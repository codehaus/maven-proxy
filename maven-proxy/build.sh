#!/bin/sh


for module in core standalone webapp;
do
  cd $module
  maven 
  cd ..
done
