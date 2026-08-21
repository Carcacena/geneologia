package br.com.jose.security;

import java.util.List;
<<<<<<< HEAD
import org.springframework.beans.factory.annotation.Value;
=======

import org.springframework.beans.factory.annotation.Autowired;
>>>>>>> ff5c8a53cbf33cbf7932575559b7f58580fa23ef
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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

<<<<<<< HEAD
    private final JwtFilter jwtFilter;

    // O Spring lê esta propriedade de forma dinâmica. Se não achar nada, usa o padrão do localhost.
    @Value("${app.cors.allowed-origins:http://localhost:8080}")
    private String allowedOrigin;

    // Injeção por construtor profissional
    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Configuração dinâmica eliminando os blocos de comentários manuais
        configuration.setAllowedOrigins(List.of(allowedOrigin));
        
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/api/auth/**", "/public/**").permitAll()
                .requestMatchers(
                    "/", "/login.html", "/*.html", "/*.js", "/*.css", 
                    "/*.png", "/*.jpg", "/*.mp3", "/mp3/**", "/favicon.ico", "/error"
                ).permitAll()
                .requestMatchers("/admin/**").authenticated()
                .requestMatchers("/pessoas/**").authenticated()
                .anyRequest().authenticated()
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
=======
    @Autowired
    private JwtFilter jwtFilter;
    
 @Bean
CorsConfigurationSource corsConfigurationSource() {

    CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowedOrigins(List.of(
        "https://geneologia-production.up.railway.app"
    ));

    configuration.setAllowedMethods(List.of(
        "GET",
        "POST",
        "PUT",
        "DELETE",
        "OPTIONS"
    ));

    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration("/**", configuration);

    return source;
}

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http

                .csrf(csrf -> csrf.disable())

                .cors(Customizer.withDefaults())

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers("/auth/**").permitAll()
                                       
                         .requestMatchers("/api/auth/**", "/public/**").permitAll() // Public routes
                        .requestMatchers(
                                "/",
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

                        .requestMatchers("/admin/**").authenticated()

                        .requestMatchers("/pessoas/**").authenticated()

                        .anyRequest().authenticated()
                )

                .sessionManagement(sess ->
                        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class)

                .build();
>>>>>>> ff5c8a53cbf33cbf7932575559b7f58580fa23ef
    }

    @Bean
    public AuthenticationManager authenticationManager(
<<<<<<< HEAD
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
=======
            AuthenticationConfiguration authenticationConfiguration)
            throws Exception {

>>>>>>> ff5c8a53cbf33cbf7932575559b7f58580fa23ef
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
<<<<<<< HEAD
=======

>>>>>>> ff5c8a53cbf33cbf7932575559b7f58580fa23ef
        return new BCryptPasswordEncoder();
    }
}
