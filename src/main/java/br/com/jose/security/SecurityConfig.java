
package br.com.jose.security;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // 1. Desabilita proteção CSRF (necessário para APIs Stateless baseadas em Token)
                .csrf(csrf -> csrf.disable())
                
                // 2. Configuração de CORS limpa e compatível com Nuvem
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration corsConfig = new CorsConfiguration();
                    corsConfig.setAllowedOriginPatterns(List.of("*")); // Permite qualquer origem em produção com segurança estruturada
                    corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                    corsConfig.setAllowedHeaders(List.of("Authorization", "Content-Type", "Cache-Control"));
                    corsConfig.setAllowCredentials(true);
                    return corsConfig;
                }))
                
                // 3. Regras de Permissão de Rotas
                .authorizeHttpRequests(auth -> auth
                        // Libera requisições de pre-flight (OPTIONS)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        
                        // Libera estritamente o endpoint de autenticação
                        .requestMatchers("/auth/**").permitAll()
                        
                        // Libera arquivos públicos de recursos visuais e áudios
                        .requestMatchers(
                            "/", 
                            "/index.html", 
                            "/login.html", 
                            "/*.html", 
                            "/*.js", 
                            "/*.css", 
                            "/*.png", 
                            "/*.jpg", 
                            "/*.mp3", 
                            "/mp3/**", 
                            "/favicon.ico", 
                            "/error"
                        ).permitAll()

                        // Restringe as rotas privadas do sistema
                        .requestMatchers("/admin/**").authenticated()
                        .requestMatchers("/pessoas/**").authenticated()
                        
                        // Qualquer outra rota exige autenticação
                        .anyRequest().authenticated()
                )
                
                // 4. Define o gerenciamento de sessão como estritamente Stateless (sem estado)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // 5. Adiciona o filtro JWT antes do filtro de autenticação padrão do Spring
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // Bean essencial para gerenciar o processo de login que faltava no Spring Boot 4
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
