import DataBase.Piece;
import Player.Player;
import Tools.Vector2d;

import java.util.Vector;

public class Move {
    private Player player;
    private Piece pieceMoved;
    private Vector2d position; //Position of the top-left corner of the piece.



    public Move(Player player, Piece pieceMoved, Vector2d position)
    {
        this.player = player;
        this.pieceMoved= pieceMoved;
        this.position=position;

    }

}
