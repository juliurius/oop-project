# ⚽ TCS Ball

A Soccer Stars–inspired turn-based football game built in **Java 21 + JavaFX**, developed as an object-oriented programming course project.

Two players take turns flicking their pawns to knock the ball into the opponent's goal — locally on one machine, or against another player over the **local network**. The physics engine handles collisions, friction, ball spin, and wall bouncing — all written from scratch.

---

## 🎮 Features

- **Turn-based gameplay** — players take turns shooting; you can only shoot once everything has come to rest
- **Slingshot-style aiming** — drag from a pawn to set direction and power, release to shoot
- **Physics from scratch** — circular-body collisions, friction, restitution, ball spin (Magnus-like effect)
- **LAN multiplayer** — host-authoritative matches over TCP, with automatic host discovery on the local network (no IP typing needed)
- **Lobby flow** — create a lobby as host or browse and join discovered hosts; both players ready up before the host starts the match
- **Player customization** — set your name, pick a flag, and choose a starting formation
- **5-pawn formations** — goalkeeper + defenders + forwards, selectable from a small catalog
- **State-driven UI** — menu, customization, lobbies, gameplay, and goal celebration are separate screens
- **Goal celebration** — animated *GOL!!* overlay with falling confetti, dismissed by clicking
- **JSON gameplay config** — board size, physics values, pawn/ball parameters, and network ports live in `game-config.json`
- **Dirty-rectangle rendering** — only the layers that actually changed are redrawn each frame

---

## 🛠 Tech Stack

| | |
|---|---|
| **Language** | Java 21 |
| **UI / rendering** | JavaFX 21 (layered Canvas 2D) |
| **Networking** | Plain Java sockets — TCP for the match, UDP broadcast for LAN discovery |
| **Config parsing** | Gson |
| **Build** | Maven |
| **Architecture** | Model–View–Controller with a finite state machine |

No external game engine, no physics library, no networking framework — everything is hand-rolled for educational purposes.

---

## 📦 Project Structure

```
src/main/java/pl/edu/tcs/tcsball/
├── Main.java                  Entry point
├── GameApp.java               JavaFX app: layers, scene, AnimationTimer (game loop)
├── GameConfig.java            Loads gameplay/network constants from game-config.json
│
├── model/                     ── DATA + RULES (knows nothing about the window)
│   ├── Vector2D.java          Immutable 2D value object (add, multiply, dot, rotate…)
│   ├── PhysicsBody.java       Base class: position, velocity, mass, restitution
│   ├── Pawn.java              Team-tagged physics body
│   ├── Ball.java              Physics body with spin
│   ├── Match.java             Aggregate state: pawns, ball, score, turn, profiles
│   ├── PhysicsEngine.java     Coordinates each physics update
│   ├── FrameDelta.java        Result of one physics step (what moved / settled)
│   ├── DomainEvent.java       Score / turn / reset events raised by the model
│   ├── ReadOnlyPawn, ReadOnlyBall   Read-only projections handed to the view
│   ├── physics/
│   │   ├── MotionUpdater.java       Movement, friction, spin
│   │   ├── CollisionResolver.java   Walls and body collisions
│   │   └── GoalDetector.java        Goal-mouth detection
│   ├── formation/             Formation catalog and team builder
│   ├── lobby/                 Lobby domain objects and ready-state rules
│   └── player/                Player profile, flags, side (LEFT/RIGHT)
│
├── controller/                ── DECISIONS + FLOW (knows the model)
│   ├── GameManager.java       Facade + game core: FSM, game loop, aiming, match sync
│   ├── GameState.java         Application/screen state enum (the FSM)
│   ├── AimController.java     Aiming geometry (selected pawn, slingshot tension)
│   ├── GameStateCodec.java    (De)serializes match state to/from the network
│   ├── InputDelta.java        Accumulates "what changed" for the redraw planner
│   ├── GameView / LobbyView / CustomizationView   Read-only interfaces for the view
│   ├── lobby/
│   │   ├── LobbyFlowController.java   Lobby navigation + "pending join" state machine
│   │   ├── LobbyManager.java          Lobby networking glue (sockets, message draining)
│   │   └── LobbyPresenter.java        Read-only projection of lobby state for the view
│   └── customization/
│       ├── CustomizationController.java   Profile editing (name / flag / formation)
│       └── CustomizationManager.java      Holds the current profile + available options
│
├── net/                       ── NETWORKING (LAN multiplayer)
│   ├── connection/            TCP: GameHostServer, GameClient, NetworkConnection
│   ├── discovery/             UDP: LanHostAnnouncer, LanHostScanner, DiscoveredHost
│   └── protocol/              Wire protocol: MessageType, NetworkMessage, NetworkProtocol
│
└── view/                      ── RENDERING (read-only access to the controller)
    ├── Renderer.java          Picks the right screen based on GameState
    ├── RenderLayers.java      Stacked canvases (background / game / ui / overlay)
    ├── RedrawPlanner.java     Dirty-rectangle planner: decides which layers to redraw
    ├── RenderPlan.java        Per-frame "what to redraw" plan
    ├── ConfettiSystem.java    Particle system for goal celebration
    ├── input/InputHandler.java   Translates mouse/keyboard events into controller calls
    ├── element/               Reusable drawing primitives (pitch, ball, pawn, button…)
    └── screen/                One class per GameState (menu, lobbies, game, goal…)
```

