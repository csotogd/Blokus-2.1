package MonteCarlo;
import DataBase.Piece;
import GameBoard.Board;
import GameBoard.Corner;
import Move.Move;
import Player.*;
import Tools.Vector2d;

import java.util.*;

public class MCTS {
    Player[] players; //players of the current game
    Node root; // root node
    Board board; // board of the game
    int numMoves; // brancing factor allowed due to simulations length

    /**
     * COnstructor
     * @param pls players in the current game, order matters !
     * @param bo board
     */
    public MCTS(Player[] pls, Board bo){
        players= pls;
        board=bo;
        root = new Node(bo, pls);
        numMoves=10;
    }

    /**
     * Constructing the tree from the root node
     * @param player index of the player you want to find a move for
     * @param timeLimit in ms
     * @return approximation of the best move in the given timelimit
     */
    public Move simulation(int player, long timeLimit){
        long start = System.currentTimeMillis(); //start of the timer
        root = new Node(board, players);
        expandGreedily(root,player,numMoves);// expand the best moves according to some heuristics
        if(root.getChildren().size()==0){ // if the expansion failed, something went wrong?
            root.expand(players[player]); //full expansion?
//            System.out.println("full expand"+ root.getChildren().size());
            if(root.getChildren().size()==0) return null;// if no move was found return null
        }
        List<Node> toExpand=new ArrayList<Node>(); //shortcut for ucb1 formula -> all new node have ucb1 = infinity
        toExpand.addAll(root.getChildren());
        while(System.currentTimeMillis()-start<timeLimit){ // while there is still time
            //chose one of the possible move
            Node choosen;
            if(toExpand.size()==0) {
                choosen = root.getChildren().get(0); // get the node with highest ucb1
                for (Node children : root.getChildren()) {
                    if (children.getUCB1() > choosen.getUCB1()) choosen = children;
                }
            }else{ //those infinity ucb1:
                choosen = toExpand.remove(0);
            }
            while(choosen.getChildren()!=null&&choosen.getChildren().size()!=0){ //if it is not a leaf node, find the leaf node with highest ucb1
                Node old = choosen;
                choosen = choosen.getChildren().get(0);
                for (Node children : old.getChildren()) {
                    if (children.getUCB1() > choosen.getUCB1()) choosen = children;
                }
            }
            if(choosen.getVisitedNum()!=0){ //if it has already been visited, we need to expand it
                choosen.initChildren();
                expandGreedily(choosen, player,numMoves/3); //expansion of fewer moves according to heuristics
                toExpand.addAll(choosen.getChildren()); // ucb1 of those will be again infinity
//                System.out.println(choosen.getDepth()+"  "+player);
                if(toExpand.size()!=0) choosen = toExpand.remove(0);
            }
            //simulate turn by turn until the end -> back propagate score
            choosen.addVisitiedNum(); // update the count of the visited number
            choosen.addScore(choosen.simulation((choosen.getDepth()+player+1)%players.length,player));//if we get a win update the score as well
        }
        /**
         * Here we chose the node with the most visited count
         * break the ties with ratio of win/loss, and numblocks
         */
        Node res = root.getChildren().get(0);//choose the most visited node move
//        for(Node children : root.getChildren()) System.out.println("player"+(player+1)+": "+children.getMove().getPiece().getLabel()+" "+children.getScore()+" "+ children.getVisitedNum());
        for(Node children : root.getChildren()) if(children.getVisitedNum()>res.getVisitedNum()|| // choose the node according to number of time visited, if equals, check if ratio win/loss is higher
                (children.getVisitedNum()==res.getVisitedNum()&&children.getScore()>res.getScore())||// if equal again, choose biggest piece
                (children.getVisitedNum()==res.getVisitedNum()&&children.getScore()==res.getScore()&&children.getMove().getPiece().getNumberOfBlocks()>res.getMove().getPiece().getNumberOfBlocks())) res=children;

        numMoves = root.getVisitedNum()/7; // next time we do simulations, we will explore a number of moves such that we can visit 7 times each
        for(Player p: players) if(p.getPlayerNumber()==res.getMove().getPlayer().getPlayerNumber()) return new Move(p,res.getMove().getPiece(), res.getMove().getPosition());
        return res.getMove();
    }

