package br.com.jose.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import br.com.jose.DTO.LoginDTO;
import br.com.jose.Service.BlacklistService;
import br.com.jose.model.Usuario;
import br.com.jose.repository.UsuarioRepository;
import br.com.jose.security.JwtService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

    @Autowired
    private BlacklistService blacklistService;

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

        System.out.println("===== LOGIN =====");
        System.out.println("Login recebido: " + login.getLogin());

        Usuario user = usuarioRepository.findByLogin(login.getLogin());

        if (user == null) {
            System.out.println("Usuário NÃO encontrado.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Usuário não encontrado"));
        }

        System.out.println("Usuário encontrado.");    
        System.out.println("Hash no banco: " + user.getSenha());

        boolean senhaOk = encoder.matches(login.getSenha(), user.getSenha());
        System.out.println("Senha confere? " + senhaOk);

        if (!senhaOk) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Senha inválida"));
        }

        String perfilSeguro = user.getPerfil() != null
                ? user.getPerfil().toUpperCase()
                : "USER";

        String token = jwtService.gerarToken(user.getLogin(), perfilSeguro);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    // Map<String, String> response = new HashMap<>();
    //    response.put("token", token);
//
   //     return ResponseEntity.ok(response);
  //  }
} // <--- Cer
