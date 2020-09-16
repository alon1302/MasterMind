package com.example.mastermind.model.game;

import java.util.ArrayList;
import java.util.Random;

public class GameManager {
    private static Random rnd = new Random();
    private String[] colors;
    private ArrayList<GameRow> gameRows;
    private ArrayList<CheckRow> checkRows;
    private GameRow hidden;
    private int turn;
    private long minutes;
    private long seconds;

    public GameManager() {
        this.colors = new String[]{"red", "green", "blue", "orange", "yellow", "light"};
        this.gameRows = new ArrayList<>();
        gameRows.add(new GameRow());
        this.checkRows = new ArrayList<>();
        checkRows.add(new CheckRow());
        this.hidden = new GameRow();
        randomizeHidden();
        this.turn = 1;
        this.minutes = 0;
        this.seconds = 0;
    }

    public ArrayList<GameRow> getGameRows() {
        return gameRows;
    }

    public ArrayList<CheckRow> getCheckRows() {
        return checkRows;
    }

    public long getMinutes() {
        return minutes;
    }

    public void setMinutes(long minutes) {
        this.minutes = minutes;
    }

    public long getSeconds() {
        return seconds;
    }

    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }

    public void randomizeHidden() {
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            int num = rnd.nextInt(6);

            while (arrayList.contains(num)) {
                num = rnd.nextInt(6);
            }

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

    public boolean isWin(){
        this.turn++;
        gameRows.add(new GameRow());
        return checkRows.get(turn - 1).isWin();
    }

    public void nextTurn(){
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