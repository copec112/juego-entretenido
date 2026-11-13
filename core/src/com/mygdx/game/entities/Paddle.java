package com.mygdx.game.entities;

import com.mygdx.game.Config;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Paddle extends GameObject implements Collidable {
    private static final int VELOCIDAD = 15; // pixeles por fotograma

    public Paddle(int x, int y, int ancho, int alto) {
        super(x, y, ancho, alto);
    }

    @Override
    public void onCollision(PingBall ball) {
        ball.setColor(Color.GREEN);
        ball.rebotarHaciaArriba();
    }

    /**
     * Mueve la paleta hacia donde indica la direccion: -1 izquierda, 1 derecha, 0 se queda.
     * No se sale de la pantalla. (Quien lee el teclado es la pantalla, no la paleta.)
     */
    public void mover(int sentido) {
        int x2 = getX() + Integer.signum(sentido) * VELOCIDAD;
        if (x2 > 0 && x2 + getWidth() < Config.ANCHO) {
            setX(x2);
        }
    }

    @Override
    public void draw(ShapeRenderer shape) {
        shape.setColor(Color.BLUE);
        shape.rect(getX(), getY(), getWidth(), getHeight());
    }
}
