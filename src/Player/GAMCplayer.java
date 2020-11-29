package Player;

import DataBase.Piece;
import GameBoard.Board;
import MonteCarlo.MonteCarlo;
import Move.Move;

import java.util.*;


public class GAMCplayer extends GeneticPlayer {

    private MonteCarlo mc;



    public GAMCplayer(int number) {
        super(number);
    }

    public Move getBestMove(Board board, int timelimit){
        HashMap<Move, Float> best_moves = calculateMoves(board);
//        int i =0;
//        for(Map.Entry<Move, Float> en: best_moves.entrySet()){
//            System.out.println( en.getValue());
//            System.out.println(en.getKey().getPiece()+ "@ "+ en.getKey().getPosition());
//            i++;
//            if(i>2) break;
//        }
        boolean firstmove = isFirstMove();
        Move move = mc.simulation(this.number-1, timelimit,best_moves);
        this.setFirstMove(firstmove);
        return new Move(this,move.getPiece(),move.getPosition());
    }

    public HashMap<Move, Float> calculateMoves(Board board){
        determinePhase();
        System.out.println("in GAMC\t   IN PHASE "+getPhase());
        for (int i = 0; i <5 ; i++) {
            System.out.print(currentWeights[i]+" ");
        }
        System.out.println();


        //while we code all the different strategies, make this call the one you want to try
        //System.out.println("In calculate move for genetic algorithm");

        float bestScore = -1000;
        ArrayList<Move> bestMoves = new ArrayList<Move>();
        HashMap<Move, Float> score_moves=new HashMap<Move, Float>();
        for (Move move : super.possibleMoveSet(board)) {
            float score = 0;
            //uncomment to test a strategy

            //the weights is what we will calculate in the genetic algorithm
            score += addsMostCorners(currentWeights[0], move, board);
            score += blocksMostCorners(currentWeights[1], move, board);
            score += closestToMiddle(currentWeights[2], move, board);
            score += biggestPiece(currentWeights[3], move, board);
            score += farFromStartingCorner(currentWeights[4], move, board);
            /*
            weights[0] = addMostCorners
            weights[1] = blocksMostCorners
            weights[2] = closestToMiddle
            weights[3] = biggestPiece
            weights[4] = farFromStartingPoint
             */

            score_moves.put(move, score*-1); //TODO: reverse order in other way if possible?
            bestMoves.add(move);

        }

        score_moves = sortByValue(score_moves);

        return score_moves;
    }

    // function to sort hashmap by values 
    public static HashMap<Move, Float> sortByValue(HashMap<Move, Float> hm)
    {
        // Create a list from elements of HashMap 
        List<Map.Entry<Move, Float> > list =
                new LinkedList<Map.Entry<Move, Float> >(hm.entrySet());

        // Sort the list 
        Collections.sort(list, new Comparator<Map.Entry<Move, Float> >() {
            public int compare(Map.Entry<Move, Float> o1,
                               Map.Entry<Move, Float> o2)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<Move, Float> temp = new LinkedHashMap<Move, Float>();
        for (Map.Entry<Move, Float> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public void setMc(MonteCarlo mc) {
        this.mc = mc;
    }

    public GAMCplayer clone(){
        GAMCplayer player = new GAMCplayer(number);
        player.startingCorner=startingCorner;
        player.setWeightsAsArray(weights.clone());
        for(Piece p:piecesList) player.getPiecesList().add(p.clone());
        player.setFirstMove(isFirstMove());

        return player;
    }
}
