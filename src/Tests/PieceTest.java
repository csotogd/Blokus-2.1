package Tests;

import DataBase.*;

import org.junit.Test;
import static org.junit.Assert.*;

public class PieceTest {
    @Test
    public void testGetAllPiece(){
        assertEquals(PieceFactory.get().getAllPieces().size(),21);
    }
    @Test
    public void testPermutations(){
        for(Piece p : PieceFactory.get().getAllPieces()){
            assertEquals(p.getPermutations().size(),p.getTotalConfig());
            //test no two permutations are the same
            for (int i = 0; i < p.getPermutations().size(); i++) {
                for (int j = i+1; j < p.getPermutations().size(); j++) {
                    boolean same = true;
                    if(p.getPermutations().get(i).length!=p.getPermutations().get(j).length ||
                            p.getPermutations().get(i)[0].length!=p.getPermutations().get(j)[0].length) same =false;
                    else
                        for (int y = 0; y < p.getPermutations().get(i).length; y++) {
                            for (int x = 0; x < p.getPermutations().get(i)[0].length; x++) {
                                if(p.getPermutations().get(i)[y][x]!=p.getPermutations().get(j)[y][x]) same=false;
                            }
                        }
                    assertFalse(same);
                }
            }
        }
    }

}