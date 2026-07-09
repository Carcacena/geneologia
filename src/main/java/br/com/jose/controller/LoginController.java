package br.com.jose.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.jose.DTO.LoginDTO;
import br.com.jose.Service.BlacklistService;
import br.com.jose.model.Usuario;
import br.com.jose.repository.UsuarioRepository;
import br.com.jose.security.JwtService;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtService jwtService;

    private final BlacklistService blacklistService = new BlacklistService();

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            blacklistService.add(token.substring(7));
        } else if (token != null) {
            blacklistService.add(token);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDTO login) {
        // 1. Busca o usuário diretamente na tabela pelo campo login mapeado no DTO
        Usuario user = usuarioRepository.findByLogin(login.getLogin());

        // 2. Compara a senha digitada '123' com o Hash BCrypt ativo no MySQL
        if (user != null && encoder.matches(login.getSenha(), user.getSenha())) {
            
            // 3. Força o perfil a ir em maiúsculo (ADMIN) para evitar conflitos de validação no JWT
            String perfilSeguro = user.getPerfil() != null ? user.getPerfil().toUpperCase() : "USER";
            
            // 4. Gera o token utilizando a sua assinatura do JwtService
            String token = jwtService.gerarToken(user.getLogin(), perfilSeguro);
            
            // 5. Monta a resposta estruturada para o appLogin.js receber
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        }

        // Caso a senha ou o usuário estejam incorretos, retorna o erro esperado pelo frontend
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .body(Collections.singletonMap("error", "Login inválido"));
    }
}
