# ⚽ TCS Ball

A Soccer Stars–inspired turn-based football game built in **Java 21 + JavaFX**, developed as an object-oriented programming course project.

Two players take turns flicking their pawns to knock the ball into the opponent's goal. The physics engine handles collisions, friction, ball spin, and wall bouncing — all written from scratch.

---

## 🎮 Features

- **Turn-based gameplay** — players take turns shooting; you can only shoot once everything has come to rest
- **Slingshot-style aiming** — drag from a pawn to set direction and power, release to shoot
- **Physics from scratch** — circular-body collisions, friction, restitution, ball spin (Magnus-like effect)
- **State-driven UI** — menu, gameplay, settings, and goal celebration are separate screens
- **Goal celebration** — animated *GOL!!* overlay with falling confetti, dismissed by clicking
- **JSON gameplay config** — board size, physics values, pawn/ball parameters, and formation live in `game-config.json`
- **5v5 formation** — goalkeeper + 2 defenders + 2 forwards, loaded from the JSON config

---

## 🛠 Tech Stack

| | |
|---|---|
| **Language** | Java 21 |
| **UI / rendering** | JavaFX 21 (Canvas 2D) |
| **Build** | Maven |
| **Architecture** | Model–View–Controller with a finite state machine |

No external game engine, no physics library — everything is hand-rolled for educational purposes.

---

## 📦 Project Structure

```
src/main/java/pl/edu/tcs/tcsball/
├── Main.java                  Entry point
├── GameApp.java               JavaFX app: canvas, scene, AnimationTimer (game loop)
├── GameConfig.java            Loads gameplay constants from src/main/resources/game-config.json
│
├── model/                     ── DATA + RULES (knows nothing about the window)
│   ├── Vector2D.java          2D math (add, multiply, dot, normalize…)
│   ├── PhysicsBody.java       Base class: position, velocity, mass, restitution
│   ├── Pawn.java              Team-tagged physics body
│   ├── Ball.java              Physics body with spin
│   ├── Formation.java         Builds the starting line-up from GameConfig / JSON
│   ├── Match.java             Aggregate state: pawns, ball, score, turn
│   ├── PhysicsEngine.java     Coordinates each physics update
│   ├── physics/
│   │   ├── MotionUpdater.java       Movement, friction, spin
│   │   ├── CollisionResolver.java   Walls and body collisions
│   │   └── GoalDetector.java        Goal-mouth detection
│   └── lobby/                 Lobby domain objects and ready-state rules
│
├── controller/                ── DECISIONS + FLOW
│   ├── GameManager.java       State machine + game commands (startGame, scoreGoal…)
│   ├── InputHandler.java      Translates mouse events into state-aware actions
│   ├── GameState.java         Application/screen state enum owned by the controller
│   ├── GameView.java          Read-only game data exposed to the view layer
│   ├── LobbyView.java         Read-only lobby data exposed to lobby screens
│   └── CustomizationView.java Read-only customization data
│
└── view/                      ── RENDERING (read-only access to the model)
    ├── Renderer.java          Picks the right screen based on GameState
    ├── ConfettiSystem.java    Particle system for goal celebration
    ├── element/               Reusable drawing primitives
    │   ├── PitchRenderer.java
    │   ├── ScoreBoardRenderer.java
    │   ├── BallRenderer.java
    │   ├── ButtonRenderer.java
    │   ├── PawnRenderer.java
    │   ├── AimingRenderer.java
    │   └── GoalOverlayRenderer.java
    └── screen/                One class per GameState
        ├── Screen.java        Interface with onEnter / onExit / render hooks
        ├── MenuScreen.java
        ├── GameScreen.java
        ├── GoalScreen.java
        └── SettingsScreen.java
```

Gameplay-related constants are stored in:

```
src/main/resources/game-config.json
```

This includes window and pitch dimensions, goal size, pawn and ball physics, stop-speed thresholds, collision tuning, and team formation offsets. Small view-only constants, such as button sizes and colors, remain local to the renderer classes.

---

## 🧠 Architecture

### MVC, strictly enforced

| Layer | Responsibility | Knows about |
|---|---|---|
| **Model** | Domain data + physics rules | Itself only |
| **Controller** | State transitions + input handling | Model |
| **View** | Drawing on Canvas | Controller read-only interfaces |

The **view never mutates** the model. Reading happens through read-only interfaces such as `GameView`, `LobbyView`, and `CustomizationView`, implemented by `GameManager`.

### Finite State Machine

Game flow is driven by a single controller-owned `GameState` enum stored privately in `GameManager`. It describes application/screen state, not the core domain model. All transitions go through one chokepoint:

