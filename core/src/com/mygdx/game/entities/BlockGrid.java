package com.mygdx.game.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mygdx.game.Config;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Grilla (matriz) de bloques, al estilo del juego 2048.
 *
 * Cada bloque esta en celdas[fila][columna] (la fila 0 es la de arriba) y
 * una celda vacia es null. La posicion en pantalla de cada bloque se calcula
 * siempre a partir de su celda, y mover(Direccion) desliza todos los bloques
 * por la matriz fusionando los vecinos de igual valor.
 */
public class BlockGrid {
    private static final int BLOCK_WIDTH = 70;
    private static final int BLOCK_HEIGHT = 26;
    private static final int SEPARACION = 10;
    private static final int MARGEN_X = 5;
    /** Al fusionar dos bloques y llegar a este valor, cae un premio de vida. */
    public static final int VALOR_PREMIO = 256;

    private final Block[][] celdas;
    private final int filas;
    private final int columnas;
    private int restantes;
    private OyentePremio oyentePremio; // se avisa cuando una fusion llega a VALOR_PREMIO
    private final Random random = new Random();
    private final GlyphLayout layout = new GlyphLayout(); // para centrar los numeros

    /**
     * Crea una matriz vacia. Los bloques los pone quien la construye con
     * colocarBloque (en el juego, LevelBuilder).
     *
     * @param filas    filas totales de la matriz
     * @param columnas columnas totales de la matriz
     */
    public BlockGrid(int filas, int columnas) {
        this.filas = filas;
        this.columnas = columnas;
        this.celdas = new Block[filas][columnas];
    }

    /** Pone un bloque con ese valor en la celda (fila, columna). La celda debe existir y estar vacia. */
    public void colocarBloque(int fila, int columna, int valor) {
        if (fila < 0 || fila >= filas || columna < 0 || columna >= columnas) {
            throw new IllegalArgumentException("La celda (" + fila + ", " + columna + ") no existe");
        }
        if (celdas[fila][columna] != null) {
            throw new IllegalArgumentException("La celda (" + fila + ", " + columna + ") ya tiene un bloque");
        }
        celdas[fila][columna] = crearBloque(fila, columna, valor);
        restantes++;
    }

    /** Quien quiera enterarse de los premios se registra aqui (null = nadie). */
    public void setOyentePremio(OyentePremio oyente) {
        this.oyentePremio = oyente;
    }

    private Block crearBloque(int fila, int columna, int valor) {
        return new Block(xCelda(columna), yCelda(fila), BLOCK_WIDTH, BLOCK_HEIGHT, valor);
    }

    /** Los bloques nuevos parten con 2 o 4 (50% cada uno). */
    private int valorInicial() {
        return random.nextBoolean() ? 2 : 4;
    }

    // Posicion en pantalla de una celda
    private int xCelda(int columna) {
        return MARGEN_X + columna * (BLOCK_WIDTH + SEPARACION);
    }

    private int yCelda(int fila) {
        return Config.ALTO - (fila + 1) * (BLOCK_HEIGHT + SEPARACION);
    }

    public int getFilas() { return filas; }
    public int getColumnas() { return columnas; }

    /** Bloque en (fila, columna), o null si esta fuera de la grilla o la celda esta vacia. */
    public Block getBlock(int fila, int columna) {
        if (fila < 0 || fila >= filas || columna < 0 || columna >= columnas) {
            return null;
        }
        return celdas[fila][columna];
    }

    // ------------------------------------------------------------------
    //  Movimiento estilo 2048
    // ------------------------------------------------------------------

    /**
     * Desliza todos los bloques en la direccion dada: se pegan al borde y los
     * vecinos de igual valor se fusionan en uno de valor doble (cada bloque se
     * fusiona como maximo una vez por movimiento). Si el movimiento cambio algo,
     * aparece un bloque nuevo (2 o 4) en una celda vacia al azar.
     *
     * Fusionar NO da puntos: el puntaje solo sube cuando la pelota rompe un
     * bloque (ver eliminarDestruidos()).
     */
    public void mover(Direccion dir) {
        boolean cambio = false;
        List<Block> llegaronAlPremio = new ArrayList<Block>(); // solo se usa dentro de este metodo

        boolean vertical = (dir == Direccion.ARRIBA || dir == Direccion.ABAJO);
        int lineas = vertical ? columnas : filas;   // filas o columnas a procesar
        int largo = vertical ? filas : columnas;    // celdas por linea

        for (int l = 0; l < lineas; l++) {
            // 1) bloques de la linea, ordenados desde el borde hacia el que se mueven
            List<Block> lista = new ArrayList<Block>();
            for (int i = 0; i < largo; i++) {
                Block b = getEnLinea(dir, l, i);
                if (b != null) {
                    lista.add(b);
                }
            }

            // 2) fusionar vecinos iguales
            List<Block> resultado = new ArrayList<Block>();
            int i = 0;
            while (i < lista.size()) {
                Block b = lista.get(i);
                if (i + 1 < lista.size() && lista.get(i + 1).getValor() == b.getValor()) {
                    b.setValor(b.getValor() * 2);
                    if (b.getValor() == VALOR_PREMIO) {
                        llegaronAlPremio.add(b);
                    }
                    restantes--;
                    cambio = true;
                    i += 2;
                } else {
                    i++;
                }
                resultado.add(b);
            }

            // 3) volver a poner la linea en la matriz, pegada al borde
            for (int j = 0; j < largo; j++) {
                Block nuevo = (j < resultado.size()) ? resultado.get(j) : null;
                if (getEnLinea(dir, l, j) != nuevo) {
                    cambio = true;
                }
                setEnLinea(dir, l, j, nuevo);
            }
        }

        reposicionar();
        if (oyentePremio != null) {
            for (Block b : llegaronAlPremio) {
                oyentePremio.alGenerarPremio(b.getX(), b.getY());
            }
        }
        if (cambio) {
            agregarBloqueNuevo();
        }
    }

