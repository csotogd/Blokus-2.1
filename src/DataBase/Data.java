package DataBase;

import Tools.Vector2d;

public class Data {
    private static String[] playersName = {"PLAYER 1","PLAYER 2","PLAYER 3","PLAYER 4"};

    private static int DIMENSION = 20;

    public static String[] getPlayersName() {
        return playersName;
    }

    public static void setPlayersName(String[] playersName) {
        Data.playersName = playersName;
    }

    public static int getDIMENSION() {
        return DIMENSION;
    }

    public static void setDIMENSION(int DIMENSION) {
        Data.DIMENSION = DIMENSION;
    }
}
