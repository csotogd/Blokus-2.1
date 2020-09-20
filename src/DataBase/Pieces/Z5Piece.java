package DataBase.Pieces;

import DataBase.Piece;

import java.util.ArrayList;
import java.util.List;

/**
 *          ##
 *           #
 *           ##
 */
public class Z5Piece extends Piece {

    private static List<int[][]> permutations=null;

    public Z5Piece() {
        super("Z5", new int[][]{{1,1,0},{0,1,0},{0,1,1}}, true, 2, 4);
    }

    @Override
    public Piece getPiece() {
        return new Z5Piece();
    }

    /**
     * methods that returns a list of arrays that contains possible permutations
     * @return list of arrays that contains possible permutations
     */

    @Override
    public List<int[][]> getPermutations() {
        if(Z5Piece.permutations==null){
            Z5Piece.permutations=new ArrayList<>();
            int count=0;

            for (int i = 0; i < nbRotation; i++) {
                Z5Piece.permutations.add(getShape());
                rotateRight();
                count++;
            }
            if(count<totalConfig){
                rotateUpsideDown();
                for (int i = 0; i < nbRotation; i++) {
                    Z5Piece.permutations.add(getShape());
                    rotateRight();
                    count++;
                }
                rotateUpsideDown();
            }
            assert(count==totalConfig);

        }
        return Z5Piece.permutations;
    }
}
