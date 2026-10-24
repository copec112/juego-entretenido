package com.mygdx.game.strategy;

/**
 * Modo normal: a los 400 puntos la pelota va mas rapida y entra una segunda;
 * a los 1000 una tercera y a los 2048 una cuarta (maximo).
 */
public class DificultadNormal implements EstrategiaDificultad {

    @Override
    public String getNombre() {
        return "Normal";
    }

    @Override
    public int pelotasPara(int puntaje) {
        if (puntaje >= 2048) return 4;
        if (puntaje >= 1000) return 3;
        if (puntaje >= 400) return 2;
        return 1;
    }

    @Override
    public int velocidadExtraPara(int puntaje) {
        return puntaje >= 400 ? 1 : 0;
    }
}
