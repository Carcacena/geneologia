
package br.com.jose.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Redireciona o acesso da raiz "/" para abrir o seu login.html automaticamente
        registry.addViewController("/").setViewName("forward:/login.html");
    }
}
