# AccessPulse - Frontend

This directory contains all client-facing UI assets, styling, scripts, and HTML templates for **AccessPulse**.

## Structure

```
frontend/
├── css/                     # Stylesheets
│   ├── style.css            # Global theme variables, typography, buttons, and layout styles
│   ├── dashboard.css        # Dashboard grids, widgets, analytics cards, and sidebar styling
│   └── auth.css             # Authentication styling (Login and Registration forms)
├── js/                      # Client-side JavaScript
│   ├── main.js              # Theme toggling, toast notifications, mobile sidebar toggles
│   ├── complaint-form.js    # Interactive complaint submission, severity calculators, validation
│   └── dashboard-charts.js  # Analytics visualization charts
└── templates/               # HTML Templates & Views
    ├── index.html           # Landing page / public portal
    ├── auth/                # Login & Registration pages
    │   ├── login.html
    │   └── register.html
    ├── admin/               # Administrator console & management pages
    │   ├── dashboard.html
    │   ├── complaints.html
    │   ├── assign-complaint.html
    │   ├── analytics.html
    │   ├── manage-locations.html
    │   └── manage-users.html
    ├── student/             # Student / Staff user portal
    │   ├── dashboard.html
    │   ├── report-complaint.html
    │   ├── my-complaints.html
    │   ├── complaint-detail.html
    │   ├── verify-resolution.html
    │   ├── notifications.html
    │   └── profile.html
    ├── maintenance/         # Maintenance staff work order portal
    │   ├── dashboard.html
    │   └── update-complaint.html
    └── fragments/           # Reusable UI fragments
        ├── navbar.html
        ├── sidebar.html
        └── toast.html
```

## Theme & Styling
- **Color Palette:** Dark aesthetic with deep dark charcoal backgrounds (`#0a0d14`, `#10141f`), glowing crimson red primary accents (`#e63946`, `#d62828`), and emerald accents for resolved statuses.
- **Icons:** FontAwesome 6 Free.
- **Fonts:** Modern system sans-serif and Google Fonts Inter.
