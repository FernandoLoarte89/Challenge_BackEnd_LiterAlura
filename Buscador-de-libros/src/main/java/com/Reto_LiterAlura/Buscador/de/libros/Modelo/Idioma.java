package com.Reto_LiterAlura.Buscador.de.libros.Modelo;

public enum Idioma {
    ES("es"),
    FR("fr"),
    EN("en"),
    PT("pt");

    private String idioma;

    Idioma(String idioma) {
        this.idioma = idioma;
    }

    public static Idioma fromString(String text) {
        for (Idioma idioma : Idioma.values()) {
            if (idioma.idioma.equalsIgnoreCase(text)) {
                return idioma;
            }
        }
        throw new IllegalArgumentException("NINGÚN LENGUAJE ENCONTRADO " + text);
    }

    public String getIdioma(){
        return this.idioma;
    }
}
