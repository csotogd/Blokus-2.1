package DataBase;

public class Data {
    private static String[] playersName = {"PLAYER 1","PLAYER 2","PLAYER 3","PLAYER 4"};

    public static String[] getPlayersName() {
        return playersName;
    }

    public static void setPlayersName(String[] playersName) {
        Data.playersName = playersName;
    }
}
