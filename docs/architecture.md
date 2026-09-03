# Architecture

This document describes the architecture of the Nextcloud Deck project. It follows a modular structure inspired by Clean Architecture, separating business logic from implementation details.

## Module Dependency Graph

The following flowchart visualizes the dependencies between the Gradle modules.

```mermaid
---
title: Architecture Flow Chart
---

graph TD
    classDef app color:#000000,fill:#ddcb55,stroke:#c2b14a
    classDef auth color:#ffffff,fill:#8855a8,stroke:#6f458a
    classDef data color:#ffffff,fill:#0082c9,stroke:#00669e
    classDef domain color:#000000,fill:#a5b872,stroke:#8b9c5f

    subgraph Apps
        android[":app:android"]:::app
        cli[":app:cli"]:::app
        javafx[":app:javafx"]:::app
        shared[":app:shared"]:::app
    end

    subgraph Auth
        apptoken[":auth:apptoken"]:::auth
        sso[":auth:sso"]:::auth
        weblogin[":auth:webloginflowv2"]:::auth
    end

    subgraph Data
        local[":data:local"]:::data
        remote[":data:remote"]:::data
        repository_data[":data:repository"]:::data
        sync_data[":data:sync"]:::data
        shared_data[":data:shared"]:::data
    end

    subgraph Domain
        model[":domain:model"]:::domain
        repository_domain[":domain:repository"]:::domain
        state[":domain:state"]:::domain
        sync_domain[":domain:sync"]:::domain
        usecases[":domain:usecases"]:::domain
    end

    %% App Dependencies
    android --> shared
    android --> sso

    cli --> shared
    cli --> apptoken

    javafx --> shared
    javafx --> weblogin
    javafx --> apptoken

    shared --> usecases
    shared --> model
    shared --> state
    shared --> local
    shared --> remote
    shared --> repository_data
    shared --> sync_data
    shared --> weblogin

    %% Auth Dependencies

    %% Data Dependencies
    local --> model
    local --> state
    local --> shared_data

    remote --> model
    remote --> shared_data

    shared_data --> model

    repository_data --> model
    repository_data --> state
    repository_data --> repository_domain
    repository_data --> local
    repository_data --> remote
    repository_data --> shared_data

    sync_data --> model
    sync_data --> repository_domain
    sync_data --> sync_domain
    sync_data --> repository_data
    sync_data --> local
    sync_data --> remote

    %% Domain Dependencies
    repository_domain --> model
    repository_domain --> state

    state --> model

    sync_domain --> model
    sync_domain --> state

    usecases --> model
    usecases --> state
    usecases --> repository_domain
    usecases --> sync_domain
```

## Core Workflows

### Data Lifecycle & Synchronization

The following sequence diagram illustrates the unified flow of account import, local data manipulation, and the subsequent synchronization process.

```mermaid
---
title: Architecture Sequence Diagram
---

sequenceDiagram
    participant App as "UI Layer (:app:*)"
    participant Domain as "Domain Layer<br/>(UseCase / Scheduler)"
    participant Data as "Data Layer<br/>(Repository / SyncManager)"
    participant DB as "Local DB (:data:local)"
    participant Remote as "Remote API (:data:remote)"

    Note over App, Remote: 1. Account Authentication / Import
    App->>Domain: execute(ImportAccountUseCase)
    Domain->>Data: addAccount(Credentials)
    Data->>DB: insert(AccountEntity)
    DB-->>Data: AccountID
    Data-->>Domain: AccountID
    Domain->>Domain: scheduleSync(AccountID)
    Domain-->>App: SyncStatus (Flow)

    Note over App, Remote: 2. Local Data Manipulation (e.g., Adding a Card)
    App->>Domain: execute(AddCardUseCase)
    Domain->>Data: createCard(CreateCard)
    Data->>DB: insert(CardEntity, status=LOCAL_EDITED)
    DB-->>Data: Void
    Data-->>Domain: Void
    Domain-->>App: CompletableFuture<Void>

    Note over App, Remote: 3. Synchronization (Background or Manual)
    App->>Domain: execute(ScheduleSyncUseCase)
    Domain->>Data: scheduleSynchronization(AccountID)
    
    Data->>DB: query(status=LOCAL_EDITED)
    DB-->>Data: List<CardEntity>
    Data->>Remote: POST /cards (New Card)
    Remote-->>Data: 201 Created (RemoteID, Etag)
    Data->>DB: update(CardEntity, RemoteID, status=SYNCED)
    
    Data->>Remote: GET /boards (Check for changes)
    Remote-->>Data: 200 OK (Remote Changes)
    Data->>DB: upsert(Remote Entities)
```

## Architectural Layers

### 1. Domain Layer (`:domain:*`)
The core of the application, containing business logic and rules. It is written in pure Java to ensure portability and ease of testing.
*   **model**: Plain Java objects (Entities) representing Deck concepts (Boards, Cards, Labels).
*   **usecases**: Interactors that implement specific business workflows.
*   **repository/sync/state**: Interfaces and abstractions for data access and state management.

### 2. Data Layer (`:data:*`)
Implements the interfaces defined in the Domain layer.
*   **local**: Persistent storage using Room (SQLite).
*   **remote**: Network communication using Retrofit and OpenAPI generated clients.
*   **repository/sync**: Orchestration between local and remote data sources, implementing the offline-first strategy.

### 3. Auth Layer (`:auth:*`)
Handles authentication mechanisms across different platforms.
*   **sso**: Android-specific Single Sign-On integration.
*   **webloginflowv2**: Nextcloud Web Login Flow implementation.

### 4. Apps Layer (`:app:*`)
Platform-specific entry points and UI implementations.
*   **android**: Jetpack Compose based Android application.
*   **javafx**: JavaFX based desktop application.
*   **cli**: Command-line interface using Picocli.
*   **shared**: A cross-cutting module that aggregates common dependencies and provides shared utilities to all client apps.

## Architectural Patterns

### Clean Architecture
The project strictly separates the **Domain** (business logic) from **Data** (infrastructure) and **Apps** (UI). The Domain layer is pure Java and has no knowledge of how data is stored or how the UI is rendered.

### CQRS (Command Query Responsibility Segregation)
The project follows the CQRS pattern with a specific rule:
*   **Queries**: UseCases that read data for the UI. They return reactive types like `Flow.Publisher`.
*   **Commands**: UseCases that write data. They return `CompletableFuture` to signal completion.
*   *Note*: UseCases should either read or write, but not both.

### Threading Model
*   **UI/Main Thread**: Applications expect to receive and apply data on the main thread.
*   **IO Thread**: UseCases are designed to be executed on background threads to keep the UI responsive.

## Coding Principles

### Strongly Typed Domain
To avoid "primitive obsession," business properties like IDs are strictly typed using Java `record`s. For example, a `CardID` is a distinct type rather than a simple `long` or `String`.

### Reactive and Immutable
The architecture favors immutability and reactive streams.
*   Public APIs in the Domain layer use standard Java constructs like `java.util.concurrent.Flow.Publisher` and `java.util.concurrent.CompletableFuture` to hide implementation details (like RxJava).
*   Converters are used internally to bridge reactive libraries with these standard APIs.

## Data Flow
1.  **UI** triggers a **UseCase** (Command or Query).
2.  **UseCase** interacts with a **Repository** (Domain interface).
3.  **Repository Implementation** (in Data layer) fetches/stores data using **Local** (Room) or **Remote** (Retrofit) sources.
4.  **Sync Engine** works in the background to reconcile **Local** and **Remote** states.
