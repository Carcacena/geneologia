
async function logar() {
    const login = document.getElementById("login").value;
    const senha = document.getElementById("senha").value;

    try {
        // LINK CORRIGIDO: Agora aponta direto para o seu servidor Spring Boot na nuvem
       
        const response = await fetch("https://railway.app", {)
            
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                login: login,
                senha: senha
            })
        });

        if (response.ok) {
            // Descobre se o Spring Boot respondeu em JSON ou Texto Puro para evitar erros
            const contentType = response.headers.get("content-type");
            let token = "";

            if (contentType && contentType.includes("application/json")) {
                const dados = await response.json();
                token = dados.token || dados; // Pega a propriedade .token ou o objeto inteiro
            } else {
                token = await response.text(); // Lê como texto puro
            }

            // Salva o token extraído com segurança
            localStorage.setItem("token", token);
            sessionStorage.setItem("tipo", "0");

            console.log("TIPO SALVO:", sessionStorage.getItem("tipo"));
            window.location.href = "menu.html";

        } else {
            alert("Login inválido. Verifique o usuário e a senha.");
        }
    } catch (error) {
        console.error("Erro na requisição:", error);
        alert("Não foi possível conectar ao servidor. Verifique se o backend na Railway está ativo e configurado com CORS.");
    }
}
