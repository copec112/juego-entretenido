package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.entities.BlockGrid;
import com.mygdx.game.entities.Paddle;
import com.mygdx.game.entities.PingBall;
import com.mygdx.game.managers.GameState;


public class MyGdxGame extends ApplicationAdapter {
	private static final int COLUMNAS = 10;

    private OrthographicCamera camera;
	private SpriteBatch batch;	   
	private BitmapFont font;
	private BitmapFont fontBloques; 
	private ShapeRenderer shape;
	private PingBall ball;
	private Paddle pad;
	private BlockGrid blocks;
	private GameState estado;
    
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
		    crearBloques(2+estado.getNivel());
			
		    shape = new ShapeRenderer();
		    ball = new PingBall(Gdx.graphics.getWidth()/2-10, 41, 10, 5, 7, true);
		    pad = new Paddle(Gdx.graphics.getWidth()/2-50,40,100,10);
		}
		public void crearBloques(int filas) {
			blocks = new BlockGrid(filas, COLUMNAS);
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
			// numeros de los bloques
			blocks.drawValores(batch, fontBloques);
			batch.end();
		}	
		
		@Override
		public void render () {
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); 		
	        shape.begin(ShapeRenderer.ShapeType.Filled);
	        pad.draw(shape);
	        // monitorear inicio del juego
	        if (ball.estaQuieto()) {
	        	ball.setXY(pad.getX()+pad.getWidth()/2-5, pad.getY()+pad.getHeight()+11);
	        	if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) ball.setEstaQuieto(false);
	        }else ball.update();
	        //verificar si se fue la bola x abajo
	        if (ball.getY()<0) {
	        	estado.perderVida();
	        	ball = new PingBall(pad.getX()+pad.getWidth()/2-5, pad.getY()+pad.getHeight()+11, 10, 5, 7, true);
	        }
	        // verificar game over
	        if (estado.getVidas()<=0) {
	        	estado.reiniciar(); // game over: vuelve todo a cero (puntaje, vidas y nivel)
	        	crearBloques(2+estado.getNivel());
	        	//ball = new PingBall(pad.getX()+pad.getWidth()/2-5, pad.getY()+pad.getHeight()+11, 10, 5, 7, true);	        	
	        }
	        // verificar si el nivel se terminó
	        if (blocks.estaVacia()) {
	        	estado.subirNivel();
	        	crearBloques(2+estado.getNivel());
	        	ball = new PingBall(pad.getX()+pad.getWidth()/2-5, pad.getY()+pad.getHeight()+11, 10, 5, 7, true);
	        }    	
	        //dibujar bloques y revisar colisiones de la pelota
	        blocks.draw(shape);
	        blocks.checkCollision(ball);
	        // sacar los bloques destruidos y sumar 1 punto por cada uno
	        estado.sumarPuntos(blocks.eliminarDestruidos());
	        
	        ball.checkCollision(pad);
	        ball.draw(shape);
	        
	        shape.end();
	        dibujaTextos();
		}
		
		@Override
		public void dispose () {

		}
	}

