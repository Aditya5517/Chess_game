package com.example.chess_game.entity;

import com.example.chess_game.enums.MoveType;
import com.example.chess_game.enums.PieceType;
import jakarta.persistence.*;

@Entity
@Table(
        name = "game_moves",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"game_id", "move_number"}
        )
)
public class GameMoveEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private GameEntity game;

    @Column(nullable = false)
    private int moveNumber;

    @Column(nullable = false)
    private String fromSquare;

    @Column(nullable = false)
    private String toSquare;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MoveType moveType;

    @Enumerated(EnumType.STRING)
    private PieceType promotionType;

    public GameMoveEntity() {
    }

    public GameMoveEntity(
            GameEntity game,
            int moveNumber,
            String fromSquare,
            String toSquare,
            MoveType moveType,
            PieceType promotionType
    ) {
        this.game = game;
        this.moveNumber = moveNumber;
        this.fromSquare = fromSquare;
        this.toSquare = toSquare;
        this.moveType = moveType;
        this.promotionType = promotionType;
    }

    public Long getId() {
        return id;
    }

    public GameEntity getGame() {
        return game;
    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public String getFromSquare() {
        return fromSquare;
    }

    public String getToSquare() {
        return toSquare;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public PieceType getPromotionType() {
        return promotionType;
    }
}