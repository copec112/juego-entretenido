# Breakout 2048
Este repositorio contiene el código fuente de la lógica del juego Breakout 2048.
## Descripción del juego
El juego plantea un híbrido entre las mecánicas de Breakout (Arkanoid) y 2048. Lo que plantea una nueva capa de estrategia y complejidad. El objetivo principal es intentar llegar a una puntuación de 2048, mediante la fusión de bloques para aumentar su número y romperlos para añadirlos al puntaje. ¿Tienes la suficiente habilidad para llegar al 2048?

## Cómo compilar y ejecutar

### Requisitos previos:

- Tener instalado JAVA 8
- Tener instalado Eclipse IDE 2020-06 

### Pasos para compilar y ejecutar:

1. Descargar la carpeta root del juego desde clone -> zip o desde la carpeta de entrega.
2. Abrir Eclipse con un worspace vacio/nuevo.
3. Desde la pestaña ´file´ seleccionar la opcion ´import´.
4. Seleccionar ´Gradle/Existing Gradle Project´.
5. Seleccionar la carpeta del proyecto como el directorio raiz.
6. Desde el proyecto, dirigirse al modulo ´My GDX Game-desktop´.
7. Hacer clic derecho en ´com.mygdx.game/DesktopLauncher.java´
8. Seleccionar ´Run as a java aplication´.
9. Finalmente, con el programa abierto, seguir las instrucciones y disfrutar.

## Funcionalidades

### Funcionando correctamente:

- Lógica breakout (manejo de raqueta/plataforma, pelota, colisión y dificultad)
- Lógica 2048 (manejo de dirección de los bloques, colisión/combinación y dificultad)
- Interacción entre las dos lógicas.
- Dificultad dinámica.
- Manejo de menús simple.
 
### Problemas conocidos:

- La pelota puede quedarse atrapada entre la pantalla y la malla de bloques.
- A pesar del cambio de nivel, el contador de nivel de la parte inferior no cambia del 1 (Detalle estético, no mecánico). 

### A mejorar:

- Mejorar el balance de dificultad.
- Pulir la presentación de la aparición y movimiento de los bloques.