    /** Celda numero i de la linea l, contando desde el borde hacia el que se mueve. */
    private Block getEnLinea(Direccion dir, int l, int i) {
        switch (dir) {
            case IZQUIERDA: return celdas[l][i];
            case DERECHA:   return celdas[l][columnas - 1 - i];
            case ARRIBA:    return celdas[i][l];
            default:        return celdas[filas - 1 - i][l]; // ABAJO
        }
    }

    private void setEnLinea(Direccion dir, int l, int i, Block b) {
        switch (dir) {
            case IZQUIERDA: celdas[l][i] = b; break;
            case DERECHA:   celdas[l][columnas - 1 - i] = b; break;
            case ARRIBA:    celdas[i][l] = b; break;
            default:        celdas[filas - 1 - i][l] = b; break; // ABAJO
        }
    }

    /** Coloca cada bloque en la posicion de pantalla que le corresponde a su celda. */
    private void reposicionar() {
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                if (celdas[f][c] != null) {
                    celdas[f][c].setXY(xCelda(c), yCelda(f));
                }
            }
        }
    }

    /** Crea un bloque (2 o 4) en una celda vacia al azar, si queda alguna. */
    private void agregarBloqueNuevo() {
        List<int[]> vacias = new ArrayList<int[]>();
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                if (celdas[f][c] == null) {
                    vacias.add(new int[] { f, c });
                }
            }
        }
        if (vacias.isEmpty()) {
            return;
        }
        int[] celda = vacias.get(random.nextInt(vacias.size()));
        celdas[celda[0]][celda[1]] = crearBloque(celda[0], celda[1], valorInicial());
        restantes++;
    }

    // ------------------------------------------------------------------
    //  Dibujo, colisiones y estado
    // ------------------------------------------------------------------

    public void draw(ShapeRenderer shape) {
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                if (celdas[f][c] != null) {
                    celdas[f][c].draw(shape);
                }
            }
        }
    }

    /** Dibuja el numero de cada bloque, centrado. Se llama con el SpriteBatch abierto. */
    public void drawValores(SpriteBatch batch, BitmapFont font) {
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                Block b = celdas[f][c];
                if (b == null) continue;
                font.setColor(b.getColorTexto());
                layout.setText(font, String.valueOf(b.getValor()));
                float tx = b.getX() + (b.getWidth() - layout.width) / 2;
                float ty = b.getY() + (b.getHeight() + layout.height) / 2;
                font.draw(batch, layout, tx, ty);
            }
        }
    }

    /**
     * La pelota solo choca con UN bloque por fotograma: el mas cercano a su centro.
     * (Antes chocaba con todos los que tocaba a la vez, y por eso "atravesaba" varios.)
     */
    public void checkCollision(PingBall ball) {
        Block masCercano = null;
        long mejorDistancia = Long.MAX_VALUE;
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                Block b = celdas[f][c];
                if (b != null && !b.isDestroyed() && ball.collidesWith(b)) {
                    long dx = b.getX() + b.getWidth() / 2 - ball.getCentroX();
                    long dy = b.getY() + b.getHeight() / 2 - ball.getCentroY();
                    long distancia = dx * dx + dy * dy;
                    if (distancia < mejorDistancia) {
                        mejorDistancia = distancia;
                        masCercano = b;
                    }
                }
            }
        }
        if (masCercano != null) {
            ball.checkCollision(masCercano);
        }
    }

    /** Saca de la grilla los bloques destruidos y devuelve los puntos que dan (la suma de sus valores). */
    public int eliminarDestruidos() {
        int puntos = 0;
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                if (celdas[f][c] != null && celdas[f][c].isDestroyed()) {
                    puntos += celdas[f][c].getValor();
                    celdas[f][c] = null;
                    restantes--;
                }
            }
        }
        return puntos;
    }

    public boolean estaVacia() {
        return restantes == 0;
    }
}