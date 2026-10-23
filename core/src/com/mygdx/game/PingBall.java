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
	    	if (estaQuieto) return;
	        setXY(getX() + xSpeed, getY() + ySpeed);
	        if (getX()-size < 0 || getX()+size > Gdx.graphics.getWidth()) {
	            xSpeed = -xSpeed;
	        }
	        if (getY()+size > Gdx.graphics.getHeight()) {
	            ySpeed = -ySpeed;
	        }
	    }
	    
	    public void checkCollision(Paddle paddle) {
	        if(collidesWith(paddle)){
	            color = Color.GREEN;
	            ySpeed = -ySpeed;
	        }
	        else{
	            color = Color.WHITE;
	        }
	    }
	    private boolean collidesWith(Paddle pp) {

	    	boolean intersectaX = (pp.getX() + pp.getWidth() >= getX()-size) && (pp.getX() <= getX()+size);
	        boolean intersectaY = (pp.getY() + pp.getHeight() >= getY()-size) && (pp.getY() <= getY()+size);		
	    	return intersectaX && intersectaY;
	    }
	    
	    public void checkCollision(Block block) {
	        if(collidesWith(block)){
	            ySpeed = - ySpeed;
	            block.destroy();
	        }
	    }
	    private boolean collidesWith(Block bb) {

	    	boolean intersectaX = (bb.getX() + bb.getWidth() >= getX()-size) && (bb.getX() <= getX()+size);
	        boolean intersectaY = (bb.getY() + bb.getHeight() >= getY()-size) && (bb.getY() <= getY()+size);		
	    	return intersectaX && intersectaY;
	    }
	    
	}
