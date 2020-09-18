package DataBase.Pieces;

import DataBase.Piece;

import java.util.ArrayList;
import java.util.List;

/**
 *          #
 *          ##
 *          #
 *          #
 */
public class YPiece extends Piece {

    private static List<int[][]> permutations=null;
    public YPiece(){
        super("Y", new int[][]{{1,0},{1,1},{1,0},{1,0}}, true, 4, 8);
    }

    @Override
    public Piece getPiece() {
        return new YPiece();
    }
    /**
     * methods that returns a list of arrays that contains possible permutations
     * @return list of arrays that contains possible permutations
     */

    @Override
    public List<int[][]> getPermutations() {
        if(YPiece.permutations==null){
            YPiece.permutations=new ArrayList<>();
            int count=0;

            for (int i = 0; i < nbRotation; i++) {
                YPiece.permutations.add(getShape());
                rotateRight();
                count++;
            }
            if(count<totalConfig){
                rotateUpsideDown();
                for (int i = 0; i < nbRotation; i++) {
                    YPiece.permutations.add(getShape());
                    rotateRight();
                    count++;
                }
                rotateUpsideDown();
            }
            assert(count==totalConfig);

        }
        return YPiece.permutations;
    }
}
