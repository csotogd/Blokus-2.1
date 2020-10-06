package DataBase;

import Tools.Vector2d;

public class Data {
    private static String[] playersName = {"PLAYER 1","PLAYER 2","PLAYER 3","PLAYER 4"};

    private static Vector2d DIMENSION = new Vector2d(20,20);

    public static String[] getPlayersName() {
        return playersName;
    }

    public static void setPlayersName(String[] playersName) {
        Data.playersName = playersName;
    }

    public static Vector2d getDIMENSION() {
        return DIMENSION;
    }

    public static void setDIMENSION(Vector2d DIMENSION) {
        Data.DIMENSION = DIMENSION;
    }
}
