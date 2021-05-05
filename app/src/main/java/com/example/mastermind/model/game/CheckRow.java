package com.example.mastermind.model.game;

import com.example.mastermind.model.Const;

import java.util.Arrays;

public class CheckRow {
    private final CheckPeg[] row;
    private int index;

    public CheckRow() {
        this.row = new CheckPeg[Const.ROW_SIZE];
        for (int i = 0; i < Const.ROW_SIZE; i++) {
            this.row[i] = new CheckPeg(Const.NULL_COLOR_IN_GAME);
        }
        this.index = 0;
    }

    public void addCheckPeg(int num) {
        if (num == Const.REF_TO_BLACK_COLOR)
            this.row[this.index] = new CheckPeg(Const.BLACK_COLOR_IN_GAME);
        else if (num == Const.REF_TO_WHITE_COLOR)
            this.row[this.index] = new CheckPeg(Const.WHITE_COLOR_IN_GAME);
        else if (num == Const.REF_TO_NULL_COLOR)
            this.row[this.index] = new CheckPeg(Const.NULL_COLOR_IN_GAME);
        this.index++;
    }

    public String[] getStringRow() {
        String[] s = new String[Const.ROW_SIZE];
        for (int i = 0; i < s.length; i++)
            s[i] = this.row[i].getColor();
        return s;
    }

    public boolean isWin() {
        for (int i = 0; i < Const.ROW_SIZE; i++)
            if (!row[i].getColor().equals(Const.BLACK_COLOR_IN_GAME))
                return false;
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
