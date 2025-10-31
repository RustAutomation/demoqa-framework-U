# Java API + UI framework (Java 21, Gradle)

## Что в проекте
- API тесты (ReqRes) — `ApiExampleTest`
- Visual тест (DemoQA) — `VisualTest`
- UI тест (Practice Form) — `PracticeFormTest`
- POM + утилиты для Allure (вложения, шаги)
- BrowserManager (Playwright)
- VisualDiff — создает PNG с полупрозрачным красным наложением для отличий

## Требования
- Java 21 (в Gradle Toolchain настроено)
- Gradle wrapper (в проекте)
- Allure CLI (для генерации и открытия отчёта)
- Playwright runtime: после добавления зависимостей выполните `mvn`/`gradle` либо запустите код, Playwright автоматически загрузит парни браузеров при первом запуске либо вы можете выполнить `mvn exec` / вручную скачать Playwright browsers
    - Для Playwright Java: `playwright install` (см. документацию Playwright Java)
- Интернет (для доступа к `https://reqres.in` и `https://demoqa.com`)

## Как запустить
1. Клонируй репозиторий и перейди в корень проекта.
2. Выполни (Unix/macOS):
   ```bash
   ./run_all.sh
 или
 ./gradlew test

 allure generate build/allure-results -o build/allure-report --clean
 allure open build/allure-report

Windows:
gradlew test
allure generate build\allure-results -o build\allure-report --clean
allure open build\allure-report
