package com.example.mastermind.model.game;

import android.util.Log;

import java.util.Arrays;

public class GameRow {

    private static int SIZE = 4;
    private GamePeg[] row;
    private int currNum;

    public GameRow(){
        row = new GamePeg[4];
        for (int i = 0; i< SIZE; i++) {
            row[i] = new GamePeg("null", i);
        }
        currNum = 0;
    }

    public void addPeg(GamePeg peg) {
        if (this.exist(peg)) {
            removePeg(peg.getColor());
        }
        if (this.row[peg.getPosition()] == null)
            this.currNum++;
        this.row[peg.getPosition()] = peg;
    }

    public void removePeg(String color) {
        for (int i=0; i<SIZE; i++) {
            if (row[i].getColor().equals(color)) {
                row[i] = new GamePeg("null", i);
            }
        }
    }

    public boolean exist(GamePeg peg) {
        if (peg.getColor().equals("null")) {
            return false;
        }
        for (int i=0; i<SIZE; i++) {
            if (this.row[i].equalsColor(peg)) {
                return true;
            }
        }
        return false;
    }

    public boolean isFull() {
        for (int i=0; i<SIZE; i++) {
            if (this.row[i].getColor().equals("null")) {
                return false;
            }
        }
        return true;
    }

    public int check(GamePeg peg) { // 1-black , 2-white, 3-none
        for (int i = 0; i< SIZE; i++) {
            GamePeg curr = this.row[i];
            if (curr.equals(peg)) {
                return 1;
            }
            else if (curr.equalsColor(peg)) {
                return 2;
            }
        }
        return 3;
    }

    public CheckRow checkGameRow (GameRow other) {
        CheckRow checkRow = new CheckRow();
        for (int i = 0; i<SIZE; i++) {
            checkRow.addCheckPeg(this.check(other.row[i]));
            Log.d("!!!!!!!!", "checkGameRow: " + checkRow.toString());
        }
        return checkRow;
    }

    public String[] getStringRow() {
        String[] s = new String[4];
        for (int i=0; i<s.length; i++) {
            s[i] = this.row[i].getColor();
        }
        return s;
    }

    @Override
    public String toString() {
        return "GameRow{" +
                "row=" + Arrays.toString(row) +
                ", currNum=" + currNum +
                '}';
    }

    public String getColorByPosition(int position) {
        return this.row[position].getColor();
    }
}
