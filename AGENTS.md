# Repository Guidelines

## Project Structure & Module Organization

This is a Java 11 libGDX project split into two Gradle modules:

- `core/src/main/java/io/github/TorenDropProject/` contains shared game logic. Major packages cover screens and modals, Ashley entities and systems, asset loaders, and pregame loading.
- `lwjgl3/src/main/java/io/github/TorenDropProject/lwjgl3/` contains the desktop launcher and startup helpers.
- `assets/` is the runtime asset root for textures, cursors, and Tiled `.tmx`/`.tsx` maps. Refer to these resources with paths relative to `assets/`, such as `cursors/arrowCursor3.png`.
- `lwjgl3/icons/` and `lwjgl3/src/main/resources/` hold launcher and window icons.
- Root Gradle files define shared Java settings and dependency versions. Generated `build/` directories and `assets/assets.txt` are not source files.

## Build, Test, and Development Commands

Use the checked-in Gradle wrapper from the repository root (use `gradlew.bat` on Windows):

- `./gradlew lwjgl3:run` launches the desktop game with `assets/` as its working directory.
- `./gradlew build` compiles all modules and creates their archives.
- `./gradlew test` runs all configured unit tests; currently, the repository has no test sources.
- `./gradlew lwjgl3:jar` creates the runnable JAR under `lwjgl3/build/libs/`.
- `./gradlew clean` removes generated build output.

The first Gradle invocation may download dependencies from configured Maven repositories.

## Coding Style & Naming Conventions

Follow `.editorconfig`: UTF-8, LF endings, spaces, final newlines, four-space indentation for Java, and two spaces for Gradle files. Target Java 11. Use `UpperCamelCase` for classes, `lowerCamelCase` for methods and fields, and descriptive package names. Keep platform-independent behavior in `core`; restrict LWJGL-specific code to `lwjgl3`. Match surrounding brace and import style, and dispose libGDX resources that your code owns. No formatter or linter is enforced, so review formatting before committing.

## Testing Guidelines

There is not yet a testing framework or coverage threshold configured. For new testable logic, add the test dependency to the relevant module and place tests under `core/src/test/java/` or `lwjgl3/src/test/java/`, mirroring the production package. Name test classes `*Test` and methods after the behavior being verified. Run `./gradlew test` and manually smoke-test visual, input, and asset changes with `./gradlew lwjgl3:run`.

## Commit & Pull Request Guidelines

Recent history uses short subjects (for example, `Cor-1-maps` and `main screen added`) but has no consistent convention. Prefer an imperative, specific subject, optionally prefixed with an issue key: `COR-12 Add collision bounds to mountain pass`.

Keep commits focused and avoid committing IDE files or build output. Pull requests should explain the player-visible and technical changes, link the relevant issue, list verification commands, and include screenshots or a short recording for UI, rendering, or map changes. Call out added assets, dependency changes, and known follow-up work.

## Assets & Configuration

Preserve asset filename case because lookups can be case-sensitive across platforms. Update code and Tiled references together when moving assets. Do not commit machine-local configuration such as `local.properties`, credentials, or generated asset indexes.

## Architecture Overview

The project is a modular desktop game with a screen-oriented core and an Entity Component System (ECS) for gameplay. `Lwjgl3Launcher` is the platform entry point; keep it limited to window and JVM setup. `Main` is the composition root: it owns shared libGDX services, queues startup assets, constructs the Ashley `Engine`, registers systems, creates screens, and delegates the frame loop to `ScreenManager`.

`ScreenManager` stores reusable `GameScreen` implementations and controls `show`, `render`, `resize`, `hide`, and `dispose` calls. Screens coordinate a feature: `MainMenuScreen` renders the menu, while `BattleScreen` combines the Tiled map renderer, camera, ECS update, and modal overlay. Scene2D UI belongs in the matching `screens/GUIs/` class, and UI actions should request navigation through the injected `ScreenManager`. Modals use a screen-space viewport and render after the world and GUI so they remain centered above camera-driven content.

Gameplay uses Ashley ECS. `PlayerEntityFactory` creates entities and attaches data-only position, velocity, and texture components. Systems should each retain one responsibility: `InputSystem` writes intent/velocity, `MovementSystem` updates position, and `RenderSystem` draws from component state. Preserve their explicit priorities (input 0, movement 1, rendering 2) because the engine updates systems sequentially each frame. Each system's `Family` must require every component it reads.

Preserve dependency direction: launcher → `Main` → screens/services → entities and systems. Prefer constructor injection already used by screens and factories; do not introduce global access when a dependency can be passed explicitly. Startup assets, including Tiled maps, are queued through `SplashScreenAssetLoader`; never block the render thread waiting for an asset. `ScreenManager` hides reusable screens during navigation and disposes every registered screen once at application shutdown. Shared resources loaded by the central `AssetManager` are disposed by `Main`; screens dispose only resources they create, such as stages, skins, fonts, renderers, or batches.
