package com.mygdx.game.strategy;

/** Desde los 400 puntos: una pelota mas y un poco mas de velocidad. */
public class DificultadMedia implements EstrategiaDificultad {

    @Override
    public String getNombre() {
        return "Media";
    }

    @Override
    public int getPuntajeMinimo() {
        return 400;
    }

    @Override
    public int getPelotas() {
        return 2;
    }

    @Override
    public int getVelocidadExtra() {
        return 1;
    }
}
