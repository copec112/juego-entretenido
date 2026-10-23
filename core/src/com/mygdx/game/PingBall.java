package com.mygdx.game;



import com.badlogic.gdx.Gdx;
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
	        if (getX()-size < 0 || getX()+size > Gdx.graphics.getWidth()) {
	            xSpeed = -xSpeed;
	        }
	        if (getY()+size > Gdx.graphics.getHeight()) {
	            ySpeed = -ySpeed;
	        }
	    }
	    
	    /** Invierte la direccion vertical (rebote contra paddle o bloque). */
	    public void rebotarVertical() {
	    	ySpeed = -ySpeed;
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

	    private boolean collidesWith(Collidable obj) {
	    	boolean intersectaX = (obj.getX() + obj.getWidth() >= getX()-size) && (obj.getX() <= getX()+size);
	        boolean intersectaY = (obj.getY() + obj.getHeight() >= getY()-size) && (obj.getY() <= getY()+size);
	    	return intersectaX && intersectaY;
	    }

	}
