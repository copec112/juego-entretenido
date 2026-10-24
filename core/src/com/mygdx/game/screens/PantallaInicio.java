package com.mygdx.game.screens;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.Config;
import com.mygdx.game.entities.Block;
import com.mygdx.game.managers.GameState;

/**
 * Pantalla de inicio: titulo, controles y fichas de 2048 que suben flotando de fondo.
 * Completa los pasos de la plantilla (Pantalla). ENTER empieza la partida y
 * ESPACIO abre las reglas y el tutorial.
 */
public class PantallaInicio extends Pantalla {
    private static final int FICHAS = 14;
    private static final int[] VALORES = { 2, 2, 4, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048 };

    /** Ficha decorativa que sube flotando. */
    private static class Ficha {
        float x, y, velocidad, tam;
        int valor;
    }

    private final Ficha[] fichas = new Ficha[FICHAS];
    private final Random random = new Random();

    public PantallaInicio(Contexto ctx) {
        super(ctx);
        for (int i = 0; i < FICHAS; i++) {
            fichas[i] = new Ficha();
            reiniciarFicha(fichas[i], true);
        }
    }

    private void reiniciarFicha(Ficha f, boolean porToda) {
        f.tam = 44 + random.nextInt(34);
        f.x = random.nextInt(Config.ANCHO - (int) f.tam);
        f.y = porToda ? random.nextInt(Config.ALTO) : -f.tam;
        f.velocidad = 18 + random.nextInt(32);
        f.valor = VALORES[random.nextInt(VALORES.length)];
    }

    @Override
    public void alMostrar() {
        ctx.getAudio().iniciarMusica();
    }

    // ------------------------------------------------------------------
    //  Pasos de la plantilla
    // ------------------------------------------------------------------

    @Override
    protected void actualizar(float delta) {
        for (Ficha f : fichas) {
            f.y += f.velocidad * delta;
            if (f.y > Config.ALTO) {
                reiniciarFicha(f, false);
            }
        }
    }

    @Override
    protected void dibujarFormas(ShapeRenderer shape) {
        // fichas flotando, transparentes para que no tapen el titulo
        for (Ficha f : fichas) {
            Color c = Block.colorPorValor(f.valor);
            shape.setColor(c.r, c.g, c.b, 0.35f);
            shape.rect(f.x, f.y, f.tam, f.tam);
        }
        // panel oscuro detras de los controles
        shape.setColor(0f, 0f, 0.1f, 0.6f);
        shape.rect(170, 118, 460, 124);
        shape.setColor(1f, 0.84f, 0f, 0.9f);
        shape.rect(170, 240, 460, 2);
    }

    @Override
    protected void dibujarTextos(SpriteBatch batch) {
        BitmapFont grande = ctx.getFontTitulo();
        BitmapFont medio = ctx.getFontMedio();
        float centro = Config.ANCHO / 2f;
        float entrada = Math.min(1f, getTiempo() / 0.6f);

        // numeros de las fichas
        for (Ficha f : fichas) {
            boolean claro = f.valor <= 8 || f.valor >= 2048;
            float tono = claro ? 0.2f : 1f;
            textoEnCentro(batch, medio, String.valueOf(f.valor), f.tam / 40f, tono, tono, tono, 0.7f,
                    f.x + f.tam / 2f, f.y + f.tam / 2f);
        }

        // titulo
        float pulso = 1f + 0.03f * (float) Math.sin(getTiempo() * 3f);
        textoCentrado(batch, grande, "BREAKOUT", 4.4f * pulso, 1f, 1f, 1f, entrada, centro, 450 - 30 * (1 - suave(entrada)));
        textoCentrado(batch, grande, "2048", 6.5f * pulso, 1f, 0.84f, 0f, entrada, centro, 375 - 30 * (1 - suave(entrada)));
        textoCentrado(batch, medio, "Rompe bloques, fusionalos y llega a 2048", 1.3f, 0.85f, 0.85f, 1f, entrada, centro, 270);

        // controles
        String[][] controles = {
            { "FLECHAS", "Mover la paleta" },
            { "ESPACIO", "Lanzar la pelota" },
            { "W A S D", "Deslizar y fusionar bloques" },
            { "M", "Sonido si / no" },
        };
        for (int i = 0; i < controles.length; i++) {
            float y = 228 - i * 26;
            textoIzquierda(batch, medio, controles[i][0], 1.4f, 1f, 0.84f, 0f, 1f, 195, y);
            textoIzquierda(batch, medio, controles[i][1], 1.4f, 1f, 1f, 1f, 1f, 320, y);
        }

        // aviso parpadeante
        float parpadeo = 0.4f + 0.6f * (0.5f + 0.5f * (float) Math.sin(getTiempo() * 4f));
        textoCentrado(batch, medio, "Presiona ENTER para jugar", 2.1f, 1f, 1f, 1f, parpadeo, centro, 102);
        textoCentrado(batch, medio, "ESPACIO: reglas y tutorial", 1.5f, 0.85f, 0.95f, 1f, 1f, centro, 68);

        int mejor = GameState.getInstance().getMejorPuntaje();
        if (mejor > 0) {
            textoCentrado(batch, medio, "Mejor puntaje: " + mejor, 1.4f, 1f, 0.84f, 0f, 1f, centro, 38);
        }
    }

    @Override
    protected void procesarEntrada() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            ctx.getAudio().sonarInicio();
            ctx.irA(new PantallaJuego(ctx));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            ctx.irA(new PantallaTutorial(ctx)); // reglas y tutorial
        }
    }
}
