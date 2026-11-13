package com.mygdx.game.strategy;

/** Inicio del juego: una sola pelota a velocidad normal. */
public class DificultadBasica extends DificultadBase {

    public DificultadBasica() {
        super("Basica", 0);
    }

    @Override
    public void aplicar(ControlPelotas juego) {
        asegurarPelotas(juego, 1);
        juego.fijarVelocidadExtra(0);
    }
}