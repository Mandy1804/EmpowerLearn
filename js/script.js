document.addEventListener('DOMContentLoaded', function() {
    
    // --- VARIÁVEIS DO UPLOAD ---
    const uploadButton = document.getElementById('upload-button');
    const profilePhotoInput = document.getElementById('profile-photo-input');

    // 1. Ligar o botão visível ao input escondido
    if (uploadButton) {
        uploadButton.addEventListener('click', function() {
            profilePhotoInput.click();
        });
    }

    // 2. Lógica de Envio da Foto para o Back-End (API)
    if (profilePhotoInput) {
        profilePhotoInput.addEventListener('change', async function(event) {
            const file = event.target.files[0];
            if (!file) return;

            if (!userId || !userType) {
                 alert("Sessão inválida. Faça login novamente.");
                 return;
            }

            const formData = new FormData();
            formData.append('file', file);
            
            // CONSTRÓI A URL DE UPLOAD DINÂMICA: /api/professores/ID/upload-foto
            const uploadUrl = `${API_BASE_URL}/${userType}s/${userId}/upload-foto`; 

            // Exibe mensagem de carregamento
            uploadButton.innerText = 'Enviando...';
            uploadButton.disabled = true;

            try {
                const response = await fetch(uploadUrl, {
                    method: 'POST',
                    body: formData // O FormData envia o arquivo
                });

                if (response.ok) {
                    alert('✅ Foto de perfil salva com sucesso!');
                    // Recarrega os dados para que a função renderProfile exiba a nova foto
                    await fetchUserData(); 
                } else {
                    alert('❌ Falha ao carregar a foto no servidor. Verifique o Back-End.');
                }
            } catch (error) {
                console.error('Erro de conexão durante o upload:', error);
                alert('❌ Erro de rede ao tentar subir a foto.');
            } finally {
                uploadButton.innerText = 'Carregar Nova Foto';
                uploadButton.disabled = false;
            }
        });
    }

// ... (Resto da função DOMContentLoaded)
    const burger = document.querySelector('.burger');
    const nav = document.querySelector('.nav-links');
    const navLinks = document.querySelectorAll('.nav-links li');

    if (burger && nav && navLinks) {
        burger.addEventListener('click', () => {
            // Toggle Nav
            nav.classList.toggle('nav-active');

            // Animate Links
            navLinks.forEach((link, index) => {
                if (link.style.animation) {
                    link.style.animation = '';
                } else {
                    link.style.animation = `navLinkFade 0.5s ease forwards ${index / 7 + 0.3}s`;
                }
            });

            // Burger Animation
            burger.classList.toggle('toggle');
        });
    }

    // Scroll suave para âncoras (se houver)
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();

            document.querySelector(this.getAttribute('href')).scrollIntoView({
                behavior: 'smooth'
            });
        });
    });


});