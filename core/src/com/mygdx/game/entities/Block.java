package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Bloque con un valor potencia de 2 (2, 4, 8, ... 2048), como en el juego 2048.
 * El color del bloque depende de su valor.
 */
public class Block extends GameObject implements Collidable {
    private int valor;
    private Color color;
    private boolean destroyed;

    public Block(int x, int y, int width, int height, int valor) {
        super(x, y, width, height);
        destroyed = false;
        setValor(valor);
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
        this.color = colorPorValor(valor);
    }

    /** Color del numero: oscuro sobre los bloques claros (2 y 4), blanco en el resto. */
    public Color getColorTexto() {
        return valor <= 4 ? Color.DARK_GRAY : Color.WHITE;
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
        shape.setColor(color);
        shape.rect(getX(), getY(), getWidth(), getHeight());
    }

    // Paleta parecida a la del 2048 original
    private static Color colorPorValor(int valor) {
        switch (valor) {
            case 2:    return rgb(0xEEE4DA);
            case 4:    return rgb(0xEDE0C8);
            case 8:    return rgb(0xF2B179);
            case 16:   return rgb(0xF59563);
            case 32:   return rgb(0xF67C5F);
            case 64:   return rgb(0xF65E3B);
            case 128:  return rgb(0xEDCF72);
            case 256:  return rgb(0xEDCC61);
            case 512:  return rgb(0xEDC850);
            case 1024: return rgb(0xEDC53F);
            default:   return rgb(0xEDC22E); // 2048 o mas
        }
    }

    private static Color rgb(int hex) {
        return new Color(((hex >> 16) & 0xFF) / 255f, ((hex >> 8) & 0xFF) / 255f, (hex & 0xFF) / 255f, 1f);
    }
}
