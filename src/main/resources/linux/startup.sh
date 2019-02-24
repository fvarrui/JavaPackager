#!/bin/sh
SCRIPTPATH="$(dirname "$(readlink -f "$0")")"
JAVA=$SCRIPTPATH/jre/bin/java
TEUTONPATH=$SCRIPTPATH/teuton-panel
GDKBACKEND=x11
pkexec $JAVA -jar $TEUTONPATH $@
exit 0
