document.addEventListener('DOMContentLoaded', function () {

    // --- VARIÁVEIS DE CONFIGURAÇÃO E ELEMENTOS ---
    const emailsCadastrados = ['joao@email.com', 'maria@email.com']; // Simulado para validação de cadastro
    const MOCK_EMAIL = 'teste@gmail.com';
    const MOCK_SENHA = 'teste';
    // Caminho para a página pós-login. Mantido como 'dashboard.html'
    const PAGINA_DESTINO = 'daschboard.html'; 

    const tabButtons = document.querySelectorAll('.tab-button');
    const authForms = document.querySelectorAll('.auth-form');
    const tipoCadastroSelect = document.getElementById('tipo-cadastro');
    const cadastroSubforms = document.querySelectorAll('.cadastro-subform');
    const loginForm = document.querySelector('#login-form form'); // Seleciona o FORM dentro da div
    const allCadastroForms = document.querySelectorAll('.cadastro-subform');


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
    // 2. LÓGICA PRINCIPAL: Troca de Abas (Login/Cadastro)
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
            
            // Oculta os sub-formulários ao voltar para o modo 'Cadastro' 
            if (tab === 'cadastro') {
                allCadastroForms.forEach(form => form.classList.add('hidden'));
                if (tipoCadastroSelect.value) {
                    document.getElementById(`form-${tipoCadastroSelect.value}`).classList.remove('hidden');
                }
            }
        });
    });


    // ----------------------------------------------------
    // 3. LÓGICA DE CADASTRO: Troca de Sub-Formulários
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
    // 4. LÓGICA DE LOGIN (MOCK E REDIRECIONAMENTO)
    // ----------------------------------------------------
    // Seleciona a div de mensagem que já existe no HTML corrigido
    const messageDivLogin = document.getElementById('login-message');
    
    // Adicionar listener de submissão ao formulário de Login
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault(); // Impede o recarregamento da página
            
            const emailInput = document.getElementById('login-email');
            const senhaInput = document.getElementById('login-senha');
            
            const email = emailInput.value;
            const senha = senhaInput.value;
            
            if (email === MOCK_EMAIL && senha === MOCK_SENHA) {
                showMessage(messageDivLogin, 'success', 'Login bem-sucedido! Redirecionando...');
                
                // Redirecionamento após delay
                setTimeout(() => {
                    // Usando window.location.href para maior compatibilidade
                    window.location.href = PAGINA_DESTINO; 
                }, 1000); 
                
            } else {
                showMessage(messageDivLogin, 'error', 'E-mail ou senha incorretos.');
            }
        });
    }


    // ----------------------------------------------------
    // 5. LÓGICA DE CADASTRO (API - Professor, Aluno, Instituição)
    // ----------------------------------------------------
    allCadastroForms.forEach(form => {
        form.addEventListener('submit', async function (e) {
            e.preventDefault();

            const emailInput = form.querySelector('input[type="email"]');
            const senhaInputs = form.querySelectorAll('input[type="password"]');
            const senhaInput = senhaInputs[0];
            const confirmarSenhaInput = senhaInputs[1];
            const messageDiv = form.querySelector('.form-message');

            if (!messageDiv) return console.error("Erro: Mensagem DIV não encontrada.");

            const email = emailInput.value;
            
            // Validações básicas (Senhas e Email Mock)
            if (emailsCadastrados.includes(email)) {
                return showMessage(messageDiv, 'error', 'Este e-mail já está cadastrado. Tente outro.');
            }
            if (senhaInput.value !== confirmarSenhaInput.value) {
                return showMessage(messageDiv, 'error', 'As senhas não coincidem. Por favor, verifique.');
            }

            // --- Lógica de Envio para a API ---
            let dataToSend = {};
            let endpoint = '';
            let successMessage = '';
            
            try {
                if (form.id === 'form-professor') {
                    endpoint = 'http://localhost:8080/api/professores';
                    successMessage = 'Cadastro de Professor realizado com sucesso!';
                    dataToSend = {
                        nome: document.getElementById('prof-nome').value,
                        email: document.getElementById('prof-email').value,
                        cep: document.getElementById('prof-cep').value,
                        senha: senhaInput.value,
                        especialidade: document.getElementById('prof-materia').value,
                        multiplasMaterias: document.getElementById('prof-multiplas-materias').value,
                        didatica: document.getElementById('prof-didatica').value,
                        experiencia: document.getElementById('prof-experiencia').value,
                        status: "ATIVO",
                        dataCadastro: new Date().toISOString().split('T')[0],
                    };
                } else if (form.id === 'form-aluno') {
                    endpoint = 'http://localhost:8080/api/alunos';
                    successMessage = 'Cadastro de Aluno realizado com sucesso!';
                    dataToSend = {
                        nome: document.getElementById('aluno-nome').value,
                        email: document.getElementById('aluno-email').value,
                        cep: document.getElementById('aluno-cep').value,
                        dataNascimento: document.getElementById('aluno-nascimento').value,
                        sexo: document.getElementById('aluno-sexo').value,
                        senha: senhaInput.value,
                        dataCadastro: new Date().toISOString().split('T')[0],
                    };
                } else if (form.id === 'form-instituicao') {
                    endpoint = 'http://localhost:8080/api/instituicoes';
                    successMessage = 'Cadastro de Instituição realizado com sucesso!';
                    dataToSend = {
                        nome: document.getElementById('inst-nome').value,
                        email: document.getElementById('inst-email').value,
                        cep: document.getElementById('inst-cep').value,
                        senha: senhaInput.value,
                        dataCadastro: new Date().toISOString().split('T')[0],
                    };
                }

                const response = await fetch(endpoint, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(dataToSend)
                });

                if (!response.ok) {
                    const errorData = await response.json().catch(() => ({ message: response.statusText }));
                    return showMessage(messageDiv, 'error', `Erro ao cadastrar: ${errorData.message || response.statusText}.`);
                }

                showMessage(messageDiv, 'success', successMessage);
                form.reset();

            } catch (error) {
                console.error('Erro de conexão:', error);
                showMessage(messageDiv, 'error', 'Erro ao conectar com o servidor. Verifique se a API está online.');
            }
        });
    });

});
