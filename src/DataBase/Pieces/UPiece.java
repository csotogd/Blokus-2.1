package DataBase.Pieces;

import DataBase.Piece;

import java.util.ArrayList;
import java.util.List;

public class UPiece extends Piece {

    private static List<int[][]> permutations=null;
    public UPiece() {
        super("U", new int[][]{{1,0,1},{1,1,1}}, true, 4, 4);
    }

    @Override
    public Piece getPiece() {
        return new UPiece();
    }

    /**
     * methods that returns a list of arrays that contains possible permutations
     * @return list of arrays that contains possible permutations
     */

    @Override
    public List<int[][]> getPermutations() {
        if(UPiece.permutations==null){
            UPiece.permutations=new ArrayList<>();
            int count=0;

            for (int i = 0; i < nbRotation; i++) {
                UPiece.permutations.add(getShape());
                rotateRight();
                count++;
            }
            if(count<totalConfig){
                rotateUpsideDown();
                for (int i = 0; i < nbRotation; i++) {
                    UPiece.permutations.add(getShape());
                    rotateRight();
                    count++;
                }
                rotateUpsideDown();
            }
            assert(count==totalConfig);

        }
        return UPiece.permutations;
    }
}
