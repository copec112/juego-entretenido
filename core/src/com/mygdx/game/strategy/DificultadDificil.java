package com.mygdx.game.strategy;

/** Modo dificil: las pelotas entran antes (200, 600 y 1200 puntos) y se aceleran dos veces. */
public class DificultadDificil implements EstrategiaDificultad {

    @Override
    public String getNombre() {
        return "Dificil";
    }

    @Override
    public int pelotasPara(int puntaje) {
        if (puntaje >= 1200) return 4;
        if (puntaje >= 600) return 3;
        if (puntaje >= 200) return 2;
        return 1;
    }

    @Override
    public int velocidadExtraPara(int puntaje) {
        if (puntaje >= 1000) return 2;
        if (puntaje >= 200) return 1;
        return 0;
    }
}
