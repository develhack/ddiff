@echo off
setlocal

set base_dir=%~dp0.

java -cp "%base_dir%\lib\*" com.develhack.ddiff.Main %*
