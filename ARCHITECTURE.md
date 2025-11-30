Architecture & Technical Design 🏛️

This document provides a deep dive into the technical decisions, package structure, and specific implementation patterns used in the Agent Directory App. It serves as a guide for developers to understand the "Why" and "How" behind the code.

1. Project Structure 📂

The project adheres to Clean Architecture principles, enforcing a strict separation of concerns. The dependency rule is respected: inner layers (Domain) know nothing about outer layers (Data/Presentation).

com.aagamshah.newartxassignment
├── data/                       # The "How" - Implementation details
│   ├── local/                  # Room Database, DAOs, Preferences
│   ├── model/                  # DTOs and Entity classes
│   ├── remote/                 # Retrofit API, RemoteMediator
│   └── repository/             # Implementation of Domain interfaces
├── domain/                     # The "What" - Business Logic (Pure Kotlin)
│   ├── model/                  # UI-agnostic data classes
│   └── repository/             # Interfaces defining data contracts
├── presentation/               # The "Show" - UI and State
│   ├── homescreen/             # Home UI components
│   ├── postscreen/             # Profile UI components
│   ├── settings/               # Settings UI components
│   └── viewmodel/              # State holders (ViewModels)
├── navigation/                 # Navigation graph and routes
└── di/                         # Manual Dependency Injection (AppContainer)


2. Data Layer Strategy

API Handling 🌐

Retrofit: Used for type-safe HTTP requests.

DTOs (Data Transfer Objects): JSON responses are parsed into DTOs (e.g., UserDto, PostDto). These are never exposed to the UI; they are immediately mapped to Domain Models or Database Entities.

Error Handling: Network exceptions are caught within the RemoteMediator or Repository, preventing app crashes and allowing the UI to fallback to cached data.

Caching Approach (Single Source of Truth) 💾

We use Room Database as the absolute source of truth.

The Pattern: The UI observes the Database -> The Repository fetches from API -> Saves to Database -> Database emits new data to UI.

Advantages:

Consistent data state.

Offline support is "free" (UI doesn't care where data comes from).

No "flicker" when navigating back to screens (data is already there).

3. Offline Mode Behavior 📴

The app implements a strict "Offline-Only" mode toggleable via Settings.

Implementation Details:

UserPreferencesRepository: Stores a boolean flag isOfflineMode in DataStore.

Logic Gate in RemoteMediator:
Inside UserRemoteMediator.load(), the very first check is:

if (preferences.offlineModeFlow.first()) {
    return MediatorResult.Success(endOfPaginationReached = true)
}


This effectively "short-circuits" the network call. Paging 3 interprets this as "No more network data," so it relies entirely on what is already in the Room DB.

4. Background Refresh & Battery Optimization 🔋

A senior-level requirement was to avoid battery drain while keeping data fresh.

Strategy

Reactive Triggers: We don't use simple Timer or WorkManager blindly. We use reactive streams (Flow).

The UserProfileViewModel combines ConnectivityObserver + OfflineMode + UserRequest.

A refresh is triggered only when: (IsOnline && !IsOfflineMode).

Lifecycle Awareness:

stateIn is used with SharingStarted.WhileSubscribed(5000).

Benefit: If the user backgrounds the app or locks the screen, the UI collectors stop. This causes the upstream flows (including network monitoring and refresh timers) to cancel automatically after 5 seconds.

Result: Zero background battery usage when the app is not in use.

5. Performance Improvements 🚀

List Rendering

Stable Keys: LazyColumn(key = { user.id }) allows Compose to avoid recomposing the entire list when a single item changes or when scrolling.

Paging 3: Loads data in small chunks (pages of 20), preventing large memory spikes.

Visual Performance

Shimmer Loading: Implemented a custom Modifier.shimmerEffect() using infiniteTransition. This is lighter than Lottie animations and provides better UX than static placeholders.

Image Optimization: Coil is configured with crossfade and memory caching to ensure smooth scrolling.

6. Network Retry & Debounce Logic 🔄

Search Debounce

To prevent API spamming when typing:

searchQueryFlow
    .debounce(300) // Wait for user to stop typing for 300ms
    .distinctUntilChanged() // Ignore duplicate queries
    .flatMapLatest { query -> repository.getUsers(query) } // Cancel old search, start new


Exponential Backoff

Paging 3's RemoteMediator handles retries for pagination automatically. For manual refreshes (like in the Profile screen), the Repository catches exceptions silently to preserve the user experience, while the UI (via LaunchedEffect) can show a Toast if the specific LoadState becomes Error.
