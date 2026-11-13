package com.mygdx.game.screens;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.Config;
import com.mygdx.game.builder.DirectorNiveles;
import com.mygdx.game.entities.BlockGrid;
import com.mygdx.game.entities.Direccion;
import com.mygdx.game.entities.OyentePremio;
import com.mygdx.game.entities.Paddle;
import com.mygdx.game.entities.PingBall;
import com.mygdx.game.entities.VidaExtra;
import com.mygdx.game.managers.GameState;
import com.mygdx.game.strategy.ControlPelotas;
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
    private final ArrayList<VidaExtra> premios = new ArrayList<VidaExtra>(); // corazones que estan cayendo
    private float avisoVida;  // segundos que falta para que se apague el aviso "+1 VIDA"
    private final Paddle pad;
    private BlockGrid blocks;
    private final GameState estado;
    private final GestorDificultad dificultad = new GestorDificultad(); // contexto del patron Strategy
    private int velocidadExtra; // velocidad extra actual segun la dificultad
    private final Random random = new Random();
    private final DirectorNiveles director = new DirectorNiveles(); // patron Builder: sabe armar cada nivel

    // Lo que se pidio con el teclado en este fotograma (lo anota procesarEntrada, lo usa actualizar)
    private int sentidoPaleta;            // -1 izquierda, 1 derecha, 0 quieta
    private boolean lanzarPedido;         // ESPACIO apretado
    private Direccion direccionPedida;    // ultima tecla W/A/S/D recien apretada (null = ninguna)

    /** Cuando una fusion llega a 256, de ese bloque cae un corazon. */
    private final OyentePremio alGenerarPremio = new OyentePremio() {
        @Override
        public void alGenerarPremio(int x, int y) {
            premios.add(new VidaExtra(x + 23, y));
        }
    };

    /** Lo que la dificultad (Strategy) puede pedirle a esta partida. */
    private final ControlPelotas controlPelotas = new ControlPelotas() {
        @Override
        public int cantidadPelotas() {
            return balls.size();
        }

        @Override
        public void agregarPelotaQuieta() {
            balls.add(nuevaPelota(true));
        }

        @Override
        public void fijarVelocidadExtra(int extra) {
            velocidadExtra = extra;
            for (PingBall b : balls) {
                b.setVelocidad(VEL_X_BASE + extra, VEL_Y_BASE + extra);
            }
        }
    };

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
        // patron Builder: el director le pide al LevelBuilder cada paso de la matriz del nivel
        blocks = director.construirNivel(estado.getNivel(), FILAS_GRILLA, COLUMNAS);
        blocks.setOyentePremio(alGenerarPremio);
    }

    // ------------------------------------------------------------------
    //  Paso 2 de la plantilla: logica
    // ------------------------------------------------------------------

    @Override
    protected void actualizar(float delta) {
        pad.mover(sentidoPaleta); // flechas (ya leidas en procesarEntrada)
        // pelotas quietas sobre la paddle: esperan el ESPACIO
        manejarPelotasQuietas();
        for (PingBall b : balls) {
            if (!b.estaQuieto()) b.update();
        }
        // cada pelota que se va por abajo cuesta una vida y vuelve quieta a la paddle (espera el ESPACIO)
        for (int i = 0; i < balls.size(); i++) {
            if (balls.get(i).cayoAlVacio()) {
                estado.perderVida();
                ctx.getAudio().sonarVida();
                balls.set(i, nuevaPelota(true));
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
        // corazones que caen: si la paleta los atrapa, vida extra
        actualizarPremios(delta);
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
        // al llegar a 2048 puntos: pantalla de victoria, y despues se sigue jugando (modo infinito)
        if (estado.verificarMeta()) {
            ctx.irA(new PantallaVictoria(ctx, this));
            return;
        }
        // con el nuevo puntaje, subir la dificultad si corresponde
        actualizarDificultad();
        for (PingBall b : balls) {
            b.checkCollision(pad);
        }
    }

    /** Mueve los corazones que caen: la paleta los atrapa (+1 vida) o se pierden al caer. */
    private void actualizarPremios(float delta) {
        if (avisoVida > 0) avisoVida -= delta;
        for (int i = premios.size() - 1; i >= 0; i--) {
            VidaExtra v = premios.get(i);
            v.update();
            if (v.tocaA(pad)) {
                estado.ganarVida();
                ctx.getAudio().sonarPremio();
                avisoVida = 1.2f;
                premios.remove(i);
            } else if (v.estaPerdida()) {
                premios.remove(i);
            }
        }
    }

    /** Deja las pelotas que corresponden al puntaje actual, quietas sobre la paddle. */
    private void servirPelotas() {
        dificultad.actualizar(estado.getPuntaje());
        balls.clear();
        dificultad.aplicar(controlPelotas); // la estrategia pone sus pelotas y su velocidad
    }

    /** Pelota nueva sobre la paddle, con la velocidad de la dificultad actual. */
    private PingBall nuevaPelota(boolean quieta) {
        int sentido = (quieta || random.nextBoolean()) ? 1 : -1;
        return new PingBall(pad.getCentroX() - PELOTA_RADIO, pad.getY() + pad.getHeight() + 1, PELOTA_RADIO,
                sentido * (VEL_X_BASE + velocidadExtra), VEL_Y_BASE + velocidadExtra, quieta);
    }

    /** Pelotas quietas: siguen a la paddle (separadas entre si) hasta que se aprieta ESPACIO. */
    private void manejarPelotasQuietas() {
        int quietas = 0;
        for (PingBall b : balls) {
            if (b.estaQuieto()) quietas++;
        }
        int k = 0;
        for (PingBall b : balls) {
            if (!b.estaQuieto()) continue;
            int separacion = (int) ((k - (quietas - 1) / 2f) * 30);
            b.setXY(pad.getCentroX() - PELOTA_RADIO + separacion, pad.getY() + pad.getHeight() + 1);
            if (lanzarPedido) {
                b.setDireccionX(k % 2 == 0 ? 1 : -1); // se reparten hacia los dos lados
                b.setEstaQuieto(false);
            }
            k++;
        }
    }

    /**
     * La dificultad evoluciona sola con el puntaje: el gestor cambia de estrategia y la nueva
     * se aplica al juego (velocidad de las pelotas y, si hace falta, una pelota nueva quieta
     * sobre la paddle, que espera el ESPACIO igual que al empezar).
     */
    private void actualizarDificultad() {
        if (dificultad.actualizar(estado.getPuntaje())) {
            dificultad.aplicar(controlPelotas);
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
        Direccion dir = direccionPedida;
        direccionPedida = null; // la orden se usa una sola vez
        if (dir == null || !hayPelotaEnMovimiento()) return;
        // fusionar bloques NO da puntos: el puntaje sube solo al romper bloques con la pelota.
        // Si dos bloques llegan a 256, la grilla avisa (alGenerarPremio) y cae un corazon.
        blocks.mover(dir);
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
        for (VidaExtra v : premios) {
            v.draw(shape);
        }
        for (PingBall b : balls) {
            b.draw(shape);
        }
    }

    @Override
    protected void dibujarTextos(SpriteBatch batch) {
        ctx.getFontHud().draw(batch, "Puntos: " + estado.getPuntaje(), 10, 25);
        ctx.getFontHud().draw(batch, "Vidas : " + estado.getVidas(), Config.ANCHO - 220, 25);
        textoCentrado(batch, ctx.getFontMedio(), "Nivel " + estado.getNivel() + (estado.isMetaAlcanzada() ? "  -  Modo infinito" : ""), 1.4f,
                0.8f, 0.8f, 0.9f, 1f, Config.ANCHO / 2f, 22);
        // numeros de los bloques
        blocks.drawValores(batch, ctx.getFontBloques());
        // aviso al atrapar un corazon
        if (avisoVida > 0) {
            textoCentrado(batch, ctx.getFontMedio(), "+1 VIDA", 1.8f, 0.4f, 1f, 0.5f, Math.min(1f, avisoVida * 2f),
                    pad.getX() + pad.getWidth() / 2f, pad.getY() + 60 + (1.2f - avisoVida) * 30);
        }
    }

    /**
     * Paso 1 de la plantilla: lee el teclado y anota lo pedido. No mueve nada:
     * eso lo hace actualizar() con lo que quedo anotado aqui.
     */
    @Override
    protected void procesarEntrada() {
        sentidoPaleta = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) sentidoPaleta = -1;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) sentidoPaleta = 1;
        lanzarPedido = Gdx.input.isKeyPressed(Input.Keys.SPACE);

        direccionPedida = null;
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) direccionPedida = Direccion.ARRIBA;
        else if (Gdx.input.isKeyJustPressed(Input.Keys.S)) direccionPedida = Direccion.ABAJO;
        else if (Gdx.input.isKeyJustPressed(Input.Keys.A)) direccionPedida = Direccion.IZQUIERDA;
        else if (Gdx.input.isKeyJustPressed(Input.Keys.D)) direccionPedida = Direccion.DERECHA;
    }
}
