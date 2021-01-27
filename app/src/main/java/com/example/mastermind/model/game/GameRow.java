package com.example.mastermind.model.game;

import com.example.mastermind.model.Const;

import java.util.Arrays;
import java.util.HashMap;

public class GameRow {

    private GamePeg[] row;
    private int currNum;
    private HashMap<String, Character> colorToCharMap;

    public GameRow(){
        row = new GamePeg[Const.ROW_SIZE];
        for (int i = 0; i < Const.ROW_SIZE; i++) {
            row[i] = new GamePeg(Const.NULL_COLOR_IN_GAME, i);
        }
        currNum = 0;
        createMap();
    }

    public void addPeg(GamePeg peg) {
        if (this.exist(peg))
            removePeg(peg.getColor());
        if (this.row[peg.getPosition()] == null)
            this.currNum++;
        this.row[peg.getPosition()] = peg;
    }

    public void removePeg(String color) {
        for (int i = 0; i < Const.ROW_SIZE; i++) {
            if (row[i].getColor().equals(color)) {
                row[i] = new GamePeg(Const.NULL_COLOR_IN_GAME, i);
            }
        }
    }

    public boolean exist(GamePeg peg) {
        if (peg.getColor().equals(Const.NULL_COLOR_IN_GAME))
            return false;
        for (int i = 0; i < Const.ROW_SIZE; i++)
            if (this.row[i].equalsColor(peg))
                return true;
        return false;
    }

    public boolean isFull() {
        for (int i = 0; i < Const.ROW_SIZE; i++)
            if (this.row[i].getColor().equals(Const.NULL_COLOR_IN_GAME))
                return false;
        return true;
    }

    public int check(GamePeg peg) { // 1-black , 2-white, 3-none
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

    public String[] getStringRow() {
        String[] s = new String[Const.ROW_SIZE];
        for (int i = 0; i < s.length; i++)
            s[i] = this.row[i].getColor();
        return s;
    }

    public String getNumStringRow() {
        String[] stringRow = new String[Const.ROW_SIZE];
        for (int i = 0; i < stringRow.length; i++)
            stringRow[i] = this.row[i].getColor();
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < Const.ROW_SIZE; i++)
            s.append(colorToCharMap.get(stringRow[i]));
        return s.toString();
    }

    public String getColorByPosition(int position) {
        return this.row[position].getColor();
    }

    @Override
    public String toString() {
        return "GameRow{" +
                "row=" + Arrays.toString(row) +
                ", currNum=" + currNum +
                '}';
    }

    private void createMap(){
        colorToCharMap = new HashMap<>();
        colorToCharMap.put(Const.NULL_COLOR_IN_GAME, Const.NULL_CHAR_IN_GAME);
        colorToCharMap.put(Const.RED_COLOR_IN_GAME, Const.RED_CHAR_IN_GAME);
        colorToCharMap.put(Const.BLUE_COLOR_IN_GAME, Const.BLUE_CHAR_IN_GAME);
        colorToCharMap.put(Const.GREEN_COLOR_IN_GAME, Const.GREEN_CHAR_IN_GAME);
        colorToCharMap.put(Const.ORANGE_COLOR_IN_GAME, Const.ORANGE_CHAR_IN_GAME);
        colorToCharMap.put(Const.YELLOW_COLOR_IN_GAME, Const.YELLOW_CHAR_IN_GAME);
        colorToCharMap.put(Const.LIGHT_COLOR_IN_GAME, Const.LIGHT_CHAR_IN_GAME);
    }
}
