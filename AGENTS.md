# Agent Guidance

## Project overview
This repository contains educational end-to-end UI and API automation tests. It is a Maven project targeting Java 17 and uses Playwright for browser automation with JUnit 5 for test execution. For API tests we use REST-Assured. 
This project has 3 isolated subprojects:

## Sub-project *automation-practice-form*
Playwright UI automation tests for web form https://demoqa.com/automation-practice-form
Project files are located here `src/test/java/automation_practice_form` 

## Sub-project *restful-booker*
REST API tests for https://restful-booker.herokuapp.com/
Project files are located here `src/test/java/restful_booker`


## Sub-project *graphql-playground*
GraphQL API tests for  https://eu-central-1-shared-euc1-02.cdn.hygraph.com/content/clv6lwqu7000001w690st4vix/master
Project files are located here `src/test/java/graphql_playground`

## Development conventions
- Keep test scenarios in `tests` packages and UI interactions/selectors in `pages` page-object classes. Do not put page-object behavior directly into test classes unless the example specifically demonstrates that approach.
- Prefer stable, user-facing locators and clear assertions. Keep changes focused on the current example/module and follow the existing Java naming and package conventions.


## Common commands
Run tests:
```bash
mvn test
```

Run specific tests (replace the param value with the class name of your test):
```bash
mvn test -Dtest=com.tests.MyTest
```


## Branch and repository safety
- Do not change course-module structure or unrelated examples as part of a focused task.
- Preserve user changes and inspect the current branch state before making broad edits.

## Validation
After Java or Maven changes, run the narrowest relevant test first, then full `mvn test` when practical. Report any environment-related failures separately from code failures (for example, missing Playwright browsers or a headless-display limitation).
