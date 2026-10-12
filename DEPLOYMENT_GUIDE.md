# AccessPulse - Cloud Deployment Guide (Render & Railway)

This guide provides end-to-end instructions for deploying **AccessPulse** to cloud platforms like **Render** or **Railway**.

---

## 🚀 Quick Decision: Database Setup

AccessPulse supports two database setups out-of-the-box:

| Mode | Best For | Configuration Required |
| :--- | :--- | :--- |
| **Option A: Instant Demo Mode (H2 Database)** | Instant testing, fast previews, zero costs | Just add environment variable: `SPRING_PROFILES_ACTIVE=h2` |
| **Option B: Production Mode (Hosted MySQL)** | Full data persistence for production | Add Railway/Render MySQL credentials to environment variables |

---

## 1. Deploying on Railway (Recommended & Fastest)

Railway supports multi-container projects, automatic Dockerfile builds, and 1-click MySQL.

### Step 1: Push your code to GitHub
Make sure your project is committed and pushed to your GitHub repository:
```bash
git add .
git commit -m "Add Docker and cloud deployment configs"
git push origin main
```

### Step 2: Create a Project on Railway
1. Go to [railway.app](https://railway.app/) and sign in with GitHub.
2. Click **New Project** → **Deploy from GitHub repo**.
3. Select your `accesspulse` repository. Railway will detect the `Dockerfile` automatically.

### Step 3: Choose your Database

#### Choice A: Instant H2 In-Memory (Zero Setup)
1. In your AccessPulse service settings on Railway, go to the **Variables** tab.
2. Add:
   - `SPRING_PROFILES_ACTIVE` = `h2`
3. Click **Deploy**. AccessPulse will start immediately and automatically seed demo users!

#### Choice B: Provision Managed MySQL on Railway
1. In the same Railway project canvas, click **+ New** → **Database** → **Add MySQL**.
2. Railway will spin up a MySQL service in the project.
3. Click on the AccessPulse service → **Variables** tab → **Add Variable**:
   - `SPRING_DATASOURCE_URL` = `jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true`
   - `SPRING_DATASOURCE_USERNAME` = `${{MySQL.MYSQLUSER}}`
   - `SPRING_DATASOURCE_PASSWORD` = `${{MySQL.MYSQLPASSWORD}}`
   - `SPRING_JPA_HIBERNATE_DDL_AUTO` = `update`

### Step 4: Generate Domain & Access App
1. In the AccessPulse service on Railway, navigate to **Settings** → **Networking** → **Generate Domain**.
2. Open the generated URL (e.g., `https://accesspulse-production.up.railway.app`).

---

## 2. Deploying on Render

### Step 1: Push Code to GitHub
Ensure the changes are pushed to GitHub.

### Step 2: Create Web Service on Render
1. Go to [render.com](https://render.com/) and log in.
2. Click **New +** → **Web Service**.
3. Connect your GitHub repository `accesspulse`.
4. Configure the service:
   - **Name**: `accesspulse`
   - **Language / Environment**: `Docker`
   - **Region**: Closest to you (e.g., Oregon, Frankfurt, Singapore)
   - **Branch**: `main`
   - **Instance Type**: `Free`
   - **Health Check Path**: `/`

### Step 3: Configure Environment Variables
Under the **Environment Variables** section, add:

#### If using H2 Database (Instant Launch):
- `SPRING_PROFILES_ACTIVE` = `h2`
- `PORT` = `10000`

#### If using External MySQL (e.g., Aiven, PlanetScale, or Railway MySQL):
- `SPRING_DATASOURCE_URL` = `jdbc:mysql://<HOST>:<PORT>/<DATABASE>?useSSL=true&serverTimezone=UTC`
- `SPRING_DATASOURCE_USERNAME` = `<USERNAME>`
- `SPRING_DATASOURCE_PASSWORD` = `<PASSWORD>`
- `SPRING_JPA_HIBERNATE_DDL_AUTO` = `update`
- `PORT` = `10000`

### Step 4: Deploy
Click **Create Web Service**. Render will build the Docker container and deploy your application.

---

## 3. Local Verification with Docker Compose

To test the entire containerized stack locally before deploying to the cloud:

```bash
docker compose up --build
```

- Access the application at: `http://localhost:8080`
- MySQL is exposed on port `3306`.
- Uploaded files are persisted in the `app-uploads` Docker volume.

To stop the containers:
```bash
docker compose down
```

---

## 4. Default Seeded Credentials

When starting on a fresh database, AccessPulse automatically seeds the following credentials:

| Role | Email | Password |
| :--- | :--- | :--- |
| **Administrator** | `admin@accesspulse.edu` | `admin123` |
| **Maintenance Staff** | `maint@accesspulse.edu` | `maint123` |
| **Maintenance Staff 2** | `elena.maint@accesspulse.edu` | `maint123` |
| **Student / User** | *(Register any account via `/register`)* | *(Your password)* |
