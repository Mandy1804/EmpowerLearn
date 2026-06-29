
(function(){
  window.elLogout = function(destino){
    ['token','userId','userType','userName','email','nome'].forEach(function(k){ localStorage.removeItem(k); });
    window.location.href = destino || 'login.html';
  };

  document.addEventListener('DOMContentLoaded', function(){
    var type = (localStorage.getItem('userType') || '').toLowerCase();
    document.querySelectorAll('#link-contratados').forEach(function(el){ el.style.display = type === 'professor' ? 'none' : 'flex'; });
    document.querySelectorAll('#link-contratacoes-prof').forEach(function(el){ el.style.display = type === 'professor' ? 'flex' : 'none'; });

    document.querySelectorAll('a').forEach(function(a){
      var txt=(a.textContent||'').trim().toLowerCase();
      if(txt === 'sair' || txt.endsWith(' sair')){
        if(!a.dataset.elLogoutBound){
          a.dataset.elLogoutBound = '1';
          a.addEventListener('click', function(ev){
            var href=a.getAttribute('href') || '';
            if(href === '#' || href === '' || a.getAttribute('onclick')){
              ev.preventDefault();
              if(confirm('Sair?')) window.elLogout('login.html');
            }
          }, true);
        }
      }
    });
  });
})();
