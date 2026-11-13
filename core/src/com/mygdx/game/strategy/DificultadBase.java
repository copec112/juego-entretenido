package com.mygdx.game.strategy;

/**
 * Parte comun de las estrategias: el nombre, el puntaje minimo y un
 * ayudante para completar las pelotas. Cada etapa decide en aplicar()
 * que hace con el juego.
 */
public abstract class DificultadBase implements EstrategiaDificultad {
    private final String nombre;
    private final int puntajeMinimo;

    protected DificultadBase(String nombre, int puntajeMinimo) {
        this.nombre = nombre;
        this.puntajeMinimo = puntajeMinimo;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public int getPuntajeMinimo() {
        return puntajeMinimo;
    }

    /** Agrega pelotas (quietas) hasta que haya al menos "cantidad". Nunca quita. */
    protected void asegurarPelotas(ControlPelotas juego, int cantidad) {
        while (juego.cantidadPelotas() < cantidad) {
            juego.agregarPelotaQuieta();
        }
    }
}
