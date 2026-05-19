package com.example.minijuegosapp.brickgame;

public class Brick {
    private boolean isVisible;
    public int row, column, width, height;
    int color;

    public Brick(int row, int column, int width, int height, int color) {
        isVisible=true;
        this.row = row;
        this.column = column;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    public void setInvisible() {
        isVisible = false;
    }

    public boolean getVisibility(){
        return isVisible;
    }
}
