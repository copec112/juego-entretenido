package com.mygdx.game.strategy;

/** Desde los 1000 puntos: entra una tercera pelota. */
public class DificultadAvanzada extends DificultadBase {

    public DificultadAvanzada() {
        super("Avanzada", 1000);
    }

    @Override
    public void aplicar(ControlPelotas juego) {
        asegurarPelotas(juego, 3);
        juego.fijarVelocidadExtra(1);
    }
}