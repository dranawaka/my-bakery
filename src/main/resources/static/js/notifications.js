/**
 * Notification Service
 * Handles toast notifications and alerts
 */
class NotificationService {
    constructor() {
        this.init();
    }

    /**
     * Initialize the notification service
     */
    init() {
        this.createNotificationContainer();
    }

    /**
     * Create notification container
     */
    createNotificationContainer() {
        if (!document.getElementById('notification-container')) {
            const container = document.createElement('div');
            container.id = 'notification-container';
            container.style.cssText = `
                position: fixed;
                top: 20px;
                right: 20px;
                z-index: 9999;
                max-width: 400px;
            `;
            document.body.appendChild(container);
        }
    }

    /**
     * Show success notification
     */
    showSuccess(message, duration = 5000) {
        this.showNotification(message, 'success', duration);
    }

    /**
     * Show error notification
     */
    showError(message, duration = 7000) {
        this.showNotification(message, 'danger', duration);
    }

    /**
     * Show warning notification
     */
    showWarning(message, duration = 5000) {
        this.showNotification(message, 'warning', duration);
    }

    /**
     * Show info notification
     */
    showInfo(message, duration = 5000) {
        this.showNotification(message, 'info', duration);
    }

    /**
     * Show notification
     */
    showNotification(message, type = 'info', duration = 5000) {
        const container = document.getElementById('notification-container');
        if (!container) return;

        const notification = document.createElement('div');
        notification.className = `alert alert-${type} alert-dismissible fade show`;
        notification.style.cssText = `
            margin-bottom: 10px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            border: none;
            border-radius: 8px;
        `;

        const icon = this.getIconForType(type);
        
        notification.innerHTML = `
            <i class="bi ${icon} me-2"></i>
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        `;

        container.appendChild(notification);

        // Auto remove after duration
        setTimeout(() => {
            if (notification.parentNode) {
                notification.remove();
            }
        }, duration);

        // Add click to dismiss
        notification.addEventListener('click', (e) => {
            if (e.target.classList.contains('btn-close')) {
                notification.remove();
            }
        });
    }

    /**
     * Get icon for notification type
     */
    getIconForType(type) {
        switch (type) {
            case 'success':
                return 'bi-check-circle-fill';
            case 'danger':
                return 'bi-exclamation-triangle-fill';
            case 'warning':
                return 'bi-exclamation-triangle-fill';
            case 'info':
                return 'bi-info-circle-fill';
            default:
                return 'bi-info-circle-fill';
        }
    }

    /**
     * Show confirmation dialog
     */
    showConfirmation(message, onConfirm, onCancel = null) {
        const modalHtml = `
            <div class="modal fade" id="confirmationModal" tabindex="-1" aria-labelledby="confirmationModalLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="confirmationModalLabel">Confirm Action</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <p>${message}</p>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                            <button type="button" class="btn btn-primary" id="confirm-btn">Confirm</button>
                        </div>
                    </div>
                </div>
            </div>
        `;

        // Add the modal to the DOM
        const modalContainer = document.createElement('div');
        modalContainer.innerHTML = modalHtml;
        document.body.appendChild(modalContainer);

        // Show the modal
        const modal = new bootstrap.Modal(document.getElementById('confirmationModal'));
        modal.show();

        // Add event listeners
        document.getElementById('confirm-btn').addEventListener('click', () => {
            modal.hide();
            if (onConfirm) onConfirm();
        });

        // Remove the modal from the DOM when it's hidden
        document.getElementById('confirmationModal').addEventListener('hidden.bs.modal', () => {
            document.body.removeChild(modalContainer);
            if (onCancel) onCancel();
        });
    }

    /**
     * Show loading spinner
     */
    showLoading(message = 'Loading...') {
        const loadingHtml = `
            <div class="modal fade" id="loadingModal" tabindex="-1" aria-labelledby="loadingModalLabel" aria-hidden="true" data-bs-backdrop="static" data-bs-keyboard="false">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content">
                        <div class="modal-body text-center py-4">
                            <div class="spinner-border text-primary mb-3" role="status">
                                <span class="visually-hidden">Loading...</span>
                            </div>
                            <p class="mb-0">${message}</p>
                        </div>
                    </div>
                </div>
            </div>
        `;

        // Add the modal to the DOM
        const modalContainer = document.createElement('div');
        modalContainer.innerHTML = loadingHtml;
        document.body.appendChild(modalContainer);

        // Show the modal
        const modal = new bootstrap.Modal(document.getElementById('loadingModal'));
        modal.show();

        return modal;
    }

    /**
     * Hide loading spinner
     */
    hideLoading() {
        const loadingModal = document.getElementById('loadingModal');
        if (loadingModal) {
            const modal = bootstrap.Modal.getInstance(loadingModal);
            if (modal) {
                modal.hide();
                // Remove from DOM after hiding
                setTimeout(() => {
                    if (loadingModal.parentNode) {
                        loadingModal.parentNode.remove();
                    }
                }, 150);
            }
        }
    }
}

// Initialize notification service when DOM is ready
let notificationService;
document.addEventListener('DOMContentLoaded', function() {
    notificationService = new NotificationService();
}); 