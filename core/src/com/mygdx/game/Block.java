package com.mygdx.game;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Block extends GameObject implements Collidable {
    private Color cc;
    private boolean destroyed;

    public Block(int x, int y, int width, int height) {
        super(x, y, width, height);
        destroyed = false;
        Random r = new Random(x + y);
        cc = new Color(0.1f + r.nextFloat(), r.nextFloat(), r.nextFloat(), 10);
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void destroy() {
        destroyed = true;
    }

    @Override
    public void onCollision(PingBall ball) {
        ball.rebotarVertical();
        destroy();
    }

    @Override
    public void draw(ShapeRenderer shape) {
        shape.setColor(cc);
        shape.rect(getX(), getY(), getWidth(), getHeight());
    }
}
