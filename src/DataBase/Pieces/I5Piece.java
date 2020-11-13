package DataBase.Pieces;

import DataBase.Piece;
import GameBoard.Corner;
import Tools.Vector2d;

import java.util.ArrayList;
import java.util.List;

/**
 *          #####
 */
public class I5Piece extends Piece {
    private static List<int[][]> permutations=null;
    private static final int[][] pieceRep = new int[][]{{1},{1},{1},{1},{1}};
    private static List<List<Corner>> corners = null;

    public I5Piece() {
        super("I5", new int[][]{{1},{1},{1},{1},{1}}, false, 2, 2);
    }

    public I5Piece(int state) {
        super("I5", false, 2, 2,state);
    }

    @Override
    public Piece getPiece() {
        return new I5Piece();
    }

    /**
     * methods that returns a list of arrays that contains possible permutations
     * @return list of arrays that contains possible permutations
     */

    @Override
    public List<int[][]> getPermutations() {
        if(I5Piece.permutations==null){
            I5Piece. permutations=new ArrayList<>();
            int count=0;

            for (int i = 0; i < nbRotation; i++) {
                I5Piece.permutations.add(getShape());
                rotateRi();
                count++;
            }
            if(count<totalConfig){
                rotateUp();
                for (int i = 0; i < nbRotation; i++) {
                    I5Piece.permutations.add(getShape());
                    rotateRi();
                    count++;
                }
                rotateUp();
            }
            assert(count==totalConfig);

        }
        return I5Piece.permutations;
    }

    @Override
    public Piece clone() {
        return new I5Piece(current_state);
    }

    @Override
    public List<List<Corner>> getCorners() {
        if (I5Piece.corners == null) { //if the corners have not been computed since last rotation, search for them
            I5Piece.corners = new ArrayList<>();
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
                I5Piece.corners.add(Corner.copyOf(current_corners));

            }
        }

        return I5Piece.corners;
    }
}
