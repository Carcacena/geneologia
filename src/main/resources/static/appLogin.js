async function logar() { 
    const login = document.getElementById('login').value; 
    const senha = document.getElementById('senha').value; 

    const response = await fetch('https://railway.app', { 
        method: 'POST', 
        headers: { 'Content-Type': 'application/json' }, 
        body: JSON.stringify({ login: login, senha: senha }) 
    }); 

    if (response.ok) { 
        // CORRIGIDO: Lê a resposta como JSON em vez de texto bruto
        const data = await response.json(); 
        
        // CORRIGIDO: Extrai a propriedade 'token' enviada pelo Map do Spring Boot
        localStorage.setItem('token', data.token); 
        sessionStorage.setItem('tipo', '0'); 
        
        console.log('TIPO SALVO:'); 
        console.log(sessionStorage.getItem('tipo')); 
        
        window.location.href = 'menu.html'; 
    } else { 
        alert('Login inválido'); 
    } 
}
