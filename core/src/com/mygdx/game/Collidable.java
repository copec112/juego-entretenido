package com.mygdx.game;

/**
 * Interfaz para los objetos con los que la pelota puede chocar
 * (Block y Paddle).
 *
 * Define el "rectangulo" del objeto (para detectar la colision) y la
 * reaccion del objeto cuando la pelota lo golpea. Asi PingBall no
 * necesita saber con que tipo de objeto choca.
 */
public interface Collidable {
    int getX();
    int getY();
    int getWidth();
    int getHeight();

    /** Reaccion del objeto cuando la pelota lo golpea. */
    void onCollision(PingBall ball);
}
