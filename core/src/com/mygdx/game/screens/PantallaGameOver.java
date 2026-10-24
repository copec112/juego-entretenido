package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.Config;
import com.mygdx.game.managers.GameState;

import java.util.Random;

/**
 * Pantalla de game over, pensada para que el final de la partida impacte:
 *
 *  - un resplandor rojo que late y dos barras que entran por arriba y abajo
 *  - el titulo "GAME OVER" cae grande, tiembla y despues late
 *  - el puntaje final sube contando desde 0
 *  - aparece el nivel alcanzado y un "NUEVO RECORD!" parpadeante (o el mejor puntaje)
 *  - el aviso "Presiona ENTER" parpadea cuando ya se puede reiniciar
 *
 * Todo depende del tiempo transcurrido desde que empezo el game over.
 * Completa los pasos de la plantilla (Pantalla).
 */
public class PantallaGameOver extends Pantalla {
    private static final float ESPERA_REINICIO = 1.2f;   // segundos antes de aceptar ENTER
    private static final float INICIO_PUNTAJE = 0.9f;    // cuando empieza a contar el puntaje
    private static final float DURACION_CONTEO = 1.4f;   // cuanto dura el conteo

    private int puntaje;
    private int nivel;
    private int mejorPuntaje;
    private boolean nuevoRecord;
    private final Random random = new Random();

    public PantallaGameOver(Contexto ctx) {
        super(ctx);
    }

    /** Al llegar: guarda el record, calla la musica y suena el golpe final. */
    @Override
    public void alMostrar() {
        GameState estado = GameState.getInstance();
        nuevoRecord = estado.terminarPartida();
        puntaje = estado.getPuntaje();
        nivel = estado.getNivel();
        mejorPuntaje = estado.getMejorPuntaje();
        ctx.getAudio().pausarMusica();
        ctx.getAudio().sonarGameOver();
    }

    /** Evita reiniciar sin querer si el jugador seguia apretando teclas al morir. */
    public boolean puedeReiniciar() {
        return getTiempo() >= ESPERA_REINICIO;
    }

    public boolean isNuevoRecord() {
        return nuevoRecord;
    }

    /** Puntaje que se esta mostrando ahora (sube de 0 al puntaje final). */
    public int getPuntajeMostrado() {
        if (getTiempo() < INICIO_PUNTAJE) return 0;
        float p = Math.min(1f, (getTiempo() - INICIO_PUNTAJE) / DURACION_CONTEO);
        return (int) (puntaje * suave(p));
    }

    // ------------------------------------------------------------------
    //  Pasos de la plantilla
    // ------------------------------------------------------------------

    @Override
    protected void actualizar(float delta) {
        // todo depende del tiempo, que la plantilla ya va sumando
    }

    @Override
    protected Color colorLimpieza() {
        return new Color(0.05f, 0f, 0f, 1f);
    }

    @Override
    protected float brilloFondo() {
        return 0.35f; // el fondo se oscurece para que resalte el rojo
    }

    /** Resplandor rojo, barras y linea. */
    @Override
    protected void dibujarFormas(ShapeRenderer shape) {
        int ancho = Config.ANCHO;
        int alto = Config.ALTO;
        float entrada = Math.min(1f, getTiempo() / 0.4f);

        // resplandor rojo que late
        float latido = 0.5f + 0.5f * (float) Math.sin(getTiempo() * 4f);
        shape.setColor(0.7f, 0f, 0f, entrada * (0.10f + 0.10f * latido));
        shape.rect(0, 0, ancho, alto);

        // barras que entran por arriba y por abajo
        float grosor = 40f * suave(entrada);
        shape.setColor(0.75f, 0.05f, 0.05f, 1f);
        shape.rect(0, alto - grosor, ancho, grosor);
        shape.rect(0, 0, ancho, grosor);

        // linea que se abre desde el centro, bajo el titulo
        float apertura = suave(Math.min(1f, Math.max(0f, (getTiempo() - 0.3f) / 0.5f)));
        float largo = ancho * 0.7f * apertura;
        shape.setColor(1f, 0.2f, 0.2f, 1f);
        shape.rect((ancho - largo) / 2f, alto * 0.62f, largo, 3f);
    }

    @Override
    protected void dibujarTextos(SpriteBatch batch) {
        BitmapFont grande = ctx.getFontTitulo();
        BitmapFont medio = ctx.getFontMedio();
        float centro = Config.ANCHO / 2f;
        float alto = Config.ALTO;

        // titulo: cae desde grande, tiembla al principio y despues late
        float escala;
        if (getTiempo() < 0.35f) {
            escala = 9f - 4f * suave(getTiempo() / 0.35f);
        } else {
            escala = 5f + 0.15f * (float) Math.sin((getTiempo() - 0.35f) * 6f);
        }
        float temblor = Math.max(0f, 1f - getTiempo() / 0.6f) * 14f;
        float dx = (random.nextFloat() - 0.5f) * 2f * temblor;
        float dy = (random.nextFloat() - 0.5f) * 2f * temblor;
        textoCentrado(batch, grande, "GAME OVER", escala, 1f, 0.12f, 0.12f, Math.min(1f, getTiempo() / 0.25f),
                centro + dx, alto * 0.84f + dy);

        // puntaje final contando hacia arriba
        if (getTiempo() >= INICIO_PUNTAJE) {
            textoCentrado(batch, medio, "PUNTAJE  " + getPuntajeMostrado(), 2.6f, 1f, 1f, 1f, 1f, centro, alto * 0.54f);
        }

        // nivel alcanzado
        float tNivel = INICIO_PUNTAJE + 0.6f;
        if (getTiempo() >= tNivel) {
            textoCentrado(batch, medio, "Nivel alcanzado: " + nivel, 1.8f, 0.8f, 0.8f, 0.8f,
                    Math.min(1f, (getTiempo() - tNivel) / 0.4f), centro, alto * 0.42f);
        }

        // record (parpadeante y dorado) o mejor puntaje
        float tRecord = INICIO_PUNTAJE + DURACION_CONTEO;
        if (getTiempo() >= tRecord) {
            if (nuevoRecord) {
                float parpadeo = 0.55f + 0.45f * (float) Math.sin(getTiempo() * 9f);
                float pulso = 2.4f + 0.2f * (float) Math.sin(getTiempo() * 9f);
                textoCentrado(batch, medio, "NUEVO RECORD!", pulso, 1f, 0.84f, 0f, parpadeo, centro, alto * 0.32f);
            } else {
                textoCentrado(batch, medio, "Mejor puntaje: " + mejorPuntaje, 1.8f, 1f, 0.84f, 0f, 1f, centro, alto * 0.32f);
            }
        }

        // aviso para reiniciar (parpadea)
        if (puedeReiniciar()) {
            float parpadeo = 0.35f + 0.65f * (0.5f + 0.5f * (float) Math.sin(getTiempo() * 5f));
            textoCentrado(batch, medio, "Presiona ENTER para jugar de nuevo", 1.6f, 1f, 1f, 1f, parpadeo, centro, alto * 0.17f);
        }
    }

    /** ENTER (pasado un momento) empieza una partida nueva. El record de la sesion se mantiene. */
    @Override
    protected void procesarEntrada() {
        if (puedeReiniciar() && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            ctx.irA(new PantallaJuego(ctx));
        }
    }
}