Gameplay and network constants live in:

```
src/main/resources/game-config.json
```

This includes window and pitch dimensions, goal size, pawn and ball physics, stop-speed
thresholds, collision tuning, spin parameters, formation offsets, and network ports/timeouts.
Shared UI colours and the font family live in `view/UiTheme.java`; only one-off,
screen-specific decorative colours and layout offsets remain local to the renderers.

---

## 🧠 Architecture

### MVC

| Layer | Responsibility | Knows about |
|---|---|---|
| **Model** | Domain data + physics rules | Itself only |
| **Controller** | State transitions, input handling, networking | Model |
| **View** | Drawing on the canvas | Controller read-only interfaces |

The **view never mutates** the model. Reading happens through read-only interfaces —
`GameView`, `LobbyView`, `CustomizationView` (`LobbyView`/`CustomizationView` extend
`GameView`) — all implemented by `GameManager`.

### Controller: facade + delegation

`GameManager` is the single facade the view and input layer talk to, but it is **not** a
monolith. It keeps the game core (FSM, game loop, aiming, host-authoritative match sync)
and delegates cohesive responsibilities to focused collaborators:

- `CustomizationController` — editing the player profile
- `LobbyFlowController` — lobby navigation and the join handshake
- `LobbyPresenter` — read-only projection of lobby/profile data
- `LobbyManager` — the actual lobby socket plumbing

Sub-controllers don't depend on `GameManager` as a type; they call back **up** only through
functional hooks (`Consumer<GameState> transitionTo`, `Runnable beginMatch`,
`Supplier<…>`), so there are no cyclic class dependencies.

### Finite State Machine

Game flow is driven by a single controller-owned `GameState` enum stored privately in
`GameManager`. State is changed only through named command methods — `startLocalGame()`,
`scoreGoal(team)`, `dismissGoal()`, `openCustomization()`, `leaveLobby()` — each of which
bundles its side effects with the transition, rather than exposing a public state setter.

```
                           ┌──────┐
            ┌──────────────│ MENU │──────────────┐
            │              └──┬───┘              │
 openCustomization()  openHostLobby()      openJoinLobby()
            │              │   │                  │
            ▼              │   │                  ▼
    ┌───────────────┐      │   │           ┌─────────────┐
    │ CUSTOMIZATION │      │   │           │  JOIN_LOBBY │
    └───────────────┘      │   │           └──────┬──────┘
                           │   │       host accepts│ (confirmJoinSuccess)
       startLocalGame()    │   ▼                   ▼
            ┌──────────────┘ ┌────────────┐  ┌──────────────┐
            │                │ HOST_LOBBY │  │ CLIENT_LOBBY │
            │                └─────┬──────┘  └──────┬───────┘
            │   startMultiplayer-  │   START_GAME   │
            │   FromLobby()        ▼                ▼
            │                ┌───────────────────────────┐
            └───────────────►│          PLAYING          │
                             └─────────────┬─────────────┘
                                scoreGoal() │ ▲ dismissGoal()
                                            ▼ │
                                     ┌─────────────┐
                                     │ GOAL_SCORED │
                                     └─────────────┘

  (leaveLobby() / quitToMenu() return to MENU from any lobby or in-game state)
```

