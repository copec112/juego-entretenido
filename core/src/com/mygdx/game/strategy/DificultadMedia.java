package com.mygdx.game.strategy;

/** Desde los 400 puntos: una pelota mas y un poco mas de velocidad. */
public class DificultadMedia extends DificultadBase {

    public DificultadMedia() {
        super("Media", 400);
    }

    @Override
    public void aplicar(ControlPelotas juego) {
        asegurarPelotas(juego, 2);
        juego.fijarVelocidadExtra(1);
    }
}
