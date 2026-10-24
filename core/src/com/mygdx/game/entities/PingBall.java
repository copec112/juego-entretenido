package com.mygdx.game.entities;



import com.mygdx.game.Config;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class PingBall extends GameObject {
	    private int size;
	    private int xSpeed;
	    private int ySpeed;
	    private Color color = Color.WHITE;
	    private boolean estaQuieto;
	    
	    public PingBall(int x, int y, int size, int xSpeed, int ySpeed, boolean iniciaQuieto) {
	        super(x, y, size * 2, size * 2);
	        this.size = size;
	        this.xSpeed = xSpeed;
	        this.ySpeed = ySpeed;
	        estaQuieto = iniciaQuieto;
	    }
	    
	    public boolean estaQuieto() {
	    	return estaQuieto;
	    }
	    public void setEstaQuieto(boolean bb) {
	    	estaQuieto=bb;
	    }
	    
	    @Override
	    public void draw(ShapeRenderer shape){
	        shape.setColor(color);
	        shape.circle(getX(), getY(), size);
	    }
	    
	    public void update() {
	    	color = Color.WHITE; // vuelve a blanco; Paddle lo pone verde al chocar
	    	if (estaQuieto) return;
	        setXY(getX() + xSpeed, getY() + ySpeed);
	        if (getX()-size < 0 || getX()+size > Config.ANCHO) {
	            xSpeed = -xSpeed;
	        }
	        if (getY()+size > Config.ALTO) {
	            ySpeed = -ySpeed;
	        }
	    }
	    
	    /** Hace que la pelota suba (rebote contra la paddle). Si ya sube, no cambia nada. */
	    public void rebotarHaciaArriba() {
	    	ySpeed = Math.abs(ySpeed);
	    }

	    /**
	     * Rebote contra un bloque: se invierte la velocidad del eje por donde "entro"
	     * la pelota, o sea el eje con menos solapamiento (golpe de lado o de arriba/abajo).
	     */
	    public void rebotarContra(Collidable obj) {
	    	int solapaX = Math.min(obj.getX() + obj.getWidth(), getX() + size) - Math.max(obj.getX(), getX() - size);
	    	int solapaY = Math.min(obj.getY() + obj.getHeight(), getY() + size) - Math.max(obj.getY(), getY() - size);
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

	    public boolean collidesWith(Collidable obj) {
	    	boolean intersectaX = (obj.getX() + obj.getWidth() >= getX()-size) && (obj.getX() <= getX()+size);
	        boolean intersectaY = (obj.getY() + obj.getHeight() >= getY()-size) && (obj.getY() <= getY()+size);
	    	return intersectaX && intersectaY;
	    }

	}