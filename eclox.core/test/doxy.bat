@echo off

if "%1" == "-v" goto VERSION
goto ISSUE_195

:ISSUE_195
@echo Y:/a/script/on/my_Generating docs for compound ex_constants::Allocation...
@echo Generating docs for compound outofscope...
@echo machine.py:161: warning: Member ABC (variable) of namespace constants is not documented.
goto END

:VERSION
@echo 1.8.13
goto END

:END
goto :EOF