package DataBase.Pieces;

import DataBase.Piece;

import java.util.ArrayList;
import java.util.List;

/**
 *          #
 *          #
 *          ##
 *           #
 */
public class NPiece extends Piece {

    private static List<int[][]> permutations=null;

    public NPiece() {
        super("N", new int[][]{{1,0},{1,0},{1,1},{0,1}}, true, 4, 8);
    }

    @Override
    public Piece getPiece() {
        return new NPiece();
    }

    /**
     * methods that returns a list of arrays that contains possible permutations
     * @return list of arrays that contains possible permutations
     */

    @Override
    public List<int[][]> getPermutations() {
        if(NPiece.permutations==null){
            NPiece.permutations=new ArrayList<>();
            int count=0;

            for (int i = 0; i < nbRotation; i++) {
                NPiece.permutations.add(getShape());
                rotateRight();
                count++;
            }
            if(count<totalConfig){
                rotateUpsideDown();
                for (int i = 0; i < nbRotation; i++) {
                    NPiece.permutations.add(getShape());
                    rotateRight();
                    count++;
                }
                rotateUpsideDown();
            }
            assert(count==totalConfig);

        }
        return NPiece.permutations;
    }
}
