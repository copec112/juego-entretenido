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
 * Pantalla de victoria: aparece una sola vez por partida al llegar a 2048 puntos.
 * Da una vida extra de recompensa y, al apretar ENTER, vuelve a la partida que estaba
 * en pausa para seguir jugando en "modo infinito" (la dificultad ya es la maxima).
 * Completa los pasos de la plantilla (Pantalla).
 */
public class PantallaVictoria extends Pantalla {
    private static final float ESPERA_CONTINUAR = 1.0f;
    private static final int CONFETIS = 70;
    private static final int[] VALORES = { 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048 };

    private final Pantalla volverA; // la partida que sigue despues
    private final float[] cx = new float[CONFETIS];
    private final float[] cy = new float[CONFETIS];
    private final float[] cv = new float[CONFETIS];
    private final float[] ct = new float[CONFETIS];
    private final int[] cc = new int[CONFETIS];
    private boolean vidaGanada;

    public PantallaVictoria(Contexto ctx, Pantalla volverA) {
        super(ctx);
        this.volverA = volverA;
        Random r = new Random();
        for (int i = 0; i < CONFETIS; i++) {
            cx[i] = r.nextInt(Config.ANCHO);
            cy[i] = r.nextInt(Config.ALTO);
            cv[i] = 60 + r.nextInt(120);
            ct[i] = 6 + r.nextInt(8);
            cc[i] = VALORES[r.nextInt(VALORES.length)];
        }
    }

    /** Al llegar: recompensa de una vida, musica de fiesta (se mantiene) y fanfarria. */
    @Override
    public void alMostrar() {
        GameState estado = GameState.getInstance();
        int antes = estado.getVidas();
        estado.ganarVida();
        vidaGanada = estado.getVidas() > antes;
        ctx.getAudio().sonarVictoria();
    }

    public boolean isVidaGanada() {
        return vidaGanada;
    }

    public boolean puedeContinuar() {
        return getTiempo() >= ESPERA_CONTINUAR;
    }

    // ------------------------------------------------------------------
    //  Pasos de la plantilla
    // ------------------------------------------------------------------

    @Override
    protected void actualizar(float delta) {
        // la animacion depende de getTiempo(), que la plantilla ya va sumando
    }

    @Override
    protected Color colorLimpieza() {
        return new Color(0.08f, 0.06f, 0f, 1f);
    }

    @Override
    protected float brilloFondo() {
        return 0.45f;
    }

    @Override
    protected void dibujarFormas(ShapeRenderer shape) {
        float t = getTiempo();
        float entrada = Math.min(1f, t / 0.5f);
        // resplandor dorado que late
        float latido = 0.5f + 0.5f * (float) Math.sin(t * 3f);
        shape.setColor(1f, 0.75f, 0f, entrada * (0.10f + 0.10f * latido));
        shape.rect(0, 0, Config.ANCHO, Config.ALTO);
        // barras doradas arriba y abajo
        float grosor = 24f * suave(entrada);
        shape.setColor(1f, 0.84f, 0f, 1f);
        shape.rect(0, Config.ALTO - grosor, Config.ANCHO, grosor);
        shape.rect(0, 0, Config.ANCHO, grosor);
        // confeti de colores cayendo (los colores de las fichas)
        for (int i = 0; i < CONFETIS; i++) {
            float y = ((cy[i] - cv[i] * t) % Config.ALTO + Config.ALTO) % Config.ALTO;
            Color c = Block.colorPorValor(cc[i]);
            shape.setColor(c.r, c.g, c.b, 0.9f);
            shape.rect(cx[i], y, ct[i], ct[i]);
        }
        // panel oscuro detras del texto
        shape.setColor(0f, 0f, 0f, 0.45f);
        shape.rect(120, 105, 560, 270);
    }

    @Override
    protected void dibujarTextos(SpriteBatch batch) {
        BitmapFont grande = ctx.getFontTitulo();
        BitmapFont medio = ctx.getFontMedio();
        float t = getTiempo();
        float centro = Config.ANCHO / 2f;
        float pulso = 1f + 0.05f * (float) Math.sin(t * 5f);
        float caida = 9f - 3f * suave(Math.min(1f, t / 0.4f)); // cae desde grande

        textoCentrado(batch, grande, "2048!", caida * pulso, 1f, 0.84f, 0f, Math.min(1f, t / 0.2f), centro, 360);
        if (t >= 0.5f) {
            textoCentrado(batch, medio, "LLEGASTE A 2048 PUNTOS", 2.0f, 1f, 1f, 1f, 1f, centro, 265);
        }
        if (t >= 0.8f) {
            String premio = vidaGanada ? "Recompensa: +1 VIDA" : "Recompensa: ya tienes todas las vidas";
            textoCentrado(batch, medio, premio, 1.7f, 0.4f, 1f, 0.5f, 1f, centro, 220);
        }
        if (t >= 1.1f) {
            textoCentrado(batch, medio, "Sigue en modo infinito: el puntaje no tiene limite", 1.3f,
                    0.85f, 0.85f, 1f, 1f, centro, 180);
        }
        if (puedeContinuar()) {
            float parpadeo = 0.35f + 0.65f * (0.5f + 0.5f * (float) Math.sin(t * 5f));
            textoCentrado(batch, medio, "Presiona ENTER para seguir jugando", 1.6f, 1f, 1f, 1f, parpadeo, centro, 135);
        }
    }

    @Override
    protected void procesarEntrada() {
        if (puedeContinuar() && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            ctx.irA(volverA); // vuelve a la misma partida, tal como estaba
        }
    }
}
