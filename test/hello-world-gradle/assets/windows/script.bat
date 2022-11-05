@echo off
FOR /F "tokens=*" %%g IN ('date /t') do (SET DATE=%%g)
FOR /F "tokens=*" %%g IN ('time /t') do (SET TIME=%%g)
echo %DATE%%TIME% >> %USERPROFILE%\log.txt