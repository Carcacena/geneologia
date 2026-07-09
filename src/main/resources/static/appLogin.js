async function logar() {

    const login = document.getElementById("login").value;
    const senha = document.getElementById("senha").value;

    const API_URL = window.location.origin;

    const response = await fetch(`${API_URL}/auth/login`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            login,
            senha
        })
    });

    if (response.ok) {

        const data = await response.json();

        localStorage.setItem("token", data.token);
        sessionStorage.setItem("tipo", "0");

        console.log("TIPO SALVO:");
        console.log(sessionStorage.getItem("tipo"));

        window.location.href = "menu.html";

    } else {

        alert("Login inválido");

    }
}
