
package com.example.chess_game.board;
import com.example.chess_game.model.Position;
import com.example.chess_game.enums.Color;
import com.example.chess_game.enums.PieceType;
import com.example.chess_game.pieces.Piece;
import com.example.chess_game.pieces.PieceFactory;

public class Board {

    private Square[][] squares;

    public Board() {
        squares = new Square[8][8];
        initializeSquares();
        initializePieces();
    }
    private void initializeSquares() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                Position position = new Position(row, col);
                squares[row][col] = new Square(position);
            }
        }
    }
    private void initializePieces() {

        PieceType[] backRow = {
                PieceType.ROOK,
                PieceType.KNIGHT,
                PieceType.BISHOP,
                PieceType.QUEEN,
                PieceType.KING,
                PieceType.BISHOP,
                PieceType.KNIGHT,
                PieceType.ROOK
        };

        for (int col = 0; col < 8; col++) {
            placePiece(backRow[col], Color.WHITE, 0, col);
            placePiece(PieceType.PAWN, Color.WHITE, 1, col);

            placePiece(PieceType.PAWN, Color.BLACK, 6, col);
            placePiece(backRow[col], Color.BLACK, 7, col);
        }
    }

    private void placePiece(PieceType type, Color color,
                            int row, int col) {

        Piece piece = PieceFactory.createPiece(type, color);

        squares[row][col].setPiece(piece);
    }

    public Square getSquare(Position position) {
        return squares[position.getRow()][position.getColumn()];
    }

    public boolean isOccupied(Position position) {
        return !getSquare(position).isEmpty();
    }

    public boolean isPathClear(Position from, Position to) {

        int rowDifference = to.getRow() - from.getRow();
        int colDifference = to.getColumn() - from.getColumn();

        boolean straight = rowDifference == 0 || colDifference == 0;
        boolean diagonal = Math.abs(rowDifference)
                == Math.abs(colDifference);

        if (!straight && !diagonal) {
            return false;
        }
        if (rowDifference == 0 && colDifference == 0) {
            return false;
        }
        int rowStep = Integer.compare(rowDifference, 0);
        int colStep = Integer.compare(colDifference, 0);
        int row = from.getRow() + rowStep;
        int col = from.getColumn() + colStep;

        while (row != to.getRow() || col != to.getColumn()) {
            if (!squares[row][col].isEmpty()) {
                return false;
            }
            row += rowStep;
            col += colStep;
        }
        return true;
    }

    public void displayBoard() {
        System.out.println();
        for (int row = 7; row >= 0; row--) {
            System.out.print((row + 1) + "  ");
            for (int col = 0; col < 8; col++) {
                Piece piece = squares[row][col].getPiece();
                if (piece == null) {
                    System.out.print(". ");
                } else {
                    String symbol = switch (piece.getType()) {
                        case KING -> "K";
                        case QUEEN -> "Q";
                        case ROOK -> "R";
                        case BISHOP -> "B";
                        case KNIGHT -> "N";
                        case PAWN -> "P";
                    };
                    if (piece.getColor() == Color.BLACK) {
                        symbol = symbol.toLowerCase();
                    }
                    System.out.print(symbol + " ");
                }
            }
            System.out.println();
        }
        System.out.println("   A B C D E F G H");
    }
}
