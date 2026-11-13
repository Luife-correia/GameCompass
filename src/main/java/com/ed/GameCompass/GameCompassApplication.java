package com.ed.GameCompass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//Ponto de entrada da aplicação Spring Boot do GameCompass.
//A anotação também habilita a configuração automática e a varredura dos
//componentes, como controllers e services, a partir deste pacote.
public class GameCompassApplication {

	//Inicializa o contexto Spring e inicia o servidor web embutido.
	public static void main(String[] args) {
		SpringApplication.run(GameCompassApplication.class, args);
	}

}
