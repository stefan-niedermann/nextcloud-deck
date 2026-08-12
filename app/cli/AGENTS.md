# Deck CLI

## New features and editing existing features

- Uses [picocli](https://picocli.info/)
- The CLI consists of a set of commands that are not interactive.
- Commands do not prompt the user for entering more information but fail with a speaking error
  message
- Allow arguments for multiple use cases
    - `--localId` for local IDs or alternatively
    - `--remoteId` for remote IDs (a synchronization may be necessary while resolving the arguments)
        - Optionally additional account related information like an `URL` or an account name

## Unit-Tests

- Tests should consider
  the [official documentation of picocli](https://picocli.info/#_testing_your_application)
- Tests should consequently mock *UseCases and use real implementation for other dependencies.
- Tests should be focused on arguments and outputs.