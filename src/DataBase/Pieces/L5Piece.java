package DataBase.Pieces;

import DataBase.Piece;

import java.util.ArrayList;
import java.util.List;
/**
 *           #
 *           #
 *           #
 *           ##
 */
public class L5Piece extends Piece {

    private static List<int[][]> permutations=null;


        public L5Piece() {
            super("L5", new int[][]{{1,0},{1,0},{1,0},{1,1}}, true, 4, 8);
        }

    @Override
    public Piece getPiece() {
        return new L5Piece();
    }

    /**
     * methods that returns a list of arrays that contains possible permutations
     * @return list of arrays that contains possible permutations
     */

    @Override
    public List<int[][]> getPermutations() {
        if(L5Piece.permutations==null){
            L5Piece.permutations=new ArrayList<>();
            int count=0;

            for (int i = 0; i < nbRotation; i++) {
                L5Piece.permutations.add(getShape());
                rotateRight();
                count++;
            }
            if(count<totalConfig){
                rotateUpsideDown();
                for (int i = 0; i < nbRotation; i++) {
                    L5Piece.permutations.add(getShape());
                    rotateRight();
                    count++;
                }
                rotateUpsideDown();
            }
            assert(count==totalConfig);

        }
        return L5Piece.permutations;
    }
}
