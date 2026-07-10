async function logar() { 
    // Corrigido: adicionadas aspas nos IDs dos elementos
    const login = document.getElementById('login').value; 
    const senha = document.getElementById('senha').value; 

    // Corrigido: adicionadas aspas na URL e no método POST
    const response = await fetch('/auth/login', { 
        method: 'POST', 
        headers: { 'Content-Type': 'application/json' }, 
        body: JSON.stringify({ login: login, senha: senha }) 
    }); 

    if (response.ok) { 
        const token = await response.text(); 
        
        // Corrigido: adicionadas aspas nas chaves do storage
        localStorage.setItem('token', token); 
        sessionStorage.setItem('tipo', '0'); 
        
        console.log('TIPO SALVO:'); 
        console.log(sessionStorage.getItem('tipo')); 
        
        // Corrigido: adicionadas aspas no nome do arquivo HTML
        window.location.href = 'menu.html'; 
    } else { 
        alert('Login inválido'); 
    } 
}
