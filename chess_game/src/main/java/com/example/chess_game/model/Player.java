package com.example.chess_game.model;

import com.example.chess_game.enums.Color;

public class Player {

    private String name;
    private Color color;
    private int eloRating;

    public Player(String name, Color color, int eloRating) {
        this.name = name;
        this.color = color;
        this.eloRating = eloRating;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public int getEloRating() {
        return eloRating;
    }

    public void setEloRating(int eloRating) {
        this.eloRating = eloRating;
    }

    public Move makeMove(Position from, Position to) {
        return new Move(from, to);
    }
}