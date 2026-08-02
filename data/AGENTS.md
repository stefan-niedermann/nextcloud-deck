# Data

- Consequently use [`MapStruct`](https://mapstruct.org/documentation/stable/api/) for mapping domain models to remote entities or database entities (or vice versa)
- Make use of the latest [`Lombok`](https://projectlombok.org/features/) features.
- Ensure we only rely on Java syntax and features that are available on the Android minSdk version defined in `:app:android`. Assume features of latest [Desugaring library](https://developer.android.com/studio/write/java8-support-table?hl=en) are available.
