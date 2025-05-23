@echo off
setlocal

:: Paths
set SRC_DIR=src\main\java
set RES_DIR=src\main\resources
set OUT_DIR=out
set LIB_DIR=lib
set JAVA_FX_LIB="lib\javafx"

echo Cleaning old build...
if exist %OUT_DIR% (
    rmdir /S /Q %OUT_DIR%
)

echo Compiling Java files...
mkdir %OUT_DIR%
javac --module-path %JAVA_FX_LIB% --add-modules javafx.controls,javafx.fxml -cp "%LIB_DIR%\sqlite-jdbc-3.49.1.0.jar" -d %OUT_DIR% %SRC_DIR%\bank\system\*.java

echo Copying FXML files...
mkdir %OUT_DIR%\bank\system
xcopy /Y /I "%RES_DIR%\bank\system\*.fxml" "%OUT_DIR%\bank\system\" >nul

echo Copying icon resources...
mkdir %OUT_DIR%\bank\system\icons
xcopy "%RES_DIR%\bank\system\icons" "%OUT_DIR%\bank\system\icons" /E /I /Y >nul

echo Creating BankSystem.jar...
jar --create --file BankSystem.jar -C %OUT_DIR% .

echo Running the app...
java --module-path %JAVA_FX_LIB% --add-modules javafx.controls,javafx.fxml -cp "BankSystem.jar;%LIB_DIR%\sqlite-jdbc-3.49.1.0.jar" bank.system.Main

endlocal
pause
