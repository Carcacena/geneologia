function ativarDragPainel() {
    let drag = false, offsetX = 0, offsetY = 0;
    painel.addEventListener("mousedown", e => {
        drag = true;
        offsetX = e.clientX - painel.offsetLeft;
        offsetY = e.clientY - painel.offsetTop;
    });
    document.addEventListener("mousemove", e => {
        if (!drag) return;
        painel.style.left = (e.clientX - offsetX) + "px";
        painel.style.top = (e.clientY - offsetY) + "px";
    });
    document.addEventListener("mouseup", () => { drag = false; });
} // ⬅️ CHAVE DE FECHAMENTO CORRETA DO DRAG


async function carregar() { // ⬅️ AGORA A FUNÇÃO FICA LIVRE E ACESSÍVEL!
    try {
        const resp = await fetch('/pessoas', {
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (!resp.ok) throw new Error(`Erro HTTP: ${resp.status}`);

        pessoas = await resp.json();
        desenhar();
    } catch (err) {
        console.error("Erro ao carregar pessoas:", err);
    }
}


//************************************************** */
function montar(lista, pai = null) {
    return lista
        .filter(p => {
            let paiId = p.nomeId ?? p.nome_id;
            return pai === null ? paiId == null : Number(paiId) === Number(pai);
        })
        .map(p => ({ ...p, filhos: montar(lista, p.id) }));
}

function render(nos, nivel = 0) {
    if (!nos.length) return '';
    let html = '<ul>';
    nos.forEach(n => {
        // Se houver uma fotoUrl, adiciona um círculo miniatura ao lado do nome
        let tagFoto = n.fotoUrl ? `<img src="${n.fotoUrl}" style="width:30px; height:30px; object-fit:cover; border-radius:50%; vertical-align:middle; margin-right:8px; border:1px solid #ccc;">` : '';

        html += `<li>
	            ${tagFoto}<strong>${n.nome}</strong>
	            ${n.nota ? `<div style="font-size:0.8em;color:#555; padding-left:38px;">${n.nota}</div>` : ''}
	            ${nivel >= 0 ? `<span class="action-btn" onclick="abrirPainel(${n.id},this,event)">⚙</span>` : ''}
	            ${render(n.filhos, nivel + 1)}
	        </li>`;
    });
    html += '</ul>';
    return html;
}

function desenhar() {
    document.getElementById('arvore').innerHTML = render(montar(pessoas));
}

function abrirPainel(id, el, ev) {
    ev.stopPropagation();
    selecionadoId = id;
    const botoes = painel.querySelectorAll("button");
    botoes[0].style.display = podeMover() ? "inline-block" : "none";
    botoes[1].style.display = podeAlterar() ? "inline-block" : "none";
    botoes[2].style.display = podeIncluir() ? "inline-block" : "none";
    botoes[3].style.display = podeExcluir() ? "inline-block" : "none";
    painel.style.display = 'block';

    painel.style.top = el.getBoundingClientRect().top + window.scrollY + 'px';
    painel.style.left = el.getBoundingClientRect().left + 30 + window.scrollX + 'px';
}

function fecharModal(tipo) {
    document.getElementById(`modal-${tipo}`).style.display = 'none';
    document.getElementById('overlay').style.display = 'none';
}

// FUNÇÃO AUXILIAR CORRIGIDA (Pegando o arquivo correto com .files[0])
async function fazerUploadArquivo(inputElement) {
    // Verifica se o elemento existe e se o usuário realmente selecionou um arquivo
    if (!inputElement || !inputElement.files || inputElement.files.length === 0) {
        return null;
    }

    const formData = new FormData();
    // 💡 AJUSTE CRUCIAL: Adicionado o [0] para enviar o arquivo físico real, e não a lista
    formData.append("file", inputElement.files[0]);

    // Faz a requisição limpa (Sem passar headers de Content-Type, o navegador faz isso sozinho para FormData)
    const resp = await fetch('/api/midia/upload', {
        method: 'POST',
        body: formData
    });

    if (!resp.ok) {
        // Exibe no console o erro real que o Spring Boot devolveu (ajuda a descobrir se é 403 ou 500)
        const textoErro = await resp.text();
        console.error("Erro devolvido pelo servidor:", textoErro);
        throw new Error("Falha no upload do arquivo físico.");
    }

    const dados = await resp.json();
    return dados.url; // Retorna o caminho relativo (Ex: /mp3/nome.jpg)
}


// INCLUIR (ATUALIZADO)
function abrirModalIncluir() {
    if (!podeIncluir()) return alert("Sem permissão");
    painel.style.display = 'none';
    document.getElementById('incluirNome').value = '';
    document.getElementById('incluirFoto').value = ''; // Limpa o input de arquivo anterior
    document.getElementById('modal-incluir').style.display = 'block';
    document.getElementById('overlay').style.display = 'block';
}

async function confirmarIncluir() {
    const nome = document.getElementById('incluirNome').value.trim();
    if (!nome) return alert("Digite o nome");

    try {
        // 1. Faz upload do arquivo primeiro se houver seleção (Livre de JWT)
        const inputFoto = document.getElementById('incluirFoto');
        const urlFotoSalva = await fazerUploadArquivo(inputFoto);

        // 2. Envia os dados cadastrais (Com o Token JWT e a url da Foto)
        const resp = await fetch('/pessoas', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify({
                nome: nome,
                nomeId: selecionadoId,
                fotoUrl: urlFotoSalva
            })
        });

        if (!resp.ok) throw new Error("Erro ao incluir");
        fecharModal('incluir');
        await carregar();
    } catch (e) {
        alert('Erro ao incluir pessoa ou mídia');
        console.error(e);
    }
}

// MOVER
function abrirModalMover() {
    if (!podeMover()) return alert("Sem permissão");
    painel.style.display = 'none';
    let html = '';
    pessoas.forEach(p => {
        if (p.id !== selecionadoId) {
            html += `<div>
	                <input type="radio" name="destino" value="${p.id}"> ${p.nome}
	            </div>`;
        }
    });
    document.getElementById('conteudoMover').innerHTML = html;
    document.getElementById('modal-mover').style.display = 'block';
    document.getElementById('overlay').style.display = 'block';
}


async function confirmarMover() {
    const sel = document.querySelector('input[name="destino"]:checked');
    if (!sel) return alert('Escolha um destino');

    try {
        const resp = await fetch('/pessoas/mover', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify({
                childId: selecionadoId,
                newParentId: Number(sel.value)
            })
        });

        if (!resp.ok) {
            alert(await resp.text());
            return;
        }
        fecharModal('mover');
        await carregar();
    } catch (e) {
        alert('Erro ao mover');
        console.error(e);
    }
}
function abrirModalAlterar() {

    if (!podeAlterar()) return alert("Sem permissão");

    // Localiza os dados da pessoa selecionada na árvore
    let p = pessoas.find(x => x.id === selecionadoId);

    if (!p) return;

    painel.style.display = 'none';

    // Pré-preenche Nome e Nota
    document.getElementById('alterarNome').value = p.nome;
    document.getElementById('alterarNota').value = p.nota || '';

    // ============================
    // FOTO ATUAL
    // ============================

    const inputFoto = document.getElementById('alterarFoto');
    const fotoAtualNome = document.getElementById('fotoAtualNome');

    // O input serve somente para escolher uma NOVA foto
    if (inputFoto) {
        inputFoto.value = '';
    }

    // Exibe separadamente a foto que já está cadastrada
    if (fotoAtualNome) {

        if (p.fotoUrl) {

            // Exemplo:
            // /mp3/90c6310c-2582-4ab7-afff-b5e5ff5b92e4.jpg
            // vira:
            // 90c6310c-2582-4ab7-afff-b5e5ff5b92e4.jpg

            const nomeArquivoBanco =
                p.fotoUrl.replace("/mp3/", "");

            fotoAtualNome.textContent =
                "Foto atual: " + nomeArquivoBanco;

        } else {

            fotoAtualNome.textContent =
                "Foto atual: nenhuma";
        }
    }

    // Abre o modal
    document.getElementById('modal-alterar').style.display = 'block';
    document.getElementById('overlay').style.display = 'block';
}
// CONFIRMAR ALTERAR (ATUALIZADO COM UPLOAD LIVRE DE JWT)
async function confirmarAlterar() {
    const nome = document.getElementById('alterarNome').value.trim();
    const nota = document.getElementById('alterarNota').value.trim();
    if (!nome) return alert("O nome não pode ficar vazio");

    try {
        // 1. Faz o upload se houver um arquivo novo selecionado (Livre de JWT)
        const inputFoto = document.getElementById('alterarFoto');
        let urlFotoSalva = await fazerUploadArquivo(inputFoto);

        // Se o usuário não escolheu uma foto nova, mantém a atual que já estava salva no banco
        const pessoaAtual = pessoas.find(x => x.id === selecionadoId);
        if (!urlFotoSalva && pessoaAtual) {
            urlFotoSalva = pessoaAtual.fotoUrl;
        }

        // 2. Envia os dados cadastrais atualizados para o banco (Com JWT)
        const resp = await fetch(`/pessoas/${selecionadoId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify({
                id: selecionadoId,
                nome: nome,
                nota: nota,
                fotoUrl: urlFotoSalva // Envia a URL nova ou mantida
            })
        });

        if (!resp.ok) throw new Error("Erro ao alterar");
        fecharModal('alterar');
        await carregar();
    } catch (e) {
        alert('Erro ao alterar dados ou mídias');
        console.error(e);
    }
}

// =========================================================================
// CONFIRMAR EXCLUIR (Garanta que o final dela termina exatamente assim)
// =========================================================================
async function confirmarExcluir() {
    try {
        const resp = await fetch(`/pessoas/${selecionadoId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': 'Bearer ' + token
            }
        });

        if (!resp.ok) throw new Error("Erro ao excluir");
        fecharModal('excluir');
        await carregar();
    } catch (e) {
        alert('Erro ao excluir');
        console.error(e);
    }
} // ⬅️ Aqui fecha a função confirmarExcluir

// =========================================================================
// 💡 FUNÇÃO DE REMOVER FOTO (Livre, limpa e visível para o navegador)
// =========================================================================
async function removerFotoCadastrada() {
    if (!confirm("Deseja realmente remover a foto desta pessoa e deixá-la sem imagem?")) return;

    try {
        const p = pessoas.find(x => x.id === selecionadoId);
        if (!p) return alert("Pessoa não encontrada na memória.");

        const resp = await fetch(`/pessoas/${selecionadoId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify({
                id: selecionadoId,
                nome: p.nome,
                nota: p.nota,
                nomeId: p.nomeId ?? p.nome_id,
                fotoUrl: null // Grava NULL no MySQL
            })
        });

        if (!resp.ok) throw new Error("Erro ao atualizar o banco.");

        document.getElementById('alterarFoto').value = '';
        fecharModal('alterar');
        await carregar(); // Atualiza a árvore em tempo real
        alert("Foto removida com sucesso!");

    } catch (e) {
        alert('Erro ao remover a foto do banco.');
        console.error(e);
    }
} // ⬅️