package MiniMax;

import DataBase.Data;
import DataBase.PieceFactory;
import GameBoard.Board;
import GameBoard.Corner;
import Player.Player;
import Move.Move;
import Tools.Vector2d;
import Player.*;
import java.util.Arrays;

import java.util.*;

public class MiniMax {
    Player[] players;
    Board board;
    private final int NBR_OF_TURNS = 1;
    private int maxDepth;
    int rootPlayerNbr;
    HashMap<Integer,Move> cutOffMoveOccurence;
    float u;

    public MiniMax(Player[] players, Board board){
        this.players = players;
        this.board = board.clone();
        maxDepth = players.length*NBR_OF_TURNS;
    }

    public Move getMove(int playerNbr){
        //long start = System.currentTimeMillis(); //start of the timer
        this.rootPlayerNbr = playerNbr;
        this.cutOffMoveOccurence = new HashMap<>();
        MiniMaxNode root = new MiniMaxNode(board,players[playerNbr-1].clone(),maxDepth);
        float[] score = maxN(root,maxDepth,playerNbr,Float.MIN_VALUE);
//        System.out.println(Arrays.toString(score));
        for(MiniMaxNode c: root.getChildren()) {
            boolean same = true;
            for (int i = 0; i < score.length; i++) {
                if (c.getScore()[i]!=score[i]) same=false;
            }
            if(same) return new Move(players[playerNbr-1],c.getMove().getPiece(), c.getMove().getPosition());
        }
        //return findBestMove(root);
        System.out.println("oops"+root.getChildren().size());
        return null;
    }

    private Move findBestMove(MiniMaxNode node){
        System.out.println("depth = " + node.getDepth() + ", score = " + node.getScore());
        if(node.getDepth()==0){
            System.out.println("depth = 0");
            return node.getMove();
        }else{
            for(MiniMaxNode n:node.getChildren()) {
                System.out.println(n.getScore());
                if(n.getScore()==node.getScore()){
                    findBestMove(n);
                }
            }
        }
        System.out.println("not possible");
        return null;
    }
    /*
        public MiniMaxNode minimax(MiniMaxNode currentNode, int depth,int playerNbr, float alpha, float beta) {
            if (depth == 0) {
                return currentNode;
            }
            //Assuming the AI is purple player
            MiniMaxNode bestNode = null;
            if (playerNbr==rootPlayerNbr) {
                float value = -10000000;
                for (Move possibleMove:players[playerNbr-1].possibleMoveSetBoosted(board)) {
                    MiniMaxNode newNode = new MiniMaxNode(currentNode,possibleMove,depth-1,players[playerNbr-1],board);
                    float nodeValue = minimax(newNode,playerNbr+1, depth - 1, alpha, beta).getScore();
                        if (nodeValue > value) {
                            value = nodeValue;
                            bestNode = newNode;
                        }
                        alpha = Math.max(alpha, value);
                        if (alpha >= beta) {
                            break;
                        }

                }
                return bestNode;

            } else {
                float value = 10000000;
                for (Move possibleMove:players[playerNbr-1].possibleMoveSetBoosted(board)) {
                    MiniMaxNode newNode = new MiniMaxNode(currentNode,possibleMove,depth-1,players[playerNbr-1],board);
                    float nodeValue = minimax(newNode, depth - 1, rootPlayerNbr,alpha, beta).getScore();
                        if (nodeValue < value) {
                            value = nodeValue;
                            bestNode = newNode;
                        }
                        beta = Math.min(beta, value);
                        if (beta <= alpha) {
                            break;
                        }

                }
                return bestNode;
            }

        }

     */
/*
    private MiniMaxNode miniMax2P(MiniMaxNode node, int depth, int playerNbr,float alpha,float beta){
        if(depth<=0){
            return node;
        }
        if(playerNbr==rootPlayerNbr){
            float bestVal = Float.MIN_VALUE;
            for (Move possibleMove:players[playerNbr-1].possibleMoveSetBoosted(board)){
                MiniMaxNode newNode = new MiniMaxNode(node,possibleMove,depth-1,players[playerNbr-1],board);
                bestVal = Math.max(bestVal,miniMax2P(newNode,depth-1,playerNbr+1,alpha,beta).getScore());
                alpha = Math.max(alpha,bestVal);
                if(beta<=alpha){
                    break;
                }
            }
            node.setScore(bestVal);
            return node;
        }else{
            float bestVal = Float.MAX_VALUE;
            for (Move possibleMove:players[playerNbr-1].possibleMoveSetBoosted(board)){
                MiniMaxNode newNode = new MiniMaxNode(node,possibleMove,depth-1,players[playerNbr-1],board);
                bestVal = Math.min(bestVal,miniMax2P(newNode,depth-1,rootPlayerNbr,alpha,beta).getScore());
                beta = Math.min(beta,bestVal);
                if(beta<=alpha){
                    break;
                }
            }
            node.setScore(bestVal);
            return node;
        }

    }

 */
    private float[] maxN(MiniMaxNode node, int depth, int playerNbr, float alpha){
        if(depth<=0) {
            float sum = 0; //commenter
            for(float score: getScore(node)) sum+=score;
            if(u<sum) u=sum;
            node.setScore(getScore(node));
            return node.getScore();
        }
        float[] best =new float[players.length];
        for (int i = 0; i < best.length; i++) best[i] = Float.MIN_VALUE;
        for (Move possibleMove : players[playerNbr-1].possibleMoveSetBoosted(board)){
            //TODO compute first the moves that have been cutoff earlier
            boolean firstTurn = players[playerNbr-1].isFirstMove();
            MiniMaxNode newNode = new MiniMaxNode(node,possibleMove,depth-1,players[playerNbr-1],board);
            int nextPlayerNbr;
            if(playerNbr>=players.length){
                nextPlayerNbr = 1;
            }else{
                nextPlayerNbr = playerNbr+1;
            }
            float[] result = maxN(newNode, depth-1, nextPlayerNbr, best[playerNbr-1]);
            newNode.removeMove();
            if(firstTurn) players[playerNbr-1].setFirstMove(true);
            if(result[playerNbr-1]>best[playerNbr-1]){
                best = result;
            }
            if(result[playerNbr-1]>=this.u-alpha) {
                node.setScore(result);
                return result;
            }
        }
        node.setScore(best);
        return best;

    }



