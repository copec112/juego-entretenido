package com.mygdx.game;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.builder.LevelBuilder;
import com.mygdx.game.entities.BlockGrid;
import com.mygdx.game.entities.Direccion;
import com.mygdx.game.entities.Paddle;
import com.mygdx.game.entities.PingBall;
import com.mygdx.game.managers.GameState;
import com.mygdx.game.screens.PantallaGameOver;
import com.mygdx.game.strategy.GestorDificultad;


public class MyGdxGame extends ApplicationAdapter {
	private static final int COLUMNAS = 10;
	private static final int FILAS_GRILLA = 7; // filas totales de la matriz de bloques
	private static final int PELOTA_RADIO = 10;
	private static final int VEL_X_BASE = 5;
	private static final int VEL_Y_BASE = 7;
	private static final int ANCHO = Config.ANCHO;
	private static final int ALTO = Config.ALTO;

    private OrthographicCamera camera;
	private FitViewport viewport; // escala el juego a cualquier ventana / pantalla completa
	private SpriteBatch batch;	   
	private BitmapFont font;
	private BitmapFont fontBloques; // fuente chica para los numeros de los bloques
	private BitmapFont fontTitulo;  // fuentes grandes para la pantalla de game over
	private BitmapFont fontMedio;
	private ShapeRenderer shape;
	private ArrayList<PingBall> balls = new ArrayList<PingBall>();
	private Paddle pad;
	private BlockGrid blocks;
	private GameState estado;
	private GestorDificultad dificultad = new GestorDificultad(); // contexto del patron Strategy
	private int velocidadExtra; // velocidad extra actual segun la dificultad
	private Random random = new Random();
	private PantallaGameOver pantallaGameOver = new PantallaGameOver();
	private boolean gameOver; // true mientras se muestra la pantalla de game over
    
		@Override
		public void create () {	
			camera = new OrthographicCamera();
		    camera.setToOrtho(false, ANCHO, ALTO);
		    viewport = new FitViewport(ANCHO, ALTO, camera);
		    viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true); // ajusta la vista desde el inicio
		    batch = new SpriteBatch();
		    font = new BitmapFont();
		    font.getData().setScale(3, 2);
		    fontBloques = new BitmapFont();
		    fontTitulo = new BitmapFont();
		    fontMedio = new BitmapFont();
		    suavizar(fontTitulo); // los textos grandes se ven mas lisos con filtro lineal
		    suavizar(fontMedio);
		    estado = GameState.getInstance();
		    estado.reiniciar();
		    crearBloques();
			
