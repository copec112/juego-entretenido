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

    /** Color del numero: oscuro sobre los bloques claros (2, 4, 8 y 2048), blanco en el resto. */
    public Color getColorTexto() {
        return (valor <= 8 || valor >= 2048) ? Color.DARK_GRAY : Color.WHITE;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void destroy() {
        destroyed = true;
    }

    @Override
    public void onCollision(PingBall ball) {
        ball.rebotarContra(this);
        destroy();
    }

    @Override
    public void draw(ShapeRenderer shape) {
        shape.setColor(color);
        shape.rect(getX(), getY(), getWidth(), getHeight());
    }

    // Paleta con colores bien distintos entre si
    private static Color colorPorValor(int valor) {
        switch (valor) {
            case 2:    return rgb(0x4FC3F7); // celeste
            case 4:    return rgb(0x66BB6A); // verde
            case 8:    return rgb(0xFFEE58); // amarillo
            case 16:   return rgb(0xFFA726); // naranjo
            case 32:   return rgb(0xEF5350); // rojo
            case 64:   return rgb(0xAB47BC); // morado
            case 128:  return rgb(0x3F51B5); // azul
            case 256:  return rgb(0x00897B); // turquesa
            case 512:  return rgb(0xEC407A); // rosado
            case 1024: return rgb(0x6D4C41); // cafe
            default:   return rgb(0xFFD700); // 2048 o mas: dorado
        }
    }

    private static Color rgb(int hex) {
        return new Color(((hex >> 16) & 0xFF) / 255f, ((hex >> 8) & 0xFF) / 255f, (hex & 0xFF) / 255f, 1f);
    }
}
