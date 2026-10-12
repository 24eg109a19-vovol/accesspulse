// Interactive Complaint Form & Real-Time Priority Calculator
document.addEventListener('DOMContentLoaded', () => {
    const impactSlider = document.getElementById('accessibilityImpact');
    const peopleSlider = document.getElementById('peopleAffected');
    const safetySlider = document.getElementById('safetyRisk');
    const locationSelect = document.getElementById('locationId');
    const categorySelect = document.getElementById('category');

    const impactValDisplay = document.getElementById('impactVal');
    const peopleValDisplay = document.getElementById('peopleVal');
    const safetyValDisplay = document.getElementById('safetyVal');

    const scoreDisplay = document.getElementById('calculatedScore');
    const levelDisplay = document.getElementById('calculatedLevel');
    const levelBadge = document.getElementById('calculatedLevelBadge');

    function calculateLivePriority() {
        const impact = parseInt(impactSlider ? impactSlider.value : 3) || 3;
        const people = parseInt(peopleSlider ? peopleSlider.value : 3) || 3;
        const safety = parseInt(safetySlider ? safetySlider.value : 3) || 3;
        
        let locImportance = 3;
        if (locationSelect && locationSelect.selectedOptions.length > 0) {
            const opt = locationSelect.selectedOptions[0];
            const scoreAttr = opt.getAttribute('data-importance');
            if (scoreAttr) {
                locImportance = parseInt(scoreAttr) || 3;
            }
        }

        const score = impact + people + safety + locImportance;

        if (impactValDisplay) impactValDisplay.innerText = impact;
        if (peopleValDisplay) peopleValDisplay.innerText = people;
        if (safetyValDisplay) safetyValDisplay.innerText = safety;

        if (scoreDisplay) scoreDisplay.innerText = score;

        let level = 'LOW';
        let badgeClass = 'badge-priority-low';
        let color = '#10b981';

        if (score <= 7) {
            level = 'LOW';
            badgeClass = 'badge-priority-low';
            color = '#10b981';
        } else if (score <= 14) {
            level = 'MEDIUM';
            badgeClass = 'badge-priority-medium';
            color = '#f59e0b';
        } else if (score <= 20) {
            level = 'HIGH';
            badgeClass = 'badge-priority-high';
            color = '#f97316';
        } else {
            level = 'CRITICAL';
            badgeClass = 'badge-priority-critical';
            color = '#ef4444';
        }

        if (levelDisplay) {
            levelDisplay.innerText = level;
            levelDisplay.style.color = color;
        }
        if (levelBadge) {
            levelBadge.className = 'badge ' + badgeClass;
            levelBadge.innerText = level;
        }
    }

    if (impactSlider) impactSlider.addEventListener('input', calculateLivePriority);
    if (peopleSlider) peopleSlider.addEventListener('input', calculateLivePriority);
    if (safetySlider) safetySlider.addEventListener('input', calculateLivePriority);
    if (locationSelect) locationSelect.addEventListener('change', calculateLivePriority);
    
    // Auto-adjust default impact when category changes
    if (categorySelect) {
        categorySelect.addEventListener('change', (e) => {
            const opt = categorySelect.selectedOptions[0];
            if (opt) {
                const defImpact = opt.getAttribute('data-default-impact');
                const defSafety = opt.getAttribute('data-default-safety');
                if (defImpact && impactSlider) {
                    impactSlider.value = defImpact;
                }
                if (defSafety && safetySlider) {
                    safetySlider.value = defSafety;
                }
                calculateLivePriority();
            }
        });
    }

    // Run initial calculation
    calculateLivePriority();

    // Image Upload Preview
    const imageInput = document.getElementById('imageFile');
    const imagePreview = document.getElementById('imagePreview');
    const previewImg = document.getElementById('previewImg');
    const uploadPrompt = document.getElementById('uploadPrompt');

    if (imageInput) {
        imageInput.addEventListener('change', (e) => {
            const file = e.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(evt) {
                    if (previewImg) previewImg.src = evt.target.result;
                    if (imagePreview) imagePreview.style.display = 'block';
                    if (uploadPrompt) uploadPrompt.style.display = 'none';
                };
                reader.readAsDataURL(file);
            }
        });
    }
});
