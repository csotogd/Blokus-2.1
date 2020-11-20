package MiniMax;

public class Score {
    private MiniMaxNode node;
    private float score;
    private boolean wasSetToNegative;

    /**
     * constructor for a value of a node
     * @param score
     * @param node
     */
    public Score(float score,MiniMaxNode node){
        this.score = score;
        this.node = node;
        this.wasSetToNegative = false;
    }

    /**
     * constructor for alpha or beta value
     * @param score
     */
    public Score(float score){
        this.score = score;
    }

    public void setNegative(){
        this.score = -score;
        this.wasSetToNegative = true;
    }

    public MiniMaxNode getNode() {
        return node;
    }

    public float getScore() {
        if(this.wasSetToNegative){
            float negScore = this.score;
            this.score = -score;
            this.wasSetToNegative = false;
            return negScore;
        }
        return score;
    }
}
