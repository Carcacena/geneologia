async function logar(event) {

    if (event) event.preventDefault();

    const login = document.getElementById("login").value;
    const senha = document.getElementById("senha").value;

    try {

        const API_URL = window.location.origin;
		const response = await fetch
			("http://localhost:8080/auth/login",{
        //const response = await fetch(`${API_URL}/auth/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                login,
                senha
            })
        });

        if (!response.ok) {
            throw new Error("Erro ao autenticar. Verifique suas credenciais.");
        }

        const dadosResposta = await response.json();

        localStorage.setItem("token", dadosResposta.token);

        window.location.href = "menu.html";

    } catch (error) {
        console.error(error);
        alert(error.message);
    }
}