### Game Loop

A single `AnimationTimer` runs ~60 times per second:

```java
public void handle(long now) {
    double dt = (now - lastUpdate) / 1_000_000_000.0;
    lastUpdate = now;

    FrameDelta delta = gameManager.update(dt);                 // 1. advance the model
    RenderPlan plan = redrawPlanner.plan(                       // 2. decide what changed
            gameManager, delta,
            gameManager.consumeEvents(),
            gameManager.consumeInputDelta());

    if (!plan.isSkip()) {
        renderer.render(gameManager, plan);                    // 3. redraw only dirty layers
    }
    redrawPlanner.remember(gameManager, delta);
}
```

`Renderer` watches for state transitions and fires `onEnter` / `onExit` lifecycle hooks on
the active `Screen`. This is how the goal overlay starts its animation and spawns confetti
exactly when entering `GOAL_SCORED`, and stops them when leaving.

### Networking

LAN multiplayer is **host-authoritative**: the host runs the physics and is the single
source of truth.

- **Discovery (UDP)** — the host periodically broadcasts its presence; clients scan the
  LAN and list discovered hosts, so there is no manual IP entry.
- **Match (TCP)** — once joined, host and client exchange `NetworkMessage`s over a TCP
  connection. The protocol is a small line-based format with a `MessageType`
  (`JOIN_REQUEST`, `LOBBY_STATE`, `PLAYER_READY`, `START_GAME`, `SHOT`, `GAME_STATE`,
  `GOAL_DISMISSED`, `QUIT`, …).
- The client sends only its **shots**; the host simulates and broadcasts authoritative
  `GAME_STATE` snapshots (encoded by `GameStateCodec`).
- Incoming socket data is read on a background thread and handed to the game thread through
  a concurrent queue, so the model is only ever mutated on the JavaFX thread.

### Physics Highlights

- **Frame-rate independent friction** — `Math.pow(FRICTION, dt * REFERENCE_FPS)` keeps deceleration identical at 60 Hz and 144 Hz
- **Normal-only collision response** — only the velocity component along the collision normal is exchanged; tangential motion is preserved, so glancing hits look natural
- **Iterative pawn separation** — pawn-pawn collision resolution runs several short passes per frame, reducing visible overlap between nearby pawns
- **Ball spin** — off-centre pawn hits impart spin to the ball, which then bends its trajectory mid-flight via per-frame velocity rotation
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

### Playing over LAN

Run the game on two machines on the same local network. One player hosts a lobby; the other
opens the join screen, picks the discovered host, and both ready up before the host starts.

---

## 🎯 Controls

| Action | How |
|---|---|
| Navigate menus / lobbies | Click the on-screen buttons |
| Set player name (customization) | Type on the keyboard |
| Cycle flag / formation (customization) | Click the arrows |
| Select pawn | Click a pawn of the team whose turn it is |
| Aim | Drag away from the pawn (slingshot style) |
| Shoot | Release the mouse button |
| Dismiss goal celebration | Click anywhere |
| Return to menu | Click the **MENU** button on the scoreboard |

Shooting is locked until all pawns and the ball have come to rest. In a network match you can
only aim and shoot on your own turn.

---

## 📚 What This Project Demonstrates

For an OOP course, this codebase showcases:

- **Inheritance** — `PhysicsBody` → `Pawn` / `Ball`
- **Polymorphism** — `Screen` interface implemented by each game-state screen, dispatched via `Map<GameState, Screen>`
- **Composition over inheritance** — `GoalScreen` reuses `GameScreen` as a field rather than extending it; `GameManager` delegates to sub-controllers
- **Immutable value objects** — `Vector2D` and the `record`-based profile/flag/event types
- **Interface segregation** — read-only controller interfaces expose only the data each renderer needs
- **Encapsulation** — private state with named command methods (`scoreGoal`, `dismissGoal`…) rather than public setters
- **Single responsibility** — focused controllers and one-thing-per-class renderers
- **Concurrency** — background socket I/O decoupled from the render thread via a concurrent queue

---

## 📄 License

Educational project — TCS, Programowanie Obiektowe.
