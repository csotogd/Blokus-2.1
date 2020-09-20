package DataBase.Pieces;

import DataBase.Piece;

import java.util.ArrayList;
import java.util.List;

/**
 *          ##
 *          ##
 */
public class O4Piece extends Piece {

    private static List<int[][]> permutations=null;

    public O4Piece() {
        super("O4", new int[][]{{1,1},{1,1}}, false, 1, 1);
    }

    @Override
    public Piece getPiece() {
        return new O4Piece();
    }

    /**
     * methods that returns a list of arrays that contains possible permutations
     * @return list of arrays that contains possible permutations
     */

    @Override
    public List<int[][]> getPermutations() {
        if(O4Piece.permutations==null){
            O4Piece.permutations=new ArrayList<>();
            int count=0;

            for (int i = 0; i < nbRotation; i++) {
                O4Piece.permutations.add(getShape());
                rotateRight();
                count++;
            }
            if(count<totalConfig){
                rotateUpsideDown();
                for (int i = 0; i < nbRotation; i++) {
                    O4Piece.permutations.add(getShape());
                    rotateRight();
                    count++;
                }
                rotateUpsideDown();
            }
            assert(count==totalConfig);

        }
        return O4Piece.permutations;
    }
}