    /**
     * choose some moves to expand a certain node, with some heuristics:
     * number of blocks, closest to the middle under 4 turns, number of
     * moves increase (if we can't play a piece, is it playable with this
     * move?), number of piece playable on the corner (similar to number
     * of corner, but actually trying to put piece on it), blocks most corners
     * @param n node to expand
     * @param player player number concerned (at the root)
     * @param numMoves number of moves (branching factor)
     * @return true
     */
    public boolean expandGreedily(Node n, int player,int numMoves){
        Player p = n.getPlayers()[(n.getDepth()+1+player)%players.length];
        List<Move> moves = p.possibleMoveSetUpdate(n.getState(), numMoves);
        if(moves.size()>numMoves) { // if there are too many moves, we need to score them and take the best
            double[] score = new double[moves.size()];
            int counter = 0;
            for (Move m : moves) score[counter++] = (m.getPiece().getNumberOfBlocks());
            //score with closest to middle under 4-5 turns
            if (p.getPiecesList().size() > 17) {
                double maxDist = Math.sqrt(Math.pow(board.getDIMENSION() / 2, 2) * 2);
                for (int i = 0; i < moves.size(); i++) {
                    score[i] += maxDist - Math.sqrt(Math.pow(board.getDIMENSION() / 2 - (moves.get(i).getPosition().get_x() + (moves.get(i).getPiece().getShape()[0].length) / 2.0), 2) +
                            Math.pow(board.getDIMENSION() / 2 - (moves.get(i).getPosition().get_y() + (moves.get(i).getPiece().getShape().length) / 2.0), 2));
                }
            }else{
                for (int i = 0; i < moves.size(); i++) {
                    double maxDist = 0;
                    for(Corner c: moves.get(i).getPiece().getCornersContacts(moves.get(i).getPosition())){
                        double dist = Math.sqrt(Math.pow(c.getPosition().get_x()-players[player].getStartingCorner().get_x(),2)+Math.pow(c.getPosition().get_y()-players[player].getStartingCorner().get_y(),2));
                        if(dist>maxDist) maxDist=dist;
                    }
                    score[i]+=maxDist/Math.sqrt(board.getDIMENSION()*2);
                }
            }
            //score with numMoves increase
            if (!p.isFirstMove() ) {
                if (p.getUnplayablePiece().size() > 0){
                    Board bclone = n.getState().clone();
                    for (int i = 0; i < moves.size(); i++) {
                        write(moves.get(i), bclone);
                        //score with numMoves increase
                        for (Corner c : moves.get(i).getPiece().getCornersContacts(moves.get(i).getPosition())) {
                            for (Piece piece : p.getUnplayablePiece()) {
                                    //if it fits in that corner: score of move += numBlocks of the biggest piece that fits / 5.0
                                    if (fitsInThere(bclone, piece, c, player + 1)) {
                                        score[i] += (piece.getNumberOfBlocks() / 2.0);
                                        break;
                                    }
                            }
                        }

                        unwrite(moves.get(i), bclone);
                    }
                } else if ( p.getUnplayablePiece().size() == 0) {
                Board bclone = n.getState().clone();
                for (int i = 0; i < moves.size(); i++) {
                    write(moves.get(i), bclone);
                    //score with numMoves increase
                    for (Corner c : moves.get(i).getPiece().getCornersContacts(moves.get(i).getPosition())) {
                        for (Piece piece : p.getPiecesList()) {
                            if (!piece.getLabel().equals(moves.get(i).getPiece().getLabel())) {
                                //if it fits in that corner: score of move += numBlocks of the biggest piece that fits / 5.0
                                if (fitsInThere(bclone, piece, c, player+1)) {
                                    score[i] += (piece.getNumberOfBlocks() / 25.0);
                                    break;
                                }
                            }
                        }
                    }
                    unwrite(moves.get(i), bclone);
                }
            }
        }
            if(p.getPiecesList().size()<18){
                //block corners
                Board bclone = n.getState().clone();
                for (int i = 0; i < moves.size(); i++) {
                    //block corners
                    HashSet<Vector2d> opponentsCorners = getOpponentsCorners(p.getStartingCorner(),bclone,players);
                    for (int j = 0; j < moves.get(i).getPiece().getShape().length; j++) {
                        for (int k = 0; k < moves.get(i).getPiece().getShape()[0].length; k++) {
                            if(moves.get(i).getPiece().getShape()[j][k]!=0 && opponentsCorners.contains(moves.get(i).getPosition().add(new Vector2d(j,k))))score[i]+=1;
                        }
                    }

                }
            }


            moves= getBest(moves,score,numMoves);
        }
        List<Node> children = n.getChildren();
        if(children==null) children = new ArrayList<Node>();
        for(Move m:moves) children.add(new Node(n, m));
        return true;
    }

