package DataBase.Pieces;

import DataBase.Piece;

import java.util.ArrayList;
import java.util.List;

/**
 *          ###
 *           #
 *           #
 */
public class T5Piece extends Piece {

    private static List<int[][]> permutations=null;
    public T5Piece() {
        super("T5", new int[][]{{1,1,1},{0,1,0},{0,1,0}}, true, 4, 4);
    }

    @Override
    public Piece getPiece() {
        return new T5Piece();
    }

    /**
     * methods that returns a list of arrays that contains possible permutations
     * @return list of arrays that contains possible permutations
     */

    @Override
    public List<int[][]> getPermutations() {
        if(T5Piece.permutations==null){
            T5Piece.permutations=new ArrayList<>();
            int count=0;

            for (int i = 0; i < nbRotation; i++) {
                T5Piece.permutations.add(getShape());
                rotateRight();
                count++;
            }
            if(count<totalConfig){
                rotateUpsideDown();
                for (int i = 0; i < nbRotation; i++) {
                    T5Piece.permutations.add(getShape());
                    rotateRight();
                    count++;
                }
                rotateUpsideDown();
            }
            assert(count==totalConfig);

        }
        return T5Piece.permutations;
    }
}
