package br.com.jose.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/midia")
@CrossOrigin(origins = "*") // Permite chamadas JavaScript vindas da tela HTML
public class MidiaController {

    // Caminho da pasta física estática do seu projeto
    private final String DIRETORIO_DESTINO = "src/main/resources/static/mp3/";

    @PostMapping("/upload")
    public ResponseEntity<?> fazerUpload(@RequestParam("file") MultipartFile arquivo) {
        try {
            if (arquivo.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("erro", "O arquivo enviado está vazio."));
            }

            // Garante que a pasta static/mp3 exista no disco rígido
            File diretorio = new File(DIRETORIO_DESTINO);
            if (!diretorio.exists()) {
                diretorio.mkdirs();
            }

            // Pega a extensão original do arquivo (.jpg, .jpeg, etc.)
            String nomeOriginal = arquivo.getOriginalFilename();
            String extensao = "";
            if (nomeOriginal != null && nomeOriginal.contains(".")) {
                extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
            }

            // Gera um nome único por UUID para evitar que uma foto sobrescreva outra
            String novoNome = UUID.randomUUID().toString() + extensao;

            // Salva fisicamente o arquivo na pasta
            Path caminhoCompleto = Paths.get(DIRETORIO_DESTINO + novoNome);
            Files.write(caminhoCompleto, arquivo.getBytes());

            // URL relativa de retorno para salvar no MySQL
            String urlRelativa = "/mp3/" + novoNome;

            return ResponseEntity.ok(Map.of(
                "url", urlRelativa,
                "mensagem", "Upload físico realizado com sucesso!"
            ));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Falha de gravação: " + e.getMessage()));
        }
    }
}