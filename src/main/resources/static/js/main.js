// AccessPulse Theme Manager & Global UI Interactions

// 0. Theme Manager (Runs immediately)
(function initTheme() {
    const savedTheme = localStorage.getItem('accesspulse-theme');
    const initialTheme = savedTheme || 'dark';
    
    document.documentElement.setAttribute('data-theme', initialTheme);
})();

document.addEventListener('DOMContentLoaded', () => {
    // Theme Toggle Button Handling
    const themeBtn = document.getElementById('themeToggleBtn');
    const themeIcon = document.getElementById('themeToggleIcon');

    function updateThemeUI(theme) {
        if (themeIcon) {
            themeIcon.className = theme === 'dark' ? 'fa-solid fa-sun' : 'fa-solid fa-moon';
            if (theme === 'dark') {
                themeIcon.style.color = '#ff4d5a'; // crimson accent
            } else {
                themeIcon.style.color = '';
            }
        }
    }

    const currentTheme = document.documentElement.getAttribute('data-theme') || 'dark';
    updateThemeUI(currentTheme);

    if (themeBtn) {
        themeBtn.addEventListener('click', () => {
            const activeTheme = document.documentElement.getAttribute('data-theme') || 'dark';
            const newTheme = activeTheme === 'dark' ? 'light' : 'dark';
            
            document.documentElement.setAttribute('data-theme', newTheme);
            localStorage.setItem('accesspulse-theme', newTheme);
            updateThemeUI(newTheme);
        });
    }

    // 1. Auto-dismiss alerts after 6 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
            alert.style.opacity = '0';
            alert.style.transform = 'translateY(-10px)';
            setTimeout(() => alert.remove(), 500);
        }, 6000);
    });

    // 2. Demo credential auto-fill helpers on Login page
    const fillButtons = document.querySelectorAll('[data-fill-email]');
    fillButtons.forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            const email = btn.getAttribute('data-fill-email');
            const pass = btn.getAttribute('data-fill-pass');
            const emailInput = document.getElementById('email');
            const passInput = document.getElementById('password');
            if (emailInput && passInput) {
                emailInput.value = email;
                passInput.value = pass;
                emailInput.style.borderColor = '#e50914';
                passInput.style.borderColor = '#e50914';
            }
        });
    });

    // 3. Modal handlers
    window.openModal = function(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.style.display = 'flex';
        }
    };

    window.closeModal = function(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.style.display = 'none';
        }
    };

    // Close modal on click outside
    window.addEventListener('click', (e) => {
        if (e.target.classList.contains('ap-modal-backdrop')) {
            e.target.style.display = 'none';
        }
    });
});
