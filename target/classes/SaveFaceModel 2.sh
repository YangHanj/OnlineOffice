#!/bin/bash
PATH=$1
SAVE=$2
ID=$3
cd /Users/yanghan/Desktop/Project/project2/FaceRecognition
/Users/yanghan/ThirdParty/conda3-4.10/anaconda3/envs/yang/bin/python LoadModel.py -path $PATH -save $SAVE -id $ID