package Tests;

import org.junit.Test;
import static org.junit.Assert.*;


import Move.Move;
import GameBoard.*;
import DataBase.*;
import DataBase.PieceFactory;
import Player.HumanPlayer;
import Tools.Vector2d;

public class CornerTest {
    @Test
    public void TestFPieceCorners(){
        Board board = new Board(2);
        HumanPlayer jesus = new HumanPlayer(1, "Jesus");
        jesus.setPiecesList(PieceFactory.get().getAllPieces());
        Piece fpiece=null;
        for(Piece p: jesus.getPiecesList()){
            if(p.getLabel().equals("F")) fpiece = p;
        }

        Move move = new Move(jesus,fpiece,new Vector2d(0,0));
        assertEquals(fpiece.getCornersContacts(move.getPosition()).size(),4);

    }
}