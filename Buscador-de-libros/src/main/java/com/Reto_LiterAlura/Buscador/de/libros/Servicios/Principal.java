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

    public void buscarAutorPorNombre() {
        System.out.println("INGRESE EN NOMBRE DEL AUTOR QUE DESEA BUSCAR:");
        var nombre = entrada.nextLine();
        Optional<Autor> autor = repository.buscarAutorPorNombre(nombre);
        if (autor.isPresent()) {
            System.out.println(
                    "\nAUTOR: " + autor.get().getNombre() +
                            "\nFECHA DE NACIMIENTO: " + autor.get().getNacimiento() +
                            "\nFECHA DE DECESO: " + autor.get().getFallecimiento() +
                            "\nLIBROS: " + autor.get().getLibros().stream()
                            .map(l -> l.getTitulo()).collect(Collectors.toList()) + "\n"
            );
        } else {
            System.out.println("EL AUTOR NO SE ENCUENTRA EN LA BASE DE DATOS...");
        }
    }

    public void listarLibrosRegistrados() {
        List<Libro> libros = repository.buscarTodosLosLibros();
        libros.forEach(l -> System.out.println(
                "*********** LIBRO ***********" +
                        "\nTÍTULO: " + l.getTitulo() +
                        "\nAUTOR: " + l.getAutor().getNombre() +
                        "\nIDIOMA: " + l.getIdioma().getIdioma() +
                        "\nNRO DE DESCARGAS: " + l.getDescargas() +
                        "\n***********\n"
        ));
    }

    public void listarAutoresRegistrados() {
        List<Autor> autores = repository.findAll();
        System.out.println();
        autores.forEach(l -> System.out.println(
                "AUTOR: " + l.getNombre() +
                        "\nFECHA DE NACIMIENTO: " + l.getNacimiento() +
                        "\nFECHA DE DECESO: " + l.getFallecimiento() +
                        "\nLIBROS: " + l.getLibros().stream()
                        .map(t -> t.getTitulo()).collect(Collectors.toList()) + "\n"
        ));
    }

    public void listarAutoresVivos() {
        System.out.println("INTRODUZCA EL AÑO QUE DESEA BUSCAR AUTOR(ES) VIVOS:");
        try {
            var fecha = Integer.valueOf(entrada.nextLine());
            List<Autor> autores = repository.buscarAutoresVivos(fecha);
            if (!autores.isEmpty()) {
                System.out.println();
                autores.forEach(a -> System.out.println(
                        "AUTOR: " + a.getNombre() +
                                "\nFECHA DE NACIMIENTO: " + a.getNacimiento() +
                                "\nFECHA DE DECESO: " + a.getFallecimiento() +
                                "\nLIBROS: " + a.getLibros().stream()
                                .map(l -> l.getTitulo()).collect(Collectors.toList()) + "\n"
                ));
            } else {
                System.out.println("NO HAY AUTORES VIVOS EN ESTE AÑO...");
            }
        } catch (NumberFormatException e) {
            System.out.println("INGRESAR UN AÑO VÁLIDO..." + e.getMessage());
        }
    }

    public void listarLibrosPorIdioma() {
        var menu = """
                ************************************************
                SELECCIONA EL IDIOMA DEL LIBRO QUE DESEA BUSCAR:
                ************************************************
                1 - ESPAÑOL
                2 - FRANCÉS
                3 - INGLÉS
                4 - PORTUGUÉZ
                ************************************************
                """;
        System.out.println(menu);

        var idioma = entrada.nextLine();
        if(idioma.equalsIgnoreCase("es") || idioma.equalsIgnoreCase("en") ||
                idioma.equalsIgnoreCase("fr") || idioma.equalsIgnoreCase("pt")){
            Idioma idioma1 = Idioma.fromString(idioma);
            List<Libro> libros = repository.buscarLibrosPorIdioma(idioma1);
            if(libros.isEmpty()){
                System.out.println("NO HAY LIBROS REGISTRADOS EN ESE IDIOMA...");
            } else{
                System.out.println();
                libros.forEach(l -> System.out.println(
                        "***** LIBRO *****" +
                                "\nTÍTULO: " + l.getTitulo() +
                                "\nAUTOR: " + l.getAutor().getNombre() +
                                "\nIDIOMA: " + l.getIdioma() +
                                "\nNRO DE DESCARGAS: " + l.getDescargas() +
                                "\n**********\n"
                ));
            }
        } else{
            System.out.println("INTRODUCE UNA OPCIÓN VÁLIDA");
        }
    }

    public void listarAutoresPorAnio() {
        var menu = """
                ********************************
                INGRESAR UNA OPCIÓN DE BUSQUEDA:
                ********************************
                1 - LISTAR AUTORES POR AÑO DE NACIMIENTO
                2 - LISTAR AUTORES POR AÑO DE DECESO
                ********************************
                """;
        System.out.println(menu);
        try {
            var opcion = Integer.valueOf(entrada.nextLine());
            switch (opcion) {
                case 1:
                    ListarAutoresPorNacimiento();
                    break;
                case 2:
                    ListarAutoresPorFallecimiento();
                    break;
                default:
                    System.out.println("OPCIÓN NO VÁLIDA...");
                    break;
            }
        } catch (NumberFormatException e) {
            System.out.println("OPCIÓN NO VÁLIDA: " + e.getMessage());
        }
    }

    public void ListarAutoresPorNacimiento() {
        System.out.println("INTRODUZCA EL AÑO DE NACIMIENTO DEL AUTOR: ");
        try {
            var nacimiento = Integer.valueOf(entrada.nextLine());
            List<Autor> autores = repository.listarAutoresPorFallecimiento(nacimiento);
            if (autores.isEmpty()) {
                System.out.println("NO EXÍSTEN AUTORES CON AÑO DE NACMINETO " + nacimiento);
            } else {
                System.out.println();
                autores.forEach(a -> System.out.println(
                        "AUTOR: " + a.getNombre() +
                                "\nFECHA DE NACIMIENTO: " + a.getNacimiento() +
                                "\nFECHA DE DECESO: " + a.getFallecimiento() +
                                "\nLIBROS: " + a.getLibros().stream().map(l -> l.getTitulo()).collect(Collectors.toList()) + "\n"
                ));
            }
        } catch (NumberFormatException e) {
            System.out.println("INGRESE UN AÑO VÁLIDO: " + e.getMessage());
        }
    }

    public void ListarAutoresPorFallecimiento() {
        System.out.println("INTRODUZCA EL AÑO DE NACIMIENTO DEL AUTOR: ");
        try {
            var fallecimiento = Integer.valueOf(entrada.nextLine());
            List<Autor> autores = repository.listarAutoresPorFallecimiento(fallecimiento);
            if (autores.isEmpty()) {
                System.out.println("NO EXÍSTEN AUTORES CON AÑO DE NACMINETO " + fallecimiento);
            } else {
                System.out.println();
                autores.forEach(a -> System.out.println(
                        "AUTOR: " + a.getNombre() +
                                "\nFECHA DE NACIMIENTO: " + a.getNacimiento() +
                                "\nFECHA DE DECESO: " + a.getFallecimiento() +
                                "\nLIBROS: " + a.getLibros().stream().map(l -> l.getTitulo()).collect(Collectors.toList()) + "\n"
                ));
            }
        } catch (NumberFormatException e) {
            System.out.println("INGRESE UN AÑO VÁLIDO: " + e.getMessage());
        }
    }

    public void top10Libros() {
        List<Libro> libros = repository.top10Libros();
        System.out.println();
        libros.forEach(l -> System.out.println(
                "***** LIBRO *****" +
                        "\nTÍTULO: " + l.getTitulo() +
                        "\nAUTOR: " + l.getAutor().getNombre() +
                        "\nIDIOMA: " + l.getIdioma().getIdioma() +
                        "\nNRO DE DESCARGAS: " + l.getDescargas() +
                        "\n**********\n"
        ));
    }
}