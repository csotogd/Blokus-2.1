package DataBase.Pieces;

import DataBase.Piece;

import java.util.ArrayList;
import java.util.List;

/**
 *          ###
 *           #
 *          NB: mirror can be possible but not at every rotation
 */
public class T4Piece extends Piece {

    private static List<int[][]> permutations=null;
    public T4Piece() {
        super("T4", new int[][]{{1,1,1},{0,1,0}}, true, 4, 4);
    }

    @Override
    public Piece getPiece() {
        return new T4Piece();
    }

    /**
     * methods that returns a list of arrays that contains possible permutations
     * @return list of arrays that contains possible permutations
     */

    @Override
    public List<int[][]> getPermutations() {
        if(T4Piece.permutations==null){
            T4Piece.permutations=new ArrayList<>();
            int count=0;

            for (int i = 0; i < nbRotation; i++) {
                T4Piece.permutations.add(getShape());
                rotateRight();
                count++;
            }
            if(count<totalConfig){
                rotateUpsideDown();
                for (int i = 0; i < nbRotation; i++) {
                    T4Piece.permutations.add(getShape());
                    rotateRight();
                    count++;
                }
                rotateUpsideDown();
            }
            assert(count==totalConfig);

        }
        return T4Piece.permutations;
    }

}
