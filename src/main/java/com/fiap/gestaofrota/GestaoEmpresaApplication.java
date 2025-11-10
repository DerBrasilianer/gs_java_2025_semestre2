package com.fiap.gestaofrota;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class GestaoEmpresaApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestaoEmpresaApplication.class, args);
	}

}
