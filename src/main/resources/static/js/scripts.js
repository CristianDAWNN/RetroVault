/* src/main/resources/static/js/scripts.js */

document.addEventListener("DOMContentLoaded", function() {
    console.log("RetroVault System Initialized 🚀");

    // ===========================
    //  1. BUSCADOR INTELIGENTE
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
    //  2. LÓGICA DE PESTAÑAS (General)
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
    //  3. PESTAÑAS MANUAL / IA (Específico Formulario Juego)
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
    //  4. GRÁFICOS (CHART.JS)
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
    //  5. LÓGICA DE ESCANEO IA (CORE)
    // ===========================
    const btnScan = document.getElementById('btn-scan-now');
    const aiInput = document.getElementById('ai-file-input');
    const loadingDiv = document.getElementById('ai-loading');
    const uploadDiv = document.getElementById('ai-upload-area');

    // Elementos de la barra de progreso
    const progressBar = document.getElementById('ai-progress-bar');
    const loadingText = document.getElementById('ai-loading-text');
    const loadingSubtext = document.getElementById('ai-loading-subtext');

    if (btnScan && aiInput) {
        btnScan.addEventListener('click', function() {
            
            // IMPORTANTE: Guardamos el archivo AQUI, antes de que pase nada más
            const file = aiInput.files[0];
            
            if (!file) {
                alert("⚠️ Por favor, selecciona una imagen primero.");
                return;
            }

            // 1. CAMBIO DE UI
            uploadDiv.style.display = 'none';
            loadingDiv.style.display = 'block';

            // 2. INICIAR ANIMACIÓN DE BARRA
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

            // 3. ENVIAR PETICIÓN
            const formData = new FormData();
            formData.append('file', file);

            fetch('/games/api/scan', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                // 4. ÉXITO: COMPLETAR BARRA
                clearInterval(interval);
                progressBar.style.width = '100%';
                progressBar.innerText = '100%';
                progressBar.classList.remove('bg-val-red');
                progressBar.classList.add('bg-success');
                loadingText.innerText = "¡ANÁLISIS COMPLETADO!";
                loadingSubtext.innerText = "Transfiriendo datos al formulario...";

                // Esperamos un poco para que el usuario vea el éxito
                setTimeout(() => {
                    console.log("Datos de la IA:", data);

                    if (data.error) {
                        alert("❌ Error: " + data.error);
                        loadingDiv.style.display = 'none';
                        uploadDiv.style.display = 'block';
                    } else {
                        // --- RELLENAR CAMPOS ---
                        
                        // Título
                        if(data.title) document.getElementById('title').value = data.title;
                        
                        // Fecha Exacta
                        if(data.release_date) {
                            document.getElementById('launchDate').value = data.release_date;
                        } else if (data.year) {
                            document.getElementById('launchDate').value = data.year + "-01-01";
                        }
                        
                        // Nota
                        if(data.rate) document.getElementById('rate').value = data.rate;

                        // --- TRANSFERIR IMAGEN (USANDO VARIABLE SEGURA 'file') ---
                        const manualInput = document.querySelector('#section-manual input[name="file"]');
                        if (manualInput && file) { 
                            const dataTransfer = new DataTransfer();
                            dataTransfer.items.add(file);
                            manualInput.files = dataTransfer.files;
                            
                            // Efecto visual
                            manualInput.style.border = "2px solid #0dcaf0";
                            manualInput.style.boxShadow = "0 0 10px rgba(13, 202, 240, 0.5)";
                        }

                        // --- SELECT CONSOLA INTELIGENTE (MEGA LISTA) ---
                        if (data.console) {
                            const consoleSelect = document.getElementById('console');
                            const aiConsole = data.console.toLowerCase().replace(/\s/g, ''); 
                            let found = false;

                            for (let i = 0; i < consoleSelect.options.length; i++) {
                                const dbConsole = consoleSelect.options[i].text.toLowerCase().replace(/\s/g, '');
                                
                                // Coincidencia básica
                                if (dbConsole.includes(aiConsole) || aiConsole.includes(dbConsole)) { consoleSelect.selectedIndex = i; found = true; break; }
                                
                                // --- NINTENDO ---
                                if ((aiConsole === 'nes' || aiConsole === 'famicom') && dbConsole.includes('nes')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'snes' || aiConsole === 'superfamicom' || aiConsole === 'supernintendo') && dbConsole.includes('snes')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'n64' || aiConsole === 'nintendo64') && dbConsole.includes('nintendo64')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'gc' || aiConsole === 'gamecube') && dbConsole.includes('gamecube')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'wii') && dbConsole === 'wii') { consoleSelect.selectedIndex = i; found = true; break; } 
                                if ((aiConsole === 'wiiu') && dbConsole.includes('wiiu')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'switch' || aiConsole === 'nintendoswitch' || aiConsole === 'ns') && dbConsole.includes('switch')) { consoleSelect.selectedIndex = i; found = true; break; }
                                
                                // Portátiles Nintendo
                                if ((aiConsole === 'gba' || aiConsole === 'gameboyadvance') && dbConsole.includes('advance')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'gbc' || aiConsole === 'gameboycolor') && dbConsole.includes('color')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'gb' || aiConsole === 'gameboy') && dbConsole === 'gameboy') { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'nds' || aiConsole === 'ds' || aiConsole === 'nintendods') && dbConsole.includes('nintendods')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === '3ds' || aiConsole === 'nintendo3ds') && dbConsole.includes('3ds')) { consoleSelect.selectedIndex = i; found = true; break; }

                                // --- SONY ---
                                if ((aiConsole === 'ps1' || aiConsole === 'psx' || aiConsole === 'playstation') && dbConsole.includes('playstation1')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'ps2' || aiConsole === 'playstation2') && dbConsole.includes('playstation2')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'ps3' || aiConsole === 'playstation3') && dbConsole.includes('playstation3')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'ps4' || aiConsole === 'playstation4') && dbConsole.includes('playstation4')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'ps5' || aiConsole === 'playstation5') && dbConsole.includes('playstation5')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'psp' || aiConsole === 'playstationportable') && dbConsole.includes('psp')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'vita' || aiConsole === 'psvita') && dbConsole.includes('vita')) { consoleSelect.selectedIndex = i; found = true; break; }

                                // --- MICROSOFT ---
                                if ((aiConsole === 'xbox') && dbConsole === 'xbox') { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'xbox360' || aiConsole === '360') && dbConsole.includes('360')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'xboxone' || aiConsole === 'xone') && dbConsole.includes('one')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'xboxseries' || aiConsole === 'seriesx' || aiConsole === 'seriess') && dbConsole.includes('series')) { consoleSelect.selectedIndex = i; found = true; break; }

                                // --- SEGA ---
                                if ((aiConsole === 'sms' || aiConsole === 'mastersystem') && dbConsole.includes('mastersystem')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'md' || aiConsole === 'genesis' || aiConsole === 'megadrive') && dbConsole.includes('genesis')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'saturn' || aiConsole === 'segasaturn') && dbConsole.includes('saturn')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'dc' || aiConsole === 'dreamcast') && dbConsole.includes('dreamcast')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'gg' || aiConsole === 'gamegear') && dbConsole.includes('gamegear')) { consoleSelect.selectedIndex = i; found = true; break; }

                                // --- RETRO / PC ---
                                if ((aiConsole === 'steam' || aiConsole === 'steamdeck') && dbConsole.includes('steam')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'pc' || aiConsole === 'windows') && dbConsole.includes('pcgaming')) { consoleSelect.selectedIndex = i; found = true; break; }
                                if ((aiConsole === 'neogeo' || aiConsole === 'aes') && dbConsole.includes('neogeo')) { consoleSelect.selectedIndex = i; found = true; break; }
                            }

                            // --- SI NO SE ENCUENTRA LA CONSOLA ---
                            if (!found) {
                                consoleSelect.value = "";
                                consoleSelect.style.border = "2px solid #ff4655"; // Rojo
                                alert(`⚠️ La IA detectó "${data.console}" pero no tienes esa consola en tu lista.\nPuedes añadirla con el botón "+"`);
                            } else {
                                consoleSelect.style.border = "2px solid #0dcaf0"; // Azul/Verde
                            }
                        }

                        // --- SELECT GÉNERO ---
                        if (data.genre) {
                            const genreSelect = document.getElementById('genre');
                            for (let i = 0; i < genreSelect.options.length; i++) {
                                if (genreSelect.options[i].text.toLowerCase().includes(data.genre.toLowerCase())) {
                                    genreSelect.selectedIndex = i;
                                    break;
                                }
                            }
                        }

                        // 5. CAMBIO DE PESTAÑA AUTOMÁTICO
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
});