    /*
        private float alphaBeta_Pruning(MiniMaxNode node, int depth, int playerNbr, float alpha, float beta) {
            //System.out.println(playerNbr);
            if(depth<=0){
                if(playerNbr==rootPlayerNbr) {
                    return node.getScore();
                }else{
                    System.out.println("STOP RECURSION COND., not possible, smth wrong");
                    //never here because we never reach a terminal node that is not the root player
                    return -node.getScore();
                }
            }else{
                //TODO order the move so that the worst ones are at the end and are pruned faster
                for (Move possibleMove : players[playerNbr-1].possibleMoveSetBoosted(board)){
                    //TODO compute first the moves that have been cutoff earlier
                    MiniMaxNode newNode = new MiniMaxNode(node,possibleMove,depth-1,players[playerNbr-1],board);
                    int nextPlayerNbr = 0;
                    if(playerNbr>=players.length){
                        nextPlayerNbr = 1;
                    }else{
                        nextPlayerNbr = playerNbr+1;
                    }
                    if(playerNbr==rootPlayerNbr || nextPlayerNbr==rootPlayerNbr){
                        alpha = Math.max(alpha, -alphaBeta_Pruning(newNode,depth-1,nextPlayerNbr,-beta,-alpha));
                    }else {
                        alpha = Math.max(alpha, alphaBeta_Pruning(newNode,depth-1,nextPlayerNbr,alpha,beta));
                    }
                    if(alpha>=beta){
                        //checkToAddToCutOff(node.getMove());
                        node.setScore(beta);
                        return beta;
                    }
                }
                //updateOcc();
                node.setScore(alpha);
                return alpha;
            }
        }


     */
    public void updateOcc(){
        for(Map.Entry<Integer, Move> entry : cutOffMoveOccurence.entrySet()){
            int oldOcc = entry.getKey();
            Move oldMove = entry.getValue();
            cutOffMoveOccurence.remove(entry);
            cutOffMoveOccurence.put(oldOcc-1,oldMove);
        }
    }

    public void checkToAddToCutOff(Move move){
        if(cutOffMoveOccurence.isEmpty()||cutOffMoveOccurence.size()<5){
            cutOffMoveOccurence.put(1,move);
        }else{
            if(cutOffMoveOccurence.containsValue(move)){
                increaseOcc(move);
            }else{
                replaceSmallestOcc(move);
            }
        }
    }

    public void increaseOcc(Move move){
        int previousOcc = 0;
        for(Map.Entry<Integer, Move> entry : cutOffMoveOccurence.entrySet()){
            if(entry.getValue().equals(move)){
                previousOcc = entry.getKey();
                cutOffMoveOccurence.remove(entry);
            }
        }
        cutOffMoveOccurence.put(++previousOcc,move);
    }

