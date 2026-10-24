package com.mygdx.game.screens;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.Config;
import com.mygdx.game.builder.LevelBuilder;
import com.mygdx.game.entities.BlockGrid;
import com.mygdx.game.entities.Direccion;
import com.mygdx.game.entities.Paddle;
import com.mygdx.game.entities.PingBall;
import com.mygdx.game.managers.GameState;
import com.mygdx.game.strategy.GestorDificultad;

/**
 * La partida: paleta, pelotas, matriz de bloques, puntaje y dificultad.
 * Completa los pasos de la plantilla (Pantalla) con la logica del juego.
 */
public class PantallaJuego extends Pantalla {
    private static final int COLUMNAS = 10;
    private static final int FILAS_GRILLA = 7; // filas totales de la matriz de bloques
    private static final int PELOTA_RADIO = 10;
    private static final int VEL_X_BASE = 5;
    private static final int VEL_Y_BASE = 7;

    private final ArrayList<PingBall> balls = new ArrayList<PingBall>();
    private final Paddle pad;
    private BlockGrid blocks;
    private final GameState estado;
    private final GestorDificultad dificultad = new GestorDificultad(); // contexto del patron Strategy
    private int velocidadExtra; // velocidad extra actual segun la dificultad
    private final Random random = new Random();

    /** Empieza una partida nueva: puntaje, vidas y nivel en su valor inicial. */
    public PantallaJuego(Contexto ctx) {
        super(ctx);
        estado = GameState.getInstance();
        estado.reiniciar();
        pad = new Paddle(Config.ANCHO / 2 - 50, 40, 100, 10);
        crearBloques();
        servirPelotas(); // sin puntaje: 1 pelota y velocidad normal
    }

    @Override
    public void alMostrar() {
        ctx.getAudio().iniciarMusica(); // vuelve la musica (se pausa en el game over)
    }

    /**
     * Crea la matriz de bloques del nivel actual: parte con algunos bloques al azar
     * (11 en el nivel 1, 2 mas por cada nivel). Las 2 filas de abajo quedan
     * libres para poder mover los bloques.
     */
    private void crearBloques() {
        // patron Builder: LevelBuilder arma la matriz del nivel
        blocks = new LevelBuilder()
                .conTamanio(FILAS_GRILLA, COLUMNAS)
                .paraNivel(estado.getNivel())
                .build();
    }

    // ------------------------------------------------------------------
    //  Paso 1 de la plantilla: logica
    // ------------------------------------------------------------------

    @Override
    protected void actualizar(float delta) {
        pad.actualizar(); // flechas: mover la paleta
        // pelotas quietas sobre la paddle: esperan el ESPACIO
        manejarPelotasQuietas();
        for (PingBall b : balls) {
            if (!b.estaQuieto()) b.update();
        }
        // cada pelota que se va por abajo cuesta una vida y vuelve a la paddle
        // Si todavia queda otra pelota jugando, la nueva sale sola; si no, espera el ESPACIO.
        boolean sigueJugando = false;
        for (PingBall b : balls) {
            if (!b.estaQuieto() && b.getY() >= 0) sigueJugando = true;
        }
        for (int i = 0; i < balls.size(); i++) {
            if (balls.get(i).getY() < 0) {
                estado.perderVida();
                ctx.getAudio().sonarVida();
                balls.set(i, nuevaPelota(!sigueJugando));
            }
        }
        // sin vidas: pantalla de game over
        if (estado.getVidas() <= 0) {
            ctx.irA(new PantallaGameOver(ctx));
            return;
        }
        // si el nivel se termino, empieza el siguiente
        if (blocks.estaVacia()) {
            estado.subirNivel();
            crearBloques();
            servirPelotas();
        }
        // teclas W, A, S, D: mover los bloques
        moverBloquesConTeclas();
        // colisiones de las pelotas con los bloques
        for (PingBall b : balls) {
            blocks.checkCollision(b);
        }
        // sacar los bloques destruidos y sumar su valor al puntaje
        int puntos = blocks.eliminarDestruidos();
        if (puntos > 0) {
            estado.sumarPuntos(puntos);
            ctx.getAudio().sonarRomper();
        }
        // con el nuevo puntaje, subir la dificultad si corresponde
        actualizarDificultad();
        for (PingBall b : balls) {
            b.checkCollision(pad);
        }
    }

    /** Deja las pelotas que corresponden al puntaje actual, quietas sobre la paddle. */
    private void servirPelotas() {
        dificultad.actualizar(estado.getPuntaje());
        velocidadExtra = dificultad.getVelocidadExtra();
        balls.clear();
        for (int i = 0; i < dificultad.getPelotas(); i++) {
            balls.add(nuevaPelota(true));
        }
    }

