package DataBase.Pieces;

import DataBase.Piece;

import java.util.ArrayList;
import java.util.List;

/**
 *          ##
 *          ##
 *          #
 */
public class PPiece extends Piece {

    private static List<int[][]> permutations=null;

    public PPiece() {
        super("P", new int[][]{{1,1},{1,1},{1,0}}, true, 4, 8);
    }

    @Override
    public Piece getPiece() {
        return new PPiece();
    }

    /**
     * methods that returns a list of arrays that contains possible permutations
     * @return list of arrays that contains possible permutations
     */

    @Override
    public List<int[][]> getPermutations() {
        if(PPiece.permutations==null){
            PPiece.permutations=new ArrayList<>();
            int count=0;

            for (int i = 0; i < nbRotation; i++) {
                PPiece.permutations.add(getShape());
                rotateRight();
                count++;
            }
            if(count<totalConfig){
                rotateUpsideDown();
                for (int i = 0; i < nbRotation; i++) {
                    PPiece.permutations.add(getShape());
                    rotateRight();
                    count++;
                }
                rotateUpsideDown();
            }
            assert(count==totalConfig);

        }
        return PPiece.permutations;
    }
}
