#!/usr/bin/env bash
set -euo pipefail

# 1) Запустить тесты
./gradlew test

# 2) Сгенерировать отчет (требуется Allure CLI установлен)
if command -v allure >/dev/null 2>&1; then
  allure generate build/allure-results -o build/allure-report --clean
  allure open build/allure-report
else
  echo "Allure CLI not found. Install it (brew install allure or download) to generate & open report"
  echo "Allure results are in build/allure-results"
fi
