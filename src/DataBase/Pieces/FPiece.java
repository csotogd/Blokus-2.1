package DataBase.Pieces;

import DataBase.Piece;
import GameBoard.Corner;
import Tools.Vector2d;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

/**
 *           ##
 *          ##
 *           #
 */
public class FPiece extends Piece {
    private static final int[][] pieceRep = new int[][]{{0, 1, 1}, {1, 1, 0}, {0, 1, 0}};
    private static List<List<Corner>> corners = null;
    private static List<int[][]> permutations = null;

    public FPiece() {
        super("F", new int[][]{{0, 1, 1}, {1, 1, 0}, {0, 1, 0}}, true, 4, 8);
    }
    public FPiece(int cs) {
        super("F", new int[][]{{0, 1, 1}, {1, 1, 0}, {0, 1, 0}}, true, 4, 8, cs);
    }

    /**
     * methods that returns a list of arrays that contains possible permutations
     *
     * @return list of arrays that contains possible permutations
     */

    @Override
    public List<int[][]> getPermutations() {
        if (FPiece.permutations == null) {
            FPiece.permutations = new ArrayList<>();
            int count = 0;

            for (int i = 0; i < nbRotation; i++) {
                FPiece.permutations.add(getShape());
                rotateRi();
                count++;
            }
            if (count < totalConfig) {
                rotateUp();
                for (int i = 0; i < nbRotation; i++) {
                    FPiece.permutations.add(getShape());
                    rotateRi();
                    count++;
                }
                rotateUp();
            }
            assert (count>totalConfig);
        }
        return FPiece.permutations;
    }

    @Override
    public Piece getPiece() {
        return new FPiece();
    }

    @Override
    public Piece clone() {
        return new FPiece(current_state);
    }

    @Override
    public List<List<Corner>> getCorners() {
        if (FPiece.corners == null) { //if the corners have not been computed since last rotation, search for them
            FPiece.corners = new ArrayList<>();
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
                FPiece.corners.add(Corner.copyOf(current_corners));

            }
        }

        return FPiece.corners;
    }

    public static void main(String[] args){
        FPiece fp = new FPiece();
        List<int[][]> perm = fp.getPermutations();
        List<List<Corner>> corn = fp.getCorners();

        for (int i = 0; i < perm.size(); i++) {
            for (int j = 0; j < perm.get(i).length; j++) {
                for (int k = 0; k < perm.get(i)[0].length; k++) {
                    System.out.print(perm.get(i)[j][k]);
                }
                System.out.println();
            }
            System.out.println();
            for(Corner c: corn.get(i)) System.out.println(c);
        }

        System.out.println(fp);
    }


}