#!/bin/bash

################################################################################################
# run for Linux/Unix platforms, to install glib and its dependent packages
# assumes that all tar.gz files are in the same staging directory
################################################################################################

scriptDirName=$(dirname $0)

#############################
# install gettext package
#############################
echo "!!!!!!!!!!! GETTEXT PACKAGE INSTALL START !!!!!!!!!!!"

cd ${scriptDirName}/../../../lib/linux-unix-libs
zipFile=`ls gettext-*.tar.gz`

tarFile=${zipFile%.*}
fileName=${tarFile%.*}

gunzip ${zipFile}
tar -xvf ${tarFile}

cd ${fileName}

./configure
make
sudo make install

cd ..

echo "!!!!!!!!!!! GETTEXT PACKAGE INSTALL DONE !!!!!!!!!!!"





#############################
# install pkg-config package
#############################
echo "!!!!!!!!!!! PKG_CONFIG PACKAGE INSTALL START !!!!!!!!!!!"

cd ${scriptDirName}/../../../lib/linux-unix-libs
zipFile=`ls pkg-config-*.tar.gz`

tarFile=${zipFile%.*}
fileName=${tarFile%.*}

gunzip ${zipFile}
tar -xvf ${tarFile}

cd ${fileName}

./configure
make
sudo make install

cd ..

echo "!!!!!!!!!!! PKG-CONFIG PACKAGE INSTALL DONE !!!!!!!!!!!"





#############################
# install glib package
#############################
echo "!!!!!!!!!!! GLIB PACKAGE INSTALL START !!!!!!!!!!!"

cd ${scriptDirName}/../../../lib/linux-unix-libs
zipFile=`ls glib-*.tar.gz`

tarFile=${zipFile%.*}
fileName=${tarFile%.*}

gunzip ${zipFile}
tar -xvf ${tarFile}

cd ${fileName}

./configure
make
sudo make install

cd ..

echo "!!!!!!!!!!! GLIB PACKAGE INSTALL DONE !!!!!!!!!!!"


cd ${BASE_DIR}
