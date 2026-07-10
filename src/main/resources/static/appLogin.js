async function logar(event) { 
  // Impede a página de recarregar ao enviar o formulário
  if (event) event.preventDefault(); 

  const login = document.getElementById('login').value; 
  const senha = document.getElementById('senha').value;

  try {
    // ATENÇÃO: Substitua a URL abaixo pela URL do seu backend no Railway

    const response = await fetch
	  ("https://geneologia-production.up.railway.app/auth/login",{
    //const response = await fetch('https://railway.app', { 
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ 
        login: login, 
        senha: senha 
      })
    });

    if (!response.ok) {
      throw new Error('Erro ao autenticar. Verifique suas credenciais.');
    }

    const dadosResposta = await response.json();
    console.log('Login realizado com sucesso:', dadosResposta);

    // Exemplo: Salvar token JWT e redirecionar
    // localStorage.setItem('token', dadosResposta.token);
    // window.location.href = '/dashboard.html';

  } catch (error) {
    console.error('Erro na requisição:', error);
    alert(error.message);
  } // <-- Fecha o bloco catch
}   // <-- FILHO DO COBOL: Essa chave fecha a "async function logar()". Faltava ela!   

}
