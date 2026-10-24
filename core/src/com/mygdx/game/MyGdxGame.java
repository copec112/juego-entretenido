package com.mygdx.game;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.entities.BlockGrid;
import com.mygdx.game.entities.Direccion;
import com.mygdx.game.entities.Paddle;
import com.mygdx.game.entities.PingBall;
import com.mygdx.game.managers.GameState;
import com.mygdx.game.strategy.DificultadDificil;
import com.mygdx.game.strategy.DificultadFacil;
import com.mygdx.game.strategy.DificultadNormal;
import com.mygdx.game.strategy.EstrategiaDificultad;


public class MyGdxGame extends ApplicationAdapter {
	private static final int COLUMNAS = 10;
	private static final int FILAS_GRILLA = 7; // filas totales de la matriz de bloques
	private static final int BLOQUES_INICIALES = 8; // bloques con que parte el nivel 1
	private static final int MAX_BLOQUES_INICIALES = 30;
	private static final int PELOTA_RADIO = 10;
	private static final int VEL_X_BASE = 5;
	private static final int VEL_Y_BASE = 7;

    private OrthographicCamera camera;
	private SpriteBatch batch;	   
	private BitmapFont font;
	private BitmapFont fontBloques; // fuente chica para los numeros de los bloques
	private ShapeRenderer shape;
	private ArrayList<PingBall> balls = new ArrayList<PingBall>();
	private Paddle pad;
	private BlockGrid blocks;
	private GameState estado;
	private EstrategiaDificultad dificultad = new DificultadNormal(); // patron Strategy
	private int velocidadExtra; // velocidad extra actual segun la dificultad
	private Random random = new Random();
    
		@Override
		public void create () {	
			camera = new OrthographicCamera();
		    camera.setToOrtho(false, 800, 480);
		    batch = new SpriteBatch();
		    font = new BitmapFont();
		    font.getData().setScale(3, 2);
		    fontBloques = new BitmapFont();
		    estado = GameState.getInstance();
		    estado.reiniciar();
		    crearBloques();
			
		    shape = new ShapeRenderer();
		    pad = new Paddle(Gdx.graphics.getWidth()/2-50,40,100,10);
		    servirPelotas();
		}
		/**
		 * Crea la matriz de bloques del nivel actual: parte casi vacia, con pocos bloques
		 * al azar (8 en el nivel 1, 2 mas por cada nivel). Las 2 filas de abajo quedan
		 * libres para poder mover los bloques.
		 */
		public void crearBloques() {
			int cantidad = Math.min(BLOQUES_INICIALES + 2 * (estado.getNivel() - 1), MAX_BLOQUES_INICIALES);
			blocks = new BlockGrid(FILAS_GRILLA, COLUMNAS, cantidad, FILAS_GRILLA - 2);
		}

		/** Deja las pelotas que corresponden al puntaje actual, quietas sobre la paddle. */
		private void servirPelotas() {
			velocidadExtra = dificultad.velocidadExtraPara(estado.getPuntaje());
			balls.clear();
			int cantidad = dificultad.pelotasPara(estado.getPuntaje());
			for (int i = 0; i < cantidad; i++) {
				balls.add(nuevaPelota(true));
			}
		}

		/** Cambia la estrategia de dificultad en pleno juego; las pelotas vuelven a la paddle. */
		public void cambiarDificultad(EstrategiaDificultad nueva) {
			dificultad = nueva;
			servirPelotas();
		}

		/** Teclas 1, 2 y 3: elegir dificultad facil, normal o dificil. */
		private void elegirDificultadConTeclas() {
			if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) cambiarDificultad(new DificultadFacil());
			else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) cambiarDificultad(new DificultadNormal());
			else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) cambiarDificultad(new DificultadDificil());
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
		 * Aplica la dificultad segun el puntaje: ajusta la velocidad de las pelotas y, si
		 * corresponde, agrega una pelota nueva que sale en movimiento desde la paddle.
		 */
		private void actualizarDificultad() {
			int extra = dificultad.velocidadExtraPara(estado.getPuntaje());
			if (extra != velocidadExtra) {
				velocidadExtra = extra;
				for (PingBall b : balls) {
					b.setVelocidad(VEL_X_BASE + extra, VEL_Y_BASE + extra);
				}
			}
			while (balls.size() < dificultad.pelotasPara(estado.getPuntaje())) {
				balls.add(nuevaPelota(false));
			}
		}

		/** W, A, S, D mueven los bloques (estilo 2048); cada movimiento valido agrega un bloque nuevo. */
		private void moverBloquesConTeclas() {
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
			font.draw(batch, "Vidas : " + estado.getVidas(), Gdx.graphics.getWidth()-220, 25);
			// dificultad actual (texto chico, entre los puntos y las vidas)
			fontBloques.setColor(Color.WHITE);
			fontBloques.draw(batch, "Dificultad: " + dificultad.getNombre() + "  [1] [2] [3]", 300, 22);
			// numeros de los bloques
			blocks.drawValores(batch, fontBloques);
			batch.end();
		}	
		
		@Override
		public void render () {
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); 		
	        shape.begin(ShapeRenderer.ShapeType.Filled);
	        pad.draw(shape);
	        // teclas 1, 2, 3: cambiar la estrategia de dificultad
	        elegirDificultadConTeclas();
	        // monitorear inicio del juego: pelotas quietas sobre la paddle
	        manejarPelotasQuietas();
	        for (PingBall b : balls) {
	        	if (!b.estaQuieto()) b.update();
	        }
	        // cada pelota que se va por abajo cuesta una vida y vuelve a la paddle
	        for (int i = 0; i < balls.size(); i++) {
	        	if (balls.get(i).getY()<0) {
	        		estado.perderVida();
	        		balls.set(i, nuevaPelota(true));
	        	}
	        }
	        // verificar game over
	        if (estado.getVidas()<=0) {
	        	estado.reiniciar(); // game over: vuelve todo a cero (puntaje, vidas y nivel)
	        	crearBloques();
	        	servirPelotas();    // sin puntaje: vuelve a 1 pelota y velocidad normal
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

