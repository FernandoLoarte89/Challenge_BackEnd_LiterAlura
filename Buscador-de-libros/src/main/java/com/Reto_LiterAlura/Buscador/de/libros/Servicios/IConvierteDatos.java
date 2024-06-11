package com.Reto_LiterAlura.Buscador.de.libros.Servicios;

public interface IConvierteDatos {
    <T> T obtenerDatos(String json, Class<T> clase);
}
