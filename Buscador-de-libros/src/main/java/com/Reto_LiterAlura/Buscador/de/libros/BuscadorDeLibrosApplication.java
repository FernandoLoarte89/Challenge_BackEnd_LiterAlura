package com.Reto_LiterAlura.Buscador.de.libros;

import com.Reto_LiterAlura.Buscador.de.libros.Repository.AutorRepository;
import com.Reto_LiterAlura.Buscador.de.libros.Servicios.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BuscadorDeLibrosApplication implements CommandLineRunner {

	@Autowired
	private AutorRepository repository;

	public static void main(String[] args) {
		SpringApplication.run(BuscadorDeLibrosApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal(repository);
		principal.mostrarMenu();
	}
}
