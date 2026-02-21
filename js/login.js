const API_BASE_URL = 'http://localhost:8080/api';
const PAGINA_DESTINO = 'dashboard.html';

document.addEventListener('DOMContentLoaded', function () {
    const tabButtons = document.querySelectorAll('.tab-button');
    const authForms = document.querySelectorAll('.auth-form');
    const tipoCadastroSelect = document.getElementById('tipo-cadastro');
    const cadastroSubforms = document.querySelectorAll('.cadastro-subform');
    const loginForm = document.querySelector('#login-form form');
    const allCadastroForms = document.querySelectorAll('.cadastro-subform');
    const messageDivLogin = document.getElementById('login-message');
    
    // --- FUNÇÕES AUXILIARES ---

    function showMessage(element, type, message) {
        const messageDiv = element.querySelector('.form-message') || element;
        if (messageDiv) {
            messageDiv.textContent = message;
            messageDiv.classList.remove('hidden', 'success', 'error');
            messageDiv.classList.add(type);
            messageDiv.style.display = 'block';
        }
    }

    async function handleLogin(email, senha) {
        
        const endpoints = [
            'professores',
            'alunos',
            'instituicoes'
        ];
        
        let success = false;
        let userData = null;

        for (const entity of endpoints) {
            try {
                const endpoint = `${API_BASE_URL}/${entity}/login`;
                const response = await fetch(endpoint, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ email, senha })
                });

                if (response.ok) {
                    userData = await response.json();
                    success = true;
                    break;
                }
            } catch (error) {
                console.error(`Erro ao tentar login em ${entity}:`, error);
               
            }
        }

        if (success && userData && userData.id) {
            // SALVA O ID DO USUÁRIO LOGADO para uso na página ver-perfil.html
            localStorage.setItem('userId', userData.id);
            localStorage.setItem('userType', userData.tipo); 
            
            showMessage(messageDivLogin, 'success', 'Login bem-sucedido! Redirecionando...');
            
            setTimeout(() => {
                window.location.href = PAGINA_DESTINO; 
            }, 1000); 

        } else if (userData && userData.message === 'Credenciais inválidas.') {
             showMessage(messageDivLogin, 'error', 'E-mail ou senha inválidos.');
        } else {
             showMessage(messageDivLogin, 'error', 'E-mail ou senha inválidos. Tente novamente.');
        }
    }

    // ----------------------------------------------------
    // A. LÓGICA DE EVENTOS E ABAS
    // ----------------------------------------------------

    // 1. Troca de Abas
    tabButtons.forEach(button => {
        button.addEventListener('click', () => {
            const tab = button.dataset.tab;
            tabButtons.forEach(btn => btn.classList.remove('active'));
            authForms.forEach(form => form.classList.add('hidden'));
            button.classList.add('active');
            document.getElementById(`${tab}-form`).classList.remove('hidden');
            document.getElementById(`${tab}-form`).classList.add('active');
            
            if (tab === 'cadastro') {
                cadastroSubforms.forEach(form => form.classList.add('hidden'));
                const tipo = tipoCadastroSelect.value;
                if (tipo) {
                    document.getElementById(`form-${tipo}`).classList.remove('hidden');
                }
            }
        });
    });

    // 2. Troca de Tipo de Cadastro
    tipoCadastroSelect.addEventListener('change', (e) => {
        const tipo = e.target.value;
        cadastroSubforms.forEach(form => form.classList.add('hidden'));
        if (tipo) {
            document.getElementById(`form-${tipo}`).classList.remove('hidden');
        }
    });

    // 3. Submissão do Login
    if (loginForm) {
        loginForm.addEventListener('submit', async function(e) {
            e.preventDefault(); 
            const email = document.getElementById('login-email').value;
            const senha = document.getElementById('login-senha').value;
            await handleLogin(email, senha);
        });
    }

    // ----------------------------------------------------
    // B. LÓGICA DE CADASTRO (API)
    // ----------------------------------------------------
    
    // Função genérica de envio de cadastro
    async function submitRegistration(e, entityPath, formFields) {
        e.preventDefault();
        const form = e.currentTarget;
        const messageDiv = form.querySelector('.form-message');
        const senha = formFields.senha.value;
        const confirmarSenha = formFields.confirmarSenha.value;

        if (senha !== confirmarSenha) {
            return showMessage(messageDiv, 'error', 'As senhas não coincidem. Por favor, verifique.');
        }
        
       
        const cepValue = formFields.cep.value.replace(/\D/g, ''); 
        if (cepValue.length !== 8) {
             return showMessage(messageDiv, 'error', 'O CEP deve conter 8 dígitos.');
        }

        const dataToSend = {
            // Campos comuns
            nome: formFields.nome.value,
            email: formFields.email.value,
            senha: senha,
            cep: cepValue,
            dataCadastro: new Date().toISOString().split('T')[0],
            
            // Campos específicos 
            ...formFields.specificData
        };

        try {
            const response = await fetch(`${API_BASE_URL}/${entityPath}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(dataToSend)
            });

            if (response.ok) {
                showMessage(messageDiv, 'success', 'Cadastro realizado com sucesso! Faça login.');
                form.reset();
            } else {
                const errorText = await response.text();
                showMessage(messageDiv, 'error', errorText || `Erro ${response.status}: Falha no cadastro.`);
            }
        } catch (error) {
            console.error('Erro de conexão:', error);
            showMessage(messageDiv, 'error', 'Erro de rede: Não foi possível conectar à API.');
        }
    }

    // 1. Cadastro de ALUNO
    document.getElementById('form-aluno').addEventListener('submit', async function(e) {
        submitRegistration(e, 'alunos', {
            nome: document.getElementById('aluno-nome'),
            email: document.getElementById('aluno-email'),
            senha: document.getElementById('aluno-senha'),
            confirmarSenha: document.getElementById('aluno-confirmar-senha'),
            cep: document.getElementById('aluno-cep'),
            specificData: {
                dataNascimento: document.getElementById('aluno-nascimento').value,
                sexo: document.getElementById('aluno-sexo').value,
            }
        });
    });

    // 2. Cadastro de PROFESSOR
    document.getElementById('form-professor').addEventListener('submit', async function(e) {
        submitRegistration(e, 'professores', {
            nome: document.getElementById('prof-nome'),
            email: document.getElementById('prof-email'),
            senha: document.getElementById('prof-senha'),
            confirmarSenha: document.getElementById('prof-confirmar-senha'),
            cep: document.getElementById('prof-cep'),
            specificData: {
                especialidade: document.getElementById('prof-materia').value,
                multiplasMaterias: document.getElementById('prof-multiplas-materias').value,
                didatica: document.getElementById('prof-didatica').value,
                experiencia: parseInt(document.getElementById('prof-experiencia').value),
            }
        });
    });

    // 3. Cadastro de INSTITUIÇÃO
    document.getElementById('form-instituicao').addEventListener('submit', async function(e) {
        submitRegistration(e, 'instituicoes', {
            nome: document.getElementById('inst-nome'),
            email: document.getElementById('inst-email'),
            senha: document.getElementById('inst-senha'),
            confirmarSenha: document.getElementById('inst-confirmar-senha'),
            cep: document.getElementById('inst-cep'),
            specificData: {
             
            }
        });
    });
});