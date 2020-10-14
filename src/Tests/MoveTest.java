package Tests;

import DataBase.Piece;
import DataBase.PieceFactory;
import GameBoard.Board;
import Move.Move;
import Player.HumanPlayer;
import Player.Player;
import Tools.Vector2d;
import org.junit.Assert;
import org.junit.Test;

public class MoveTest {
    HumanPlayer player1, player2;
    Board board;

    @Test
    public void testNormal(){
        make2PlayerGame();
        Piece piece = getPiece("V5", player1);
        Move move = new Move(player1, piece, new Vector2d(0, 0));
        move.makeMove(board);
        Piece piece2 = getPiece("U", player1);
        Move move2 = new Move(player1, piece2, new Vector2d(3, 3));
        Assert.assertTrue(move2.isAllowed(board));
        move2.makeMove(board);
    }

    @Test
    public void testFirstMove(){
        make2PlayerGame();
        Piece piece = getPiece("I5", player1);
        Move move = new Move(player1, piece, new Vector2d(0, 0));
        Assert.assertTrue(move.isAllowed(board));
    }

    @Test
    public void testNotTouchingCorner(){
        make2PlayerGame();
        Piece p1 = getPiece("I1", player1);
        Move m1 = new Move(player1, p1, new Vector2d(5, 3));
        Assert.assertFalse(m1.isAllowed(board));
    }

    @Test
    public void testOutsideBoundaries(){
        make2PlayerGame();
        Piece p1 = getPiece("V5", player1);
        Move m1 = new Move(player1, p1, new Vector2d(0, 0));
        m1.makeMove(board);
        Piece p2 = getPiece("I5", player1);
        Move m2 = new Move(player1, p2, new Vector2d(-3, 4));
        Assert.assertFalse(m2.isAllowed(board));
    }

    @Test
    public void testOverlapOwnPiece(){
        make2PlayerGame();
        Piece p1 = getPiece("V5", player1);
        Move m1 = new Move(player1, p1, new Vector2d(0, 0));
        m1.makeMove(board);
        Piece p2 = getPiece("L5", player1);
        p2.rotateUpsideDown();
        p2.rotateRight();
        Move m2 = new Move(player1, p2, new Vector2d(0, 0));
        Assert.assertFalse(m2.isAllowed(board));
    }

    @Test
    public void testOverlapOtherPiece(){
        make2PlayerGame();
        goToMiddle();

        Piece p1 = getPiece("W", player1);
        Move m1 = new Move(player1, p1, new Vector2d(8, 9));
        m1.makeMove(board);
        Piece p2 = getPiece("I3", player2);
        Move m2 = new Move(player2, p2, new Vector2d(9, 10));
        Assert.assertFalse(m2.isAllowed(board));
    }

    @Test
    public void testTouchSideOfSelf(){
        make2PlayerGame();
        Piece p1 = getPiece("V5", player1);
        Move m1 = new Move(player1, p1, new Vector2d(0, 0));
        m1.makeMove(board);
        Piece p2 = getPiece("L4", player1);
        Move m2 = new Move(player1, p2, new Vector2d(1, 0));
        Assert.assertFalse(m2.isAllowed(board));
    }

    @Test
    public void testTouchingSideOfOther(){
        make2PlayerGame();
        goToMiddle();

        Piece p1 = getPiece("L4", player1);
        p1.rotateUpsideDown(); p1.rotateLeft();
        Move m1 = new Move(player1, p1, new Vector2d(8, 9));
        m1.makeMove(board);
        Piece p2 = getPiece("I1", player2);
        Move m2 = new Move(player2, p2, new Vector2d(11, 10));
        Assert.assertTrue(m2.isAllowed(board));
    }

    public void make2PlayerGame(){
        player1 = new HumanPlayer(1);
        player2 = new HumanPlayer(2);
        board = new Board(new Player[]{player1,player2});

        player1.setStartingCorner(new Vector2d(0,0));
        player2.setStartingCorner(new Vector2d(board.getDIMENSION()-1,board.getDIMENSION()-1));
        player1.setPiecesList(PieceFactory.get().getAllPieces());
        player2.setPiecesList(PieceFactory.get().getAllPieces());
    }

    public void goToMiddle(){
        Piece p1 = getPiece("I5", player1);
        Move m1 = new Move(player1, p1, new Vector2d(0, 0));
        m1.makeMove(board);
        Piece p2 = getPiece("I5", player2);
        Move m2 = new Move(player2,  p2, new Vector2d(19, 15));
        m2.makeMove(board);
        Piece p3 = getPiece("I4", player1);
        p3.rotateRight();
        Move m3 = new Move(player1, p3, new Vector2d(1, 5));
        m3.makeMove(board);
        Piece p4 = getPiece("I4", player2);
        p4.rotateRight();
        Move m4 = new Move(player2, p4, new Vector2d(15, 14));
        m4.makeMove(board);
        Piece p5 = getPiece("V5", player1);
        Move m5 = new Move(player1, p5, new Vector2d(5, 6));
        m5.makeMove(board);
        Piece p6 = getPiece("V5", player2);
        Move m6 = new Move(player2, p6, new Vector2d(12, 11));
        m6.makeMove(board);
        //Available corners at (8, 9) and (11, 10)
    }

    public Piece getPiece(String label, Player player){
        for (Piece p : player.getPiecesList()){
            if (p.getLabel().equals(label)){
                return p;
            }
        }
        return null;
    }
}
