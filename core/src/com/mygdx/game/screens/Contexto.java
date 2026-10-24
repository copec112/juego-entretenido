package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.Config;
import com.mygdx.game.managers.GestorAudio;

/**
 * Recursos que comparten todas las pantallas (camara, pinceles, fuentes, fondo y audio)
 * y la pantalla que se esta mostrando. Las pantallas se cambian con irA(...).
 */
public class Contexto {
    private final OrthographicCamera camera;
    private final FitViewport viewport; // escala el juego a cualquier ventana / pantalla completa
    private final SpriteBatch batch;
    private final ShapeRenderer shape;
    private final BitmapFont fontHud;      // textos del juego (puntos y vidas)
    private final BitmapFont fontBloques;  // numeros de los bloques
    private final BitmapFont fontTitulo;   // textos grandes (se redimensionan con setScale)
    private final BitmapFont fontMedio;
    private Texture fondo;                 // imagen de fondo (null si no se encuentra)
    private final GestorAudio audio;
    private Pantalla actual;

    public Contexto() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Config.ANCHO, Config.ALTO);
        viewport = new FitViewport(Config.ANCHO, Config.ALTO, camera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        batch = new SpriteBatch();
        shape = new ShapeRenderer();

        fontHud = new BitmapFont();
        fontHud.getData().setScale(3, 2);
        fontBloques = new BitmapFont();
        fontTitulo = new BitmapFont();
        fontMedio = new BitmapFont();
        suavizar(fontTitulo); // los textos grandes se ven mas lisos con filtro lineal
        suavizar(fontMedio);

        try {
            fondo = new Texture("fondo.png");
            fondo.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        } catch (Exception e) {
            fondo = null; // sin imagen: se ve el color liso de fondo
        }
        audio = new GestorAudio();
    }

    private void suavizar(BitmapFont f) {
        f.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    /** Cambia a otra pantalla (la nueva se entera con alMostrar()). */
    public void irA(Pantalla nueva) {
        actual = nueva;
        nueva.alMostrar();
    }

    public Pantalla getPantallaActual() { return actual; }

    public void renderizar(float delta) {
        if (actual != null) {
            actual.renderizar(delta);
        }
    }

    public void redimensionar(int ancho, int alto) {
        viewport.update(ancho, alto, true); // mantiene la proporcion 800x480 con barras negras
    }

    /** Prepara la camara y los pinceles para dibujar en el mundo de 800x480. */
    public void prepararVista() {
        viewport.apply();
        camera.update();
        shape.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);
    }

    public SpriteBatch getBatch() { return batch; }
    public ShapeRenderer getShape() { return shape; }
    public BitmapFont getFontHud() { return fontHud; }
    public BitmapFont getFontBloques() { return fontBloques; }
    public BitmapFont getFontTitulo() { return fontTitulo; }
    public BitmapFont getFontMedio() { return fontMedio; }
    public Texture getFondo() { return fondo; }
    public GestorAudio getAudio() { return audio; }

    public void dispose() {
        audio.dispose();
        batch.dispose();
        shape.dispose();
        fontHud.dispose();
        fontBloques.dispose();
        fontTitulo.dispose();
        fontMedio.dispose();
        if (fondo != null) fondo.dispose();
    }
}
