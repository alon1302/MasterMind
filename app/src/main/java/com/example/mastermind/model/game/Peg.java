package com.example.mastermind.model.game;

public abstract class Peg {
    private String color;

    public Peg(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean equalsColor(Peg other) {
        return this.color.equals(other.color);
    }

    @Override
    public String toString() {
        return "Peg{" +
                "color='" + color + '\'' +
                '}';
    }
}
