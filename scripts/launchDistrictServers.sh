#!/bin/bash

for i in `seq 60002 60005`;
do
	echo "../controller/ClientController 0 $i test_files test_results$i"
	java ../controller/ClientController 0 i test_files test_results"$i"
	sleep 5
done  
