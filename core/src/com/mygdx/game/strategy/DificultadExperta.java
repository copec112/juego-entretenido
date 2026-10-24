package com.mygdx.game.strategy;

/** Desde los 2048 puntos: entra una cuarta pelota (maximo). */
public class DificultadExperta implements EstrategiaDificultad {

    @Override
    public String getNombre() {
        return "Experta";
    }

    @Override
    public int getPuntajeMinimo() {
        return 2048;
    }

    @Override
    public int getPelotas() {
        return 4;
    }

    @Override
    public int getVelocidadExtra() {
        return 1;
    }
}
