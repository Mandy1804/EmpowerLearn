document.addEventListener('DOMContentLoaded', function () {
    // Alternância entre Login e Cadastro
    const tabButtons = document.querySelectorAll('.tab-button');
    const authForms = document.querySelectorAll('.auth-form');

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
        });
    });

    // Alternância entre tipos de cadastro
    const tipoCadastroSelect = document.getElementById('tipo-cadastro');
    const cadastroSubforms = document.querySelectorAll('.cadastro-subform');

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

    // Validação e envio dos formulários
    const forms = document.querySelectorAll('.cadastro-subform');
    const emailsCadastrados = ['joao@email.com', 'maria@email.com']; // Simulado

    forms.forEach(form => {
        form.addEventListener('submit', async function (e) {
            e.preventDefault();

            const emailInput = form.querySelector('input[type="email"]');
            const senhaInputs = form.querySelectorAll('input[type="password"]');
            const senhaInput = senhaInputs[0];
            const confirmarSenhaInput = senhaInputs[1];
            const messageDiv = form.querySelector('.form-message');

            if (!emailInput || !senhaInput || !confirmarSenhaInput || !messageDiv) {
                console.error("Erro: campos não encontrados no formulário.");
                return;
            }

            const email = emailInput.value;

            // Valida se e-mail já está cadastrado (mock)
            if (emailsCadastrados.includes(email)) {
                showMessage(messageDiv, 'error', 'Este e-mail já está cadastrado. Tente outro.');
                return;
            }

            // Valida se as senhas coincidem
            if (senhaInput.value !== confirmarSenhaInput.value) {
                showMessage(messageDiv, 'error', 'As senhas não coincidem. Por favor, verifique.');
                return;
            }

            // Cadastro do professor (envio para o backend)
            if (form.id === 'form-professor') {
                const professorData = {
                    nome: document.getElementById('prof-nome').value,
                    email: document.getElementById('prof-email').value,
                    senha: senhaInput.value,
                    telefone: "", // Adicionar campo se quiser
                    especialidade: document.getElementById('prof-materia').value,
                    formacao: document.getElementById('prof-didatica').value,
                    experiencia: document.getElementById('prof-experiencia').value,
                    status: "ATIVO",
                    dataCadastro: new Date().toISOString().split('T')[0],
                };

                try {
                    const response = await fetch('http://localhost:8080/h2-console', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(professorData)
                    });

                    if (!response.ok) {
                        const errorData = await response.json();
                        showMessage(messageDiv, 'error', `Erro: ${errorData.message || response.statusText}`);
                        return;
                    }

                    showMessage(messageDiv, 'success', 'Cadastro realizado com sucesso!');
                    form.reset();

                } catch (error) {
                    console.error(error);
                    showMessage(messageDiv, 'error', 'Erro ao conectar com o servidor.');
                }

            } else {
                // Outros cadastros (aluno, instituição)
                showMessage(messageDiv, 'success', 'Cadastro realizado com sucesso!');
                form.reset();
            }
        });
    });

    // Função para mostrar mensagens
    function showMessage(element, type, message) {
        element.textContent = message;
        element.classList.remove('hidden', 'success', 'error');
        element.classList.add(type);
        element.style.display = 'block';
    }
});
