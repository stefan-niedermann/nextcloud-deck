# Domain

Contains business rules

## e2e Tests

- The e2e Tests are testing the use cases with a real backend.
- e2e Tests should usually perform actions on one virtual device, synchronize the changes to another virtual device and make the assertions on the second virtual device to implicitly cover the synchronization mechanism of each use case. This may not be applicable for tests that do not rely on synchronization (for example importing an account to a device).