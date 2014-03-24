@echo off

set PinImage=PinImage.jar
set PinImageConfig=%1

echo Starting PinImage
java -Dfile.encoding=UTF-8 -jar %PinImage% %PinImageConfig%