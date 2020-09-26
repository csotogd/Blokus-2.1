package DataBase.Pieces;

import DataBase.Piece;

import java.util.ArrayList;
import java.util.List;

/**
 *         ##
 */

public class I2Piece extends Piece {

    private static List<int[][]> permutations=null;
    public I2Piece() {
        super("I2", new int[][]{{1},{1}}, false, 2, 2);
    }

    @Override
    public Piece getPiece() {
        return new I2Piece();
    }

    /**
     * methods that returns a list of arrays that contains possible permutations
     * @return list of arrays that contains possible permutations
     */

    @Override
    public List<int[][]> getPermutations() {
        if(I2Piece.permutations==null){
            I2Piece.permutations=new ArrayList<>();
            int count=0;

            for (int i = 0; i < nbRotation; i++) {
                I2Piece.permutations.add(getShape());
                rotateRight();
                count++;
            }
            if(count<totalConfig){
                rotateUpsideDown();
                for (int i = 0; i < nbRotation; i++) {
                    I2Piece.permutations.add(getShape());
                    rotateRight();
                    count++;
                }
                rotateUpsideDown();
            }
            assert(count==totalConfig);

        }
        return I2Piece.permutations;
    }
}
