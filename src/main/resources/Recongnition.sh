#!/bin/bash
PATH=$1
SAVE=$2
cd /Users/yanghan/Desktop/Project/project2/FaceRecognition
/Users/yanghan/ThirdParty/conda3-4.10/anaconda3/envs/yang/bin/python Recongnition.py -path $PATH -save $SAVE
