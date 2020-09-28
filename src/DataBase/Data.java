package DataBase;

public class Data {
    private static String[] playersName = {"PLAYER1","PLAYER2","PLAYER3","PLAYER4"};

    public static String[] getPlayersName() {
        return playersName;
    }

    public static void setPlayersName(String[] playersName) {
        Data.playersName = playersName;
    }
}
