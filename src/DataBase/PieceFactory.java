package DataBase;

import DataBase.Pieces.*;

import java.util.ArrayList;
import java.util.List;

public class PieceFactory {
    private static PieceFactory singleton=null;
    private ArrayList<Piece> prototype;

    private PieceFactory(){
        prototype =new ArrayList<>();

        prototype.add(new I5Piece());
        prototype.add(new L5Piece());
        prototype.add(new NPiece());
        prototype.add(new FPiece());
        prototype.add(new PPiece());
        prototype.add(new T5Piece());
        prototype.add(new UPiece());
        prototype.add(new V5Piece());
        prototype.add(new WPiece());
        prototype.add(new XPiece());
        prototype.add(new YPiece());
        prototype.add(new Z5Piece());
        prototype.add(new I4Piece());
        prototype.add(new L4Piece());
        prototype.add(new O4Piece());
        prototype.add(new T4Piece());
        prototype.add(new Z4Piece());
        prototype.add(new I3Piece());
        prototype.add(new V3Piece());
        prototype.add(new I2Piece());
        prototype.add(new I1Piece());

        for (Piece piece : prototype){
            piece.calculateNumberOfBlocks();
            piece.getCorners();
        }

    }
    public static PieceFactory get(){
        if(singleton==null) singleton=new PieceFactory();
        return singleton;
    }

    /**
     * The only interesting method for this class
     * @return a new list containing all the existing pieces (new objects as well)
     */
    public ArrayList<Piece> getAllPieces(){
        ArrayList<Piece> result = new ArrayList<>();
        for(Piece p : prototype) result.add(p.getPiece());

        return result;
    }

    /**
     * method used to minimize the nbr of pieces in order to be able to use the minimax searches in 4 players
     * @return a new list containing some of the existing pieces (new objects as well)
     */
    public ArrayList<Piece> getSomePieces(){
        ArrayList<Piece> result = new ArrayList<>();
        for(Piece p : prototype) {
            if(p.getNumberOfBlocks()!=5){
                result.add(p.getPiece());
            }
        }

        return result;
    }
}
