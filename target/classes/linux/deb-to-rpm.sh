#!/bin/bash

function exist_command() {
        [ ! $(which $1) ] && echo "$1 is needed to generate rpm from deb package" && exit 1  
}

exist_command alien
exist_command rpmbuild

filename=$(basename -- $1)
package=$(echo "${filename%.*}" | tr "_" "-")
dir=$(dirname -- $1)

cd $dir
alien -g --to-rpm $filename
rpmbuild --buildroot $(pwd)/$package --nodeps -bb $package/*.spec
