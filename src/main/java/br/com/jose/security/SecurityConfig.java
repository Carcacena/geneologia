package br.com.jose.security;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Desativa o CSRF para permitir as requisições POST do formulário JSON
            .csrf(csrf -> csrf.disable())
            
            // 2. Ativa a configuração de CORS corrigida abaixo
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 3. Gerenciamento de Sessão STATELESS para funcionamento do JWT
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 4. Regras de Autorização e Liberação de Acesso
            .authorizeHttpRequests(auth -> auth
                // Libera totalmente os endpoints de login (com ou sem o prefixo /api)
                .requestMatchers("/usuario", "/usuario/**").permitAll()                   
                .requestMatchers("/auth/login", "/auth/login/", "/api/auth/login").permitAll()
                .requestMatchers("/auth/**", "/api/auth/**").permitAll() 
                
                // Libera a rota de erro padrão para evitar loops visuais
                .requestMatchers("/error").permitAll()
                
                // ÁREA FREE: Libera as telas HTML e arquivos JavaScript da raiz da pasta static
                .requestMatchers("/*.html", "/*.js", "/favicon.ico").permitAll()
                
                // ÁREA FREE DE INTRODUÇÃO: Libera mídias (fotos da ferrovia e áudio) e estilizações
                .requestMatchers("/mp3/**", "/static/**", "/css/**", "/js/**").permitAll()
                
                // Qualquer outra requisição restrita do sistema exigirá o Token JWT válido
                .anyRequest().authenticated() 
            )
            
            // 5. Injeta o filtro JWT na cadeia antes da autenticação padrão
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CORREÇÃO CIRÚRGICA: Configuração de CORS compatível com as credenciais do Railway
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permite qualquer padrão de origem de forma flexível (essencial para o HTTPS do Railway)
        configuration.setAllowedOriginPatterns(List.of("*")); 
        
        // Libera todos os métodos necessários para a navegação do sistema
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // Libera os cabeçalhos obrigatórios, incluindo o Authorization que carrega o JWT
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type", "Accept", "X-Requested-With"));
        
        // Mantido em true para permitir a gravação correta de cookies e tokens de sessão no navegador
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
