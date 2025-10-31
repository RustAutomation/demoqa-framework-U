@echo off
gradlew test
where allure >nul 2>nul
if %errorlevel%==0 (
  allure generate build\allure-results -o build\allure-report --clean
  allure open build\allure-report
) else (
  echo "Allure CLI not found. Install it and run allure generate ..."
)
pause
