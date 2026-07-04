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

    // Busca a URL do frontend na nuvem via variável de ambiente.
    // Se não existir (local), assume "http://localhost:8080" ou você pode alterar para a porta do seu front (ex: 3000, 5173).
    @Value("${app.frontend.url:http://localhost:8080}")
    private String frontendUrl;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                    
                    // Lista dinâmica de origens permitidas
                    List<String> allowedOrigins = new ArrayList<>();
                    allowedOrigins.add("http://localhost:8080");
                    allowedOrigins.add("http://localhost:3000"); // Comum para React local
                    allowedOrigins.add("http://localhost:5173"); // Comum para Vite/Vue/React local
                    
                    // Adiciona a URL de produção se ela estiver configurada
                    if (frontendUrl != null && !frontendUrl.isEmpty()) {
                        allowedOrigins.add(frontendUrl);
                    }
                    
                    corsConfig.setAllowedOrigins(allowedOrigins);
                    // Adicionado o método OPTIONS, fundamental para deploy em nuvem
                    corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                    // Adicionado Cache-Control para evitar problemas de cache de token
                    corsConfig.setAllowedHeaders(List.of("Authorization", "Content-Type", "Cache-Control"));
                    corsConfig.setAllowCredentials(true);
                    
                    return corsConfig;
                }))
                .authorizeHttpRequests(auth -> auth
                        // Libera todas as requisições de pre-flight (OPTIONS) do proxy da nuvem
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()

                        .requestMatchers(
                            "/",
                            "/*.html",
                            "/*.js",
                            "/*.css",
                            "/*.jpg",
                            "/*.png",
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