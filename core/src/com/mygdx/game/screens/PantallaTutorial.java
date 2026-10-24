package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.Config;
import com.mygdx.game.entities.Block;
import com.mygdx.game.entities.VidaExtra;

/**
 * Tutorial de 3 paginas: objetivo, controles, y fusiones / premios / dificultad.
 * Se abre desde la pantalla de inicio con ESPACIO.
 *
 *   FLECHAS izquierda / derecha (o ESPACIO)  -> cambiar de pagina
 *   ENTER                                    -> empezar a jugar
 *   ESC                                      -> volver al inicio
 *
 * Completa los pasos de la plantilla (Pantalla).
 */
public class PantallaTutorial extends Pantalla {
    private static final int PAGINAS = 3;

    private int pagina; // 0, 1 o 2

    public PantallaTutorial(Contexto ctx) {
        super(ctx);
    }

    public int getPagina() {
        return pagina;
    }

    // ------------------------------------------------------------------
    //  Pasos de la plantilla
    // ------------------------------------------------------------------

    @Override
    protected void actualizar(float delta) {
        // el tutorial no tiene logica: solo cambia de pagina con las teclas
    }

    @Override
    protected float brilloFondo() {
        return 0.55f; // fondo mas oscuro para que se lea el texto
    }

    @Override
    protected void dibujarFormas(ShapeRenderer shape) {
        // panel oscuro detras del contenido
        shape.setColor(0f, 0f, 0.08f, 0.72f);
        shape.rect(50, 50, 700, 400);
        shape.setColor(1f, 0.84f, 0f, 0.9f);
        shape.rect(50, 392, 700, 2);

        if (pagina == 2) {
            // fila 1: dos bloques iguales se fusionan
            ficha(shape, 100, 330, 2);
            ficha(shape, 170, 330, 2);
            flecha(shape, 240, 347);
            ficha(shape, 305, 330, 4);
            // fila 2: 128 + 128 = 256 y cae un corazon
            ficha(shape, 100, 270, 128);
            ficha(shape, 170, 270, 128);
            flecha(shape, 240, 287);
            ficha(shape, 305, 270, 256);
            new VidaExtra(375, 275).draw(shape);
        }

        // puntitos de pagina
        for (int i = 0; i < PAGINAS; i++) {
            if (i == pagina) shape.setColor(1f, 0.84f, 0f, 1f);
            else shape.setColor(1f, 1f, 1f, 0.35f);
            shape.circle(Config.ANCHO / 2f - 20 + i * 20, 110, 5);
        }
    }

    /** Rectangulo de un bloque de 2048 con el color de su valor. */
    private void ficha(ShapeRenderer shape, float x, float y, int valor) {
        Color c = Block.colorPorValor(valor);
        shape.setColor(c.r, c.g, c.b, 1f);
        shape.rect(x, y, 60, 34);
    }

    /** Flecha hacia la derecha: una barra y una punta triangular. */
    private void flecha(ShapeRenderer shape, float x, float y) {
        shape.setColor(1f, 1f, 1f, 1f);
        shape.rect(x, y - 3, 34, 6);
        shape.triangle(x + 34, y + 11, x + 34, y - 11, x + 52, y);
    }

    @Override
    protected void dibujarTextos(SpriteBatch batch) {
        BitmapFont f = ctx.getFontMedio();
        float centro = Config.ANCHO / 2f;

        switch (pagina) {
            case 0: paginaObjetivo(batch, f, centro); break;
            case 1: paginaControles(batch, f, centro); break;
            default: paginaFusiones(batch, f, centro); break;
        }

        // pie de pagina
        textoCentrado(batch, f, "Pagina " + (pagina + 1) + " de " + PAGINAS, 1.2f, 0.8f, 0.8f, 0.9f, 1f, centro, 100);
        float parpadeo = 0.5f + 0.5f * (0.5f + 0.5f * (float) Math.sin(getTiempo() * 4f));
        textoCentrado(batch, f, "FLECHAS: cambiar pagina     ENTER: jugar     ESC: volver", 1.2f, 1f, 1f, 1f, parpadeo, centro, 70);
    }

    private void titulo(SpriteBatch batch, BitmapFont f, String texto, float centro) {
        textoCentrado(batch, f, texto, 2.4f, 1f, 0.84f, 0f, 1f, centro, 440);
    }

