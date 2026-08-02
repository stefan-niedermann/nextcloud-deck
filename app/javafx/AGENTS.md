# Deck PC

- Use latest Java syntax and features available in the version defined in build.gradle.kts 
- A [JavaFx](https://openjfx.io/javadoc/26/) based application using [FXML](https://openjfx.io/javadoc/26/javafx.fxml/javafx/fxml/doc-files/introduction_to_fxml.html)
- Supports multiple independent Windows (Stages) with their own state
- Each Window (Stage) lifecycle is managed by `StageManager`
- A `*Scene` is usually one Window (Stage) content and is composed by multiple `Features`
- Features do have own State and Access to Services and UseCases
- Views directly inherit an existing JavaFx element like a Node or a Parent or Pane and do not have any access to any injected classes. They usually have a `bind()` method that accepts a domain query model (See `:domain:model` module, `query` package).

## Unit-Tests
- Tests should exclusively use [`TestFX`](https://github.com/TestFX/TestFX)
- Tests should consequently mock *UseCases and use real implementation for other dependencies.
- Tests should be focused on UI state and interaction.
- Tests should prefer interacting with the test subject via `AccessibleRole` and other semantic information