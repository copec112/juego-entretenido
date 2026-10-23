package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Clase abstracta base de todo objeto que aparece en pantalla
 * (Block, Paddle y PingBall).
 *
 * Guarda la posicion y el tamano (encapsulados) y obliga a cada
 * subclase a definir como se dibuja.
 *
 * Nota: para Block y Paddle, (x, y) es la esquina inferior izquierda.
 * Para PingBall, (x, y) es el centro del circulo.
 */
public abstract class GameObject {
    private int x;
    private int y;
    private int width;
    private int height;

    public GameObject(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /** Cada objeto sabe dibujarse a si mismo. */
    public abstract void draw(ShapeRenderer shape);

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
