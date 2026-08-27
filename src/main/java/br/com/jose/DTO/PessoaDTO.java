package br.com.jose.DTO;

public class PessoaDTO {

    private Long id;
    private String nome;
    private String nota;
    private Long nomeId;
    private String fotoUrl; // ⬅️ CAMPO ADICIONADO PARA TRAFEGAR O CAMINHO DA FOTO

    // ✅ obrigatório para o Spring
    public PessoaDTO() {
    }

    // Mantido para compatibilidade se houver chamadas antigas no projeto
    public PessoaDTO(Long id, String nome, String nota, Long nomeId) {
        this.id = id;
        this.nome = nome;
        this.nota = nota;
        this.nomeId = nomeId;
    }

    // ⬅️ NOVO CONSTRUTOR COMPLETO EXIGIDO PELO PESSOACONTROLLER
    public PessoaDTO(Long id, String nome, String nota, Long nomeId, String fotoUrl) {
        this.id = id;
        this.nome = nome;
        this.nota = nota;
        this.nomeId = nomeId;
        this.fotoUrl = fotoUrl;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNota() {
		return nota;
	}

	public void setNota(String nota) {
		this.nota = nota;
	}

	public Long getNomeId() {
		return nomeId;
	}

	public void setNomeId(Long nomeId) {
		this.nomeId = nomeId;
	}

    // ⬅️ GETTER E SETTER ADICIONADOS PARA A FOTO
	public String getFotoUrl() {
		return fotoUrl;
	}

	public void setFotoUrl(String fotoUrl) {
		this.fotoUrl = fotoUrl;
	}
}
   