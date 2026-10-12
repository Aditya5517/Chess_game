package com.example.chess_game.model;
public class Position {

    private int row;
    private int column;

    public Position(int row, int column) {

        if (row < 0 || row > 7 ||
                column < 0 || column > 7) {

            throw new IllegalArgumentException(
                    "Invalid position"
            );
        }

        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}