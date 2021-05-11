package com.example.mastermind.model.game;

import com.example.mastermind.model.Const;

import java.util.ArrayList;
import java.util.Random;

public class GameManager {
    private static final Random rnd = new Random();
    private final String[] colors;
    private final ArrayList<GameRow> gameRows;
    private final ArrayList<CheckRow> checkRows;
    private GameRow hidden;
    private int turn;

    public GameManager() {
        this.colors = new String[]{Const.RED_COLOR_IN_GAME, Const.GREEN_COLOR_IN_GAME, Const.BLUE_COLOR_IN_GAME, Const.ORANGE_COLOR_IN_GAME, Const.YELLOW_COLOR_IN_GAME, Const.LIGHT_COLOR_IN_GAME};
        this.gameRows = new ArrayList<>();
        gameRows.add(new GameRow());
        this.checkRows = new ArrayList<>();
        checkRows.add(new CheckRow());
        this.hidden = new GameRow();
        randomizeHidden();
        this.turn = 1;
    }

    public void randomizeHidden() {
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i = 0; i < Const.ROW_SIZE; i++) {
            int num = rnd.nextInt(colors.length);
            while (arrayList.contains(num))
                num = rnd.nextInt(colors.length);
            hidden.addPeg(new GamePeg(this.colors[num], i));
            arrayList.add(num);
        }
    }

    public void pegToGameRow(String color, int position) {
        GamePeg gamePeg = new GamePeg(color, position);
        GameRow gameRow = this.gameRows.get(turn - 1);
        gameRow.addPeg(gamePeg);
        this.gameRows.set(turn - 1, gameRow);
    }

    public void nextTurn() {
        this.turn++;
        gameRows.add(new GameRow());
        checkRows.add(new CheckRow());
    }

    public boolean isGameContinue() {
        checkRows.set(turn - 1, gameRows.get(turn - 1).checkGameRow(hidden));
        if (checkRows.get(turn - 1).isWin())
            return false;
        nextTurn();
        return true;
    }

    public boolean isWin() {
        boolean win = checkRows.get(turn - 1).isWin();
        this.turn++;
        gameRows.add(new GameRow());
        return win;
    }

    public ArrayList<GameRow> getGameRows() {
        return gameRows;
    }

    public ArrayList<CheckRow> getCheckRows() {
        return checkRows;
    }

    public int getTurn() {
        return turn;
    }

    public GameRow getHidden() {
        return hidden;
    }

    public void setHidden(GameRow hidden) {
        this.hidden = hidden;
    }

}