    public void replaceSmallestOcc(Move move){
        int min = Integer.MAX_VALUE;
        for(Integer i:cutOffMoveOccurence.keySet()){
            if(i<min)min = i;
        }
        cutOffMoveOccurence.remove(min);
        cutOffMoveOccurence.put(1,move);
    }

    public float[] getArea(Board board){
        float[] res = new float[players.length];
        for(int i=0;i<players.length;i++) {
            int farestX = Integer.MIN_VALUE;
            int farestY = Integer.MIN_VALUE;

            for (Corner corner : board.getCorner(players[i].getStartingCorner())) {
                if (corner.getPosition().get_x() > farestX) farestX = corner.getPosition().get_x();
                if (corner.getPosition().get_y() > farestY) farestY = corner.getPosition().get_y();
            }
            res[i]= (float) Math.sqrt((Math.pow(farestX - players[i].getStartingCorner().get_x(), 2) + Math.pow(farestY - players[i].getStartingCorner().get_y(), 2)));
        }
        return res;
    }


    public List<Integer> normalize(List<Integer>heuristics){
        int max = Integer.MIN_VALUE;
        for (Integer i:heuristics) {
            if(i>=max)max = i;
        }
        for (int i = 0;i<heuristics.size();i++) {
            heuristics.set(i,heuristics.get(i)/max);
        }
        return heuristics;
    }


    public float[] getScore(MiniMaxNode node) {
        float maxBlockScore=10, maxCornerScore=7, maxAreaScore = 4; // *weight* of different attribute
        float[] distribution;
        if(players.length>2) distribution=new float[]{0.6f,0.3f,0.1f,0}; //distribution of the points for 1st, 2nd, 3rd players (4th=0)
        else distribution=new float[]{0.9f,0.1f}; // same for 2 players TODO: 2 players can simply be the relative proportion
        float[] score = new float[players.length];
        //for (int i = 0; i < score.length; i++) score[i]=0;
        //biggest piece heuristic
        float[] nbrOfBlocks = getBlocksScore(node.getBoard()); //total number of block TODO: count only new pieces placed?
        int[] index = getSortedIndex(nbrOfBlocks);// order the indices
        for (int i = 0; i < index.length; i++) {
            if(i<players.length-2 && score[index[i]]==score[index[i+1]]){ //if there is at least a next player compare to next player
                int count=1; //number of players with the same score
                for (int j = i; j < score.length && score[index[j]]==score[index[i]]; j++) { //how many of them?
                    count++;
                }
                float tot=0; // total of proportion for the players with the same score
                for (int j = 0; j < count; j++) {
                    tot+=distribution[i+j];
                }
                for (int j = 0; j < count; j++) { //update them
                    score[i+j]+=tot*maxBlockScore/count;
                }
                i+=count;//skip those
            }else {
                score[index[i]] += maxBlockScore * distribution[i];
            }
        }


        //closest to middle heuristic
        float[] area = getArea(node.getBoard());
        //Adds most corners heuristic
        index = getSortedIndex(area);
        for (int i = 0; i < index.length; i++) {
            if(i<players.length-2 && score[index[i]]==score[index[i+1]]){
                int count=1;
                for (int j = i; j < score.length && score[index[j]]==score[index[i]]; j++) {
                    count++;
                }
                float tot=0;
                for (int j = 0; j < count; j++) {
                    tot+=distribution[i+j];
                }
                for (int j = 0; j < count; j++) {
                    score[i+j]+=tot*maxAreaScore/count;
                }
                i+=count;
            }else score[index[i]]+=maxAreaScore*distribution[i];
        }
        float[] nbrOfCorner = new float[players.length];
        for(int i=0; i<players.length;i++) nbrOfCorner[i]=board.getCorner(players[i].getStartingCorner()).size();
        index = getSortedIndex(nbrOfCorner);
        for (int i = 0; i < index.length; i++) {
            if(i<players.length-2 && score[index[i]]==score[index[i+1]]){
                int count=1;
                for (int j = i; j < score.length && score[index[j]]==score[index[i]]; j++) {
                    count++;
                }
                float tot=0;
                for (int j = 0; j < count; j++) {
                    tot+=distribution[i+j];
                }
                for (int j = 0; j < count; j++) {
                    score[i+j]+=tot*maxCornerScore/count;
                }
                i+=count;
            }else score[index[i]]+=maxCornerScore*distribution[i];
        }



        for (int i = 0; i < score.length; i++) {
            score[i]=nbrOfBlocks[i]+nbrOfCorner[i]+area[i];
        }
        node.setScore(score);
        return score;
    }

