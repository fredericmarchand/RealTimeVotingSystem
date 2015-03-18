#!/bin/bash

if [ "$#" -ne 1 ]; then
    echo "Illegal number of parameters"
    exit
fi

JAR_PATH=$1

for i in `seq 60002 60310`;
do
	java -jar $JAR_PATH $i $i
done  
