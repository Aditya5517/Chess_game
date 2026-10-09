# Chess Game

A Chess Game application built using **Java and Spring Boot**, following Object-Oriented Programming (OOP) principles.

The project aims to implement a complete chess system with an 8×8 chessboard, chess pieces, move validation, game management, and player matchmaking.

## Tech Stack

- Java 21
- Spring Boot
- Maven
- JUnit 5
- IntelliJ IDEA

## Features

**Currently Implemented:**
- Chess piece types and colors using enums
- Position and Square classes
- Six chess pieces: King, Queen, Rook, Bishop, Knight, and Pawn
- Abstract Piece class using inheritance
- PieceFactory for creating chess pieces

**Planned Features:**
- 8×8 chessboard initialization
- Chess piece movement and move validation
- Special moves: Castling, En Passant, Promotion
- Check, Checkmate, Stalemate, and Draw detection
- Player and game management
- Elo-based player matchmaking
- REST APIs and chess user interface

## Project Structure

```text
src/main/java/com/example/chess_game/
├── board/
│   └── Square.java
├── enums/
│   ├── Color.java
│   ├── GameStatus.java
│   ├── MoveType.java
│   └── PieceType.java
├── model/
│   └── Position.java
├── pieces/
│   ├── Piece.java
│   ├── King.java
│   ├── Queen.java
│   ├── Rook.java
│   ├── Bishop.java
│   ├── Knight.java
│   ├── Pawn.java
│   └── PieceFactory.java
└── ChessGameApplication.java
```

## How to Run

1. Clone the repository.
2. Open the project in IntelliJ IDEA.
3. Make sure Java 21 is installed.
4. Run `ChessGameApplication.java`.

Alternatively, run the following command in the project directory:

```bash
./mvnw spring-boot:run
```

For Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

## OOP Concepts Used

- **Abstraction:** Abstract Piece class
- **Inheritance:** Six chess piece classes extend Piece
- **Encapsulation:** Private fields with getters
- **Polymorphism:** Handling different pieces using the Piece reference
- **Factory Design Pattern:** Creating chess pieces using PieceFactory

## Project Status

Under Development — Core chess models and piece creation are implemented. Game logic and other features will be added gradually.
