document.addEventListener('DOMContentLoaded', function () {

    // --- VARIÁVEIS DE CONFIGURAÇÃO E ELEMENTOS ---
    
    // ENDPOINT BASE DA API: Onde seu Spring Boot está rodando
    const API_BASE_URL = 'http://localhost:8080/api';
    const PAGINA_DESTINO = 'daschboard.html'; // Corrigido para dashboard.html
    
    // REMOVIDO: MOCK_EMAIL e MOCK_SENHA e emailsCadastrados (Não precisamos mais simular)
    
    const tabButtons = document.querySelectorAll('.tab-button');
    const authForms = document.querySelectorAll('.auth-form');
    const tipoCadastroSelect = document.getElementById('tipo-cadastro');
    const cadastroSubforms = document.querySelectorAll('.cadastro-subform');
    const loginForm = document.querySelector('#login-form form');
    const allCadastroForms = document.querySelectorAll('.cadastro-subform');

    // Seleciona a div de mensagem que já existe no HTML
    const messageDivLogin = document.getElementById('login-message'); 

    // ----------------------------------------------------
    // 1. FUNÇÃO AUXILIAR: Mostrar Mensagens de Feedback
    // ----------------------------------------------------
    function showMessage(element, type, message) {
        element.textContent = message;
        element.classList.remove('hidden', 'success', 'error');
        element.classList.add(type);
        element.style.display = 'block';
    }


    // ----------------------------------------------------
    // 2. LÓGICA PRINCIPAL: Troca de Abas (Mantida)
    // ----------------------------------------------------
    tabButtons.forEach(button => {
        button.addEventListener('click', () => {
            const tab = button.dataset.tab;

            tabButtons.forEach(btn => btn.classList.remove('active'));
            authForms.forEach(form => {
                form.classList.remove('active');
                form.classList.add('hidden');
            });

            button.classList.add('active');
            document.getElementById(`${tab}-form`).classList.remove('hidden');
            document.getElementById(`${tab}-form`).classList.add('active');
            
            if (tab === 'cadastro') {
                cadastroSubforms.forEach(form => form.classList.add('hidden'));
                if (tipoCadastroSelect.value) {
                    document.getElementById(`form-${tipoCadastroSelect.value}`).classList.remove('hidden');
                }
            }
        });
    });


    // ----------------------------------------------------
    // 3. LÓGICA DE TROCA DE SUB-FORMULÁRIOS (Mantida)
    // ----------------------------------------------------
    tipoCadastroSelect.addEventListener('change', (e) => {
        const tipo = e.target.value;

        cadastroSubforms.forEach(form => {
            form.classList.add('hidden');
            const messageDiv = form.querySelector('.form-message');
            if (messageDiv) messageDiv.classList.add('hidden');
        });

        if (tipo) {
            document.getElementById(`form-${tipo}`).classList.remove('hidden');
        }
    });


    // ----------------------------------------------------
    // 4. LÓGICA DE LOGIN REAL (CHAMADA À API)
    // ----------------------------------------------------
    if (loginForm) {
        loginForm.addEventListener('submit', async function(e) {
            e.preventDefault(); 
            
            const email = document.getElementById('login-email').value;
            const senha = document.getElementById('login-senha').value;
            
            // NOTE: A Lógica abaixo tenta logar APENAS como Professor. 
            // Em uma app completa, você teria que tentar Aluno, Professor E Instituição
            // ou ter um endpoint /login universal.
            
            try {
                // Tenta login como Professor: /api/professores/login
                const response = await fetch(`${API_BASE_URL}/professores/login`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ email: email, senha: senha })
                });

                if (response.ok) {
                    // Login bem-sucedido!
                    showMessage(messageDivLogin, 'success', 'Login bem-sucedido! Redirecionando...');
                    
                    // Em um projeto real, você armazenaria o token de segurança retornado aqui.
                    
                    setTimeout(() => {
                        window.location.href = PAGINA_DESTINO; 
                    }, 1000); 

                } else if (response.status === 401) {
                    // 401: Unauthorized (Não Autorizado - Senha/Email Incorreto)
                    showMessage(messageDivLogin, 'error', 'E-mail ou senha inválidos. Tente novamente.');
                } else {
                    // Outros erros (500, etc.)
                    showMessage(messageDivLogin, 'error', 'Erro desconhecido ao tentar logar. Verifique o servidor.');
                }
                
            } catch (error) {
                console.error('Erro de conexão:', error);
                showMessage(messageDivLogin, 'error', 'Erro de rede: Não foi possível conectar à API.');
            }
        });
    }


    // ----------------------------------------------------
    // 5. LÓGICA DE CADASTRO REAL (CHAMADA À API)
    // ----------------------------------------------------
    allCadastroForms.forEach(form => {
        form.addEventListener('submit', async function (e) {
            e.preventDefault();

            const emailInput = form.querySelector('input[type="email"]');
            const senhaInput = form.querySelector('input[type="password"]');
            // Assume que o segundo input type=password é o Confirmar Senha
            const confirmarSenhaInput = form.querySelectorAll('input[type="password"]')[1]; 
            const messageDiv = form.querySelector('.form-message');

            if (senhaInput.value !== confirmarSenhaInput.value) {
                return showMessage(messageDiv, 'error', 'As senhas não coincidem. Por favor, verifique.');
            }
            
            // --- Mapeamento do Front-End para a API Java ---
            let dataToSend = {};
            let endpoint = '';
            let successMessage = '';

            if (form.id === 'form-professor') {
                endpoint = `${API_BASE_URL}/professores`;
                successMessage = 'Cadastro de Professor realizado com sucesso!';
                dataToSend = {
                    nome: document.getElementById('prof-nome').value,
                    email: document.getElementById('prof-email').value,
                    cep: document.getElementById('prof-cep').value,
                    senha: senhaInput.value,
                    especialidade: document.getElementById('prof-materia').value,
                    // REMOVIDO: multiplasMaterias (O backend precisa de um getter para ele, se não o tiver)
                    didatica: document.getElementById('prof-didatica').value,
                    experiencia: document.getElementById('prof-experiencia').value,
                    status: "ATIVO",
                    dataCadastro: new Date().toISOString().split('T')[0],
                };
            } else if (form.id === 'form-aluno') {
                endpoint = `${API_BASE_URL}/alunos`;
                successMessage = 'Cadastro de Aluno realizado com sucesso!';
                dataToSend = {
                    nome: document.getElementById('aluno-nome').value,
                    email: document.getElementById('aluno-email').value,
                    cep: document.getElementById('aluno-cep').value,
                    senha: senhaInput.value,
                    dataNascimento: document.getElementById('aluno-nascimento').value,
                    sexo: document.getElementById('aluno-sexo').value,
                    dataCadastro: new Date().toISOString().split('T')[0],
                };
            } else if (form.id === 'form-instituicao') {
                endpoint = `${API_BASE_URL}/instituicoes`;
                successMessage = 'Cadastro de Instituição realizado com sucesso!';
                dataToSend = {
                    nome: document.getElementById('inst-nome').value,
                    email: document.getElementById('inst-email').value,
                    cep: document.getElementById('inst-cep').value,
                    senha: senhaInput.value,
                    dataCadastro: new Date().toISOString().split('T')[0],
                };
            }
            
            // Envio para a API
            if (endpoint) {
                try {
                    const response = await fetch(endpoint, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(dataToSend)
                    });

                    if (response.ok) {
                        showMessage(messageDiv, 'success', successMessage);
                        form.reset();
                    } else {
                        // Tenta obter erro do Back-End (ex: Email já existe)
                        // A API Java envia uma string simples no erro, então lemos como texto:
                        const errorText = await response.text();
                        let errorMessage = errorText || 'Erro no servidor. Verifique o console.';
                        
                        // Garante que o erro do Java (ex: 'Erro: Este e-mail já está cadastrado.') seja exibido
                        showMessage(messageDiv, 'error', errorMessage);
                    }

                } catch (error) {
                    console.error('Erro de conexão:', error);
                    showMessage(messageDiv, 'error', 'Erro de rede: Não foi possível conectar à API.');
                }
            }
        });
    });

});