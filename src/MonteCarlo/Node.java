package MonteCarlo;

import DataBase.Piece;
import GameBoard.Board;
import GameBoard.Corner;
import Move.Move;
import Player.Player;
import Tools.Vector2d;

import java.util.*;

public class Node {
    private Node parent; // here, it will always be the root node
    private Board state; // board with which we clone to do simulation on
    private Move move; //move attributed to this node
    private List<Node> children; // possible move from this point on (or move to be explored)
    private int visitedNum; //number of time we visited a node
    private double score; // number of time we wins (1) or draw (0.5)
    private static double c=8; // constant for ucb1
    private Player[] players; // players typically cloned to do simulations
    private double ucbScore;

    /**
     * CONSTRUCTOR for the root (parent point to itself)
     * @param state current state of the game
     */
    public Node(Board state, Player[] ps){
        this.state=state;
        parent = this;
        move = null;
        children = new ArrayList<>();
        visitedNum =0;
        score = 0;
        players = new Player[ps.length];
        int count =0;
        for(Player p : ps) players[count++]=p.clone();

    }

    public Player[] getPlayers() {
        return players;
    }


    /**
     * Regular constructor
     * @param parent parent node
     * @param move move to make to arrive from parent to this node
     */
    public Node(Node parent, Move move){
        this.parent=parent;
        this.move = move;

        //children = new ArrayList<>();
        visitedNum = 0;
        score = 0;
        ucbScore = move.getPiece().getNumberOfBlocks()+move.getPiece().getCorners().get(0).size();
    }

    /**
     * create the children of the current state
     * @param player
     */
    public boolean expand(Player player){
        for(Move m : player.possibleMoveSet(state)) children.add(new Node(this,m));
        if(children.size()>0) return true;
        return false;
    }

    /**
     * expand the node with moves that the GA deemed interesting
     * @param best_moves score_move hashmap containing the moves sorted in descending order
     * @param numMoves number of move to be explored
     * @return true if expansion was successful
     */
    public boolean expandGA(ArrayList<Move> best_moves, int numMoves){
        int i=0;
        for (Move move : best_moves) {
             children.add(new Node(this, move));
             i++;
             if(i>=numMoves) break;
        }

        if(children.size()>0) return true;
        return false;
    }

    /**
     * Expand with random moves a state
     * @param player the player for which we want to expand the moves
     * @return true if it was successful, false otherwise
     */
    public boolean randomExpand(Player player, int numMoves){

        for(int i=0;i<numMoves;i++){
            Node n = new Node(this,player.randomPossibleMoveClone(state, player.getPiecesList()));
            if(!children.contains(n)) children.add(n);
        }
        if(children.size()>0) return true;
        return false;
    }

    /**
     * Expand with random moves bias towards bigger piece
     * @param player the player for which we want to expand the moves
     * @return true if it was successful, false otherwise
     */
    public boolean randomExpandBias(Player player,int numMoves){
        List biggestPieces;
        if(player.getPiecesList().size()<5){
            biggestPieces = player.getPiecesList();
        }else{
            biggestPieces = getBiggestPieces(player);
        }
        int count=0;
        for(int i=0;i<numMoves;i++){
            Node n = new Node(this,player.randomPossibleMoveClone(state, biggestPieces));
            if(!children.contains(n)) {
                children.add(n);
                count=0;
            }else{
                count++;
                if(count>3) break;
            }
        }
        if(children.size()==0) return false;
        return true;

    }

    /**
     * @return ArrayList of current possible moves a player can make.
     */
    public ArrayList<Move> possibleMoveSet(Board board,Player player, List<Piece> pieceSet){
        ArrayList<Corner> cornersOnBoard = board.getCorner(player.getStartingCorner());
        ArrayList<Move> moveSet=new ArrayList<>();

        for (Piece piecetoClone: pieceSet){
            if (!piecetoClone.isUsed()) {
                Piece piece = piecetoClone.clone(); // we clone it cause we rotate it and we do not want that to affect the real piece displayed
                for (int i = 0; i < piece.getTotalConfig(); i++) {
                    //get all the corners for that piece.
                    if(player.isFirstMove()) {
                        Vector2d adjust = new Vector2d(0,0);
                        if(player.getStartingCorner().get_x()!=0) adjust.set_x(piece.getShape()[0].length-1);
                        if(player.getStartingCorner().get_y()!=0) adjust.set_y(piece.getShape().length-1);
                        Move firstMove = new Move(player, piece.clone(), player.getStartingCorner().subtract(adjust));
                        if (firstMove.isAllowed(board)) moveSet.add(firstMove);
                    }else
                        for (Corner pieceCorner : piece.getCornersContacts(new Vector2d(0, 0))) {
                            for (Corner corner : cornersOnBoard) {
                                for (Vector2d emptyCorner : corner.getToCornerPositions()) { //for all the possible empty squares that would become corner contact
                                    //move the piece so that it is contact with the corner with the part of it we want
                                    Vector2d positionOfPiece= emptyCorner.subtract(pieceCorner.getPosition());
                                    Move move = new Move(player, piece.clone(), positionOfPiece);
                                    if (move.isAllowed(board))
                                        moveSet.add(move);
                                }

                            }
                        }
                    piece.rotateRight();
                    if(i==piece.getNbRotation()-1) piece.rotateUpsideDown();
                }
            }
        }
        return moveSet;
    }

