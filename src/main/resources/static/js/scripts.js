/* src/main/resources/static/js/scripts.js */

document.addEventListener("DOMContentLoaded", function() {
    console.log("RetroVault System Initialized 🚀");

    // ===========================
    //  BUSCADOR EN GAMES
    // ===========================
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('keyup', function() {
            const term = this.value.toLowerCase().trim();
            const games = document.querySelectorAll('.game-item');
            games.forEach(game => {
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
            const targetId = btn.getAttribute('data-target');
            document.querySelectorAll('.val-tab-content').forEach(c => c.style.display = 'none');
            btn.classList.add('active');
            const targetContent = document.getElementById(targetId);
            if(targetContent) {
                targetContent.style.display = 'block';
                targetContent.style.opacity = 0;
                setTimeout(() => targetContent.style.opacity = 1, 50);
            }
        });
    });

    // ===========================
    //  PESTAÑAS MANUAL / IA en añadir games
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
    //  GRÁFICOS (CHART.JS)
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

    // ===========================
    //  LÓGICA DE ESCANEO IA
    // ===========================
    const btnScan = document.getElementById('btn-scan-now');
    const aiInput = document.getElementById('ai-file-input');
    const loadingDiv = document.getElementById('ai-loading');
    const uploadDiv = document.getElementById('ai-upload-area');

    const progressBar = document.getElementById('ai-progress-bar');
    const loadingText = document.getElementById('ai-loading-text');
    const loadingSubtext = document.getElementById('ai-loading-subtext');

    if (btnScan && aiInput) {
        btnScan.addEventListener('click', function() {
            
            // GUARDAMOS LA PORTADA
            const file = aiInput.files[0];
            
            if (!file) {
                alert("⚠️ Por favor, selecciona una imagen primero.");
                return;
            }

            // CAMBIO DE UI
            uploadDiv.style.display = 'none';
            loadingDiv.style.display = 'block';

            // INICIAR ANIMACIÓN DE BARRA
            let progress = 0;
            progressBar.style.width = '0%';
            progressBar.innerText = '0%';
            progressBar.classList.remove('bg-success');
            progressBar.classList.add('bg-val-red');
            loadingText.innerText = "ANALIZANDO PIXELES...";
            loadingSubtext.innerText = "Conectando con Gemini...";

            const interval = setInterval(() => {
                if (progress < 90) {
                    progress += Math.random() * 5; 
                    if(progress > 90) progress = 90;
                    
                    progressBar.style.width = progress + '%';
                    progressBar.innerText = Math.round(progress) + '%';
                    
                    if(progress > 20 && progress < 50) loadingSubtext.innerText = "Identificando plataforma...";
                    if(progress >= 50 && progress < 80) loadingSubtext.innerText = "Leyendo textos de la portada...";
                    if(progress >= 80) loadingSubtext.innerText = "Generando estructura JSON...";
                }
            }, 200);

            // ENVIAR PETICION
            const formData = new FormData();
            formData.append('file', file);

            fetch('/games/api/scan', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                // EXITO COMPLETAR BARRA
                clearInterval(interval);
                progressBar.style.width = '100%';
                progressBar.innerText = '100%';
                progressBar.classList.remove('bg-val-red');
                progressBar.classList.add('bg-success');
                loadingText.innerText = "¡ANÁLISIS COMPLETADO!";
                loadingSubtext.innerText = "Transfiriendo datos al formulario...";

                setTimeout(() => {
                    console.log("Datos de la IA:", data);

                    if (data.error) {
                        alert("❌ Error: " + data.error);
                        loadingDiv.style.display = 'none';
                        uploadDiv.style.display = 'block';
                    } else {
                        // RELLENAR CAMPOS
                        if(data.title) document.getElementById('title').value = data.title;
                        
                        if(data.release_date) {
                            document.getElementById('launchDate').value = data.release_date;
                        } else if (data.year) {
                            document.getElementById('launchDate').value = data.year + "-01-01";
                        }
                        
                        if(data.rate) document.getElementById('rate').value = data.rate;

                        // TRANSFERIR IMAGEN
                        const manualInput = document.querySelector('#section-manual input[name="file"]');
                        if (manualInput && file) { 
                            const dataTransfer = new DataTransfer();
                            dataTransfer.items.add(file);
                            manualInput.files = dataTransfer.files;
                            
                            manualInput.style.border = "2px solid #0dcaf0";
                            manualInput.style.boxShadow = "0 0 10px rgba(13, 202, 240, 0.5)";
                        }

                        // SELECT CONSOLA INTELIGENTE
                        if (data.console) {
                            const consoleSelect = document.getElementById('console');
                            const aiConsole = data.console.toLowerCase().replace(/\s/g, ''); 
                            let found = false;

                            for (let i = 0; i < consoleSelect.options.length; i++) {
                                const dbConsole = consoleSelect.options[i].text.toLowerCase().replace(/\s/g, '');
                                
                                if (dbConsole.includes(aiConsole) || aiConsole.includes(dbConsole)) { consoleSelect.selectedIndex = i; found = true; break; }
                                
                                if ((aiConsole === 'nes' || aiConsole === 'famicom') && dbConsole.includes('nes')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'snes' || aiConsole === 'superfamicom' || aiConsole === 'supernintendo') && dbConsole.includes('snes')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'n64' || aiConsole === 'nintendo64') && dbConsole.includes('nintendo64')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'gc' || aiConsole === 'gamecube') && dbConsole.includes('gamecube')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'wii') && dbConsole === 'wii') { consoleSelect.selectedIndex = i; found = true; break; } 
                                if ((aiConsole === 'wiiu') && dbConsole.includes('wiiu')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'switch' || aiConsole === 'nintendoswitch' || aiConsole === 'ns') && dbConsole.includes('switch')) { consoleSelect.selectedIndex = i; found = true; break; }
                                
                                if ((aiConsole === 'gba' || aiConsole === 'gameboyadvance') && dbConsole.includes('advance')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'gbc' || aiConsole === 'gameboycolor') && dbConsole.includes('color')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'gb' || aiConsole === 'gameboy') && dbConsole === 'gameboy') { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'nds' || aiConsole === 'ds' || aiConsole === 'nintendods') && dbConsole.includes('nintendods')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === '3ds' || aiConsole === 'nintendo3ds') && dbConsole.includes('3ds')) { consoleSelect.selectedIndex = i; found = true; break; }

                                if ((aiConsole === 'ps1' || aiConsole === 'psx' || aiConsole === 'playstation') && dbConsole.includes('playstation1')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'ps2' || aiConsole === 'playstation2') && dbConsole.includes('playstation2')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'ps3' || aiConsole === 'playstation3') && dbConsole.includes('playstation3')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'ps4' || aiConsole === 'playstation4') && dbConsole.includes('playstation4')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'ps5' || aiConsole === 'playstation5') && dbConsole.includes('playstation5')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'psp' || aiConsole === 'playstationportable') && dbConsole.includes('psp')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'vita' || aiConsole === 'psvita') && dbConsole.includes('vita')) { consoleSelect.selectedIndex = i; found = true; break; }

                                if ((aiConsole === 'xbox') && dbConsole === 'xbox') { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'xbox360' || aiConsole === '360') && dbConsole.includes('360')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'xboxone' || aiConsole === 'xone') && dbConsole.includes('one')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'xboxseries' || aiConsole === 'seriesx' || aiConsole === 'seriess') && dbConsole.includes('series')) { consoleSelect.selectedIndex = i; found = true; break; }

                                if ((aiConsole === 'sms' || aiConsole === 'mastersystem') && dbConsole.includes('mastersystem')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'md' || aiConsole === 'genesis' || aiConsole === 'megadrive') && dbConsole.includes('genesis')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'saturn' || aiConsole === 'segasaturn') && dbConsole.includes('saturn')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'dc' || aiConsole === 'dreamcast') && dbConsole.includes('dreamcast')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'gg' || aiConsole === 'gamegear') && dbConsole.includes('gamegear')) { consoleSelect.selectedIndex = i; found = true; break; }

                                if ((aiConsole === 'steam' || aiConsole === 'steamdeck') && dbConsole.includes('steam')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'pc' || aiConsole === 'windows') && dbConsole.includes('pcgaming')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'neogeo' || aiConsole === 'aes') && dbConsole.includes('neogeo')) { consoleSelect.selectedIndex = i; found = true; break; }
                            }

                            // SI NO SE ENCUENTRA LA CONSOLA
                            if (!found) {
                                consoleSelect.value = "";
                                consoleSelect.style.border = "2px solid #ff4655";
                                alert(`⚠️ La IA detectó "${data.console}" pero no tienes esa consola en tu lista.\nPuedes añadirla con el botón "+"`);
                            } else {
                                consoleSelect.style.border = "2px solid #0dcaf0";
                            }
                        }

                        // SELECT GÉNERO 
                        if (data.genre) {
                            const genreSelect = document.getElementById('genre');
                            for (let i = 0; i < genreSelect.options.length; i++) {
                                if (genreSelect.options[i].text.toLowerCase().includes(data.genre.toLowerCase())) {
                                    genreSelect.selectedIndex = i;
                                    break;
                                }
                            }
                        }

                        // CAMBIO DE PESTAÑA AUTOMÁTICO
                        loadingDiv.style.display = 'none';
                        uploadDiv.style.display = 'block'; 
                        document.getElementById('btn-mode-manual').click(); 
                        document.getElementById('title').focus();
                    }
                }, 800);
            })
            .catch(error => {
                clearInterval(interval);
                console.error('Error:', error);
                alert("❌ Error de conexión o respuesta inválida.");
                loadingDiv.style.display = 'none';
                uploadDiv.style.display = 'block';
            })
            .finally(() => {
                aiInput.value = '';
            });
        });
    }

    // ===========================
    //  RECOMENDACIONES
    // ===========================
    const btnRecManual = document.getElementById('btn-rec-manual');
    const btnRecAi = document.getElementById('btn-rec-ai');
    const sectionRecManual = document.getElementById('section-rec-manual');
    const sectionRecAi = document.getElementById('section-rec-ai');

    // CAMBIAR PESTAÑAS
    if (btnRecManual && btnRecAi) {
        btnRecManual.addEventListener('click', () => {
            btnRecManual.classList.add('active', 'btn-danger');
            btnRecManual.classList.remove('btn-outline-danger');
            btnRecAi.classList.remove('active', 'btn-danger');
            btnRecAi.classList.add('btn-outline-danger');
            sectionRecManual.style.display = 'block';
            sectionRecAi.style.display = 'none';
        });

        btnRecAi.addEventListener('click', () => {
            btnRecAi.classList.add('active', 'btn-danger');
            btnRecAi.classList.remove('btn-outline-danger');
            btnRecManual.classList.remove('active', 'btn-danger');
            btnRecManual.classList.add('btn-outline-danger');
            sectionRecAi.style.display = 'block';
            sectionRecManual.style.display = 'none';
        });
    }

    // PETICION IA
    const btnGetAiRec = document.getElementById('btn-get-ai-rec');
    if (btnGetAiRec) {
        btnGetAiRec.addEventListener('click', function() {
            const genre = document.getElementById('ai-genre-input').value.trim();
            if(!genre) { alert("Escribe qué estás buscando primero"); return; }

            const resultsDiv = document.getElementById('ai-rec-results');
            
            // INYECTAR BARRA DE CARGA HTML
            resultsDiv.innerHTML = `
                <div id="ai-rec-loading" class="mt-4 fade-in-up">
                    <div class="text-center mb-3">
                        <h5 id="ai-rec-loading-text" class="text-val-red fw-bold val-font mb-1">CONSULTANDO ORÁCULO...</h5>
                        <p id="ai-rec-loading-subtext" class="text-secondary small mb-0">Conectando con Gemini...</p>
                    </div>
                    <div class="progress val-progress" style="height: 25px; background-color: #1f2326; border: 1px solid #333;">
                        <div id="ai-rec-progress-bar" class="progress-bar bg-val-red progress-bar-striped progress-bar-animated" role="progressbar" style="width: 0%; font-weight: bold;">0%</div>
                    </div>
                </div>
            `;

            const progressBar = document.getElementById('ai-rec-progress-bar');
            const loadingText = document.getElementById('ai-rec-loading-text');
            const loadingSubtext = document.getElementById('ai-rec-loading-subtext');

            // INICIAR ANIMACION
            let progress = 0;
            const interval = setInterval(() => {
                if (progress < 90) {
                    progress += Math.random() * 5; 
                    if(progress > 90) progress = 90;
                    
                    progressBar.style.width = progress + '%';
                    progressBar.innerText = Math.round(progress) + '%';
                    
                    // TEXTOS DINAMICOS
                    if(progress > 25 && progress < 55) loadingSubtext.innerText = "Buscando en los archivos históricos...";
                    if(progress >= 55 && progress < 85) loadingSubtext.innerText = "Filtrando por consolas y preferencias...";
                    if(progress >= 85) loadingSubtext.innerText = "Generando lista de recomendaciones...";
                }
            }, 200);

            // ENVIAR PETICION
            fetch(`/api/recommendations/ai?genre=${encodeURIComponent(genre)}`)
                .then(response => response.json())
                .then(data => {
                    // EXITO COMPLETAR BARRA
                    clearInterval(interval);
                    progressBar.style.width = '100%';
                    progressBar.innerText = '100%';
                    progressBar.classList.remove('bg-val-red');
                    progressBar.classList.add('bg-success');
                    
                    loadingText.innerText = "¡RECOMENDACIONES LISTAS!";
                    loadingText.classList.remove('text-val-red');
                    loadingText.classList.add('text-success');
                    loadingSubtext.innerText = "Mostrando resultados...";

                    // ESPERAMOS Y MOSTRAMOS RESULTADOS
                    setTimeout(() => {
                        if (data.length > 0 && data[0].error) {
                            resultsDiv.innerHTML = `<div class="alert alert-danger">Error: ${data[0].error}</div>`;
                            return;
                        }
                        let html = '<div class="row mt-4">';
                        data.forEach((game, index) => {
                            html += `
                            <div class="col-12 mb-3 fade-in-up" style="animation-delay: ${index * 0.1}s">
                                <div class="val-dashboard-card border-start border-danger border-4">
                                    <h5 class="text-white fw-bold mb-1">${game.title}</h5>
                                    <span class="badge bg-dark border border-danger text-val-red mb-2">${game.console}</span>
                                    <p class="text-secondary small mb-0"><i>"${game.reason}"</i></p>
                                </div>
                            </div>`;
                        });
                        html += '</div>';
                        resultsDiv.innerHTML = html;
                    }, 800);
                })
                .catch(() => {
                    clearInterval(interval);
                    resultsDiv.innerHTML = '<div class="alert alert-danger mt-4">Error al conectar con la IA.</div>';
                });
        });
    }

    // PETICION MANUAL
    const btnGetManualRec = document.getElementById('btn-get-manual-rec');
    if (btnGetManualRec) {
        btnGetManualRec.addEventListener('click', function() {
            const genre = document.getElementById('manual-genre-input').value;
            if(!genre) { alert("Selecciona un género de la lista"); return; }

            const resultsDiv = document.getElementById('manual-rec-results');
            resultsDiv.innerHTML = '<div class="text-center text-val-red my-4"><div class="spinner-border"></div></div>';

            fetch(`/api/recommendations/manual?genre=${encodeURIComponent(genre)}`)
                .then(res => res.json())
                .then(data => {
                    if(data.length === 0) {
                        resultsDiv.innerHTML = '<div class="alert alert-dark text-center text-white border-danger">No hay juegos de este género en la base de datos todavía. ¡Sé el primero en añadir uno!</div>';
                        return;
                    }
                    let html = '<div class="row">';
                    data.forEach((game, index) => {
                        const consoleName = game.consoleName || "Desconocida";
                        html += `
                        <div class="col-12 mb-3 fade-in-up" style="animation-delay: ${index * 0.1}s">
                            <div class="val-dashboard-card border-start border-danger border-4 d-flex justify-content-between align-items-center">
                                <div>
                                    <h5 class="text-white fw-bold mb-1">${game.title}</h5>
                                    <span class="badge bg-dark border border-secondary text-secondary">${consoleName}</span>
                                </div>
                                <div class="text-center">
                                    <span class="d-block text-val-red small fw-bold">NOTA</span>
                                    <span class="badge bg-val-red fs-5">${game.rate}/10</span>
                                </div>
                            </div>
                        </div>`;
                    });
                    html += '</div>';
                    resultsDiv.innerHTML = html;
                })
                .catch(() => resultsDiv.innerHTML = '<div class="alert alert-danger">Error al cargar la base de datos.</div>');
        });
    }
});