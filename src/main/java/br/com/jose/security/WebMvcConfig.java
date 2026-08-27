package br.com.jose.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type", "Accept", "X-Requested-With", "Cache-Control")
                .allowCredentials(false)
                .maxAge(3600);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/login.html");
    }

    // =========================================================================
    // 💡 MÉTODO ADICIONADO: MAPEAMENTO FÍSICO DAS FOTOS EM TEMPO REAL
    // =========================================================================
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Vincula as requisições da URL (/mp3/...) à pasta real do disco do computador
        registry.addResourceHandler("/mp3/**")
                .addResourceLocations("file:src/main/resources/static/mp3/")
                .addResourceLocations("classpath:/static/mp3/");
    }
}