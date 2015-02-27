#!/bin/bash

for i in `seq 60002 60310`;
do
	java ../controller/DistrictServer districtName port
done  
