```mermaid
---
title: Lifecycle of a Stage
---
flowchart TD
    initializing((ApplicationRouter initializes StageManager)) --> loading[Show splashscreen]
    loading --> resolving[Subscribe to ArgsResolver]
    
    subgraph Watching ["Watching (Reactive Stream)"]
        resolving --> resolved{Resolving successful?}
        resolved -- success --> detectingchanges[Check if Args changed]
        resolved -- failure --> recovering[recoverError]
    
        detectingchanges -- changed --> savepromptevaulation{SavePromptGuarded}
    
        savepromptevaulation -- allowed --> showcontent((Show content))
    end
    
    recovering -- success --> loading
    recovering -- failure --> stopped((Show Error Scene))
```