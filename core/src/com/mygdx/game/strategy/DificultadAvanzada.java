package com.mygdx.game.strategy;

/** Desde los 1000 puntos: entra una tercera pelota. */
public class DificultadAvanzada implements EstrategiaDificultad {

    @Override
    public String getNombre() {
        return "Avanzada";
    }

    @Override
    public int getPuntajeMinimo() {
        return 1000;
    }

    @Override
    public int getPelotas() {
        return 3;
    }

    @Override
    public int getVelocidadExtra() {
        return 1;
    }
}