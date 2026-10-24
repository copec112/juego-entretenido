package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.mygdx.game.screens.Contexto;
import com.mygdx.game.screens.Pantalla;
import com.mygdx.game.screens.PantallaInicio;

/**
 * Punto de entrada del juego. Crea los recursos compartidos (Contexto) y deja que la
 * pantalla activa (inicio, juego o game over) dibuje cada fotograma.
 */
public class MyGdxGame extends ApplicationAdapter {
	private Contexto contexto;

	@Override
	public void create () {
		contexto = new Contexto();
		contexto.irA(new PantallaInicio(contexto));
	}

	@Override
	public void resize (int width, int height) {
		contexto.redimensionar(width, height);
	}

	@Override
	public void render () {
		contexto.renderizar(Gdx.graphics.getDeltaTime());
	}

	/** Pantalla que se esta mostrando ahora. */
	public Pantalla getPantallaActual() {
		return contexto.getPantallaActual();
	}

	@Override
	public void dispose () {
		contexto.dispose();
	}
}

