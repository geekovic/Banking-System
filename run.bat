@echo off
setlocal

set LIB_DIR=lib
set JAVA_FX_LIB="lib\javafx"

echo Running the app...
java --module-path %JAVA_FX_LIB% --add-modules javafx.controls,javafx.fxml -cp "BankSystem.jar;%LIB_DIR%\sqlite-jdbc-3.49.1.0.jar" bank.system.Main

endlocal
pause