    /** Pelota nueva sobre la paddle, con la velocidad de la dificultad actual. */
    private PingBall nuevaPelota(boolean quieta) {
        int sentido = (quieta || random.nextBoolean()) ? 1 : -1;
        return new PingBall(pad.getX() + pad.getWidth() / 2 - 5, pad.getY() + pad.getHeight() + 11, PELOTA_RADIO,
                sentido * (VEL_X_BASE + velocidadExtra), VEL_Y_BASE + velocidadExtra, quieta);
    }

    /** Pelotas quietas: siguen a la paddle (separadas entre si) hasta que se aprieta ESPACIO. */
    private void manejarPelotasQuietas() {
        int quietas = 0;
        for (PingBall b : balls) {
            if (b.estaQuieto()) quietas++;
        }
        boolean lanzar = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        int k = 0;
        for (PingBall b : balls) {
            if (!b.estaQuieto()) continue;
            int separacion = (int) ((k - (quietas - 1) / 2f) * 30);
            b.setXY(pad.getX() + pad.getWidth() / 2 - 5 + separacion, pad.getY() + pad.getHeight() + 11);
            if (lanzar) {
                b.setDireccionX(k % 2 == 0 ? 1 : -1); // se reparten hacia los dos lados
                b.setEstaQuieto(false);
            }
            k++;
        }
    }

    /**
     * La dificultad evoluciona sola con el puntaje: el gestor cambia de estrategia, se ajusta
     * la velocidad de las pelotas y, si hace falta, entra una pelota nueva en movimiento.
     */
    private void actualizarDificultad() {
        dificultad.actualizar(estado.getPuntaje());
        int extra = dificultad.getVelocidadExtra();
        if (extra != velocidadExtra) {
            velocidadExtra = extra;
            for (PingBall b : balls) {
                b.setVelocidad(VEL_X_BASE + extra, VEL_Y_BASE + extra);
            }
        }
        while (balls.size() < dificultad.getPelotas()) {
            balls.add(nuevaPelota(false));
        }
    }

    /** True si al menos una pelota ya fue lanzada (no esta quieta sobre la paddle). */
    private boolean hayPelotaEnMovimiento() {
        for (PingBall b : balls) {
            if (!b.estaQuieto()) return true;
        }
        return false;
    }

    /**
     * W, A, S, D mueven los bloques (estilo 2048); cada movimiento valido agrega un bloque nuevo.
     * Solo se puede mover mientras hay una pelota en juego: antes de lanzarla, los bloques no se mueven.
     */
    private void moverBloquesConTeclas() {
        if (!hayPelotaEnMovimiento()) return;
        Direccion dir = null;
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) dir = Direccion.ARRIBA;
        else if (Gdx.input.isKeyJustPressed(Input.Keys.S)) dir = Direccion.ABAJO;
        else if (Gdx.input.isKeyJustPressed(Input.Keys.A)) dir = Direccion.IZQUIERDA;
        else if (Gdx.input.isKeyJustPressed(Input.Keys.D)) dir = Direccion.DERECHA;
        if (dir != null) {
            // fusionar bloques NO da puntos: el puntaje sube solo al romper bloques con la pelota
            blocks.mover(dir);
        }
    }

    // ------------------------------------------------------------------
    //  Pasos de dibujo de la plantilla
    // ------------------------------------------------------------------

    @Override
    protected float brilloFondo() {
        return 0.8f; // un poco mas oscuro para que resalten los bloques
    }

    @Override
    protected void dibujarFormas(ShapeRenderer shape) {
        pad.draw(shape);
        blocks.draw(shape);
        for (PingBall b : balls) {
            b.draw(shape);
        }
    }

    @Override
    protected void dibujarTextos(SpriteBatch batch) {
        ctx.getFontHud().draw(batch, "Puntos: " + estado.getPuntaje(), 10, 25);
        ctx.getFontHud().draw(batch, "Vidas : " + estado.getVidas(), Config.ANCHO - 220, 25);
        textoCentrado(batch, ctx.getFontMedio(), "Nivel " + estado.getNivel(), 1.4f,
                0.8f, 0.8f, 0.9f, 1f, Config.ANCHO / 2f, 22);
        // numeros de los bloques
        blocks.drawValores(batch, ctx.getFontBloques());
    }

    @Override
    protected void procesarEntrada() {
        // la entrada de la partida (paleta, ESPACIO y WASD) se procesa en actualizar()
    }
}
