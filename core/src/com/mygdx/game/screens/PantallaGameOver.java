package com.mygdx.game.screens;

import java.util.Random;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

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
 */
public class PantallaGameOver {
    private static final float ESPERA_REINICIO = 1.2f;   // segundos antes de aceptar ENTER
    private static final float INICIO_PUNTAJE = 0.9f;    // cuando empieza a contar el puntaje
    private static final float DURACION_CONTEO = 1.4f;   // cuanto dura el conteo

    private float tiempo;
    private int puntaje;
    private int nivel;
    private int mejorPuntaje;
    private boolean nuevoRecord;

    private final GlyphLayout layout = new GlyphLayout(); // para centrar los textos
    private final Random random = new Random();

    /** Empieza la pantalla con los datos de la partida que acaba de terminar. */
    public void iniciar(int puntaje, int nivel, int mejorPuntaje, boolean nuevoRecord) {
        this.tiempo = 0f;
        this.puntaje = puntaje;
        this.nivel = nivel;
        this.mejorPuntaje = mejorPuntaje;
        this.nuevoRecord = nuevoRecord;
    }

    public void actualizar(float delta) {
        tiempo += delta;
    }

    /** Evita reiniciar sin querer si el jugador seguia apretando teclas al morir. */
    public boolean puedeReiniciar() {
        return tiempo >= ESPERA_REINICIO;
    }

    public boolean isNuevoRecord() {
        return nuevoRecord;
    }

    /** Puntaje que se esta mostrando ahora (sube de 0 al puntaje final). */
    public int getPuntajeMostrado() {
        if (tiempo < INICIO_PUNTAJE) return 0;
        float p = Math.min(1f, (tiempo - INICIO_PUNTAJE) / DURACION_CONTEO);
        return (int) (puntaje * suave(p));
    }

    // ------------------------------------------------------------------
    //  Dibujo
    // ------------------------------------------------------------------

    /** Fondo: resplandor rojo, barras y linea. Se llama con el ShapeRenderer abierto (modo Filled). */
    public void dibujarFondo(ShapeRenderer shape, int ancho, int alto) {
        float entrada = Math.min(1f, tiempo / 0.4f);

        // resplandor rojo que late
        float latido = 0.5f + 0.5f * (float) Math.sin(tiempo * 4f);
        shape.setColor(0.7f, 0f, 0f, entrada * (0.10f + 0.10f * latido));
        shape.rect(0, 0, ancho, alto);

        // barras que entran por arriba y por abajo
        float grosor = 40f * suave(entrada);
        shape.setColor(0.75f, 0.05f, 0.05f, 1f);
        shape.rect(0, alto - grosor, ancho, grosor);
        shape.rect(0, 0, ancho, grosor);

        // linea que se abre desde el centro, bajo el titulo
        float apertura = suave(Math.min(1f, Math.max(0f, (tiempo - 0.3f) / 0.5f)));
        float largo = ancho * 0.7f * apertura;
        shape.setColor(1f, 0.2f, 0.2f, 1f);
        shape.rect((ancho - largo) / 2f, alto * 0.62f, largo, 3f);
    }

    /** Textos. Se llama con el SpriteBatch abierto. */
    public void dibujarTextos(SpriteBatch batch, BitmapFont grande, BitmapFont medio, int ancho, int alto) {
        float centro = ancho / 2f;

        // titulo: cae desde grande, tiembla al principio y despues late
        float escala;
        if (tiempo < 0.35f) {
            escala = 9f - 4f * suave(tiempo / 0.35f);
        } else {
            escala = 5f + 0.15f * (float) Math.sin((tiempo - 0.35f) * 6f);
        }
        float temblor = Math.max(0f, 1f - tiempo / 0.6f) * 14f;
        float dx = (random.nextFloat() - 0.5f) * 2f * temblor;
        float dy = (random.nextFloat() - 0.5f) * 2f * temblor;
        texto(batch, grande, "GAME OVER", escala, 1f, 0.12f, 0.12f, Math.min(1f, tiempo / 0.25f),
                centro + dx, alto * 0.84f + dy);

        // puntaje final contando hacia arriba
        if (tiempo >= INICIO_PUNTAJE) {
            texto(batch, medio, "PUNTAJE  " + getPuntajeMostrado(), 2.6f, 1f, 1f, 1f, 1f, centro, alto * 0.54f);
        }

        // nivel alcanzado
        float tNivel = INICIO_PUNTAJE + 0.6f;
        if (tiempo >= tNivel) {
            texto(batch, medio, "Nivel alcanzado: " + nivel, 1.8f, 0.8f, 0.8f, 0.8f,
                    Math.min(1f, (tiempo - tNivel) / 0.4f), centro, alto * 0.42f);
        }

        // record (parpadeante y dorado) o mejor puntaje
        float tRecord = INICIO_PUNTAJE + DURACION_CONTEO;
        if (tiempo >= tRecord) {
            if (nuevoRecord) {
                float parpadeo = 0.55f + 0.45f * (float) Math.sin(tiempo * 9f);
                float pulso = 2.4f + 0.2f * (float) Math.sin(tiempo * 9f);
                texto(batch, medio, "NUEVO RECORD!", pulso, 1f, 0.84f, 0f, parpadeo, centro, alto * 0.32f);
            } else {
                texto(batch, medio, "Mejor puntaje: " + mejorPuntaje, 1.8f, 1f, 0.84f, 0f, 1f, centro, alto * 0.32f);
            }
        }

        // aviso para reiniciar (parpadea)
        if (puedeReiniciar()) {
            float parpadeo = 0.35f + 0.65f * (0.5f + 0.5f * (float) Math.sin(tiempo * 5f));
            texto(batch, medio, "Presiona ENTER para jugar de nuevo", 1.6f, 1f, 1f, 1f, parpadeo, centro, alto * 0.17f);
        }
    }

    /** Dibuja un texto centrado en "centroX"; "arriba" es la coordenada Y de su borde superior. */
    private void texto(SpriteBatch batch, BitmapFont fuente, String texto, float escala,
                       float r, float g, float b, float a, float centroX, float arriba) {
        fuente.getData().setScale(escala);
        fuente.setColor(r, g, b, a);   // el color se fija antes de medir el texto
        layout.setText(fuente, texto);
        fuente.draw(batch, layout, centroX - layout.width / 2f, arriba);
    }

    /** Curva "ease out": empieza rapido y frena al llegar (0 -> 1). */
    private static float suave(float p) {
        float q = 1f - p;
        return 1f - q * q * q;
    }
}
