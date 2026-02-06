/* src/main/resources/static/js/scripts.js */

document.addEventListener("DOMContentLoaded", function() {
    console.log("RetroVault cargado correctamente 🚀");
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('keyup', function() {
            const term = searchInput.value.toLowerCase();
            const cards = document.querySelectorAll('.game-item');
            cards.forEach(card => {
                const titleElement = card.querySelector('.card-title');
                
                if (titleElement) {
                    const titleText = titleElement.textContent.toLowerCase();
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