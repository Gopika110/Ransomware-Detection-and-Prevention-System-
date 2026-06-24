// Shared UI Components and Navigation Guard

document.addEventListener('DOMContentLoaded', () => {
    // 1. Session check - redirect if not logged in
    const token = sessionStorage.getItem('ransomware_token');
    const isLoginPage = window.location.pathname.endsWith('login.html');
    
    if (!token && !isLoginPage) {
        window.location.href = 'login.html';
        return;
    }
    
    if (token && isLoginPage) {
        window.location.href = 'dashboard.html';
        return;
    }

    // Initialize layout components if not on login page
    if (!isLoginPage) {
        initializeLayout();
    }
});

function initializeLayout() {
    const layout = document.getElementById('app-layout');
    if (!layout) return;

    // Determine current active page name
    const path = window.location.pathname;
    const pageName = path.substring(path.lastIndexOf('/') + 1) || 'dashboard.html';

    // 1. Inject Sidebar
    const sidebar = document.createElement('div');
    sidebar.className = 'sidebar';
    sidebar.innerHTML = getSidebarHtml(pageName);
    layout.insertBefore(sidebar, layout.firstChild);

    // 2. Wrap existing content in main-content wrapper
    const mainContent = document.querySelector('.main-content');
    if (mainContent) {
        // Inject Top Header inside main content at the beginning
        const headerTitle = mainContent.getAttribute('data-page-title') || 'Dashboard';
        const topHeader = document.createElement('header');
        topHeader.className = 'top-header';
        topHeader.innerHTML = getHeaderHtml(headerTitle);
        mainContent.insertBefore(topHeader, mainContent.firstChild);
    }

    // Set up logout listener
    const logoutBtn = document.getElementById('btn-logout-trigger');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', (e) => {
            e.preventDefault();
            logout();
        });
    }

    // Start periodic polling for system status badge
    pollSystemStatus();
    setInterval(pollSystemStatus, 5000);
}

function logout() {
    sessionStorage.removeItem('ransomware_token');
    sessionStorage.removeItem('ransomware_user');
    window.location.href = 'login.html';
}

function pollSystemStatus() {
    const backendUrl = localStorage.getItem('backend_api_url') || 'http://localhost:8080';
    fetch(`${backendUrl}/api/dashboard/stats`)
        .then(response => response.json())
        .then(data => {
            updateStatusBadge(data.status);
        })
        .catch(err => {
            console.error("Failed to fetch system status:", err);
            updateStatusBadge("OFFLINE");
        });
}

function updateStatusBadge(status) {
    const badge = document.getElementById('system-status-badge');
    if (!badge) return;

    badge.className = 'system-badge';
    
    if (status === 'SAFE') {
        badge.classList.add('safe');
        badge.innerHTML = '<span class="badge-dot"></span>SYSTEM SECURE';
    } else if (status === 'WARNING ALERT') {
        badge.classList.add('warning');
        badge.innerHTML = '<span class="badge-dot"></span>WARNING ACTIVE';
    } else if (status === 'THREAT DETECTED') {
        badge.classList.add('danger');
        badge.innerHTML = '<span class="badge-dot"></span>THREAT MITIGATION ACTIVE';
    } else {
        badge.className = 'system-badge warning';
        badge.innerHTML = '<span class="badge-dot"></span>BACKEND OFFLINE';
    }
}

