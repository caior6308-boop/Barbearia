;(function () {
  'use strict';

  /* ========== TOAST SYSTEM ========== */

  function criarToastContainer() {
    if (document.getElementById('toast-container')) return;
    var container = document.createElement('div');
    container.id = 'toast-container';
    container.style.cssText =
      'position:fixed;top:20px;right:20px;z-index:99999;display:flex;flex-direction:column;gap:8px;max-width:400px;width:100%;pointer-events:none';
    document.body.appendChild(container);
  }

  function mostrarToast(mensagem, tipo) {
    tipo = tipo || 'success';
    criarToastContainer();

    var toast = document.createElement('div');
    toast.style.cssText =
      'pointer-events:auto;padding:14px 20px;border-radius:10px;font-weight:600;font-size:0.875rem;box-shadow:0 8px 32px rgba(0,0,0,0.15);animation:slideInRight 0.3s ease-out;display:flex;align-items:center;gap:10px;transform-origin:right';

    var icone = document.createElement('span');
    icone.style.cssText = 'font-size:1.125rem;line-height:1';

    if (tipo === 'success') {
      toast.style.background = '#ecfdf5';
      toast.style.color = '#059669';
      toast.style.border = '1px solid #a7f3d0';
      icone.textContent = '\u2713';
    } else if (tipo === 'error') {
      toast.style.background = '#fef2f2';
      toast.style.color = '#dc2626';
      toast.style.border = '1px solid #fecaca';
      icone.textContent = '\u2717';
    } else if (tipo === 'warning') {
      toast.style.background = '#fffbeb';
      toast.style.color = '#d97706';
      toast.style.border = '1px solid #fde68a';
      icone.textContent = '\u26A0';
    }

    toast.appendChild(icone);
    var texto = document.createElement('span');
    texto.textContent = mensagem;
    toast.appendChild(texto);

    var closeBtn = document.createElement('button');
    closeBtn.innerHTML = '\u00D7';
    closeBtn.style.cssText =
      'margin-left:auto;background:none;border:none;font-size:1.25rem;cursor:pointer;color:inherit;opacity:0.5;padding:0 0 0 8px;line-height:1';
    closeBtn.onclick = function () {
      fecharToast(toast);
    };
    toast.appendChild(closeBtn);

    document.getElementById('toast-container').appendChild(toast);

    setTimeout(function () {
      fecharToast(toast);
    }, 5000);
  }

  function fecharToast(toast) {
    toast.style.animation = 'slideOutRight 0.25s ease-in forwards';
    setTimeout(function () {
      if (toast.parentNode) toast.parentNode.removeChild(toast);
    }, 250);
  }

  /* Inject animation keyframes */
  var styleSheet = document.createElement('style');
  styleSheet.textContent =
    '@keyframes slideInRight{from{opacity:0;transform:translateX(100%) scale(0.9)}to{opacity:1;transform:translateX(0) scale(1)}}@keyframes slideOutRight{from{opacity:1;transform:translateX(0) scale(1)}to{opacity:0;transform:translateX(100%) scale(0.9)}}';
  document.head.appendChild(styleSheet);

  /* ========== PHONE MASK ========== */

  function aplicarMascaraTelefone(input) {
    input.addEventListener('input', function () {
      var valor = this.value.replace(/\D/g, '');
      if (valor.length > 11) valor = valor.slice(0, 11);
      if (valor.length <= 2) {
        this.value = '(' + valor;
      } else if (valor.length <= 7) {
        this.value = '(' + valor.slice(0, 2) + ') ' + valor.slice(2);
      } else {
        this.value =
          '(' +
          valor.slice(0, 2) +
          ') ' +
          valor.slice(2, 7) +
          '-' +
          valor.slice(7);
      }
    });
  }

  /* ========== CONFIRM DIALOG ========== */

  function confirmarAcao(mensagem, callback) {
    if (confirm(mensagem)) {
      callback();
    }
  }

  /* ========== INIT ========== */

  document.addEventListener('DOMContentLoaded', function () {
    /* Alternar telas de login/cadastro/recuperar */
    var loginBody = document.getElementById('pagina-login');
    if (loginBody && loginBody.getAttribute('data-abrir-cadastro') === 'true') {
      alternarTelas('cadastro');
    }

    /* Phone mask on all tel inputs */
    document.querySelectorAll('input[type="text"][name="telefone"]').forEach(aplicarMascaraTelefone);

    /* Confirm on delete buttons */
    document.querySelectorAll('form button[type="submit"].btn-outline-danger').forEach(function (btn) {
      btn.addEventListener('click', function (e) {
        if (!confirm('Tem certeza que deseja excluir este item?')) {
          e.preventDefault();
        }
      });
    });

    /* Auto-dismiss alerts after 5s */
    document.querySelectorAll('.alert-dismissible').forEach(function (alert) {
      setTimeout(function () {
        var closeBtn = alert.querySelector('.btn-close');
        if (closeBtn) closeBtn.click();
      }, 5000);
    });
  });

  /* ========== GLOBAL FUNCTIONS ========== */

  window.alternarTelas = function (tela) {
    var sectionLogin = document.getElementById('section-login');
    var sectionCadastro = document.getElementById('section-cadastro');
    var sectionRecuperar = document.getElementById('section-recuperar');

    if (sectionLogin) sectionLogin.classList.remove('active');
    if (sectionCadastro) sectionCadastro.classList.remove('active');
    if (sectionRecuperar) sectionRecuperar.classList.remove('active');

    var alvo = document.getElementById('section-' + tela);
    if (alvo) alvo.classList.add('active');
  };

  window.mostrarToast = mostrarToast;
})();