    /**
     * Checks if the piece fits on the corner given
     * @param bclone board on which operations are made
     * @param piece piece to be fit
     * @param c corner where to fit the piece
     * @param playerNumber identification of the block of the player
     * @return true if it fits at least with one configuration
     */
    private boolean fitsInThere(Board bclone, Piece piece, Corner c, int playerNumber) {
        for (int i = 0; i < piece.getTotalConfig(); i++) {
            for(Vector2d emptyC:c.getToCornerPositions()){
                for (int j = 0; j < piece.getCorners().get(0).size(); j++){
                    Vector2d position =emptyC.subtract(piece.getCorners().get(piece.getCurrentState()).get(j).getPosition());
                    for(Corner corner:piece.getCornersContacts(position)) if(corner.getPosition().equals(emptyC))
                    if(bclone.inBoard(position) && bclone.inBoard(position.add(new Vector2d(piece.getShape()[0].length,piece.getShape().length))) ){
                        boolean fit = true;
                        for (int k = 0; k < piece.getShape().length&&fit; k++) {
                            for (int l = 0; l < piece.getShape()[0].length&&fit; l++) {
                                if(piece.getShape()[k][l]!=0){
                                    if(bclone.boardArray[position.get_y()+k][position.get_x()+l]!=0||
                                            (position.get_x()+l-1>0&&bclone.boardArray[position.get_y()+k][position.get_x()+l-1]==playerNumber)||
                                            (position.get_x()+l+1<bclone.getDIMENSION()&&bclone.boardArray[position.get_y()+k][position.get_x()+l+1]==playerNumber)||
                                            (position.get_y()+k-1>0&&bclone.boardArray[position.get_y()+k-1][position.get_x()+l]==playerNumber)||
                                            (position.get_y()+k+1<bclone.getDIMENSION()&&bclone.boardArray[position.get_y()+k+1][position.get_x()+l]==playerNumber)) fit=false;
                                }
                            }
                        }
                        if(fit) return true;
                    }
                }
            }
            piece.rotateRight();
            if(i==piece.getNbRotation()-1) piece.rotateUpsideDown();
        }
        return false;
    }

    /**
     * given a list of moves, a list of score, return the numMoves best moves
     * @param moves list of moves
     * @param score score of moves
     * @param numMoves number of best moves to return
     * @return list of best moves
     */
    public List<Move> getBest(List<Move> moves,double[] score, int numMoves){
        final int size = moves.size();

        final int[] sortedIndex = new int[size];
        for (int i = 0; i < size; i++)
            sortedIndex[i] = i;

        boolean sorted;
        do {
            sorted = true;
            int bubble = sortedIndex[0];
            for (int i = 0; i < size - 1; i++) {
                if (score[bubble] > score[sortedIndex[i + 1]]) {
                    sortedIndex[i] = sortedIndex[i + 1];
                    sortedIndex[i + 1] = bubble;
                    sorted = false;
                } else {
                    bubble = sortedIndex[i + 1];
                }
            }
        } while (!sorted);
        ArrayList<Move> best = new ArrayList<>();
        for (int i = 0; i < numMoves; i++) {
            best.add(moves.get(sortedIndex[sortedIndex.length-1-i]));
        }
        return best;
    }

    /**
     * temporarly write the piece on the board without changing
     * anything else
     * @param m move to be written
     * @param b board on which to write
     */
    public void write(Move m, Board b){
        for (int i = 0; i < m.getPiece().getShape().length; i++) {
            for (int j = 0; j < m.getPiece().getShape()[0].length; j++) {
                if(m.getPiece().getShape()[i][j]!=0) b.boardArray[m.getPosition().get_y()+i][m.getPosition().get_x()+j]=m.getPlayer().getPlayerNumber();
            }
        }
    }

