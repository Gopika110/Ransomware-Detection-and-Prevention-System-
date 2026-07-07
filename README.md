# Aegis Cyber Shield - Ransomware Detection & Prevention System

🛡️ **Aegis Cyber Shield** is a real-time, lightweight Ransomware Detection and Prevention System. It uses dynamic file-system monitoring combined with multi-layered analysis (extension filtering, Shannon entropy check, and frequency analysis) to identify and block potential ransomware attacks on localized storage directories.

---

## 🚀 Key Features

- **Real-Time Directory Monitoring**: Observes directory modifications (creation, deletion, updates) instantly using Java's native `WatchService` API.
- **Multi-Layered Threat Detection**:
  - **Suspicious Extension Filtering**: Automatically flags files with common ransomware extensions (e.g., `.locked`, `.crypto`, `.ransom`, `.enc`, `.locky`, `.wannacry`).
  - **Shannon Entropy Analysis**: Samples the first 4KB of modified files to compute Shannon Entropy, identifying high-entropy files indicative of encrypted payloads.
  - **Frequency Analysis**: Detects rapid bulk modifications or renames within a configurable time window to flag potential mass-encryption events.
- **Automated Prevention & Block Mode**: When active, block mode isolates affected paths and stops malicious processes, logging incident details.
- **Interactive Security Dashboard**:
  - Real-time file activity telemetry and dynamic charts (using Chart.js).
  - Configurable scanner settings (monitored path, entropy/frequency thresholds, active toggles).
  - Threat logs and action control (Resolve/Block).
  - Generation and downloading of comprehensive security reports.
- **Ransomware Attack Simulator**: Built-in administrator tool to safely simulate a mass-encryption attack vector and observe system telemetry and alert mitigations.

---

## 🛠️ Technologies Used

### Backend
- **Java 17** (OpenJDK)
- **Spring Boot 3.2.5** (Starter Web, Starter Data JPA)
- **H2 Database** (In-memory, default for instant launch)
- **MySQL** (Production database option)
- **Hibernate / JPA** (Object-relational mapping)
- **Lombok** (Boilerplate code reduction)
- **Spring Security Crypto** (BCrypt hashing for user credentials)

### Frontend
- **HTML5 & Vanilla CSS3** (Modern dark-mode glassmorphism interface)
- **Vanilla JavaScript** (ES6+ API client layer and component injection)
- **Chart.js** (Dynamic charting for file activity telemetry)

---

## 📁 Project Structure

```text
Ransomware Detection and Prevention System/
│
├── backend/                       # Spring Boot backend application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/ransomware/detection/
│   │   │   │   ├── controller/    # REST API endpoints (Auth, Dashboard, Settings, etc.)
│   │   │   │   ├── model/         # JPA Entities (User, Threat, Alert, FileLog, Report)
│   │   │   │   ├── repository/    # JPA Repositories (UserRepository, ThreatRepository, etc.)
│   │   │   │   ├── service/       # Business Logic (FileMonitoringService, DetectionEngine, etc.)
│   │   │   │   └── RansomwareDetectionApplication.java
│   │   │   └── resources/         # Application properties and database schemas (schema.sql, data.sql)
│   │   └── test/                  # Test directory
│   └── pom.xml                    # Maven dependency file
│
├── frontend/                      # Web UI control panel
│   ├── css/
│   │   └── styles.css             # Main stylesheet (Modern Dark UI)
│   ├── js/
│   │   ├── api.js                 # API wrapper & frontend routing
│   │   └── components.js          # Reusable components & session handling
│   ├── login.html                 # Login page
│   ├── dashboard.html             # Telemetry & simulation interface
│   ├── monitoring.html            # File activity logs list
│   ├── detection.html             # Threat management view
│   ├── alerts.html                # Security notifications center
│   └── reports.html               # Security reports manager
│
├── monitored_directory/           # Default directory monitored by Aegis Shield
│
├── setup_env.ps1                  # Setup script (Downloads JDK & Maven locally)
├── run_backend.ps1 / .cmd         # Backend start scripts
├── run_frontend.cmd               # Frontend Python server script (starts Python http.server)
└── verify_compile.cmd             # Checks and compiles the backend code
```

