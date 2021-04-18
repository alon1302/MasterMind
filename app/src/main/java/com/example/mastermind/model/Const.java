package com.example.mastermind.model;

public class Const {

    public static final String ROOMS_IN_FIREBASE = "Rooms";
    public static final String RECORDS_IN_FIREBASE = "Records";
    public static final String REMATCH_IN_FIREBASE = "Rematch";
    public static final String WHOS_TURN_IN_FIREBASE = "WhosTurn";
    public static final String WINNER_IN_FIREBASE = "Winner";
    public static final String NONE_IN_FIREBASE = "None";
    public static final String GAME_IN_FIREBASE = "Game";
    public static final String HIDDEN_IN_FIREBASE = "Hidden";
    public static final String AVAILABLE_ROOMS_IN_FIREBASE = "AvailableRooms";
    public static final String TURNS_IN_FIREBASE = "Turns";
    public static final String NULL_ROW_IN_FIREBASE = "nnnn";
    public static final String PLAYER1_IN_FIREBASE = "Player1";
    public static final String PLAYER2_IN_FIREBASE = "Player2";
    public static final String USERS_IN_FIREBASE = "Users";
    public static final String COLLECTION_IN_FIREBASE = "Collection";
    public static final String COINS_IN_FIREBASE = "Coins";
    public static final String USER_NAME_IN_FIREBASE = "name";
    public static final String USER_EMAIL_IN_FIREBASE = "email";
    public static final String USER_ID_IN_FIREBASE = "id";
    public static final String USER_IMG_URL_IN_FIREBASE = "imgUrl";

    public static final String PLAYER1 = "Player1";
    public static final String PLAYER2 = "Player2";

    public static final String PROFILE_IMG_IN_STORAGE = "Profile Images";

    public static final String NULL_COLOR_IN_GAME = "null";
    public static final String RED_COLOR_IN_GAME = "red";
    public static final String GREEN_COLOR_IN_GAME = "green";
    public static final String BLUE_COLOR_IN_GAME = "blue";
    public static final String ORANGE_COLOR_IN_GAME = "orange";
    public static final String YELLOW_COLOR_IN_GAME = "yellow";
    public static final String LIGHT_COLOR_IN_GAME = "light";
    public static final String BLACK_COLOR_IN_GAME = "black";
    public static final String WHITE_COLOR_IN_GAME = "white";

    public static final char NULL_CHAR_IN_GAME = 'n';
    public static final char RED_CHAR_IN_GAME = '0';
    public static final char GREEN_CHAR_IN_GAME = '1';
    public static final char BLUE_CHAR_IN_GAME = '2';
    public static final char ORANGE_CHAR_IN_GAME = '3';
    public static final char YELLOW_CHAR_IN_GAME = '4';
    public static final char LIGHT_CHAR_IN_GAME = '5';

    public static final int END_GAME_SITUATION_WIN = 1;
    public static final int END_GAME_SITUATION_LOSE = 2;
    public static final int END_GAME_SITUATION_TIE = 0;

    public static final int REF_TO_BLACK_COLOR = 1;
    public static final int REF_TO_WHITE_COLOR = 2;
    public static final int REF_TO_NULL_COLOR = 3;

    public static final int ROW_SIZE = 4;

    public static final String NOTIFICATION_CHANNEL_NAME = "Comeback";
    public static final String SHARED_PREFERENCES_ID = "ThemesPrefs:";
    public static final String SHARED_PREFERENCES_KEY_INDEX = "index";

    public static final String INTENT_EXTRA_KEY_FROM = "from";
    public static final String INTENT_EXTRA_KEY_TYPE = "type";
    public static final String INTENT_EXTRA_KEY_CODE = "code";
    public static final String INTENT_EXTRA_KEY_PLAYER1 = "player1";
    public static final String INTENT_EXTRA_KEY_PLAYER2 = "player2";
    public static final String INTENT_EXTRA_KEY_PLAYER = "player";
    public static final String INTENT_EXTRA_KEY_WHO_IS_WIN = "whoIsWin";
    public static final String INTENT_EXTRA_KEY_USER1 = "user1";
    public static final String INTENT_EXTRA_KEY_USER2 = "user2";
    public static final String INTENT_EXTRA_KEY_OPPONENT = "opponent";
    public static final String INTENT_EXTRA_KEY_OPPONENT_HIDDEN = "opponentHidden";
    public static final String INTENT_EXTRA_KEY_MINUTES = "minutes";
    public static final String INTENT_EXTRA_KEY_SECONDS = "seconds";
    public static final String INTENT_EXTRA_KEY_TIME = "time";
    public static final String INTENT_EXTRA_KEY_IS_ONLINE = "isOnline";
    public static final String INTENT_EXTRA_VALUE_WITH_CODE = "withCode";
    public static final String INTENT_EXTRA_VALUE_FIND_ENEMY = "findEnemy";

    public static final long NOTIFICATION_TIME = 7200000;

    public static final String FRAGMENT_TITLE_ABOUT = "About";
    public static final String FRAGMENT_TITLE_RULES = "Rules";
    public static final String FRAGMENT_TITLE_GOAL = "Goal";

    public static final int ACTION_CREATE_ROOM = 0;
    public static final int ACTION_JOIN_ROOM = 1;
    public static final int ACTION_ROOM_CREATED = 2;
    public static final int ACTION_CHOOSE_HIDDEN = 3;
    public static final int ACTION_HIDDEN_TO_FIREBASE = 4;
    public static final int ACTION_TO_USER_TURN = 5;
    public static final int ACTION_TO_OPPONENT_TURN = 6;
    public static final int ACTION_UPLOAD_ROW_CHANGES = 7;
    public static final int ACTION_START_GAME_AFTER_CHOOSE = 8;
    public static final int ACTION_TURN_ROTATION = 9;
    public static final int ACTION_WAITING_TO_WIN = 10;
    public static final int ACTION_DISMISS_WAITING_DIALOG = 11;

    public static final int HINT_COST = 500;
    public static final int THEME_COST = 1500;

    public static final int ONLINE = 0;
    public static final int OFFLINE = 1;


}