    /**
     * heuristic method to choose biggest pieces
     * @param player player for which we need to select the pieces
     * @return subset of piece the player has
     */
    public List<Piece> getBiggestPieces(Player player){
        int maxscore = 0;
        LinkedList<Piece> temp = new LinkedList<>();
        for(Piece p:player.getPiecesList()){
            if(p.getNumberOfBlocks()+p.getCorners().get(0).size()>=maxscore){
                temp.addFirst(p);
                maxscore = p.getNumberOfBlocks()+p.getCorners().get(0).size();
            }
        }
        List<Piece> biggestPieces = new ArrayList<>();
        while(biggestPieces.size()<3&&temp.size()>0){
            biggestPieces.add(temp.removeFirst());
        }
        return biggestPieces;
    }

    /**
     * Simulate a play until it can't find a move for any player
     * @return the final score for the playerOfInterest (1 for a win)
     */
    public double simulation(int playerturn, int playerOfInterest){
        initializeNode();// make the move, copy players
        int countPass=0; //number of time a player has passed during a simulation
        Board board = state.clone(); //clone the board
     //   Player[] temp = new Player[players.length];//clone the players
     //   for(Player p : players) temp[p.getPlayerNumber()-1]=p.clone();
        boolean[] passed = new boolean[players.length];
        for (int i = 0; i < passed.length; i++) {
            passed[i]=false;
        }
        while(countPass<players.length){ //while not everyone has passed in a turn
            if(!passed[playerturn]) {
                Move move = players[playerturn].randomPossibleMove(board); //random move
                if(move!=null) {
                    move.writePieceIntoBoard(board);
                    players[playerturn].getPiecesList().remove(move.getPiece());
                }else{
                    passed[playerturn]=true;
                    countPass++;
                }
            }
            playerturn = (playerturn+1)%players.length;
        }
        //board.print();
        int[] playerScores=new int[players.length];
        for(Player p: players) for(Piece piece: p.getPiecesList()) playerScores[p.getPlayerNumber()-1]+=piece.getNumberOfBlocks();
        int same=0;
       // System.out.println(move.getPiece().getLabel()+" "+playerScores[0]+" "+playerScores[1]+" "+playerScores[2]+" "+playerScores[3]);
        for(int score:playerScores) {
            if(playerScores[playerOfInterest]>score) return 0;//loss
            else if(playerScores[playerOfInterest]==score) same++;
        }
        if(same>1) return 0.5;//draw
        return 1;//win
    }

    /**
     * clones the player, and write the move on the board
     */
    public void initializeNode(){
        state = parent.getState().clone();
        players = new Player[parent.getPlayers().length];
        int count =0;
        for(Player p : parent.getPlayers()) { //copy the players but take care to remove the piece played
            players[count++]=p.clone();
            if(players[count-1].getPlayerNumber()==move.getPlayer().getPlayerNumber()) {
                players[count - 1].removePiece(move.getPiece().getLabel());
                players[count - 1].setNotFirstMove();
            }
        }
        move.writePieceIntoBoard(state);
    }

    /**
     * UCB1  higher for bigger win loss ratio, and higher for not visited in a long time
     * @return evaluations of exploration possibility
     */
    public double getUCB1(){
        if(visitedNum!=0) ucbScore= score/(double) visitedNum + Node.c*Math.sqrt(Math.log(2*parent.getVisitedNum())/(double) visitedNum);
        else ucbScore= Node.c*Math.sqrt(Math.log(2*parent.getVisitedNum())/(double) visitedNum);
        return ucbScore;
    }

    public List<Node> getChildren(){
        return this.children;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Board getState() {
        return state;
    }

    public void setState(Board state) {
        this.state = state;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public int getVisitedNum() {
        return visitedNum;
    }

    public void addVisitiedNum() {
        this.visitedNum++;
        if(parent!=this) {
            parent.addVisitiedNum();
        }
    }

    public double getScore() {
        return score;
    }

    public void addScore(double s) {
        this.score +=s;
        if(parent!=this) parent.addScore(s);
    }
}