function getSidebarHtml(activePage) {
    const user = sessionStorage.getItem('ransomware_user') || 'Admin';
    const pages = [
        { name: 'dashboard.html', label: 'Dashboard', icon: `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M3.75 6A2.25 2.25 0 0 1 6 3.75h2.25A2.25 2.25 0 0 1 10.5 6v2.25a2.25 2.25 0 0 1-2.25 2.25H6a2.25 2.25 0 0 1-2.25-2.25V6ZM3.75 15.75A2.25 2.25 0 0 1 6 13.5h2.25a2.25 2.25 0 0 1 2.25 2.25V18a2.25 2.25 0 0 1-2.25 2.25H6A2.25 2.25 0 0 1 3.75 18v-2.25ZM13.5 6a2.25 2.25 0 0 1 2.25-2.25H18A2.25 2.25 0 0 1 20.25 6v2.25A2.25 2.25 0 0 1 18 10.5h-2.25a2.25 2.25 0 0 1-2.25-2.25V6ZM13.5 15.75a2.25 2.25 0 0 1 2.25-2.25H18a2.25 2.25 0 0 1 2.25 2.25V18A2.25 2.25 0 0 1 18 20.25h-2.25A2.25 2.25 0 0 1 13.5 18v-2.25Z" /></svg>` },
        { name: 'monitoring.html', label: 'File Monitoring', icon: `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M9 17.25v1.007a3 3 0 0 1-.879 2.122L7.5 21h9l-.621-.621A3 3 0 0 1 15 18.257V17.25m6-12V15a2.25 2.25 0 0 1-2.25 2.25H5.25A2.25 2.25 0 0 1 3 15V5.25m18 0A2.25 2.25 0 0 0 18.75 3H5.25A2.25 2.25 0 0 0 3 5.25m18 0V12a2.25 2.25 0 0 1-2.25 2.25H5.25A2.25 2.25 0 0 1 3 12V5.25" /></svg>` },
        { name: 'detection.html', label: 'Threats List', icon: `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M9 12.75 11.25 15 15 9.75m-3-7.036A11.959 11.959 0 0 1 3.598 6 11.99 11.99 0 0 0 3 9.749c0 5.592 3.824 10.29 9 11.623 5.176-1.332 9-6.03 9-11.622 0-1.31-.21-2.571-.598-3.751h-.152c-3.196 0-6.1-1.248-8.25-3.285Z" /></svg>` },
        { name: 'alerts.html', label: 'Security Alerts', icon: `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M14.857 17.082a23.848 23.848 0 0 0 5.454-1.31A8.967 8.967 0 0 1 18 9.75V9A6 6 0 0 0 6 9v.75a8.967 8.967 0 0 1-2.312 6.022c1.733.64 3.56 1.085 5.455 1.31m5.714 0a24.255 24.255 0 0 1-5.714 0m5.714 0a3 3 0 1 1-5.714 0M3.124 7.5A8.969 8.969 0 0 1 5.292 3m13.416 0a8.969 8.969 0 0 1 2.168 4.5" /></svg>` },
        { name: 'reports.html', label: 'Reports', icon: `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M19.5 14.25v-2.625a3.375 3.375 0 0 0-3.375-3.375h-1.5A1.125 1.125 0 0 1 13.5 7.125v-1.5a3.375 3.375 0 0 0-3.375-3.375H8.25m0 12.75h7.5m-7.5 3H12M10.5 2.25H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 0 0-9-9Z" /></svg>` },
        { name: 'settings.html', label: 'Scanner Settings', icon: `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" d="M9.594 3.94c.09-.542.56-.94 1.11-.94h2.593c.55 0 1.02.398 1.11.94l.213 1.281c.063.374.313.686.645.87.074.04.147.083.22.127.324.196.72.257 1.075.124l1.217-.456a1.125 1.125 0 0 1 1.37.49l1.296 2.247a1.125 1.125 0 0 1-.26 1.43l-1.003.828c-.293.241-.438.613-.43.992a7.723 7.723 0 0 1 0 .255c-.008.378.137.75.43.991l1.004.827c.424.35.534.954.26 1.43l-1.298 2.247a1.125 1.125 0 0 1-1.369.491l-1.217-.456c-.355-.133-.75-.072-1.076.124a6.57 6.57 0 0 1-.22.128c-.331.183-.581.495-.644.869l-.213 1.28c-.09.543-.56.941-1.11.941h-2.594c-.55 0-1.02-.398-1.11-.94l-.213-1.281c-.062-.374-.312-.686-.644-.87a6.52 6.52 0 0 1-.22-.127c-.325-.196-.72-.257-1.076-.124l-1.217.456a1.125 1.125 0 0 1-1.369-.49l-1.297-2.247a1.125 1.125 0 0 1 .26-1.43l1.004-.827c.292-.24.437-.613.43-.992a6.932 6.932 0 0 1 0-.255c.007-.378-.138-.75-.43-.991l-1.004-.827a1.125 1.125 0 0 1-.26-1.43l1.297-2.247a1.125 1.125 0 0 1 1.37-.491l1.216.456c.356.133.751.072 1.076-.124.072-.044.146-.087.22-.128.332-.183.582-.495.645-.869l.214-1.28Z" /><path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z" /></svg>` }
    ];

    let menuItemsHtml = '';
    pages.forEach(p => {
        const isActive = activePage === p.name ? 'active' : '';
        menuItemsHtml += `
            <li class="sidebar-item ${isActive}">
                <a href="${p.name}">
                    ${p.icon}
                    <span>${p.label}</span>
                </a>
            </li>
        `;
    });

    return `
        <div class="sidebar-brand">
            <svg class="brand-icon" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2.5" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" d="M9 12.75L11.25 15 15 9.75m-3-7.036A11.959 11.959 0 013.598 6 11.99 11.99 0 003 9.749c0 5.592 3.824 10.29 9 11.623 5.176-1.332 9-6.03 9-11.622 0-1.31-.21-2.571-.598-3.751h-.152c-3.196 0-6.1-1.248-8.25-3.285z" />
            </svg>
            <span class="brand-text">Aegis Shield</span>
        </div>
        <ul class="sidebar-menu">
            ${menuItemsHtml}
        </ul>
        <div class="sidebar-footer">
            <div class="user-profile">
                <div class="avatar">${user.substring(0,2).toUpperCase()}</div>
                <div class="user-info">
                    <div class="user-name">${user}</div>
                    <div class="user-role">Administrator</div>
                </div>
                <button id="btn-logout-trigger" class="btn-logout" title="Log Out">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" style="width: 20px; height: 20px;">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 9V5.25A2.25 2.25 0 0 0 13.5 3h-6a2.25 2.25 0 0 0-2.25 2.25v13.5A2.25 2.25 0 0 0 7.5 21h6a2.25 2.25 0 0 0 2.25-2.25V15M12 9l-3 3m0 0 3 3m-3-3h12.75" />
                    </svg>
                </button>
            </div>
        </div>
    `;
}

function getHeaderHtml(pageTitle) {
    return `
        <div class="page-title">
            <h1>${pageTitle}</h1>
        </div>
        <div class="header-actions">
            <div id="system-status-badge" class="system-badge safe">
                <span class="badge-dot"></span>SYSTEM SECURE
            </div>
        </div>
    `;
}
