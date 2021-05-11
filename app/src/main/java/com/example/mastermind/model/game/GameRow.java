package com.example.mastermind.model.game;

import com.example.mastermind.model.Const;

import java.util.Arrays;

public class GameRow {

    private final GamePeg[] row;

    public GameRow() {
        row = new GamePeg[Const.ROW_SIZE];
        for (int i = 0; i < Const.ROW_SIZE; i++)
            row[i] = new GamePeg(Const.NULL_COLOR_IN_GAME, i);
    }

    public boolean exist(GamePeg peg) {
        if (peg.getColor().equals(Const.NULL_COLOR_IN_GAME))
            return false;
        for (int i = 0; i < Const.ROW_SIZE; i++)
            if (this.row[i].equalsColor(peg))
                return true;
        return false;
    }

    public void addPeg(GamePeg peg) {
        if (this.exist(peg))
            removePeg(peg.getColor());
        this.row[peg.getPosition()] = peg;
    }

    public void removePeg(String color) {
        for (int i = 0; i < Const.ROW_SIZE; i++) {
            if (row[i].getColor().equals(color)) {
                row[i] = new GamePeg(Const.NULL_COLOR_IN_GAME, i);
            }
        }
    }

    public int check(GamePeg peg) {
        for (int i = 0; i < Const.ROW_SIZE; i++) {
            GamePeg curr = this.row[i];
            if (curr.equals(peg))
                return Const.REF_TO_BLACK_COLOR;
            else if (curr.equalsColor(peg))
                return Const.REF_TO_WHITE_COLOR;
        }
        return Const.REF_TO_NULL_COLOR;
    }

    public CheckRow checkGameRow (GameRow other) {
        CheckRow checkRow = new CheckRow();
        for (int i = 0; i < Const.ROW_SIZE; i++)
            checkRow.addCheckPeg(this.check(other.row[i]));
        return checkRow;
    }

    public boolean isFull() {
        for (int i = 0; i < Const.ROW_SIZE; i++)
            if (this.row[i].getColor().equals(Const.NULL_COLOR_IN_GAME))
                return false;
        return true;
    }

    public String[] getStringRow() {
        String[] s = new String[Const.ROW_SIZE];
        for (int i = 0; i < s.length; i++)
            s[i] = this.row[i].getColor();
        return s;
    }

    public String getNumStringRow() {
        String[] stringRow = getStringRow();
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < Const.ROW_SIZE; i++)
            s.append(Const.STRING_TO_CHAR_MAP.get(stringRow[i]));
        return s.toString();
    }

    public String getColorByPosition(int position) {
        return this.row[position].getColor();
    }

    @Override
    public String toString() {
        return "GameRow{" +
                "row=" + Arrays.toString(row) + '}';
    }
}
