package com.mygdx.game.entities;

/**
 * Interfaz para los objetos con los que la pelota puede chocar
 * (Block y Paddle).
 *
 * Es un Rectangular (para detectar la colision) que ademas define la
 * reaccion del objeto cuando la pelota lo golpea. Asi PingBall no
 * necesita saber con que tipo de objeto choca.
 */
public interface Collidable extends Rectangular {
    /** Reaccion del objeto cuando la pelota lo golpea. */
    void onCollision(PingBall ball);
}
