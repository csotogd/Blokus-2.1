package DataBase.Pieces;

import DataBase.Piece;

import java.util.ArrayList;
import java.util.List;

/**
 *          ###
 */
public class I3Piece extends Piece {

    private static List<int[][]> permutations=null;
    public I3Piece() {
        super("I3", new int[][]{{1},{1},{1}}, false, 2, 2);
    }

    @Override
    public Piece getPiece() {
        return new I3Piece();
    }

    /**
     * methods that returns a list of arrays that contains possible permutations
     * @return list of arrays that contains possible permutations
     */

    @Override
    public List<int[][]> getPermutations() {
        if(I3Piece.permutations==null){
            I3Piece.permutations=new ArrayList<>();
            int count=0;

            for (int i = 0; i < nbRotation; i++) {
                I3Piece.permutations.add(getShape());
                rotateRight();
                count++;
            }
            if(count<totalConfig){
                rotateUpsideDown();
                for (int i = 0; i < nbRotation; i++) {
                    I3Piece.permutations.add(getShape());
                    rotateRight();
                    count++;
                }
                rotateUpsideDown();
            }
            assert(count==totalConfig);

        }
        return I3Piece.permutations;
    }
}
