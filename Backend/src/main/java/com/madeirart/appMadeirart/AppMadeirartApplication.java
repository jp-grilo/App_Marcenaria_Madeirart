package com.madeirart.appMadeirart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class AppMadeirartApplication {

	public static void main(String[] args) {
		criarDiretorioBancoDados();
		
		SpringApplication.run(AppMadeirartApplication.class, args);
	}

	/**
	 * Cria o diretório para o banco de dados SQLite se ele não existir.
	 * Localização: %APPDATA%/madeirart (Windows) ou ~/madeirart (Linux/Mac)
	 */
	private static void criarDiretorioBancoDados() {
		try {
			String userHome = System.getProperty("user.home");
			String osName = System.getProperty("os.name").toLowerCase();
			
			Path dbDir;
			if (osName.contains("win")) {
				// Windows: %APPDATA%/madeirart
				dbDir = Paths.get(userHome, "AppData", "Roaming", "madeirart");
			} else {
				// Linux/Mac: ~/madeirart
				dbDir = Paths.get(userHome, "madeirart");
			}
			
			if (!Files.exists(dbDir)) {
				Files.createDirectories(dbDir);
				System.out.println("✓ Diretório criado: " + dbDir.toAbsolutePath());
			} else {
				System.out.println("✓ Diretório já existe: " + dbDir.toAbsolutePath());
			}
		} catch (Exception e) {
			System.err.println("Erro ao criar diretório do banco de dados: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