    /**
     * delete the piece on the board without changing
     * anything else
     * @param m move to be erase
     * @param b board
     */
    public void unwrite(Move m, Board b){
        for (int i = 0; i < m.getPiece().getShape().length; i++) {
            for (int j = 0; j < m.getPiece().getShape()[0].length; j++) {
                if(m.getPiece().getShape()[i][j]!=0) b.boardArray[m.getPosition().get_y()+i][m.getPosition().get_x()+j]=0;
            }
        }
    }

    /**
     * get the "empty" corners of the opponents in a HashSet (so that the contains operation
     * is only O(1)
     * @param startingPosition starting position not to consider
     * @param board board game
     * @param players
     * @return HashSet of Vector2d (positions) of empty opponents corners
     */
    public HashSet<Vector2d> getOpponentsCorners(Vector2d startingPosition, Board board, Player[] players){
        HashSet<Vector2d> corners = new HashSet<>();
        for(Player p: players){
            if(!p.getStartingCorner().equals(startingPosition)){
                if(!p.isFirstMove()){
                    boolean[][] checked = new boolean[board.getDIMENSION()][board.getDIMENSION()];
                    findCorners(board, checked,p.getStartingCorner(),corners,p.getPlayerNumber());
                }
            }
        }
        return corners;
    }

    /**
     * helper method to find corners and add them to the arraylist
     * @param checked position on the board that is already checked
     * @param position current coordinates
     * @param corners arraylist that contains the corners
     * @param player id of the player
     */
    private void findCorners(Board board,boolean[][] checked, Vector2d position, HashSet<Vector2d> corners, int player) {

        if(board.inBoard(position) && !checked[position.get_y()][position.get_x()]){
            checked[position.get_y()][position.get_x()]=true;
            if(board.boardArray[position.get_y()][position.get_x()]==player){
                boolean top=true, down=true, left=true, right=true; //NOT occupied by own blocks ie"free"
                //if top block is inside the board limit we can check whether it is the player's block
                if(board.inBoard(position.add(new Vector2d(0,-1))) && board.boardArray[position.get_y()-1][position.get_x()]==player){
                    top=false;//top is occupied by own block, continue the recursion at that block
                    findCorners(board, checked, position.add(new Vector2d(0,-1)), corners, player);
                }
                if(board.inBoard(position.add(new Vector2d(0,1))) && board.boardArray[position.get_y()+1][position.get_x()]==player){
                    down=false;
                    findCorners(board, checked, position.add(new Vector2d(0,1)), corners, player);
                }
                if(board.inBoard(position.add(new Vector2d(-1,0))) && board.boardArray[position.get_y()][position.get_x()-1]==player){
                    left=false;
                    findCorners(board, checked, position.add(new Vector2d(-1,0)), corners, player);
                }
                if(board.inBoard(position.add(new Vector2d(1,0))) && board.boardArray[position.get_y()][position.get_x()+1]==player){
                    right=false;
                    findCorners(board, checked, position.add(new Vector2d(1,0)), corners, player);
                }


                if(top && left
                        && board.inBoard(new Vector2d(position.get_x()-1, position.get_y()-1))
                        && board.boardArray[position.get_y()-1][position.get_x()-1]==0){
                    corners.add(position.add(new Vector2d(-1,-1)));
                }
                if(top && right
                        && board.inBoard(new Vector2d(position.get_x()+1, position.get_y()-1))
                        && board.boardArray[position.get_y()-1][position.get_x()+1]==0){
                        corners.add(position.add(new Vector2d(1,-1)));

                }
                if(down && left
                        && board.inBoard(new Vector2d(position.get_x()-1, position.get_y()+1))
                        && board.boardArray[position.get_y()+1][position.get_x()-1]==0){
                        corners.add(position.add(new Vector2d(-1,1)));

                }
                if(down && right
                        && board.inBoard(new Vector2d(position.get_x()+1, position.get_y()+1))
                        && board.boardArray[position.get_y()+1][position.get_x()+1]==0){
                        corners.add(position.add(new Vector2d(1,1)));

                }

                //now check for corners
                if(board.isOccupiedBy(position.add(new Vector2d(1,1)),player)) findCorners(board, checked,position.add(new Vector2d(1,1)),corners,player);
                if(board.isOccupiedBy(position.add(new Vector2d(-1,1)),player)) findCorners(board,checked,position.add(new Vector2d(-1,1)),corners,player);
                if(board.isOccupiedBy(position.add(new Vector2d(-1,-1)),player)) findCorners(board,checked,position.add(new Vector2d(-1,-1)),corners,player);
                if(board.isOccupiedBy(position.add(new Vector2d(1,-1)),player)) findCorners(board,checked,position.add(new Vector2d(1,-1)),corners,player);

            }

        }
    }

