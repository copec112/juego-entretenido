package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Clase abstracta base de todo objeto que aparece en pantalla
 * (Block, Paddle, PingBall y VidaExtra).
 *
 * Guarda la posicion y el tamano (encapsulados) y obliga a cada
 * subclase a definir como se dibuja.
 *
 * Para TODOS los objetos, (x, y) es la esquina inferior izquierda de su
 * rectangulo. Asi cualquier GameObject puede usarse en lugar de otro (por
 * ejemplo, en las colisiones) sin que la posicion signifique algo distinto
 * segun la subclase.
 */
public abstract class GameObject implements Rectangular {
    private int x;
    private int y;
    private final int width;
    private final int height;

    public GameObject(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /** Cada objeto sabe dibujarse a si mismo. */
    public abstract void draw(ShapeRenderer shape);

    @Override
    public int getX() { return x; }
    @Override
    public int getY() { return y; }
    @Override
    public int getWidth() { return width; }
    @Override
    public int getHeight() { return height; }

    /** X del centro del rectangulo. */
    public int getCentroX() { return x + width / 2; }
    /** Y del centro del rectangulo. */
    public int getCentroY() { return y + height / 2; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /** True si el rectangulo de este objeto se cruza (o toca) con el del otro. */
    public boolean intersecta(Rectangular otro) {
        boolean cruzaX = x + width >= otro.getX() && x <= otro.getX() + otro.getWidth();
        boolean cruzaY = y + height >= otro.getY() && y <= otro.getY() + otro.getHeight();
        return cruzaX && cruzaY;
    }
}