    public float[] getBlocksScore(Board board){
        float[] res = new float[players.length];
        for (int i = 0; i < players.length; i++) {
            res[i] = 0;
        }
        for(int[] line:board.boardArray) for(int i:line) if(i!=0) res[i-1]++;
        return res;
    }

    /**
     * sort the array indices (ascending order)
     * @param array array to be sorted
     * @return indices of array sorted
     */
    public int[] getSortedIndex(float[] array){
        int max=-1, min=-1;
        int[] res = new int[players.length];
        for (int i = 0; i < array.length; i++) {
            if(max==-1||array[max]<array[i]) max=i;
            if(min==-1||array[min]>array[i]) min=i;
        }
        res[0]=max;
        res[res.length-1]=min;
        if(players.length>2){
            int second=-1,third=-1;
            for (int i = 0; i < players.length; i++) {
                if(i!=max&&i!=min){
                    if(second==-1) second=i;
                    else if(array[i]>array[second]) second=i;
                    if(third==-1) third=i;
                    else if(array[third]<array[i]) third=i;
                }
            }
            res[1]=second;
            res[2]=third;
        }
        return res;
//        ArrayList<Integer> result = new ArrayList<>(players.length);
//        for (int i = 0; i < array.length; i++) {
//            result.set(i,-1);
//        }
//        for (int i = 0; i < array.length; i++) {
//            for (int j = 0; j < array.length; j++) {
//                if(i==-1) result.set(i,j);
//                else if(!result.contains(j) && array[result.get(i)]<array[j]) result.set(i, j);
//            }
//        }
//        return result;
    }


    public static void main(String[] args) {
        Player p1 = new HumanPlayer(1, "jo");
        Player p2 = new HumanPlayer(2, "notJo");
        Player p3 = new HumanPlayer(3, "jo2");
        Player p4 = new HumanPlayer(4, "notJo2");
        p1.setStartingCorner(new Vector2d(0, 0));
        p2.setStartingCorner(new Vector2d(19, 0));
        p3.setStartingCorner(new Vector2d(19, 19));
        p4.setStartingCorner(new Vector2d(0, 19));
        p1.setPiecesList(PieceFactory.get().getAllPieces());
        p2.setPiecesList(PieceFactory.get().getAllPieces());
        p3.setPiecesList(PieceFactory.get().getAllPieces());
        p4.setPiecesList(PieceFactory.get().getAllPieces());
//        Board b = new Board(new Player[]{p1, p2});
//        MiniMax m = new MiniMax(new Player[]{p1,p2},b);

        Board b = new Board(new Player[]{p1, p2,p3,p4});
        MiniMax m = new MiniMax(new Player[]{p1,p2,p3,p4},b);

        int i= 0;
        while(i<15){
            Move move1 = m.getMove(p1.getPlayerNumber());
            if(move1.makeMove(b)) p1.removePiece(move1.getPiece().getLabel());
            b.print();

            Move move2 = m.getMove(p2.getPlayerNumber());
            if(move2.makeMove(b)) p2.removePiece(move2.getPiece().getLabel());
            b.print();



            Move move3 = m.getMove(p3.getPlayerNumber());
            if(move3.makeMove(b)) p3.removePiece(move1.getPiece().getLabel());
            b.print();

            Move move4 = m.getMove(p4.getPlayerNumber());
            System.out.println(Arrays.toString(m.cutOffMoveOccurence.keySet().toArray()));
            if(move4.makeMove(b)) p4.removePiece(move1.getPiece().getLabel());
            b.print();







            i++;
        }


    }

    /*
    Move move1 = m.simulate(p1.getPlayerNumber());
            if(move1.makeMove(b)) p1.removePiece(move1.getPiece().getLabel());

            Move move2 = m.simulate(p1.getPlayerNumber());
            if(move2.makeMove(b)) p1.removePiece(move1.getPiece().getLabel());

            Move move3 = m.simulate(p1.getPlayerNumber());
            if(move3.makeMove(b)) p1.removePiece(move1.getPiece().getLabel());

            Move move4 = m.simulate(p1.getPlayerNumber());
            if(move4.makeMove(b)) p1.removePiece(move1.getPiece().getLabel());
     */


}