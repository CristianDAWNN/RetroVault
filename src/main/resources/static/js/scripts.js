document.addEventListener("DOMContentLoaded", function() {

    //  BUSCADOR DE JUEGOS
    const searchInput = document.getElementById('searchInput');
    
    if (searchInput) {
        searchInput.addEventListener('keyup', function() {
            const term = this.value.toLowerCase().trim();
            const games = document.querySelectorAll('.game-item');

            games.forEach(game => {
                const fullText = game.textContent.toLowerCase(); //Lógica para buscar por nombre, consola o genero

                if (fullText.includes(term)) {
                    game.style.display = 'block';
                } else {
                    game.style.display = 'none';
                }
            });
        });
    }

    //  LÓGICA DE PESTAÑAS
    const tabButtons = document.querySelectorAll('.val-tab-btn');
    
    tabButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.val-tab-btn').forEach(b => b.classList.remove('active'));
            document.querySelectorAll('.val-tab-content').forEach(c => c.style.display = 'none');
            
            btn.classList.add('active');
            const targetId = btn.getAttribute('data-target');
            const targetContent = document.getElementById(targetId);
            if(targetContent) {
                targetContent.style.display = 'block';
                targetContent.style.opacity = 0;
                setTimeout(() => targetContent.style.opacity = 1, 50);
            }
        });
    });

    //  GRÁFICOS (CHART.JS)
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