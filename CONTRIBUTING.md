# Processual overview

## Issues / Pull Requests

- You should discuss new features and behavior changes in an issue before starting with the implementation

## Commit messages

- Commit messages must strictly follow [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/#specification)
- Commit messages must be `Signed-off-by`
- Commit messages must contain `Co-authored-by` mentioning the used LLM in case a LLM was involved in the code changes
- Commit messages should focus on the motivation behind a change, not be a summary of what was changed

# Technical overview

## Project setup

- Gradle multi module project setup
- Written primarily in Java and using Kotlin only where necessary
- Applications Targets Android, Desktop (via JavaFx) and CLI
- Little dependencies.
    - Public APIs should not expose implementation details.
        - Use `Flow.Publisher` for observable data instead of an `RxJava` `Flowable`.
          Use converters like `FlowAdapters` to keep the implementation details secret.
        - Use `CompletableFuture` for one shot queries or commands
    - Prefer plain native Java constructs like `record` over project Lombok

## Architecture

- Clean Architecture with encapsulated UseCases in Domain layers
- Threading
    - Applications usually have one main thread and expect to apply data on the main thread
    - *UseCases should be executed on IO threads

## Coding style

- Reactive and immutable Programming
- Strong typed business properties
    - Business properties like IDs should always be strictly typed (instead of using a primitive)
    - If an `ID` is bound hard to another domain object like `Card`, declare the `ID` record as within this domain object.
- Avoid reflection access at runtime for performance reasons, prefer compile time annotation frameworks

## Implement Unit-Tests

- Ensure that new features or changed behavior are covered by Unit-Tests.
- Avoid white-box testing.
- Ensure tests describe behavior
- Ensure tests run successfully
