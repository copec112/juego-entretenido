package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Premio con forma de corazon que cae desde un bloque que se fusiono hasta 256.
 * Si la paleta lo atrapa, el jugador gana una vida; si cae al piso, se pierde.
 * Es un GameObject mas: sabe su posicion, dibujarse y detectar a la paleta.
 */
public class VidaExtra extends GameObject {
    private static final int TAMANO = 24;
    private static final int VELOCIDAD = 3; // pixeles por fotograma

    /** (x, y) es la esquina inferior izquierda de la caja del corazon. */
    public VidaExtra(int x, int y) {
        super(x, y, TAMANO, TAMANO);
    }

    /** Cae un poco en cada fotograma. */
    public void update() {
        setY(getY() - VELOCIDAD);
    }

    /** True si ya se fue por debajo de la pantalla (no la atrapo nadie). */
    public boolean estaPerdida() {
        return getY() + getHeight() < 0;
    }

    /** True si la caja del corazon se cruza con la de la paleta. */
    public boolean tocaA(Paddle paleta) {
        boolean cruzaX = getX() + getWidth() >= paleta.getX() && getX() <= paleta.getX() + paleta.getWidth();
        boolean cruzaY = getY() + getHeight() >= paleta.getY() && getY() <= paleta.getY() + paleta.getHeight();
        return cruzaX && cruzaY;
    }

    @Override
    public void draw(ShapeRenderer shape) {
        float cx = getX() + getWidth() / 2f;
        float cy = getY() + getHeight() / 2f;
        // borde blanco para que se vea sobre cualquier fondo
        shape.setColor(1f, 1f, 1f, 1f);
        corazon(shape, cx, cy, 1.25f);
        // corazon rojo
        shape.setColor(0.93f, 0.15f, 0.25f, 1f);
        corazon(shape, cx, cy, 1f);
    }

    /** Dos circulos arriba y un triangulo abajo forman el corazon. */
    private void corazon(ShapeRenderer shape, float cx, float cy, float escala) {
        float r = 6f * escala;
        shape.circle(cx - 5f * escala, cy + 3f * escala, r);
        shape.circle(cx + 5f * escala, cy + 3f * escala, r);
        shape.triangle(cx - 10.6f * escala, cy + 1.5f * escala,
                       cx + 10.6f * escala, cy + 1.5f * escala,
                       cx, cy - 11f * escala);
    }
}
