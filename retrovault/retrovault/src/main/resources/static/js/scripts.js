/* src/main/resources/static/js/scripts.js */

document.addEventListener("DOMContentLoaded", function() {
    console.log("RetroVault cargado correctamente 🚀");
    
    // --- LÓGICA DEL BUSCADOR EN TIEMPO REAL ---
    const searchInput = document.getElementById('searchInput');

    // Solo ejecutamos esto si existe la barra de búsqueda en esta página
    if (searchInput) {
        searchInput.addEventListener('keyup', function() {
            // 1. Obtenemos el texto en minúsculas
            const term = searchInput.value.toLowerCase();
            
            // 2. Buscamos todas las tarjetas de juegos (que marcamos con 'game-item')
            const cards = document.querySelectorAll('.game-item');

            // 3. Recorremos cada tarjeta
            cards.forEach(card => {
                // Buscamos el título dentro de la tarjeta
                const titleElement = card.querySelector('.card-title');
                
                if (titleElement) {
                    const titleText = titleElement.textContent.toLowerCase();

                    // 4. Si el título incluye lo que escribimos, mostramos. Si no, ocultamos.
                    if (titleText.includes(term)) {
                        card.style.display = ''; // Restaurar visibilidad (block/flex)
                    } else {
                        card.style.display = 'none'; // Ocultar
                    }
                }
            });
        });
    }
});