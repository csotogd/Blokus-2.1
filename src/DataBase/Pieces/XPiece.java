package DataBase.Pieces;

import DataBase.Piece;

import java.util.ArrayList;
import java.util.List;

public class XPiece extends Piece {

    private static List<int[][]> permutations=null;
    public XPiece() {
        super("X", new int[][]{{0,1,0},{1,1,1},{0,1,0}}, false, 1, 1);
    }

    @Override
    public Piece getPiece() {
        return new XPiece();
    }

    /**
     * methods that returns a list of arrays that contains possible permutations
     * @return list of arrays that contains possible permutations
     */

    @Override
    public List<int[][]> getPermutations() {
        if(XPiece.permutations==null){
            XPiece.permutations=new ArrayList<>();
            int count=0;

            for (int i = 0; i < nbRotation; i++) {
                XPiece.permutations.add(getShape());
                rotateRight();
                count++;
            }
            if(count<totalConfig){
                rotateUpsideDown();
                for (int i = 0; i < nbRotation; i++) {
                    XPiece.permutations.add(getShape());
                    rotateRight();
                    count++;
                }
                rotateUpsideDown();
            }
            assert(count==totalConfig);

        }
        return XPiece.permutations;
    }
}
