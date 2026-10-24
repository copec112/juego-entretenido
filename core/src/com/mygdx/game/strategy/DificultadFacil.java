package com.mygdx.game.strategy;

/** Modo facil: como maximo 2 pelotas, y recien a los 800 puntos. */
public class DificultadFacil implements EstrategiaDificultad {

    @Override
    public String getNombre() {
        return "Facil";
    }

    @Override
    public int pelotasPara(int puntaje) {
        return puntaje >= 800 ? 2 : 1;
    }

    @Override
    public int velocidadExtraPara(int puntaje) {
        return puntaje >= 800 ? 1 : 0;
    }
}
