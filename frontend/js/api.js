// API Integration Module
const DEFAULT_BACKEND_URL = 'http://localhost:8080';

// Initialize and save API base URL
if (!localStorage.getItem('backend_api_url')) {
    localStorage.setItem('backend_api_url', DEFAULT_BACKEND_URL);
}

const getApiBaseUrl = () => localStorage.getItem('backend_api_url') || DEFAULT_BACKEND_URL;

// Helper function to perform fetch requests
async function request(endpoint, options = {}) {
    const baseUrl = getApiBaseUrl();
    const url = `${baseUrl}${endpoint}`;
    
    // Add default headers
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };
    
    const config = {
        ...options,
        headers
    };
    
    try {
        const response = await fetch(url, config);
        
        // Handle unauthorized session
        if (response.status === 401 && !endpoint.includes('/auth/login')) {
            sessionStorage.removeItem('ransomware_token');
            sessionStorage.removeItem('ransomware_user');
            window.location.href = 'login.html';
            throw new Error('Unauthorized Session. Redirecting...');
        }
        
        if (!response.ok) {
            const errData = await response.json().catch(() => ({}));
            throw new Error(errData.message || `HTTP error! Status: ${response.status}`);
        }
        
        // If response is text (like CSV download) or empty body
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('text/csv')) {
            return await response.text();
        }
        
        if (response.status === 204) {
            return null;
        }
        
        return await response.json();
    } catch (error) {
        console.error(`API Request failed on ${endpoint}:`, error);
        throw error;
    }
}

// API Endpoints Wrap
const API = {
    // Auth Endpoints
    auth: {
        login: (username, password) => request('/api/auth/login', {
            method: 'POST',
            body: JSON.stringify({ username, password })
        })
    },
    
    // Dashboard Telemetry
    dashboard: {
        getStats: () => request('/api/dashboard/stats')
    },
    
    // File Activity Logs
    monitoring: {
        getActivities: () => request('/api/monitoring/activities'),
        getSuspicious: () => request('/api/monitoring/suspicious')
    },
    
    // Threat Management
    threats: {
        getAll: () => request('/api/threats'),
        resolve: (id) => request(`/api/threats/${id}/resolve`, { method: 'POST' }),
        block: (id) => request(`/api/threats/${id}/block`, { method: 'POST' })
    },
    
    // Alerts Notifications
    alerts: {
        getAll: () => request('/api/alerts'),
        clear: () => request('/api/alerts/clear', { method: 'POST' }),
        read: (id) => request(`/api/alerts/${id}/read`, { method: 'POST' })
    },
    
    // Reporting
    reports: {
        getAll: () => request('/api/reports'),
        generate: (title) => request('/api/reports/generate', {
            method: 'POST',
            body: JSON.stringify({ title })
        }),
        downloadUrl: () => `${getApiBaseUrl()}/api/reports/download`
    },
    
    // Scan & Scanner Settings
    settings: {
        get: () => request('/api/settings'),
        update: (settings) => request('/api/settings', {
            method: 'POST',
            body: JSON.stringify(settings)
        })
    },
    
    // Threat Simulator
    simulation: {
        start: () => request('/api/simulation/start', { method: 'POST' }),
        reset: () => request('/api/simulation/reset', { method: 'POST' })
    }
};
