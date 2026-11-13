package com.mygdx.game.strategy;

/** Desde los 2048 puntos: entra una cuarta pelota (maximo). */
public class DificultadExperta extends DificultadBase {

    public DificultadExperta() {
        super("Experta", 2048);
    }

    @Override
    public void aplicar(ControlPelotas juego) {
        asegurarPelotas(juego, 4);
        juego.fijarVelocidadExtra(1);
    }
}
