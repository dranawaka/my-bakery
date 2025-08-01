/**
 * Authentication Module for My Bakery Management System
 * Handles user authentication, token management, and session control
 */

// Base URL for API endpoints
const API_BASE_URL = window.location.origin + '/api';

// Token storage keys
const TOKEN_KEY = 'bakery_auth_token';
const REFRESH_TOKEN_KEY = 'bakery_refresh_token';
const USER_KEY = 'bakery_user';

// Authentication state
let isAuthenticated = false;
let currentUser = null;

/**
 * Initialize the authentication module
 */
function initAuth() {
    // Check if user is already logged in
    const token = localStorage.getItem(TOKEN_KEY);
    if (token) {
        // Validate token and get user info
        getCurrentUser()
            .then(response => {
                // Handle the wrapped response structure from the backend
                const user = response.data || response;
                setAuthenticatedUser(user);
                hideLoginModal();
            })
            .catch(error => {
                console.error('Token validation failed:', error);
                logout();
                showLoginModal();
            });
    } else {
        showLoginModal();
    }

    // Set up login form submission
    document.getElementById('login-form').addEventListener('submit', function(event) {
        event.preventDefault();
        
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        
        login(email, password)
            .then(response => {
                // Handle the wrapped response structure from the backend
                const data = response.data || response;
                setAuthenticatedUser(data.user);
                storeTokens(data.token, data.refreshToken);
                hideLoginModal();
                
                // Navigate to dashboard page
                const dashboardLink = document.querySelector('.nav-link[data-page="dashboard"]');
                if (dashboardLink) {
                    dashboardLink.click();
                } else {
                    // Fallback if dashboard link not found
                    loadDashboardData();
                }
            })
            .catch(error => {
                console.error('Login failed:', error);
                showLoginError('Invalid email or password. Please try again.');
            });
    });

    // Set up logout button
    document.getElementById('logout-link').addEventListener('click', function(event) {
        event.preventDefault();
        logout();
    });
}

/**
 * Login user with email and password
 * @param {string} email - User email
 * @param {string} password - User password
 * @returns {Promise} - Promise with login response
 */
function login(email, password) {
    return fetch(`${API_BASE_URL}/auth/login`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ email, password })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Login failed');
        }
        return response.json();
    });
}

/**
 * Get current user information
 * @returns {Promise} - Promise with user data
 */
function getCurrentUser() {
    const token = localStorage.getItem(TOKEN_KEY);
    
    return fetch(`${API_BASE_URL}/auth/me`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to get user info');
        }
        return response.json();
    });
}

/**
 * Refresh the authentication token
 * @returns {Promise} - Promise with new token
 */
function refreshToken() {
    const refreshToken = localStorage.getItem(REFRESH_TOKEN_KEY);
    
    return fetch(`${API_BASE_URL}/auth/refresh-token`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ refreshToken })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Token refresh failed');
        }
        return response.json();
    })
    .then(response => {
        // Handle the wrapped response structure from the backend
        const data = response.data || response;
        storeTokens(data.token, data.refreshToken);
        return data.token;
    });
}

/**
 * Logout the current user
 */
function logout() {
    const token = localStorage.getItem(TOKEN_KEY);
    
    // Call logout API
    fetch(`${API_BASE_URL}/auth/logout`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
    .catch(error => console.error('Logout API error:', error))
    .finally(() => {
        // Clear local storage and reset state
        localStorage.removeItem(TOKEN_KEY);
        localStorage.removeItem(REFRESH_TOKEN_KEY);
        localStorage.removeItem(USER_KEY);
        
        isAuthenticated = false;
        currentUser = null;
        
        // Update UI
        document.getElementById('current-user').textContent = 'User';
        
        // Show login modal
        showLoginModal();
    });
}

/**
 * Store authentication tokens in local storage
 * @param {string} token - JWT token
 * @param {string} refreshToken - Refresh token
 */
function storeTokens(token, refreshToken) {
    localStorage.setItem(TOKEN_KEY, token);
    localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
}

/**
 * Set the authenticated user
 * @param {Object} user - User object
 */
function setAuthenticatedUser(user) {
    currentUser = user;
    isAuthenticated = true;
    localStorage.setItem(USER_KEY, JSON.stringify(user));
    
    // Update UI with user info
    document.getElementById('current-user').textContent = user.firstName || user.email;
}

/**
 * Show the login modal
 */
function showLoginModal() {
    const loginModal = new bootstrap.Modal(document.getElementById('loginModal'), {
        backdrop: 'static',
        keyboard: false
    });
    loginModal.show();
}

/**
 * Hide the login modal
 */
function hideLoginModal() {
    const loginModalElement = document.getElementById('loginModal');
    const loginModal = bootstrap.Modal.getInstance(loginModalElement);
    if (loginModal) {
        loginModal.hide();
    }
}

/**
 * Show login error message
 * @param {string} message - Error message
 */
function showLoginError(message) {
    const form = document.getElementById('login-form');
    
    // Remove any existing error message
    const existingError = form.querySelector('.alert');
    if (existingError) {
        existingError.remove();
    }
    
    // Create and add new error message
    const errorDiv = document.createElement('div');
    errorDiv.className = 'alert alert-danger mt-3';
    errorDiv.textContent = message;
    form.appendChild(errorDiv);
}

/**
 * Get authentication token for API requests
 * @returns {string} - JWT token
 */
function getAuthToken() {
    return localStorage.getItem(TOKEN_KEY);
}

/**
 * Check if user is authenticated
 * @returns {boolean} - Authentication status
 */
function isUserAuthenticated() {
    return isAuthenticated;
}

/**
 * Get the current user
 * @returns {Object} - User object
 */
function getUser() {
    if (!currentUser) {
        const userJson = localStorage.getItem(USER_KEY);
        if (userJson) {
            currentUser = JSON.parse(userJson);
        }
    }
    return currentUser;
}

// Initialize authentication when DOM is loaded
document.addEventListener('DOMContentLoaded', initAuth);

// Export authentication functions
window.auth = {
    login,
    logout,
    getCurrentUser,
    refreshToken,
    getAuthToken,
    isAuthenticated: isUserAuthenticated,
    getUser
};