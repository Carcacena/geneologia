<<<<<<< HEAD
async function logar() {
    const login = document.getElementById("login").value;
	const token = localStorage.getItem("token");
    const senha = document.getElementById("senha").value;

   // const response = await fetch("http://localhost:8080/auth/login", {
	const response = await fetch(`${API_URL}/auth/login`, {	
=======
<<<<<<< HEAD
async function logar() {
    const login = document.getElementById("login").value;
	
    const senha = document.getElementById("senha").value;

    const response = await fetch("http://localhost:8080/auth/login", {
	//const response = await fetch(`${API_URL}/auth/login`, {	
>>>>>>> feeace0223390a9c1eb88dc5e2649d51132e90a6
	

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

	    const token = await response.text();

	    localStorage.setItem("token", token);

	    sessionStorage.setItem("tipo", "0");

	    console.log("TIPO SALVO:");
	    console.log(sessionStorage.getItem("tipo"));

	    window.location.href = "menu.html";

	} else {

	    alert("Login inválido");
	}
	}
<<<<<<< HEAD

=======
=======
>>>>>>> feeace0223390a9c1eb88dc5e2649d51132e90a6
async function logar(event) {

    if (event) event.preventDefault();

    const login = document.getElementById("login").value;
    const senha = document.getElementById("senha").value;

    try {

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
<<<<<<< HEAD
=======
>>>>>>> a68c6dae46999f363582e64fdb89d1bcb02df095
>>>>>>> feeace0223390a9c1eb88dc5e2649d51132e90a6
