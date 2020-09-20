package DataBase.Pieces;

import DataBase.Piece;

import java.util.ArrayList;
import java.util.List;

/**
 *          ###
 */
public class I4Piece extends Piece {
    private static List<int[][]> permutations=null;
    public I4Piece() {
        super("I4", new int[][]{{1,1,1,1}}, false, 2, 2);
    }

    @Override
    public Piece getPiece() {
        return new I4Piece();
    }

    /**
     * methods that returns a list of arrays that contains possible permutations
     * @return list of arrays that contains possible permutations
     */

    @Override
    public List<int[][]> getPermutations() {
        if(I4Piece.permutations==null){
            I4Piece.permutations=new ArrayList<>();
            int count=0;

            for (int i = 0; i < nbRotation; i++) {
                I4Piece.permutations.add(getShape());
                rotateRight();
                count++;
            }
            if(count<totalConfig){
                rotateUpsideDown();
                for (int i = 0; i < nbRotation; i++) {
                    I4Piece.permutations.add(getShape());
                    rotateRight();
                    count++;
                }
                rotateUpsideDown();
            }
            assert(count==totalConfig);

        }
        return I4Piece.permutations;
    }
}
