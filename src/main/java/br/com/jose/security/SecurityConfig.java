package br.com.jose.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Value("${app.frontend.url:http://localhost:8080}")
    private String frontendUrl;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                    
                    List<String> allowedOrigins = new ArrayList<>();
                    allowedOrigins.add("http://localhost:8080");
                    allowedOrigins.add("http://localhost:3000"); 
                    allowedOrigins.add("http://localhost:5173");
                    allowedOrigins.add("http://127.0.0.1:5500"); // Adicionado para Live Server do VS Code
                    
                    // CORREÇÃO 1: Adiciona explicitamente a URL real de produção da Railway
                    allowedOrigins.add("https://railway.app");
                    //if (frontendUrl != null && !frontendUrl.isEmpty() && !frontendUrl.equals("http://localhost:8080")) {
                    //    allowedOrigins.add(frontendUrl);
                   // }
                    
                    corsConfig.setAllowedOrigins(allowedOrigins);
                    corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                    
                    // CORREÇÃO 2: Aceita qualquer cabeçalho enviado pelo navegador
                    corsConfig.setAllowedHeaders(List.of("*")); 
                    corsConfig.setAllowCredentials(true);
                    
                    return corsConfig;
                }))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()

                        // CORREÇÃO 3: Ajustado o padrão AntMatcher para capturar arquivos estáticos corretamente em qualquer nível
                        .requestMatchers(
                            "/",
                            "/**/*.html",
                            "/**/*.js",
                            "/**/*.css",
                            "/**/*.jpg",
                            "/**/*.png",
                            "/favicon.ico",
                            "/mp3/**"
                        ).permitAll()

                        .requestMatchers("/admin/**").authenticated()
                        .requestMatchers("/pessoas/**").authenticated()

                        .anyRequest().authenticated()
                ) 		
                .sessionManagement(sess ->
                        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