---

## 📥 Installation Instructions

### Prerequisites
- **PowerShell** (for Windows automated setup)
- **Python 3.x** (to host the frontend server)
- **Git** (to clone the repo)

### 1. Automated Quick Setup
We provide an automated setup script that pulls the required compiler runtime environment (`OpenJDK 17` and `Maven 3.9.6`) into a local `.tools` folder, preventing any interference with your global path settings.

Open **PowerShell** as an administrator and run:
```powershell
.\setup_env.ps1
```
This script will:
- Download and set up Java JDK 17 and Apache Maven 3.9.6.
- Create the default folder `monitored_directory/` with a default `welcome.txt` file.
- Verify the local configurations.

### 2. Manual Setup (Alternative)
If you prefer using your global tools, make sure you have:
- Java JDK 17 installed.
- Maven 3.x installed.
Ensure `java` and `mvn` are available in your system path.

---

## 🖥️ Usage Guide

### 1. Run the Backend Server
Start the Spring Boot backend server on port `8080` by running:

**On Command Prompt:**
```cmd
run_backend.cmd
```

**On PowerShell:**
```powershell
.\run_backend.ps1
```

> 💡 **H2 Database Console**: The application is pre-configured to run with an **in-memory H2 database** by default. You can access the H2 database console at `http://localhost:8080/h2-console` with JDBC URL `jdbc:h2:mem:ransomware_db`, username `sa`, and an empty password.

### 2. Run the Frontend Dashboard
Launch the local web server to serve the frontend pages (hosted on `http://localhost:5000/`):
```cmd
run_frontend.cmd
```

### 3. Log In to Aegis Shield
Navigate to [http://localhost:5000/](http://localhost:5000/) on your browser.
Use the default administrator credentials loaded from the baseline database:
- **Username**: `admin`
- **Password**: `admin123`

### 4. Running a Ransomware Simulation
1. Go to the **Dashboard** in the Control Panel.
2. Click **Start Simulation**.
3. The simulator will rapidly create simulated user files in `monitored_directory` and encrypt them with random high-entropy bytes (AES simulation), renaming them to `.locked`.
4. Observe the live telemetry chart spikes, critical warnings, and automated process isolations in real-time.
5. Click **Reset Workspace** on the dashboard to restore the database and clean up simulated files.

---

## 📸 Screenshots

*Since screenshots are currently not captured in the repository, you can review the interface elements by launching the application. Below are placeholders representing the visual dashboard components:*

| Dashboard Telemetry Overview | Scanner Settings Configuration |
|:---:|:---:|
| ![Telemetry Dashboard Placeholder](https://via.placeholder.com/600x350.png?text=Aegis+Shield+Telemetry+Dashboard) | ![Scanner Settings Placeholder](https://via.placeholder.com/600x350.png?text=Scanner+Settings+Panel) |

---

## 🔮 Future Enhancements

- **Process Terminations**: Automatically terminate the parent process PID responsible for mass-encryption patterns (kernel-level or command-line process tracing).
- **Decryption Recovery Key Cache**: Integrate safe backup directories (shadow copy snapshots) before allowing high-entropy writes, facilitating instant file restoration.
- **Dynamic White-Listing**: Allow specific software suites (e.g., video compilers, IDE compression tools) to perform high-entropy actions without triggering system alerts.
- **Cross-Platform File System Watchers**: Support native eBPF tracing for Linux and kernel filter drivers for Windows system level integration.

---

## 🤝 Contributing Guidelines

1. Fork the repository.
2. Create a new feature branch: `git checkout -b feature/your-feature-name`.
3. Commit your changes: `git commit -m "feat: add feature details"`.
4. Push to the branch: `git push origin feature/your-feature-name`.
5. Open a Pull Request for review.

---

## 📄 License

This project is licensed under the [MIT License](LICENSE) - see the LICENSE file details (standard MIT licensing terms apply).

---

## ✍️ Author
- **Project Developer / Author**
- **Repository Name**: Gopika110/Ransomware-Detection-and-Prevention-System-
