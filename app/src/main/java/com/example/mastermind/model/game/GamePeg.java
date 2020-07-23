package com.example.mastermind.model.game;

public class GamePeg extends Peg {
    private int position;

    public GamePeg(String color, int position) {
        super(color);
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean equals(GamePeg other) {
        return super.equalsColor(other) && this.position == other.position;
    }

    @Override
    public String toString() {
        return super.toString() + "GamePeg{" +
                "position=" + position +
                '}';
    }
}
