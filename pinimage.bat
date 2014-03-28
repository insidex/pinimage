@echo off

java -jar %PINIMAGEJAR% %PINCMD% %PARAMS%

REM java -Duser.language=en -Duser.country=US -Duser.variant=EN -Dfile.encoding=UTF-8 -jar %PinImage% %PinImageConfig%
REM java -jar pinimage.jar cmd parse "c:\tmp\pdf-in2\bill_first_page.pdf" "Покупатель:([^,]+), ИНН~1" "Покупатель:[^,]+, ИНН (\d+)~1"