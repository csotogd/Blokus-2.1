package DataBase.Pieces;

import DataBase.Piece;

import java.util.ArrayList;
import java.util.List;

/**
 *          #
 *          #
 *          ###
 */
public class V5Piece extends Piece {

    private static List<int[][]> permutations=null;

    public V5Piece() {
        super("V5", new int[][]{{1,0,0},{1,0,0},{1,1,1}}, true,4, 4);
    }

    @Override
    public Piece getPiece() {
        return new V5Piece();
    }

    /**
     * methods that returns a list of arrays that contains possible permutations
     * @return list of arrays that contains possible permutations
     */

    @Override
    public List<int[][]> getPermutations() {
        if(V5Piece.permutations==null){
            V5Piece.permutations=new ArrayList<>();
            int count=0;

            for (int i = 0; i < nbRotation; i++) {
                V5Piece.permutations.add(getShape());
                rotateRight();
                count++;
            }
            if(count<totalConfig){
                rotateUpsideDown();
                for (int i = 0; i < nbRotation; i++) {
                    V5Piece.permutations.add(getShape());
                    rotateRight();
                    count++;
                }
                rotateUpsideDown();
            }
            assert(count==totalConfig);

        }
        return V5Piece.permutations;
    }
}
