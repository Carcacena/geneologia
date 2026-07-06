async function logar() {
    const login = document.getElementById("login").value;
    const senha = document.getElementById("senha").value;

    try {
        // CORREÇÃO 1: Alterado de localhost para a URL ativa do seu Railway
        const response = await fetch("https://genealogia-production.up.railway.app/auth/login", {
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
            // CORREÇÃO 2: O seu backend retorna um JSON {"token": "..."} e não texto puro
            const dados = await response.json();

            // Salva apenas a string do token que está dentro do objeto JSON
            localStorage.setItem("token", dados.token);

            sessionStorage.setItem("tipo", "0");

            console.log("TIPO SALVO:", sessionStorage.getItem("tipo"));

            window.location.href = "menu.html";
        } else {
            alert("Login inválido. Verifique o usuário e a senha.");
        }
    } catch (error) {
        console.error("Erro na requisição:", error);
        alert("Não foi possível conectar ao servidor. Verifique as configurações de CORS no Spring Boot.");
    }
}
