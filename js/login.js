document.addEventListener('DOMContentLoaded', function() {
    // Lógica para alternar entre Login e Cadastro
    const tabButtons = document.querySelectorAll('.tab-button');
    const authForms = document.querySelectorAll('.auth-form');

    tabButtons.forEach(button => {
        button.addEventListener('click', () => {
            const tab = button.dataset.tab;

            // Desativa todos os botões e formulários
            tabButtons.forEach(btn => btn.classList.remove('active'));
            authForms.forEach(form => form.classList.remove('active'));
            authForms.forEach(form => form.classList.add('hidden'));

            // Ativa o botão e o formulário clicados
            button.classList.add('active');
            document.getElementById(`${tab}-form`).classList.remove('hidden');
            document.getElementById(`${tab}-form`).classList.add('active');
        });
    });

    // Lógica para alternar entre os formulários de cadastro
    const tipoCadastroSelect = document.getElementById('tipo-cadastro');
    const cadastroSubforms = document.querySelectorAll('.cadastro-subform');

    tipoCadastroSelect.addEventListener('change', (e) => {
        const tipo = e.target.value;

        // Esconde todos os subformulários de cadastro
        cadastroSubforms.forEach(form => {
            form.classList.add('hidden');
            form.querySelector('.form-message').classList.add('hidden'); // Oculta mensagens ao trocar de formulário
        });

        // Mostra o formulário correspondente
        if (tipo) {
            document.getElementById(`form-${tipo}`).classList.remove('hidden');
        }
    });

    // Lógica de validação e envio dos formulários de cadastro
    const forms = document.querySelectorAll('.cadastro-subform');
    const emailsCadastrados = ['joao@email.com', 'maria@email.com']; // Simulação de um banco de dados de e-mails já cadastrados

    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            e.preventDefault(); // Impede o envio padrão do formulário

            const emailInput = form.querySelector('input[type="email"]');
            const email = emailInput.value;
            const senhaInput = form.querySelector('input[type="password"]');
            const confirmarSenhaInput = form.querySelector('input[type="password"]:nth-of-type(2)');
            const messageDiv = form.querySelector('.form-message');

            // 1. Validar se o e-mail já existe
            if (emailsCadastrados.includes(email)) {
                showMessage(messageDiv, 'error', 'Este e-mail já está cadastrado. Tente outro.');
                return;
            }

            // 2. Validar se as senhas são iguais
            if (senhaInput.value !== confirmarSenhaInput.value) {
                showMessage(messageDiv, 'error', 'As senhas não coincidem. Por favor, verifique.');
                return;
            }

            // Se tudo estiver OK, simular o envio do formulário
            // Aqui você enviaria os dados para um servidor
            // e trataria a resposta

            if (form.id === 'form-professor') {
                showMessage(messageDiv, 'success', 'Cadastro realizado com sucesso! Enviamos um e-mail para que você conclua o processo. Por favor, verifique sua caixa de entrada.');
            } else {
                showMessage(messageDiv, 'success', 'Cadastro realizado com sucesso! Bem-vindo à EmpowerLearn!');
            }

            // Opcional: Limpar os campos do formulário após o sucesso
            form.reset();
        });
    });

    function showMessage(element, type, message) {
        element.textContent = message;
        element.classList.remove('hidden', 'success', 'error');
        element.classList.add(type);
        element.style.display = 'block'; // Garante que a div seja exibida
    }
});