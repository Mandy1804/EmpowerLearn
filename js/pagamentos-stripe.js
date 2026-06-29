(function () {
  const API_BASE_URL = (window.APP_CONFIG && window.APP_CONFIG.API_BASE_URL) || 'http://localhost:8080/api';

  function obterUsuarioAtual() {
    return {
      usuarioId: Number(localStorage.getItem('userId') || 1),
      usuarioTipo: (localStorage.getItem('userType') || 'ALUNO').toUpperCase(),
      nome: localStorage.getItem('userName') || localStorage.getItem('nome') || 'Cliente Teste',
      email: localStorage.getItem('email') || 'cliente.teste@empowerlearn.com.br'
    };
  }

  function baseUrlFront() {
    const path = window.location.pathname;
    const idx = path.lastIndexOf('/');
    const dir = idx >= 0 ? path.substring(0, idx) : '';
    return `${window.location.origin}${dir}`;
  }

  function formatarMoeda(valor) {
    const n = Number(valor || 0);
    if (n === 0) return 'Grátis';
    return n.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  }

  function mostrarErro(msg) {
    const el = document.getElementById('el-alerta-planos');
    if (!el) return alert(msg);
    el.textContent = msg;
    el.style.display = 'block';
  }

  function recursosParaLista(recursos) {
    if (!recursos) return [];
    if (Array.isArray(recursos)) return recursos;
    return String(recursos).split(/[|;]/).map((r) => r.trim()).filter(Boolean);
  }

  function ordemPlano(codigo) {
    const ordem = { GRATUITO: 0, BASICO: 1, PRO: 2, PREMIUM: 3 };
    return ordem[String(codigo || '').toUpperCase()] ?? 99;
  }

  function renderPlano(plano) {
    const card = document.createElement('article');
    const codigo = String(plano.codigo || '').toUpperCase();
    const gratuito = Number(plano.valor || 0) === 0 || codigo === 'GRATUITO';
    card.className = `el-plano-card ${codigo === 'PRO' ? 'destaque' : ''} ${gratuito ? 'gratuito' : ''}`;

    const recursos = recursosParaLista(plano.recursos)
      .map((recurso) => `<li>${recurso}</li>`)
      .join('');

    const badge = gratuito
      ? '<span class="el-plano-badge">Uso inicial gratuito</span>'
      : (codigo === 'PRO' ? '<span class="el-plano-badge">Ideal para instituições</span>' : '');

    card.innerHTML = `
      ${badge}
      <h2>${plano.nome || codigo}</h2>
      <p class="el-plano-descricao">${plano.descricao || ''}</p>
      <div class="el-plano-preco">${formatarMoeda(plano.valor)}</div>
      <div class="el-plano-periodo">${gratuito ? 'sem cobrança' : (plano.periodicidade || 'mensal')}</div>
      <ul class="el-plano-recursos">${recursos}</ul>
      <button class="el-btn-pagar ${gratuito ? 'gratis' : ''}" data-plano="${codigo}">
        ${gratuito ? 'Começar grátis' : 'Ampliar acesso'}
      </button>
    `;

    card.querySelector('button').addEventListener('click', function () {
      if (gratuito) {
        const usuario = obterUsuarioAtual();
        if (!localStorage.getItem('token')) {
          window.location.href = 'login.html';
          return;
        }
        window.location.href = usuario.usuarioTipo === 'PROFESSOR' ? 'daschboard-professor.html' : 'daschboard.html';
        return;
      }
      iniciarCheckout(codigo, this);
    });

    return card;
  }

  async function carregarPlanos() {
    const grid = document.getElementById('el-planos-grid');
    if (!grid) return;

    try {
      grid.innerHTML = '<p class="el-loading">Carregando planos...</p>';
      const resp = await fetch(`${API_BASE_URL}/planos`);
      if (!resp.ok) throw new Error(`Erro ao carregar planos: HTTP ${resp.status}`);
      const planos = await resp.json();
      grid.innerHTML = '';
      planos
        .slice()
        .sort((a, b) => ordemPlano(a.codigo) - ordemPlano(b.codigo))
        .forEach((plano) => grid.appendChild(renderPlano(plano)));
    } catch (err) {
      console.error(err);
      grid.innerHTML = '';
      mostrarErro('Não foi possível carregar os planos. Confira se o backend está rodando na porta 8080.');
    }
  }

  async function iniciarCheckout(planoCodigo, botao) {
    const usuario = obterUsuarioAtual();

    if (usuario.usuarioTipo === 'PROFESSOR') {
      mostrarErro('Professor não precisa pagar plano. O acesso pago é voltado para alunos, responsáveis e instituições que precisam ampliar limites de uso.');
      return;
    }

    const textoOriginal = botao.textContent;
    botao.disabled = true;
    botao.textContent = 'Abrindo pagamento...';

    try {
      const resp = await fetch(`${API_BASE_URL}/pagamentos/stripe/checkout`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          planoCodigo,
          usuarioId: usuario.usuarioId,
          usuarioTipo: usuario.usuarioTipo,
          nome: usuario.nome,
          email: usuario.email,
          backBaseUrl: baseUrlFront()
        })
      });

      const data = await resp.json().catch(() => ({}));
      if (!resp.ok) {
        const erroOriginal = String(data.message || data.erro || '');
        if (/stripe|api key|checkout/i.test(erroOriginal)) {
          throw new Error('Não foi possível iniciar o pagamento. Verifique a configuração de pagamento ou tente novamente em instantes.');
        }
        throw new Error(erroOriginal || `HTTP ${resp.status}`);
      }
      if (!data.checkoutUrl) throw new Error('Não foi possível abrir a página de pagamento.');
      window.location.href = data.checkoutUrl;
    } catch (err) {
      console.error(err);
      mostrarErro(err.message || 'Não foi possível iniciar o pagamento.');
      botao.disabled = false;
      botao.textContent = textoOriginal;
    }
  }

  async function carregarStatusRetorno() {
    const statusEl = document.getElementById('el-status-pagamento');
    if (!statusEl) return;

    const params = new URLSearchParams(window.location.search);
    const sessionId = params.get('session_id');
    const statusUrl = params.get('status');

    if (!sessionId) {
      if (statusUrl === 'cancelado') {
        statusEl.textContent = 'Pagamento cancelado. Você pode voltar aos planos e escolher novamente quando quiser.';
      } else if (statusUrl === 'gratuito') {
        statusEl.textContent = 'Plano gratuito ativado. Você já pode continuar usando os recursos iniciais da EmpowerLearn.';
      } else {
        statusEl.textContent = 'Retorno recebido. Você pode voltar aos planos ou continuar navegando pela plataforma.';
      }
      return;
    }

    try {
      const resp = await fetch(`${API_BASE_URL}/pagamentos/stripe/status/${encodeURIComponent(sessionId)}`);
      const data = await resp.json();
      if (!resp.ok) {
        const erroOriginal = String(data.message || data.erro || '');
        if (/stripe|api key|checkout/i.test(erroOriginal)) {
          throw new Error('Não foi possível iniciar o pagamento. Verifique a configuração de pagamento ou tente novamente em instantes.');
        }
        throw new Error(erroOriginal || `HTTP ${resp.status}`);
      }
      const status = data.paymentStatus === 'paid' || data.status === 'PAGO' ? 'pagamento aprovado' : (data.status || data.paymentStatus || 'recebido');
      statusEl.textContent = `Status: ${status}. Plano: ${data.planoCodigo || '-'}.`;
    } catch (err) {
      console.error(err);
      statusEl.textContent = 'Retorno recebido. Caso seu acesso não seja atualizado, tente novamente ou procure o suporte da plataforma.';
    }
  }

  document.addEventListener('DOMContentLoaded', function () {
    carregarPlanos();
    carregarStatusRetorno();
  });
})();
