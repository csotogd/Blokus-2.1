package Tools;

/**
 * A Vector2d belongs to the R2 plane where x and y are real numbers
 */
public class Vector2d {
    private int x;
    private int y;

    /**
     * Constructs a default vector at the origin
     */
    public Vector2d(){
        x = 0;
        y = 0;
    }

    /**
     * Constructs a vector with a given x and y
     * @param x real number on the x axis
     * @param y real number on the y axis
     */
    public Vector2d(int x, int y){
        this.x = x;
        this.y = y;
    }

    /**
     * @return Gets the value of x
     */
    public int get_x(){
        return x;
    }

    /**
     * Sets the value of x
     * @param x value to set x
     */
    public void set_x(int x){
        this.x = x;
    }

    /**
     * @return Gets the value of y
     */
    public int get_y(){
        return y;
    }

    /**
     * Sets the value of y
     * @param y value to set y
     */
    public void set_y(int y){
        this.y = y;
    }

    /**
     * Adds parameter vector to the vector using vector addition
     * @param vector given vector to add
     * @return A new vector that is the sum of the two vectors
     */
    public Vector2d add(Vector2d vector){
        return new Vector2d(x + vector.get_x(), y + vector.get_y());
    }

    /**
     * Subtracts parameter vector from the vector using vector subtraction
     * @param vector given vector to subtract
     * @return A new vector that is the result of subtracting the two vectors
     */
    public Vector2d subtract(Vector2d vector){
        return new Vector2d(x - vector.get_x(), y - vector.get_y());
    }

    /**
     * Multiplies a vector by a given scalar
     * @param scalar scalar to multiply the vector
     * @return A new vector that is the result of the product of the vector and scalar
     */
    public Vector2d scale(int scalar){
        return new Vector2d(x * scalar, y * scalar);
    }

    /**
     * @return Gets the magnitude of a vector (or the distance from the origin)
     */
    public int magnitude(){
        return (int) Math.sqrt((x*x) + (y*y));
    }

    /**
     *
     * @param vec2
     * @return distance between this and vec2
     */
    public float moduleDistance(Vector2d vec2){

        return (float) Math.sqrt(Math.pow(x-vec2.get_x(), 2) + Math.pow(y-vec2.get_y(), 2));// VERY IMPORTANT ^ is not power, it is XOR for the bit forms of corresponding vars
    }

    /**
     * @return Gets the normalised vector (a vector of length one with same direction)
     */
    public Vector2d normalise(){
        return new Vector2d(x/magnitude(), y/magnitude());
    }

    /*
    public int dotProduct(Vector2d vector){
        return (x * vector.get_x()) + (y * vector.get_y());
    }

    // cos0 = A.B / |A|*|B|
    public int theta(Vector2d vector){
        return Math.acos((this.dotProduct(vector)) / (this.magnitude() * vector.magnitude()));
    }

    public Vector2d rotate(int angle){
        return new Vector2d((x * Math.cos(angle)) + (y * Math.sin(angle)),((-x) * Math.sin(angle) ) + (y * Math.cos(angle)));
    }*/

    /**
     * Prints the vector in a readable form.
     */
    public void printVector(){
        System.out.println("x = " + x + " y = " + y);
    }

    public static void main(String[] args) {
        Vector2d vector1= new Vector2d(15,6);
        Vector2d vector2 = new Vector2d(19, 0);
        System.out.println();
    }
}
