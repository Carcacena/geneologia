async function logar() { 
// Corrigido a declaração da variável e o fechamento do parêntese do getElementById
const login = document.getElementById('login').value; 
const senha = document.getElementById('senha').value;

try {
  // Ajuste a URL final para a rota correta do seu backend
  const response = await fetch('https://railway.app', { 
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ 
      login: login, 
      senha: senha 
    })
  });

  // Verifica se a requisição foi bem sucedida (status 200-299)
  if (!response.ok) {
    throw new Error('Erro ao autenticar. Verifique suas credenciais.');
  }

  // Extrai o JSON retornado pelo servidor
  const dadosResposta = await response.json();
  console.log('Login realizado com sucesso:', dadosResposta);

} catch (error) {
  console.error('Erro na requisição:', error);
}
