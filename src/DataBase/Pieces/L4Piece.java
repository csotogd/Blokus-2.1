package DataBase.Pieces;

import DataBase.Piece;
import GameBoard.Corner;
import Tools.Vector2d;

import java.util.ArrayList;
import java.util.List;

/**
 *          #
 *          #
 *          ##
 */
public class L4Piece extends Piece {
    private static List<int[][]> permutations=null;
    private static final int[][] pieceRep = new int[][]{{1,0},{1,0},{1,1}};
    private static List<List<Corner>> corners = null;

    public L4Piece() {
        super("L4", new int[][]{{1,0},{1,0},{1,1}}, true, 4, 8);
    }

    public L4Piece(int state) {
        super("L4", true, 4, 8,state);
    }

    @Override
    public Piece getPiece() {
        return new L4Piece();
    }

    /**
     * methods that returns a list of arrays that contains possible permutations
     * @return list of arrays that contains possible permutations
     */

    @Override
    public List<int[][]> getPermutations() {
        if(L4Piece.permutations==null){
            L4Piece.permutations=new ArrayList<>();
            int count=0;

            for (int i = 0; i < nbRotation; i++) {
                L4Piece.permutations.add(getShape());
                rotateRi();
                count++;
            }
            if(count<totalConfig){
                rotateUp();
                for (int i = 0; i < nbRotation; i++) {
                    L4Piece.permutations.add(getShape());
                    rotateRi();
                    count++;
                }
                rotateUp();
            }
            assert(count==totalConfig);

        }
        return L4Piece.permutations;
    }

    @Override
    public Piece clone() {
        return new L4Piece(current_state);
    }

    @Override
    public List<List<Corner>> getCorners() {
        if (L4Piece.corners == null) { //if the corners have not been computed since last rotation, search for them
            L4Piece.corners = new ArrayList<>();
            for(int[][] rep: getPermutations()) {
                ArrayList<Corner> current_corners = new ArrayList<>(); //contains the corners of the piece
                for (int y = 0; y < rep.length; y++) {
                    for (int x = 0; x < rep[0].length; x++) {
                        if (rep[y][x] != 0) {
                            boolean top = true, right = true, down = true, left = true; //is not occupied by a block
                            Vector2d current_position = new Vector2d(x, y);
                            if (y > 0 && rep[y - 1][x] != 0)
                                top = false; //if top outside OR top block occupied
                            if (y < rep.length - 1 && rep[y + 1][x] != 0)
                                down = false; //if down outside OR down block occupied
                            if (x > 0 && rep[y][x - 1] != 0)
                                left = false; //if left outside OR left block occupied
                            if (x < rep[0].length - 1 && rep[y][x + 1] != 0)
                                right = false; //if right outside OR right block occupied
                            Corner current = null;
                            if (top && left) { //now adding corresponding to corner positions
                                current = new Corner(current_position, current_position.add(new Vector2d(-1, -1)), true);
                                current_corners.add(current);
                                current.getRelativeToCornerPositions()[0] = current.getToCornerPositions().get(0);
                            }
                            if (top && right) {
                                if (current == null) {
                                    current = new Corner(current_position, current_position.add(new Vector2d(1, -1)), true);
                                    current_corners.add(current);
                                    current.getRelativeToCornerPositions()[1] = current.getToCornerPositions().get(0);
                                } else {
                                    current.addAdjacent(current_position.add(new Vector2d(1, -1)));
                                    current.getRelativeToCornerPositions()[1] = current.getToCornerPositions().get(current.getToCornerPositions().size() - 1);
                                }
                            }
                            if (down && left) {
                                if (current == null) {
                                    current = new Corner(current_position, current_position.add(new Vector2d(-1, 1)), true);
                                    current.getRelativeToCornerPositions()[3] = current.getToCornerPositions().get(0);
                                    current_corners.add(current);
                                } else {
                                    current.addAdjacent(current_position.add(new Vector2d(-1, 1)));
                                    current.getRelativeToCornerPositions()[3] = current.getToCornerPositions().get(current.getToCornerPositions().size() - 1);
                                }
                            }
                            if (down && right) {
                                if (current == null) {
                                    current = new Corner(current_position, current_position.add(new Vector2d(1, 1)), true);
                                    current.getRelativeToCornerPositions()[2] = current.getToCornerPositions().get(0);
                                    current_corners.add(current);
                                } else {
                                    current.addAdjacent(current_position.add(new Vector2d(1, 1)));
                                    current.getRelativeToCornerPositions()[2] = current.getToCornerPositions().get(current.getToCornerPositions().size() - 1);
                                }
                            }

                        }
                    }

                }
                L4Piece.corners.add(Corner.copyOf(current_corners));

            }
        }

        return L4Piece.corners;
    }
}
