// Dashboard & Analytics Charts using Chart.js
document.addEventListener('DOMContentLoaded', () => {
    // 1. Status Distribution Chart
    const statusCtx = document.getElementById('statusChart');
    if (statusCtx) {
        const resolved = parseInt(statusCtx.getAttribute('data-resolved') || 0);
        const inProgress = parseInt(statusCtx.getAttribute('data-inprogress') || 0);
        const pending = parseInt(statusCtx.getAttribute('data-pending') || 0);

        new Chart(statusCtx, {
            type: 'doughnut',
            data: {
                labels: ['Resolved & Closed', 'In Progress / Assigned', 'Pending / Under Review'],
                datasets: [{
                    data: [resolved, inProgress, pending],
                    backgroundColor: ['#10b981', '#ff3344', '#f59e0b'],
                    borderWidth: 2,
                    borderColor: '#121216'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: { 
                            boxWidth: 12, 
                            color: '#a1a1aa',
                            font: { family: 'Inter', size: 12 } 
                        }
                    }
                },
                cutout: '70%'
            }
        });
    }

    // 2. Priority Level Chart
    const priorityCtx = document.getElementById('priorityChart');
    if (priorityCtx) {
        const critical = parseInt(priorityCtx.getAttribute('data-critical') || 0);
        const high = parseInt(priorityCtx.getAttribute('data-high') || 0);
        const medium = parseInt(priorityCtx.getAttribute('data-medium') || 0);
        const low = parseInt(priorityCtx.getAttribute('data-low') || 0);

        new Chart(priorityCtx, {
            type: 'bar',
            data: {
                labels: ['Low', 'Medium', 'High', 'Critical'],
                datasets: [{
                    label: 'Barrier Count',
                    data: [low, medium, high, critical],
                    backgroundColor: ['#10b981', '#f59e0b', '#ff4d5a', '#e50914'],
                    borderRadius: 6
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false }
                },
                scales: {
                    x: {
                        ticks: { color: '#a1a1aa' },
                        grid: { color: 'rgba(255, 255, 255, 0.05)' }
                    },
                    y: {
                        beginAtZero: true,
                        ticks: { stepSize: 1, color: '#a1a1aa' },
                        grid: { color: 'rgba(255, 255, 255, 0.05)' }
                    }
                }
            }
        });
    }
});
