package com.mygdx.game.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * Musica de fondo y efectos de sonido del juego.
 *
 * Si algun archivo de audio no se encuentra (o el equipo no tiene audio),
 * ese sonido simplemente no suena: el juego sigue funcionando igual.
 * La tecla M lo silencia o lo vuelve a activar (alternarSilencio).
 */
public class GestorAudio {
    private static final float VOLUMEN_MUSICA = 0.35f;
    private static final float VOLUMEN_EFECTOS = 0.5f;

    private Music musica;
    private Sound romper;
    private Sound vida;
    private Sound gameOver;
    private Sound inicio;
    private Sound victoria;

    private boolean musicaActiva;  // la partida quiere que suene la musica
    private boolean silenciado;    // el jugador la silencio con M

    public GestorAudio() {
        try {
            musica = Gdx.audio.newMusic(Gdx.files.internal("musica.ogg"));
            musica.setLooping(true);
            musica.setVolume(VOLUMEN_MUSICA);
        } catch (Exception e) {
            musica = null; // sin musica
        }
        romper = cargarSonido("romper.wav");
        vida = cargarSonido("vida.wav");
        gameOver = cargarSonido("gameover.wav");
        inicio = cargarSonido("inicio.wav");
        victoria = cargarSonido("victoria.wav");
    }

    private Sound cargarSonido(String archivo) {
        try {
            return Gdx.audio.newSound(Gdx.files.internal(archivo));
        } catch (Exception e) {
            return null;
        }
    }

    // ---- musica ----

    public void iniciarMusica() {
        musicaActiva = true;
        aplicarMusica();
    }

    public void pausarMusica() {
        musicaActiva = false;
        aplicarMusica();
    }

    /** Suena solo si la partida la quiere y el jugador no la silencio. */
    private void aplicarMusica() {
        if (musica == null) return;
        boolean debeSonar = musicaActiva && !silenciado;
        if (debeSonar && !musica.isPlaying()) {
            musica.play();
        } else if (!debeSonar && musica.isPlaying()) {
            musica.pause();
        }
    }

    /** Tecla M: silencia todo o lo vuelve a activar. */
    public void alternarSilencio() {
        silenciado = !silenciado;
        aplicarMusica();
    }

    public boolean isSilenciado() {
        return silenciado;
    }

    // ---- efectos ----

    public void sonarRomper()   { reproducir(romper); }
    public void sonarVida()     { reproducir(vida); }
    public void sonarGameOver() { reproducir(gameOver); }
    public void sonarInicio()   { reproducir(inicio); }
    public void sonarVictoria() { reproducir(victoria); }
    public void sonarPremio()   { reproducir(inicio); } // corazon atrapado

    private void reproducir(Sound s) {
        if (s != null && !silenciado) {
            s.play(VOLUMEN_EFECTOS);
        }
    }

    public void dispose() {
        if (musica != null) musica.dispose();
        if (romper != null) romper.dispose();
        if (vida != null) vida.dispose();
        if (gameOver != null) gameOver.dispose();
        if (inicio != null) inicio.dispose();
        if (victoria != null) victoria.dispose();
    }
}