    private void linea(SpriteBatch batch, BitmapFont f, String texto, float y) {
        textoIzquierda(batch, f, texto, 1.35f, 1f, 1f, 1f, 1f, 85, y);
    }

    private void paginaObjetivo(SpriteBatch batch, BitmapFont f, float centro) {
        titulo(batch, f, "OBJETIVO", centro);
        textoCentrado(batch, f, "Llega a 2048 PUNTOS", 2.2f, 1f, 1f, 1f, 1f, centro, 375);
        linea(batch, f, "- Rompe bloques con la pelota: ganas tantos puntos", 320);
        linea(batch, f, "  como el numero que tiene el bloque.", 295);
        linea(batch, f, "- Fusionar bloques NO da puntos, solo los hace crecer.", 262);
        linea(batch, f, "- Si la pelota cae, pierdes una vida. Tienes 3.", 229);
        linea(batch, f, "- Sin vidas es GAME OVER. Rompe todos los bloques", 196);
        linea(batch, f, "  para pasar al siguiente nivel.", 171);
        linea(batch, f, "- Al llegar a 2048 ganas, y puedes seguir sin limite.", 138);
    }

    private void paginaControles(SpriteBatch batch, BitmapFont f, float centro) {
        titulo(batch, f, "CONTROLES", centro);
        String[][] teclas = {
            { "FLECHAS", "Mueven la paleta a izquierda y derecha" },
            { "ESPACIO", "Lanza la pelota (tambien las nuevas)" },
            { "W A S D", "Deslizan todos los bloques, como en 2048" },
            { "M", "Activa o silencia el sonido" },
        };
        for (int i = 0; i < teclas.length; i++) {
            float y = 365 - i * 48;
            textoIzquierda(batch, f, teclas[i][0], 1.5f, 1f, 0.84f, 0f, 1f, 85, y);
            textoIzquierda(batch, f, teclas[i][1], 1.35f, 1f, 1f, 1f, 1f, 245, y);
        }
        linea(batch, f, "Los bloques solo se mueven con la pelota en juego.", 170);
        linea(batch, f, "Cada movimiento agrega un bloque nuevo (2 o 4).", 143);
    }

    private void paginaFusiones(SpriteBatch batch, BitmapFont f, float centro) {
        titulo(batch, f, "FUSIONES Y PREMIOS", centro);
        // numeros de las fichas dibujadas en dibujarFormas
        numeroFicha(batch, f, 100, 330, 2);
        numeroFicha(batch, f, 170, 330, 2);
        numeroFicha(batch, f, 305, 330, 4);
        numeroFicha(batch, f, 100, 270, 64);
        numeroFicha(batch, f, 170, 270, 64);
        numeroFicha(batch, f, 305, 270, 128);
        textoIzquierda(batch, f, "Dos bloques iguales se fusionan", 1.3f, 0.9f, 0.9f, 1f, 1f, 425, 355);
        textoIzquierda(batch, f, "Al formar un 128 cae un corazon", 1.3f, 1f, 0.5f, 0.6f, 1f, 425, 295);
        linea(batch, f, "- Atrapa el corazon con la paleta: +1 vida (maximo 5).", 235);
        linea(batch, f, "- Con 2048 puntos ganas otra vida y sigues sin limite.", 205);
        linea(batch, f, "- La dificultad sube sola con el puntaje:", 172);
        textoIzquierda(batch, f, "400: 2 pelotas y mas velocidad  |  1000: 3 pelotas  |  2048: 4", 1.2f,
                0.85f, 0.85f, 1f, 1f, 105, 145);
    }

    private void numeroFicha(SpriteBatch batch, BitmapFont f, float x, float y, int valor) {
        boolean claro = valor <= 8 || valor >= 2048;
        float tono = claro ? 0.2f : 1f;
        textoEnCentro(batch, f, String.valueOf(valor), 1.5f, tono, tono, tono, 1f, x + 30, y + 17);
    }

    @Override
    protected void procesarEntrada() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            pagina = Math.min(PAGINAS - 1, pagina + 1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            pagina = Math.max(0, pagina - 1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            pagina = (pagina + 1) % PAGINAS; // ESPACIO avanza y vuelve al principio
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            ctx.getAudio().sonarInicio();
            ctx.irA(new PantallaJuego(ctx));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            ctx.irA(new PantallaInicio(ctx));
        }
    }
}
