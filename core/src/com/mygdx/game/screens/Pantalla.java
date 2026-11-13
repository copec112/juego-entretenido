package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.Config;

/**
 * Patron Template Method.
 *
 * renderizar() es el METODO PLANTILLA: fija el orden de los pasos de cada fotograma
 * y nadie puede cambiarlo (es final):
 *
 *   1. procesarEntrada()       -> leer las teclas de la pantalla   (abstracto)
 *   2. actualizar(delta)       -> logica de la pantalla            (abstracto)
 *   3. limpiar la pantalla     -> color liso                       (gancho, se puede cambiar)
 *   4. dibujarFondo()          -> imagen de fondo                  (gancho, se puede cambiar)
 *   5. dibujarFormas(shape)    -> figuras (rectangulos, pelotas)   (abstracto)
 *   6. dibujarTextos(batch)    -> textos                           (abstracto)
 *   7. teclas generales        -> M silencia el sonido             (igual para todas)
 *
 * Cada pantalla concreta (inicio, juego, game over) solo completa los pasos
 * abstractos y, si quiere, modifica los ganchos. Asi el "esqueleto" del fotograma
 * esta escrito una sola vez.
 */
public abstract class Pantalla {
    protected final Contexto ctx;
    private float tiempo; // segundos desde que se muestra la pantalla
    private final GlyphLayout layout = new GlyphLayout(); // para medir y centrar textos

    protected Pantalla(Contexto ctx) {
        this.ctx = ctx;
    }

    // ------------------------------------------------------------------
    //  Metodo plantilla
    // ------------------------------------------------------------------

    public final void renderizar(float delta) {
        tiempo += delta;
        procesarEntrada();                                  // 1
        actualizar(delta);                                  // 2
        limpiar();                                          // 3
        ctx.prepararVista();
        dibujarFondo();                                     // 4
        Gdx.gl.glEnable(GL20.GL_BLEND);                     // para las transparencias
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        ctx.getShape().begin(ShapeRenderer.ShapeType.Filled);
        dibujarFormas(ctx.getShape());                      // 5
        ctx.getShape().end();
        ctx.getBatch().begin();
        dibujarTextos(ctx.getBatch());                      // 6
        ctx.getBatch().end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {     // 7
            ctx.getAudio().alternarSilencio();
        }
    }

    /** Segundos transcurridos desde que se muestra la pantalla. */
    protected float getTiempo() {
        return tiempo;
    }

    // ------------------------------------------------------------------
    //  Pasos que cada pantalla debe completar
    // ------------------------------------------------------------------

    /** Lee las teclas y deja anotado que se pidio; no mueve nada del juego (eso es de actualizar). */
    protected abstract void procesarEntrada();

    protected abstract void actualizar(float delta);

    protected abstract void dibujarFormas(ShapeRenderer shape);

    protected abstract void dibujarTextos(SpriteBatch batch);

    // ------------------------------------------------------------------
    //  Ganchos: tienen una version por defecto que se puede cambiar
    // ------------------------------------------------------------------

    /** Se llama una vez al pasar a esta pantalla (ctx.irA). */
    public void alMostrar() { }

    /** Color con que se limpia la pantalla antes de dibujar. */
    protected Color colorLimpieza() {
        return Color.BLACK;
    }

    /** Que tan claro se ve el fondo (1 = normal, menos = mas oscuro). */
    protected float brilloFondo() {
        return 1f;
    }

    /** Dibuja la imagen de fondo a todo el mundo de 800x480 (si se pudo cargar). */
    protected void dibujarFondo() {
        if (ctx.getFondo() == null) return;
        float b = brilloFondo();
        SpriteBatch batch = ctx.getBatch();
        batch.begin();
        batch.setColor(b, b, b, 1f);
        batch.draw(ctx.getFondo(), 0, 0, Config.ANCHO, Config.ALTO);
        batch.setColor(1f, 1f, 1f, 1f);
        batch.end();
    }

    private void limpiar() {
        Color c = colorLimpieza();
        Gdx.gl.glClearColor(c.r, c.g, c.b, c.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    // ------------------------------------------------------------------
    //  Ayudas para dibujar texto
    // ------------------------------------------------------------------

    /** Texto centrado horizontalmente en centroX; "arriba" es la Y de su borde superior. */
    protected void textoCentrado(SpriteBatch batch, BitmapFont fuente, String texto, float escala,
                                 float r, float g, float b, float a, float centroX, float arriba) {
        fuente.getData().setScale(escala);
        fuente.setColor(r, g, b, a); // el color se fija antes de medir el texto
        layout.setText(fuente, texto);
        fuente.draw(batch, layout, centroX - layout.width / 2f, arriba);
    }

    /** Texto centrado horizontal y verticalmente en el punto (centroX, centroY). */
    protected void textoEnCentro(SpriteBatch batch, BitmapFont fuente, String texto, float escala,
                                 float r, float g, float b, float a, float centroX, float centroY) {
        fuente.getData().setScale(escala);
        fuente.setColor(r, g, b, a);
        layout.setText(fuente, texto);
        fuente.draw(batch, layout, centroX - layout.width / 2f, centroY + layout.height / 2f);
    }

    /** Texto que parte en la coordenada x (alineado a la izquierda). */
    protected void textoIzquierda(SpriteBatch batch, BitmapFont fuente, String texto, float escala,
                                  float r, float g, float b, float a, float x, float arriba) {
        fuente.getData().setScale(escala);
        fuente.setColor(r, g, b, a);
        layout.setText(fuente, texto);
        fuente.draw(batch, layout, x, arriba);
    }

    /** Curva "ease out": empieza rapido y frena al llegar (0 -> 1). */
    protected static float suave(float p) {
        float q = 1f - p;
        return 1f - q * q * q;
    }
}
