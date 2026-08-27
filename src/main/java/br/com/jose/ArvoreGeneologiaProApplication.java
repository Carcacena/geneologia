package br.com.jose;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "br.com.jose") // garante que tudo em br.com.jose e subpacotes será escaneado
public class ArvoreGeneologiaProApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ArvoreGeneologiaProApplication.class);
        
        // Força o Spring Boot 4 a rodar estritamente na 8080, ignorando o Railway magic
        app.setDefaultProperties(java.util.Collections.singletonMap("server.port", "8080"));
        
        app.run(args);
    }
}
