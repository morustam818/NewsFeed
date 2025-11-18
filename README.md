# NewsFeed App

A simple Android news feed app that displays top headlines from news API.

## Architecture

This app uses **Clean Architecture** with **MVVM** pattern. The code is organized into three main layers:

### 1. Domain Layer
- **What it does**: Contains business logic and rules
- **Contains**:
  - Models (pure Kotlin data classes)
  - Use Cases (business operations)
  - Repository interfaces (what we need, not how we get it)

### 2. Data Layer
- **What it does**: Handles all data operations
- **Contains**:
  - Repository implementations
  - API service (network calls)
  - Database (Room for local storage)
  - Mappers (convert between different data formats)

### 3. UI Layer
- **What it does**: Shows data to users
- **Contains**:
  - Screens (Compose UI)
  - ViewModels (manages UI state)
  - Components (reusable UI pieces)

## How It Works

1. **UI** asks ViewModel for data
2. **ViewModel** uses Use Cases to get data
3. **Use Cases** call Repository
4. **Repository** gets data from:
   - Network (API) for fresh data
   - Database (Room) for cached data
5. Data flows back: Repository → Use Case → ViewModel → UI

## Key Guidelines

### State Management
- ViewModels use **StateFlow** to hold UI state
- UI states are defined as **sealed classes** (Initial, Loading, Success, Error)
- UI observes state and updates automatically

### Intent Pattern
- UI sends **Intents** (actions) to ViewModel
- ViewModel processes intents and updates state
- Only one public method: `sendIntent()`

### Data Flow
- **One-way data flow**: UI → Intent → ViewModel → Use Case → Repository
- Data flows down, events flow up

### Dependency Injection
- Uses **Hilt** for dependency injection
- All dependencies are provided through constructors
- Makes testing easier

### Error Handling
- All network operations return `AppNetworkResult`
- Three states: Loading, Success, Failed
- Errors are shown to users with messages

### Caching Strategy
- App uses **Network Bound Resource** pattern
- Shows cached data first (from database)
- Fetches fresh data from network in background
- Updates database with new data

## Testing

- Unit tests use **Kotest** framework
- **MockK** for mocking dependencies
- Tests cover ViewModels, Repositories, and Use Cases

## Tech Stack

- **Kotlin** - Programming language
- **Jetpack Compose** - UI framework
- **ViewModel** - Manages UI state
- **Room** - Local database
- **Retrofit** - Network calls
- **Hilt** - Dependency injection
- **Coroutines & Flow** - Async operations
- **Coil** - Image loading

## Project Structure

```
app/src/main/java/com/webmd/newsfeed/
├── data/           # Data layer
│   ├── local/      # Database (Room)
│   ├── remote/     # API service
│   ├── mapper/     # Data converters
│   └── repository/ # Repository implementations
├── domain/         # Domain layer
│   ├── model/      # Business models
│   ├── repository/ # Repository interfaces
│   └── usecase/    # Business use cases
├── ui/             # UI layer
│   ├── screen/     # Compose screens
│   ├── viewmodel/  # ViewModels
│   └── component/  # UI components
└── di/             # Dependency injection modules
```

## Simple Rules

1. **Domain layer** doesn't know about Android or frameworks
2. **Data layer** implements what domain layer needs
3. **UI layer** only talks to ViewModels, never directly to data
4. **ViewModels** use Use Cases, not Repositories directly
5. **One responsibility** per class
6. **Test everything** that has business logic

