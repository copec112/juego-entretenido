package com.mygdx.game.strategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Contexto del patron Strategy.
 *
 * Guarda la estrategia de dificultad actual y delega en ella. Segun el
 * puntaje va cambiando de estrategia: mientras mas se juega, mas dificil.
 *
 * Para ajustar el juego basta con editar la lista de etapas: quitar una
 * estrategia baja el maximo de pelotas, y el puntaje en que entra cada una
 * esta en su propia clase (getPuntajeMinimo).
 */
public class GestorDificultad {
    private final List<EstrategiaDificultad> etapas = new ArrayList<EstrategiaDificultad>();
    private EstrategiaDificultad estrategia;

    public GestorDificultad() {
        // de la mas facil a la mas dificil (ordenadas por puntaje minimo)
        etapas.add(new DificultadBasica());
        etapas.add(new DificultadMedia());
        etapas.add(new DificultadAvanzada());
        etapas.add(new DificultadExperta());
        estrategia = etapas.get(0);
    }

    /**
     * Elige la estrategia que corresponde al puntaje: la ultima etapa cuyo
     * puntaje minimo ya se alcanzo. Devuelve true si la estrategia cambio.
     */
    public boolean actualizar(int puntaje) {
        EstrategiaDificultad elegida = etapas.get(0);
        for (EstrategiaDificultad e : etapas) {
            if (puntaje >= e.getPuntajeMinimo()) {
                elegida = e;
            }
        }
        boolean cambio = (elegida != estrategia);
        estrategia = elegida;
        return cambio;
    }

    public EstrategiaDificultad getEstrategia() {
        return estrategia;
    }

    // El contexto delega en la estrategia actual
    public int getPelotas() { return estrategia.getPelotas(); }
    public int getVelocidadExtra() { return estrategia.getVelocidadExtra(); }
}