```java
private void transitionTo(GameState next) {
    gameState = next;
}
```

Every transition is a **named domain event**, not a setter — e.g. `scoreGoal(team)`, `dismissGoal()`, `openSettings()`. Each method bundles its side effects with the state change, so it's impossible to e.g. change state to `GOAL_SCORED` without also updating the score.

```
              ┌──────┐
              │ MENU │ ◄──────────────┐
              └──┬───┘                │
                 │ startGame()        │ quitToMenu()
                 ▼                    │
           ┌─────────────┐            │
       ┌──►│   PLAYING   ├────────────┤
       │   └──────┬──────┘            │
       │          │                   │
dismissGoal()    scoreGoal()          │ closeSettings()
       │          │                   │
       │          ▼                   │
       │   ┌─────────────┐     ┌──────────┐
       └───┤ GOAL_SCORED │     │ SETTINGS │
           └─────────────┘     └────▲─────┘
                                    │
                              openSettings()
```

### Game Loop

A single `AnimationTimer` runs ~60 times per second:

```java
public void handle(long now) {
    double dt = (now - lastUpdate) / 1_000_000_000.0;
    lastUpdate = now;

    gameManager.update(dt);       // 1. advance the model (physics, state transitions)
    renderer.render(gameManager); // 2. draw the current state
}
```

`Renderer` watches for state transitions and fires `onEnter` / `onExit` lifecycle hooks on the active `Screen`. This is how the goal overlay starts its animation and spawns confetti exactly when entering `GOAL_SCORED`, and stops them when leaving.

### Physics Highlights

- **Frame-rate independent friction** — `Math.pow(FRICTION, dt * REFERENCE_FPS)` keeps deceleration identical at 60 Hz and 144 Hz
- **Normal-only collision response** — only the velocity component along the collision normal is exchanged; tangential motion is preserved, so glancing hits look natural
- **Iterative pawn separation** — pawn-pawn collision resolution runs several short passes per frame, reducing visible overlap between nearby pawns
- **Ball spin** — off-center pawn hits impart spin to the ball, which then bends its trajectory mid-flight via per-frame velocity rotation
- **Goal detection** — the ball is checked against goal-mouth bounds before wall collision response, so it passes through the goal line cleanly

---

## ▶ Running the Project

### Prerequisites

- JDK 21 (or newer)
- Maven 3.8+

### Build & run

```bash
mvn clean compile
mvn javafx:run
```

JavaFX is pulled in as a Maven dependency — no separate SDK install needed.

---

## 🎯 Controls

| Action | How |
|---|---|
| Select pawn | Click on a pawn of the team whose turn it is |
| Aim | Drag away from the pawn (slingshot style) |
| Shoot | Release the mouse button |
| Dismiss goal celebration | Click anywhere |
| Return to menu (in-game) | Click the **MENU** button on the top-left of the scoreboard |
| Open settings (from menu) | Click **USTAWIENIA** |

Shooting is locked until all pawns and the ball have come to rest.

---

## 🗺 Roadmap

### Polish (single-player)

- [ ] Shot counter on the scoreboard, frozen during goal celebration
- [ ] In-game settings via Esc (preserves the current match)
- [ ] Turn change deferred until everything stops (cleaner UX, single source of truth)

### Online multiplayer (planned)

- [ ] TCP-based host-authoritative networking (`net/` package)
- [ ] Message protocol: `ShotMessage`, `StateMessage`, `GoalMessage`, `TurnMessage`
- [ ] Lobby + connect-by-IP flow in the menu
- [ ] Graceful disconnect handling

The state machine and read-only view interface were designed up-front to make this addition straightforward — new states like `LOBBY`, `MY_TURN`, `WAITING_FOR_OPPONENT` will slot in without restructuring the rest.

---

## 📚 What This Project Demonstrates

For an OOP course, this codebase showcases:

- **Inheritance** — `PhysicsBody` → `Pawn` / `Ball`
- **Polymorphism** — `Screen` interface implemented by each game-state screen, dispatched via `Map<GameState, Screen>`
- **Composition over inheritance** — `GoalScreen` reuses `GameScreen` as a field rather than extending it
- **Interface segregation** — read-only controller interfaces expose only the data each renderer needs
- **Encapsulation** — private state with named transition methods (`scoreGoal`, `dismissGoal`…) rather than public setters
- **Single responsibility** — each element renderer draws exactly one type of thing
- **Open/closed principle** — adding a new screen means a new `Screen` implementation; `Renderer` doesn't change

---

## 📄 License

Educational project — TCS, Programowanie Obiektowe.
