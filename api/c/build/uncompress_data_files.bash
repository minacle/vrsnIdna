#!/bin/bash
# 
# File:   uncompress_data_files.bash
# Author: prsrinivasan
#
# Created on Oct 18, 2010, 3:40:00 PM
#

for zipfile in `ls ../../../data/unicode/*.gz`
do
    filename=$(basename $zipfile)
    extension=${filename##*.}
    filename=${filename%.*}

    echo "uncompressing ${zipfile}"
    gunzip -c ${zipfile} > $(dirname $zipfile)/${filename}
done

echo "done"
