[![CircleCI](https://dl.circleci.com/status-badge/img/gh/sudhir-karkala/spring6-recipe-app/tree/main.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/sudhir-karkala/spring6-recipe-app/tree/main)

# Spring Recipe App

A simple web application for collecting recipes and managing the details that make them useful: categories, preparation and cooking times, ingredients, directions, and notes. Browse the recipe list, open a recipe for its full details, or edit and manage its ingredients from the browser.

## What you can do

- **Browse recipes** from the home page and open a recipe to see its details.
- **Create and edit recipes**, including description, categories, difficulty, servings, source, URL, directions, and notes.
- **Choose time units** independently for prep time and cook time (Minutes or Hours).
- **Manage ingredients for each recipe**: add new ingredients, view ingredient details, edit them, and delete them.
- **Keep ingredient quantities and units together**, with support for units of measure.
- **Delete recipes and ingredients using form submissions**, rather than links that modify data when opened.

To manage ingredients, save the recipe first, then choose **Manage** in its Ingredients section.

## Run locally

### Requirements

- Java 21
- No separate Maven installation is needed; the project includes the Maven Wrapper.

From the project directory, start the application:

```bash
./mvnw spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080) in your browser. The recipe list is also available at [http://localhost:8080/recipes](http://localhost:8080/recipes).

To stop the application, press `Ctrl+C` in the terminal.

## Build and tests

Run the full build, unit tests, and integration tests with:

```bash
./mvnw clean verify
```

The same command is used by the CircleCI pipeline, which runs on Java 21.

## Technology

- Java 21
- Spring Boot 3.3
- Spring MVC and Thymeleaf for web pages and forms
- Spring Data JPA for persistence
- H2 database
- Maven Wrapper for reproducible builds
- CircleCI for continuous integration

## Data storage

The default configuration uses an in-memory H2 database. Sample data is loaded when the application starts, but changes made while using the app are not retained after the application stops. This setup is convenient for local development and demonstration; it is not configured as persistent production storage.
