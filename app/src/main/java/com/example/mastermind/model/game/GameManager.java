package com.example.mastermind.model.game;

import com.example.mastermind.model.Const;

import java.util.ArrayList;
import java.util.Random;

public class GameManager {
    private static Random rnd = new Random();
    private String[] colors;
    private ArrayList<GameRow> gameRows;
    private ArrayList<CheckRow> checkRows;
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

    public ArrayList<GameRow> getGameRows() {
        return gameRows;
    }

    public ArrayList<CheckRow> getCheckRows() {
        return checkRows;
    }

    public void randomizeHidden() {
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i = 0; i < Const.ROW_SIZE; i++) {
            int num = rnd.nextInt(colors.length);
            while (arrayList.contains(colors.length))
                num = rnd.nextInt(colors.length);
            hidden.addPeg(new GamePeg(this.colors[num], i));
            arrayList.add(num);
        }
    }

    public boolean nextTurnIsNotWin() {
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

    public void nextTurn() {
        this.turn++;
        gameRows.add(new GameRow());
        checkRows.add(new CheckRow());
    }

    public int getTurn() {
        return turn;
    }

    public void pegToGameRow(String color, int position) {
        GamePeg gamePeg = new GamePeg(color, position);
        GameRow gameRow = this.gameRows.get(turn - 1);
        gameRow.addPeg(gamePeg);
        this.gameRows.set(turn - 1, gameRow);
    }

    public void setHidden(GameRow hidden) {
        this.hidden = hidden;
    }

    public GameRow getHidden() {
        return hidden;
    }
}