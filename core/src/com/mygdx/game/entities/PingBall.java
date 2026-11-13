package com.mygdx.game.entities;

import com.mygdx.game.Config;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * La pelota. Como todo GameObject, (x, y) es la esquina inferior izquierda
 * de su caja (de lado 2 * radio); el circulo se dibuja inscrito en esa caja.
 */
public class PingBall extends GameObject {
    private final int radio;
    private int xSpeed;
    private int ySpeed;
    private Color color = Color.WHITE;
    private boolean estaQuieto;

    public PingBall(int x, int y, int radio, int xSpeed, int ySpeed, boolean iniciaQuieto) {
        super(x, y, radio * 2, radio * 2);
        this.radio = radio;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        estaQuieto = iniciaQuieto;
    }

    public boolean estaQuieto() {
        return estaQuieto;
    }

    public void setEstaQuieto(boolean quieto) {
        estaQuieto = quieto;
    }

    @Override
    public void draw(ShapeRenderer shape) {
        shape.setColor(color);
        shape.circle(getCentroX(), getCentroY(), radio);
    }

    public void update() {
        color = Color.WHITE; // vuelve a blanco; Paddle lo pone verde al chocar
        if (estaQuieto) return;
        setXY(getX() + xSpeed, getY() + ySpeed);
        if (getX() < 0 || getX() + getWidth() > Config.ANCHO) {
            xSpeed = -xSpeed;
        }
        if (getY() + getHeight() > Config.ALTO) {
            ySpeed = -ySpeed;
        }
    }

    /** True si la pelota se fue por debajo de la pantalla (el centro paso del borde de abajo). */
    public boolean cayoAlVacio() {
        return getCentroY() < 0;
    }

    /** Hace que la pelota suba (rebote contra la paddle). Si ya sube, no cambia nada. */
    public void rebotarHaciaArriba() {
        ySpeed = Math.abs(ySpeed);
    }

    /**
     * Rebote contra un bloque: se invierte la velocidad del eje por donde "entro"
     * la pelota, o sea el eje con menos solapamiento (golpe de lado o de arriba/abajo).
     */
    public void rebotarContra(Rectangular obj) {
        int solapaX = Math.min(obj.getX() + obj.getWidth(), getX() + getWidth()) - Math.max(obj.getX(), getX());
        int solapaY = Math.min(obj.getY() + obj.getHeight(), getY() + getHeight()) - Math.max(obj.getY(), getY());
        if (solapaX < solapaY) {
            xSpeed = -xSpeed;
        } else {
            ySpeed = -ySpeed;
        }
    }

    /** Cambia la rapidez de la pelota sin alterar hacia donde va. */
    public void setVelocidad(int vx, int vy) {
        xSpeed = Integer.signum(xSpeed) * vx;
        ySpeed = Integer.signum(ySpeed) * vy;
    }

    /** Fija el sentido horizontal: 1 = derecha, -1 = izquierda. */
    public void setDireccionX(int sentido) {
        xSpeed = Math.abs(xSpeed) * sentido;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    /** Si choca con el objeto, este decide como reaccionar. */
    public void checkCollision(Collidable obj) {
        if (collidesWith(obj)) {
            obj.onCollision(this);
        }
    }

    public boolean collidesWith(Rectangular obj) {
        return intersecta(obj);
    }
}