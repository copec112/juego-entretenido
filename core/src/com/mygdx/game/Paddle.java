package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Paddle extends GameObject implements Collidable {

    public Paddle(int x, int y, int ancho, int alto) {
        super(x, y, ancho, alto);
    }

    @Override
    public void onCollision(PingBall ball) {
        ball.setColor(Color.GREEN);
        ball.rebotarVertical();
    }

    @Override
    public void draw(ShapeRenderer shape) {
        shape.setColor(Color.BLUE);
        int x2 = getX();
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) x2 = getX() - 15;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) x2 = getX() + 15;
        if (x2 > 0 && x2 + getWidth() < Gdx.graphics.getWidth()) {
            setX(x2);
        }
        shape.rect(getX(), getY(), getWidth(), getHeight());
    }
}
