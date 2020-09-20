package DataBase.Pieces;

import DataBase.Piece;

import java.util.ArrayList;
import java.util.List;

/**
 *         ##
 *         #
 */

public class V3Piece extends Piece {

    private static List<int[][]> permutations=null;

    public V3Piece() {
        super("V3", new int[][]{{1,1}, {1,0}}, true, 4, 4);
    }

    @Override
    public Piece getPiece() {
        return new V3Piece();
    }

    /**
     * methods that returns a list of arrays that contains possible permutations
     * @return list of arrays that contains possible permutations
     */

    @Override
    public List<int[][]> getPermutations() {
        if(V3Piece.permutations==null){
            V3Piece.permutations=new ArrayList<>();
            int count=0;

            for (int i = 0; i < nbRotation; i++) {
                V3Piece.permutations.add(getShape());
                rotateRight();
                count++;
            }
            if(count<totalConfig){
                rotateUpsideDown();
                for (int i = 0; i < nbRotation; i++) {
                    V3Piece.permutations.add(getShape());
                    rotateRight();
                    count++;
                }
                rotateUpsideDown();
            }
            assert(count==totalConfig);

        }
        return V3Piece.permutations;
    }
}