    /**
     * Constructing the tree from the root node
     * @param player index of the player you want to find a move for
     * @param timeLimit in ms
     * @return approximation of the best move in the given timelimit
     */
    public Move simulation(int player, long timeLimit, ArrayList<Move> moves){
        long start = System.currentTimeMillis(); //start of the timer
        root = new Node(board, players);
        root.expandGA(moves, numMoves);// expand the best moves according to some heuristics
        if(root.getChildren().size()==0){ // if the expansion failed, something went wrong?
            root.expand(players[player]); //full expansion?
//            System.out.println("full expand"+ root.getChildren().size());
            if(root.getChildren().size()==0) return null;// if no move was found return null
        }
        List<Node> toExpand=new ArrayList<Node>(); //shortcut for ucb1 formula -> all new node have ucb1 = infinity
        toExpand.addAll(root.getChildren());
        while(System.currentTimeMillis()-start<timeLimit){ // while there is still time
            //chose one of the possible move
            Node choosen;
            if(toExpand.size()==0) {
                choosen = root.getChildren().get(0); // get the node with highest ucb1
                for (Node children : root.getChildren()) {
                    if (children.getUCB1() > choosen.getUCB1()) choosen = children;
                }
            }else{ //those infinity ucb1:
                choosen = toExpand.remove(0);
            }
            while(choosen.getChildren()!=null && choosen.getChildren().size()!=0){ //if it is not a leaf node, find the leaf node with highest ucb1
                Node old = choosen;
                choosen = choosen.getChildren().get(0);
                for (Node children : old.getChildren()) {
                    if (children.getUCB1() > choosen.getUCB1()) choosen = children;
                }
            }
            if(choosen.getVisitedNum()!=0){ //if it has already been visited, we need to expand it
                choosen.initChildren();
                root.expandGA(moves, numMoves/3); //expansion of fewer moves according to heuristics
                toExpand.addAll(choosen.getChildren()); // ucb1 of those will be again infinity
//                System.out.println(choosen.getDepth()+"  "+player);
                if(toExpand.size()!=0) choosen = toExpand.remove(0);
            }
            //simulate turn by turn until the end -> back propagate score
            choosen.addVisitiedNum(); // update the count of the visited number
            choosen.addScore(choosen.simulation((choosen.getDepth()+player+1)%players.length,player));//if we get a win update the score as well
        }
        /**
         * Here we chose the node with the most visited count
         * break the ties with ratio of win/loss, and numblocks
         */
        Node res = root.getChildren().get(0);//choose the most visited node move
//        for(Node children : root.getChildren()) System.out.println("player"+(player+1)+": "+children.getMove().getPiece().getLabel()+" "+children.getScore()+" "+ children.getVisitedNum());
        for(Node children : root.getChildren()) if(children.getVisitedNum()>res.getVisitedNum()|| // choose the node according to number of time visited, if equals, check if ratio win/loss is higher
                (children.getVisitedNum()==res.getVisitedNum()&&children.getScore()>res.getScore())||// if equal again, choose biggest piece
                (children.getVisitedNum()==res.getVisitedNum()&&children.getScore()==res.getScore()&&children.getMove().getPiece().getNumberOfBlocks()>res.getMove().getPiece().getNumberOfBlocks())) res=children;

        numMoves = root.getVisitedNum()/7; // next time we do simulations, we will explore a number of moves such that we can visit 7 times each
        for(Player p: players) if(p.getPlayerNumber()==res.getMove().getPlayer().getPlayerNumber()) return new Move(p,res.getMove().getPiece(), res.getMove().getPosition());
        return res.getMove();
    }

}