		    shape = new ShapeRenderer();
		    pad = new Paddle(ANCHO/2-50,40,100,10);
		    servirPelotas();
		}
		/**
		 * Crea la matriz de bloques del nivel actual: parte con algunos bloques al azar
		 * (11 en el nivel 1, 2 mas por cada nivel). Las 2 filas de abajo quedan
		 * libres para poder mover los bloques.
		 */
		public void crearBloques() {
			// patron Builder: LevelBuilder arma la matriz del nivel
			blocks = new LevelBuilder()
					.conTamanio(FILAS_GRILLA, COLUMNAS)
					.paraNivel(estado.getNivel())
					.build();
		}

		private void suavizar(BitmapFont f) {
			f.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		}

		/** Termina la partida: guarda el record y muestra la pantalla de game over. */
		private void iniciarGameOver() {
			boolean record = estado.terminarPartida();
			pantallaGameOver.iniciar(estado.getPuntaje(), estado.getNivel(), estado.getMejorPuntaje(), record);
			gameOver = true;
		}

		/** Vuelve a jugar desde cero (puntaje, vidas y nivel). El record de la sesion se mantiene. */
		private void reiniciarPartida() {
			Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
			estado.reiniciar();
			crearBloques();
			servirPelotas(); // sin puntaje: vuelve a 1 pelota y velocidad normal
			gameOver = false;
		}

		/** Un fotograma de la pantalla de game over; ENTER (pasado un momento) reinicia la partida. */
		private void renderGameOver() {
			pantallaGameOver.actualizar(Gdx.graphics.getDeltaTime());
			Gdx.gl.glClearColor(0.05f, 0f, 0f, 1f);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			prepararVista();
			Gdx.gl.glEnable(GL20.GL_BLEND); // para las transparencias del fondo
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			shape.begin(ShapeRenderer.ShapeType.Filled);
			pantallaGameOver.dibujarFondo(shape, ANCHO, ALTO);
			shape.end();
			camera.update();
			batch.setProjectionMatrix(camera.combined);
			batch.begin();
			pantallaGameOver.dibujarTextos(batch, fontTitulo, fontMedio, ANCHO, ALTO);
			batch.end();
			Gdx.gl.glDisable(GL20.GL_BLEND);
			if (pantallaGameOver.puedeReiniciar() && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
				reiniciarPartida();
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
			return new PingBall(pad.getX()+pad.getWidth()/2-5, pad.getY()+pad.getHeight()+11, PELOTA_RADIO,
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
				b.setXY(pad.getX()+pad.getWidth()/2-5+separacion, pad.getY()+pad.getHeight()+11);
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
		public void dibujaTextos() {
			//actualizar matrices de la cámara
			camera.update();
			//actualizar 
			batch.setProjectionMatrix(camera.combined);
			batch.begin();
			//dibujar textos
			font.draw(batch, "Puntos: " + estado.getPuntaje(), 10, 25);
			font.draw(batch, "Vidas : " + estado.getVidas(), ANCHO-220, 25);
			// numeros de los bloques
			blocks.drawValores(batch, fontBloques);
			batch.end();
		}	
		
		@Override
		public void resize (int width, int height) {
			viewport.update(width, height, true); // mantiene la proporcion 800x480 con barras negras
		}

		/** Prepara la camara y el ShapeRenderer para dibujar en el mundo de 800x480. */
		private void prepararVista() {
			viewport.apply();
			camera.update();
			shape.setProjectionMatrix(camera.combined);
		}

		@Override
		public void render () {
			if (gameOver) {
				renderGameOver();
				return;
			}
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			prepararVista();
	        shape.begin(ShapeRenderer.ShapeType.Filled);
	        pad.draw(shape);
	        // monitorear inicio del juego: pelotas quietas sobre la paddle
	        manejarPelotasQuietas();
	        for (PingBall b : balls) {
	        	if (!b.estaQuieto()) b.update();
	        }
	        // cada pelota que se va por abajo cuesta una vida y vuelve a la paddle
	        // Si todavia queda otra pelota jugando, la nueva sale sola; si no, espera el ESPACIO.
	        boolean sigueJugando = false;
	        for (PingBall b : balls) {
	        	if (!b.estaQuieto() && b.getY()>=0) sigueJugando = true;
	        }
	        for (int i = 0; i < balls.size(); i++) {
	        	if (balls.get(i).getY()<0) {
	        		estado.perderVida();
	        		balls.set(i, nuevaPelota(!sigueJugando));
	        	}
	        }
	        // verificar game over: sin vidas se muestra la pantalla de game over
	        if (estado.getVidas()<=0) {
	        	shape.end();
	        	iniciarGameOver();
	        	return;
	        }
	        // verificar si el nivel se terminó
	        if (blocks.estaVacia()) {
	        	estado.subirNivel();
	        	crearBloques();
	        	servirPelotas();
	        }    	
	        // teclas W, A, S, D: mover los bloques
	        moverBloquesConTeclas();
	        //dibujar bloques y revisar colisiones de las pelotas
	        blocks.draw(shape);
	        for (PingBall b : balls) {
	        	blocks.checkCollision(b);
	        }
	        // sacar los bloques destruidos y sumar su valor al puntaje
	        estado.sumarPuntos(blocks.eliminarDestruidos());
	        // con el nuevo puntaje, subir la dificultad si corresponde
	        actualizarDificultad();
	        
	        for (PingBall b : balls) {
	        	b.checkCollision(pad);
	        	b.draw(shape);
	        }
	        
	        shape.end();
	        dibujaTextos();
		}
		
		@Override
		public void dispose () {

		}
	}

