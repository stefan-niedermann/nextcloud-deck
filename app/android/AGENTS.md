# Deck Android

Written completely in Kotlin

## New features
- Strictly following [Recommendations for Android architecture](https://developer.android.com/topic/architecture/recommendations) and uses [Jetpack libraries](https://developer.android.com/jetpack/androidx/explorer?case=all)
- UI follows [Material 3 Expressive](https://m3.material.io/) using [Jetpack Compose](https://developer.android.com/compose)

## Unit-Tests
- Tests should exclusively use [`Robolectric`](https://robolectric.org/) and not require an actual Android Virtual Device
- Tests should consequently mock *UseCases and use real implementation for the reset.
- Tests should be focused on UI state and interaction.
- Tests should interact with the test subject via semantic attributes like `role` (or `testTag` if necessary) of UI elements or mocked dependencies.