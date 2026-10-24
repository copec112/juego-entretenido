package com.mygdx.game.strategy;

/** Inicio del juego: una sola pelota a velocidad normal. */
public class DificultadBasica implements EstrategiaDificultad {

    @Override
    public String getNombre() {
        return "Basica";
    }

    @Override
    public int getPuntajeMinimo() {
        return 0;
    }

    @Override
    public int getPelotas() {
        return 1;
    }

    @Override
    public int getVelocidadExtra() {
        return 0;
    }
}