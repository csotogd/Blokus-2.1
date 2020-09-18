package DataBase.Pieces;

import DataBase.Piece;

import java.util.ArrayList;
import java.util.List;

/**
 *          #
 *          ##
 *           ##
 */
public class WPiece extends Piece {

    private static List<int[][]> permutations=null;

    public WPiece() {
        super("W", new int[][]{{1,0,0},{1,1,0},{0,1,1}}, true, 4, 4);
    }

    @Override
    public Piece getPiece() {
        return new WPiece();
    }

    /**
     * methods that returns a list of arrays that contains possible permutations
     * @return list of arrays that contains possible permutations
     */

    @Override
    public List<int[][]> getPermutations() {
        if(WPiece.permutations==null){
            WPiece.permutations=new ArrayList<>();
            int count=0;

            for (int i = 0; i < nbRotation; i++) {
                WPiece.permutations.add(getShape());
                rotateRight();
                count++;
            }
            if(count<totalConfig){
                rotateUpsideDown();
                for (int i = 0; i < nbRotation; i++) {
                    WPiece.permutations.add(getShape());
                    rotateRight();
                    count++;
                }
                rotateUpsideDown();
            }
            assert(count==totalConfig);

        }
        return WPiece.permutations;
    }
}
