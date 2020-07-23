package com.example.mastermind.model.game;

import java.util.Arrays;

public class CheckRow {
    private static int SIZE = 4;
    private CheckPeg[] row;
    private int index;

    public CheckRow() {
        this.row = new CheckPeg[SIZE];
        for (int i = 0; i < SIZE; i++) {
            this.row[i] = new CheckPeg("null");
        }
        this.index = 0;
    }

    public void addCheckPeg(int num) { // 1-black , 2-white, 3-none
        if (num == 3) {// none
            this.row[this.index] = new CheckPeg("null");
            this.index++;
        }
        else if (num == 1) { // black
            this.row[this.index] = new CheckPeg("black");
            this.index++;
        }
        else {
            this.row[this.index] = new CheckPeg("white");
            this.index++;
        }
    }

    public String[] getStringRow() {
        String[] s = new String[4];
        for (int i=0; i<s.length; i++) {
            s[i] = this.row[i].getColor();
        }
        return s;
    }

    public boolean isWin() {
        for (int i = 0; i<SIZE; i++) {
            if (!row[i].getColor().equals("black")) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "CheckRow{" +
                "row=" + Arrays.toString(row) +
                ", index=" + index +
                '}';
    }
}
