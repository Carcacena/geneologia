package br.com.jose.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.jose.DTO.PessoaDTO;
import br.com.jose.model.Pessoa;
import br.com.jose.repository.PessoaRepository;

@RestController
@RequestMapping("/pessoas")
@CrossOrigin(origins = "*") // Permite a comunicação direta com o frontend livre de erros
public class PessoaController {

    @Autowired
    private PessoaRepository pessoaRepository;

    // ==========================
    // LISTAR (Atualizado para passar a fotoUrl ao frontend)
    // ==========================
    @GetMapping
    public List<PessoaDTO> listarPessoas() {
        return pessoaRepository.findAll()
                .stream()
                .map(p -> new PessoaDTO(
                        p.getId(),
                        p.getNome(),
                        p.getNota(),
                        p.getNomeId(),
                        p.getFotoUrl())) // ⬅️ Usando o construtor completo que criamos na DTO
                .toList();
    }

    // ==========================
    // SALVAR (Atualizado para gravar a fotoUrl vinda do upload)
    // ==========================
    @PostMapping
    public PessoaDTO salvarPessoa(@RequestBody PessoaDTO pessoaDTO) {
        Pessoa p = new Pessoa();
        p.setNome(pessoaDTO.getNome());
        p.setNota(pessoaDTO.getNota());
        p.setNomeId(pessoaDTO.getNomeId());
        p.setFotoUrl(pessoaDTO.getFotoUrl()); // ⬅️ Grava a fotoUrl recebida no banco MySQL
        pessoaRepository.save(p);

        return new PessoaDTO(p.getId(), p.getNome(), p.getNota(), p.getNomeId(), p.getFotoUrl());
    }

    // ==========================
    // MOVER (Mantido idêntico à sua regra de negócio original)
    // ==========================
    @PostMapping("/mover")
    public String mover(@RequestBody Map<String, Long> dados) {

        Long childId = dados.get("childId");
        Long newParentId = dados.get("newParentId");

        Pessoa filho = pessoaRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));

        if (childId.equals(newParentId)) return "Erro: não pode mover para si mesmo";

        if (isDescendente(childId, newParentId)) return "Erro: não pode mover para filho/neto";

        filho.setNomeId(newParentId);
        pessoaRepository.save(filho);

        return "OK";
    }

    // ==========================
    // ALTERAR (Compatível tanto com o seu PUT /{id} do JS quanto com o seu POST/alterar antigo)
    // ==========================
    @PutMapping("/{id}")
    public ResponseEntity<String> alterar(@PathVariable Long id, @RequestBody PessoaDTO pessoaDTO) {
        try {
            Pessoa p = pessoaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));

            p.setNome(pessoaDTO.getNome());
            p.setNota(pessoaDTO.getNota());
            p.setFotoUrl(pessoaDTO.getFotoUrl()); // ⬅️ Atualiza a URL ou mantém a existente

            pessoaRepository.save(p);
            return ResponseEntity.ok("OK");

        } catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ==========================
    // EXCLUIR (Chamado pelo fetch DELETE do confirmarExcluir do JavaScript)
    // ==========================
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletar(@PathVariable Long id) {
        try {
            if (!pessoaRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            pessoaRepository.deleteById(id);
            return ResponseEntity.ok("OK");
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ==========================
    // VALIDAÇÃO DE CICLO (Mantido original)
    // ==========================
    private boolean isDescendente(Long origemId, Long destinoId) {
        List<Pessoa> lista = pessoaRepository.findAll();
        return verificar(origemId, destinoId, lista);
    }

    private boolean verificar(Long origemId, Long destinoId, List<Pessoa> lista) {
        for (Pessoa p : lista) {
            if (origemId.equals(p.getNomeId())) {
                if (p.getId().equals(destinoId)) return true;
                if (verificar(p.getId(), destinoId, lista)) return true;
            }
        }
        return false;
    }
}