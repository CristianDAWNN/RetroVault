/* src/main/resources/static/js/scripts.js */

document.addEventListener("DOMContentLoaded", function() {
    console.log("RetroVault System Initialized 🚀");

    // ===========================
    // BUSCADOR DE JUEGOS (INTELIGENTE)
    // ===========================
    const searchInput = document.getElementById('searchInput');
    
    if (searchInput) {
        searchInput.addEventListener('keyup', function() {
            const term = this.value.toLowerCase().trim();
            const games = document.querySelectorAll('.game-item');

            games.forEach(game => {
                // Busca en todo el texto de la tarjeta (Título, Consola, Género)
                const fullText = game.textContent.toLowerCase();

                if (fullText.includes(term)) {
                    game.style.display = 'block';
                } else {
                    game.style.display = 'none';
                }
            });
        });
    }

    // ===========================
    //  LÓGICA DE PESTAÑAS
    // ===========================
    const tabButtons = document.querySelectorAll('.val-tab-btn[data-target]');
    
    tabButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            const parent = btn.parentElement;
            parent.querySelectorAll('.val-tab-btn').forEach(b => b.classList.remove('active'));
            
            // Ocultar todos los contenidos relacionados
            const targetId = btn.getAttribute('data-target');
            // Asumimos que los contenidos están al mismo nivel
            document.querySelectorAll('.val-tab-content').forEach(c => c.style.display = 'none');
            
            // Activar botón actual
            btn.classList.add('active');
            
            // Mostrar contenido
            const targetContent = document.getElementById(targetId);
            if(targetContent) {
                targetContent.style.display = 'block';
                targetContent.style.opacity = 0;
                setTimeout(() => targetContent.style.opacity = 1, 50);
            }
        });
    });

    // ===========================
    //  VISTAS ESPECÍFICas DE JUEGOS (MANUAL / IA)
    // ===========================
    const btnManual = document.getElementById('btn-mode-manual');
    const btnAi = document.getElementById('btn-mode-ai');
    const sectionManual = document.getElementById('section-manual');
    const sectionAi = document.getElementById('section-ai');

    if (btnManual && btnAi && sectionManual && sectionAi) {
        
        btnManual.addEventListener('click', () => {
            btnManual.classList.add('active');
            btnAi.classList.remove('active');
            
            sectionManual.style.display = 'block';
            sectionAi.style.display = 'none';
        });

        btnAi.addEventListener('click', () => {
            btnAi.classList.add('active');
            btnManual.classList.remove('active');
            
            sectionAi.style.display = 'block';
            sectionManual.style.display = 'none';
        });
    }

    // ===========================
    // GRÁFICOS (CHART.JS)
    // ===========================
    if (window.retroData && typeof Chart !== 'undefined') {
        
        const commonOptions = {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { position: 'bottom', labels: { color: '#ff4655', font: { family: 'Kanit' } } }
            },
            elements: { arc: { borderWidth: 0 } }
        };

        const colors = ['#ff4655', '#0dcaf0', '#ffc107', '#20c997', '#6610f2', '#d63384', '#fd7e14'];

        // Gráfico Géneros
        const ctxGenre = document.getElementById('genreChart');
        if (ctxGenre) {
            new Chart(ctxGenre, {
                type: 'doughnut',
                data: {
                    labels: window.retroData.genres.labels,
                    datasets: [{
                        data: window.retroData.genres.data,
                        backgroundColor: colors,
                        hoverOffset: 10
                    }]
                },
                options: commonOptions
            });
        }

        // Gráfico Consolas
        const ctxConsole = document.getElementById('consoleChart');
        if (ctxConsole) {
            new Chart(ctxConsole, {
                type: 'doughnut',
                data: {
                    labels: window.retroData.consoles.labels,
                    datasets: [{
                        data: window.retroData.consoles.data,
                        backgroundColor: colors,
                        hoverOffset: 10
                    }]
                },
                options: commonOptions
            });
        }
    }
});