package DataBase.Pieces;

import DataBase.Piece;

import java.util.ArrayList;
import java.util.List;

/**
 *          ##
 *           ##
 */
public class Z4Piece extends Piece {

    private static List<int[][]> permutations=null;

    public Z4Piece() {
        super("Z4", new int[][]{{1,1,0},{0,1,1}}, true, 2, 4);
    }

    @Override
    public Piece getPiece() {
        return new Z4Piece();
    }

    /**
     * methods that returns a list of arrays that contains possible permutations
     * @return list of arrays that contains possible permutations
     */

    @Override
    public List<int[][]> getPermutations() {
        if(Z4Piece.permutations==null){
            Z4Piece.permutations=new ArrayList<>();
            int count=0;

            for (int i = 0; i < nbRotation; i++) {
                Z4Piece.permutations.add(getShape());
                rotateRight();
                count++;
            }
            if(count<totalConfig){
                rotateUpsideDown();
                for (int i = 0; i < nbRotation; i++) {
                    Z4Piece.permutations.add(getShape());
                    rotateRight();
                    count++;
                }
                rotateUpsideDown();
            }
            assert(count==totalConfig);

        }
        return Z4Piece.permutations;
    }
}
