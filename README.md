## CHALLENGE BACK-END LITERALURA :fa-leanpub:
========================================
###Descripción y alcances  :fa-list-ul:
-------------

Desafío de programación, para construir un catálogo de libros: el LiterAlura. Se realizará solicitudes a una API de libros, se manipulará datos JSON, se guardará en una base de datos y se filtraár y mostrará los libros y autores.

- Configuración del Ambiente Java;
- Creación del Proyecto;
- Consumo de la API;
- Análisis de la Respuesta JSON;
- Inserción y consulta en la base de datos;
- Exibición de resultados a los usuarios;

###Funciona con  :fa-hand-o-down:
-------------
Una API externa para obtener información sobre libros y autores: https://gutendex.com/ También almacena información en una base de datos local para que puedas acceder a ella incluso sin conexión a internet.

###Funcionalidades del Proyecto :fa-list-ol:

1 - BUSCAR LIBROS POR TÍTULO
2 - BUSCAR AUTOR POR NOMBRE
3 - LISTAR LIBROS REGISTRADOS
4 - LISTAR AUTORES REGISTRADOS
5 - LISTAR AUTORES VIVOS EN EL AÑO
6 - LISTAR LIBROS POR IDIOMA
7 - LISTAR AUTORES POR AÑO
8 - TOP 10 LIBROS MÁS BUSCADOS

###Tecnologías utilizadas :fa-cogs:

- Java 17
- Spring Boot
- Spring Data JPA
- PostgreSQL

##Como empezar  :fa-desktop:
------------
###Clona el repositorio :fa-pencil-square-o:

`<link>` : <https://github.com/FernandoLoarte89/Challenge_BackEnd_LiterAlura.git>

###Instala las dependencias :fa-pencil-square-o:

###Ejecuta la aplicación :fa-pencil-square-o:

####Java

```javascript
package com.Reto_LiterAlura.Buscador.de.libros.Servicios;

import com.Reto_LiterAlura.Buscador.de.libros.Modelo.*;
import com.Reto_LiterAlura.Buscador.de.libros.Repository.AutorRepository;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

    private Scanner entrada = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private String URL_BASE = "https://gutendex.com/books/";
    private AutorRepository repository;

    public Principal(AutorRepository repository) {
        this.repository = repository;
    }

    public void mostrarMenu() {
        var opcion = -1;
        var menu = """
                **************************************
                ELIJA LA OPCIÓN A TRAVÉS DE SU NÚMERO:
                **************************************
                1 - BUSCAR LIBROS POR TÍTULO
                2 - BUSCAR AUTOR POR NOMBRE
                3 - LISTAR LIBROS REGISTRADOS
                4 - LISTAR AUTORES REGISTRADOS
                5 - LISTAR AUTORES VIVOS EN EL AÑO
                6 - LISTAR LIBROS POR IDIOMA
                7 - LISTAR AUTORES POR AÑO
                8 - TOP 10 LIBROS MÁS BUSCADOS
                **************************************
                0 - SALIR
                **************************************
                ELIJA UNA OPCIÓN:
                """;

        while (opcion != 0) {
            System.out.println(menu);
            try {
                opcion = Integer.valueOf(entrada.nextLine());
                switch (opcion) {
                    case 1:
                        buscarLibroPorTitulo();
                        break;
                    case 2:
                        buscarAutorPorNombre();
                        break;
                    case 3:
                        listarLibrosRegistrados();
                        break;
                    case 4:
                        listarAutoresRegistrados();
                        break;
                    case 5:
                        listarAutoresVivos();
                        break;
                    case 6:
                        listarLibrosPorIdioma();
                        break;
                    case 7:
                        listarAutoresPorAnio();
                        break;
                    case 8:
                        top10Libros();
                        break;
                    case 0:
                        System.out.println("GRACIAS VUELVA PRONTO...");
                        System.out.println("CERRANDO LA APLICACIÓN...");
                        break;
                    default:
                        System.out.println("OPCIÓN NO VALIDA...");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("OPCIÓN NO VÁLIDA..." + e.getMessage());

            }
        }
    }
```

####principal.java - BuscarLibroPorTitulo

```javascript
public void buscarLibroPorTitulo() {
        System.out.println("INTRODUSCA EL NOMBRE DEL LIBRO QUE DESEA BUSCAR: ");
        var nombre = entrada.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + nombre.replace(" ", "+").toLowerCase());
        var datos = conversor.obtenerDatos(json, Datos.class);
        Optional<DatosLibro> libroBuscado = datos.libros().stream()
                .findFirst();

        if (libroBuscado.isPresent()) {
            System.out.println(
                    "\n*********** LIBRO  ***********" +
                            "\nTÍTULO: " + libroBuscado.get().titulo() +
                            "\nAUTOR: " + libroBuscado.get().autores().stream()
                            .map(a -> a.nombre()).limit(1).collect(Collectors.joining()) +
                            "\nIDIOMA: " + libroBuscado.get().idiomas().stream().collect(Collectors.joining()) +
                            "\nNRO DE DESCARGAS: " + libroBuscado.get().descargas() +
                            "\n***********\n"
            );

            try {
                List<Libro> libroEncontrado = libroBuscado.stream().map(a -> new Libro(a)).collect(Collectors.toList());
                Autor autorAPI = libroBuscado.stream().
                        flatMap(l -> l.autores().stream()
                                .map(a -> new Autor(a)))
                        .collect(Collectors.toList()).stream().findFirst().get();
                Optional<Autor> autorBD = repository.buscarAutorPorNombre(libroBuscado.get().autores().stream()
                        .map(a -> a.nombre())
                        .collect(Collectors.joining()));
                Optional<Libro> libroOptional = repository.buscarLibroPorNombre(nombre);
                if (libroOptional.isPresent()) {
                    System.out.println("El libro ya está guardado en la BD.");
                } else {
                    Autor autor;
                    if (autorBD.isPresent()) {
                        autor = autorBD.get();
                        System.out.println("EL autor ya esta guardado en la BD");
                    } else {
                        autor = autorAPI;
                        repository.save(autor);
                    }
                    autor.setLibros(libroEncontrado);
                    repository.save(autor);
                }
            } catch (Exception e) {
                System.out.println("ADVERTENCIA...! " + e.getMessage());
            }

        } else {
            System.out.println("LIBRO NO ENCONTRADO!");
        }
    }
```

![badge literalura](https://github.com/FernandoLoarte89/Challenge_BackEnd_LiterAlura/assets/157989840/c1c0b859-54df-46aa-be99-f3b35e283fd4)

###Final
=============================================
