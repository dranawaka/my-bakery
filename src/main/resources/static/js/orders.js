/**
 * Order Management Service
 * Handles all order-related operations and UI interactions
 */
class OrderService {
    constructor() {
        this.currentOrders = [];
        this.currentFilter = 'ALL';
        this.init();
    }

    /**
     * Initialize the order service
     */
    init() {
        this.setupEventListeners();
    }

    /**
     * Set up event listeners for order functionality
     */
    setupEventListeners() {
        // Event delegation for dynamic elements
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('view-order-btn')) {
                const orderId = e.target.getAttribute('data-id');
                this.viewOrderDetails(orderId);
            } else if (e.target.classList.contains('update-status-btn')) {
                const orderId = e.target.getAttribute('data-id');
                const currentStatus = e.target.getAttribute('data-status');
                this.showUpdateStatusModal(orderId, currentStatus);
            } else if (e.target.classList.contains('cancel-order-btn')) {
                const orderId = e.target.getAttribute('data-id');
                this.confirmCancelOrder(orderId);
            } else if (e.target.classList.contains('filter-status')) {
                e.preventDefault();
                const status = e.target.getAttribute('data-status');
                this.filterOrdersByStatus(status);
            } else if (e.target.id === 'select-all-orders') {
                this.toggleSelectAll(e.target.checked);
            } else if (e.target.classList.contains('order-checkbox')) {
                this.updateBulkActions();
            } else if (e.target.id === 'bulk-update-status-btn') {
                this.showBulkUpdateStatusModal();
            } else if (e.target.id === 'bulk-cancel-btn') {
                this.confirmBulkCancel();
            } else if (e.target.id === 'export-csv-btn') {
                e.preventDefault();
                this.exportToCSV();
            } else if (e.target.id === 'export-pdf-btn') {
                e.preventDefault();
                this.exportToPDF();
            }
        });

        // Search functionality
        document.addEventListener('input', (e) => {
            if (e.target.id === 'order-search') {
                this.filterOrders(e.target.value.toLowerCase());
            }
        });

        // Refresh button
        document.addEventListener('click', (e) => {
            if (e.target.id === 'refresh-orders-btn') {
                this.fetchOrders();
            }
        });
    }

    /**
     * Load orders page content
     */
    loadOrdersPage() {
        const ordersPage = document.getElementById('orders-page');
        ordersPage.innerHTML = `
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h3>Orders Management</h3>
                <div>
                    <button id="refresh-orders-btn" class="btn btn-outline-secondary me-2">
                        <i class="bi bi-arrow-clockwise me-2"></i>Refresh
                    </button>
                    <div class="btn-group me-2">
                        <button type="button" class="btn btn-outline-primary dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">
                            <i class="bi bi-funnel me-2"></i>Filter
                        </button>
                        <ul class="dropdown-menu">
                            <li><a class="dropdown-item filter-status" data-status="ALL" href="#">All Orders</a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item filter-status" data-status="PENDING" href="#">Pending</a></li>
                            <li><a class="dropdown-item filter-status" data-status="PROCESSING" href="#">Processing</a></li>
                            <li><a class="dropdown-item filter-status" data-status="SHIPPED" href="#">Shipped</a></li>
                            <li><a class="dropdown-item filter-status" data-status="DELIVERED" href="#">Delivered</a></li>
                            <li><a class="dropdown-item filter-status" data-status="COMPLETED" href="#">Completed</a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item filter-status" data-status="CANCELLED" href="#">Cancelled</a></li>
                            <li><a class="dropdown-item filter-status" data-status="REFUNDED" href="#">Refunded</a></li>
                        </ul>
                    </div>
                    <div class="btn-group">
                        <button type="button" class="btn btn-outline-success dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">
                            <i class="bi bi-download me-2"></i>Export
                        </button>
                        <ul class="dropdown-menu">
                            <li><a class="dropdown-item" href="#" id="export-csv-btn">Export to CSV</a></li>
                            <li><a class="dropdown-item" href="#" id="export-pdf-btn">Export to PDF</a></li>
                        </ul>
                    </div>
                </div>
            </div>
            
            <div class="card mb-4">
                <div class="card-header">
                    <div class="row align-items-center">
                        <div class="col">
                            <h5 class="mb-0">Orders List</h5>
                        </div>
                        <div class="col-md-4">
                            <input type="text" id="order-search" class="form-control" placeholder="Search orders...">
                        </div>
                    </div>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-striped table-hover">
                            <thead>
                                <tr>
                                    <th>
                                        <input type="checkbox" id="select-all-orders" class="form-check-input">
                                    </th>
                                    <th>Order #</th>
                                    <th>Customer</th>
                                    <th>Date</th>
                                    <th>Status</th>
                                    <th>Total</th>
                                    <th>Delivery Method</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody id="orders-table">
                                <tr>
                                    <td colspan="8" class="text-center">
                                        <div class="spinner-border text-primary" role="status">
                                            <span class="visually-hidden">Loading...</span>
                                        </div>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
                <div class="card-footer" id="bulk-actions" style="display: none;">
                    <div class="d-flex justify-content-between align-items-center">
                        <span id="selected-count">0 orders selected</span>
                        <div class="btn-group">
                            <button type="button" class="btn btn-outline-primary btn-sm" id="bulk-update-status-btn">
                                <i class="bi bi-arrow-repeat me-1"></i>Update Status
                            </button>
                            <button type="button" class="btn btn-outline-danger btn-sm" id="bulk-cancel-btn">
                                <i class="bi bi-x-circle me-1"></i>Cancel Orders
                            </button>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Order Details Modal -->
            <div class="modal fade" id="orderDetailsModal" tabindex="-1" aria-labelledby="orderDetailsModalLabel" aria-hidden="true">
                <div class="modal-dialog modal-lg">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="orderDetailsModalLabel">Order Details</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body" id="order-details-content">
                            <div class="text-center py-4">
                                <div class="spinner-border text-primary" role="status">
                                    <span class="visually-hidden">Loading...</span>
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                            <div id="order-action-buttons"></div>
                        </div>
                    </div>
                </div>
            </div>
        `;

        // Fetch orders
        this.fetchOrders();
    }

    /**
     * Fetch orders from API
     */
    async fetchOrders() {
        const tableBody = document.getElementById('orders-table');
        if (!tableBody) return;

        tableBody.innerHTML = `
            <tr>
                <td colspan="8" class="text-center">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                </td>
            </tr>
        `;

                try {
            if (typeof apiService === 'undefined') {
                tableBody.innerHTML = `
                    <tr>
                        <td colspan="8" class="text-center text-danger">
                            API service not available
                        </td>
                    </tr>
                `;
                return;
            }
            
            const response = await apiService.get('/orders');
            if (response.success) {
                this.currentOrders = response.data;
                this.displayOrders(response.data);
            } else {
                tableBody.innerHTML = `
                    <tr>
                        <td colspan="8" class="text-center text-danger">
                            Failed to load orders: ${response.error.message}
                        </td>
                    </tr>
                `;
            }
        } catch (error) {
            console.error('Error fetching orders:', error);
            tableBody.innerHTML = `
                <tr>
                    <td colspan="8" class="text-center text-danger">
                    Failed to load orders. Please try again later.
                    </td>
                </tr>
            `;
        }
    }

    /**
     * Display orders in the table
     */
    displayOrders(orders) {
        const tableBody = document.getElementById('orders-table');
        if (!tableBody) return;

        if (!orders || orders.length === 0) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="8" class="text-center">No orders found</td>
                </tr>
            `;
            return;
        }

        tableBody.innerHTML = '';

        orders.forEach(order => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>
                    <input type="checkbox" class="form-check-input order-checkbox" data-id="${order.id}">
                </td>
                <td>${order.orderNumber}</td>
                <td>${order.customer ? order.customer.firstName + ' ' + order.customer.lastName : 'Guest'}</td>
                <td>${this.formatDate(order.orderDate)}</td>
                <td><span class="badge bg-${this.getStatusBadgeClass(order.status)}">${order.status}</span></td>
                <td>${this.formatCurrency(order.totalAmount)}</td>
                <td>${order.deliveryMethod || 'N/A'}</td>
                <td>
                    <button class="btn btn-sm btn-outline-primary view-order-btn" data-id="${order.id}">
                        <i class="bi bi-eye"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-secondary update-status-btn" data-id="${order.id}" data-status="${order.status}">
                        <i class="bi bi-arrow-repeat"></i>
                    </button>
                    ${order.status !== 'CANCELLED' && order.status !== 'COMPLETED' && order.status !== 'REFUNDED' ? 
                        `<button class="btn btn-sm btn-outline-danger cancel-order-btn" data-id="${order.id}">
                            <i class="bi bi-x-circle"></i>
                        </button>` : ''}
                </td>
            `;
            tableBody.appendChild(row);
        });
    }

    /**
     * Filter orders by search term
     */
    filterOrders(searchTerm) {
        const rows = document.querySelectorAll('#orders-table tr');
        
        rows.forEach(row => {
            const text = row.textContent.toLowerCase();
            if (text.includes(searchTerm)) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });
    }

    /**
     * Filter orders by status
     */
    async filterOrdersByStatus(status) {
        this.currentFilter = status;
        
        if (status === 'ALL') {
            this.fetchOrders();
            return;
        }

        const tableBody = document.getElementById('orders-table');
        if (!tableBody) return;

        tableBody.innerHTML = `
            <tr>
                <td colspan="8" class="text-center">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                </td>
            </tr>
        `;

        try {
            const response = await apiService.get(`/orders/status/${status}`);
            if (response.success) {
                this.currentOrders = response.data;
                this.displayOrders(response.data);
            } else {
                tableBody.innerHTML = `
                    <tr>
                        <td colspan="8" class="text-center text-danger">
                            Failed to load orders: ${response.error.message}
                        </td>
                    </tr>
                `;
            }
        } catch (error) {
            console.error('Error fetching orders by status:', error);
            tableBody.innerHTML = `
                <tr>
                    <td colspan="8" class="text-center text-danger">
                        Failed to load orders. Please try again later.
                    </td>
                </tr>
            `;
        }
    }

    /**
     * View order details
     */
    async viewOrderDetails(orderId) {
        const modalContent = document.getElementById('order-details-content');
        const actionButtons = document.getElementById('order-action-buttons');
        
        if (!modalContent || !actionButtons) return;

        modalContent.innerHTML = `
            <div class="text-center py-4">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
            </div>
        `;
        
        actionButtons.innerHTML = '';
        
        // Show the modal
        const modal = new bootstrap.Modal(document.getElementById('orderDetailsModal'));
        modal.show();
        
        try {
            const response = await apiService.get(`/orders/${orderId}`);
            if (response.success) {
                const order = response.data;
                
                // Format order details
                modalContent.innerHTML = `
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <h6>Order Information</h6>
                            <p><strong>Order Number:</strong> ${order.orderNumber}</p>
                            <p><strong>Date:</strong> ${this.formatDate(order.orderDate)}</p>
                            <p><strong>Status:</strong> <span class="badge bg-${this.getStatusBadgeClass(order.status)}">${order.status}</span></p>
                            <p><strong>Delivery Method:</strong> ${order.deliveryMethod || 'N/A'}</p>
                            ${order.deliveryDate ? `<p><strong>Delivery Date:</strong> ${this.formatDate(order.deliveryDate)}</p>` : ''}
                        </div>
                        <div class="col-md-6">
                            <h6>Customer Information</h6>
                            <p><strong>Name:</strong> ${order.customer ? order.customer.firstName + ' ' + order.customer.lastName : 'Guest'}</p>
                            <p><strong>Email:</strong> ${order.customer ? order.customer.email : 'N/A'}</p>
                            <p><strong>Phone:</strong> ${order.customer && order.customer.phone ? order.customer.phone : 'N/A'}</p>
                        </div>
                    </div>
                    
                    ${this.renderOrderTimeline(order)}
                    
                    ${order.shippingAddress ? `
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <h6>Shipping Address</h6>
                            <p>${order.shippingAddress.street}<br>
                            ${order.shippingAddress.city}, ${order.shippingAddress.state} ${order.shippingAddress.zipCode}<br>
                            ${order.shippingAddress.country}</p>
                        </div>
                        <div class="col-md-6">
                            <h6>Billing Address</h6>
                            <p>${order.billingAddress ? `
                                ${order.billingAddress.street}<br>
                                ${order.billingAddress.city}, ${order.billingAddress.state} ${order.billingAddress.zipCode}<br>
                                ${order.billingAddress.country}
                            ` : 'Same as shipping address'}</p>
                        </div>
                    </div>
                    ` : ''}
                    
                    <h6>Order Items</h6>
                    <div class="table-responsive">
                        <table class="table table-sm">
                            <thead>
                                <tr>
                                    <th>Product</th>
                                    <th>Quantity</th>
                                    <th>Unit Price</th>
                                    <th>Total</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${order.items.map(item => `
                                <tr>
                                    <td>${item.productName || 'Product #' + item.productId}</td>
                                    <td>${item.quantity}</td>
                                    <td>${this.formatCurrency(item.unitPrice)}</td>
                                    <td>${this.formatCurrency(item.totalPrice || (item.quantity * item.unitPrice))}</td>
                                </tr>
                                `).join('')}
                            </tbody>
                            <tfoot>
                                <tr>
                                    <td colspan="3" class="text-end"><strong>Subtotal:</strong></td>
                                    <td>${this.formatCurrency(order.totalAmount - (order.taxAmount || 0) - (order.shippingAmount || 0) + (order.discountAmount || 0))}</td>
                                </tr>
                                ${order.discountAmount ? `
                                <tr>
                                    <td colspan="3" class="text-end"><strong>Discount:</strong></td>
                                    <td>-${this.formatCurrency(order.discountAmount)}</td>
                                </tr>
                                ` : ''}
                                ${order.taxAmount ? `
                                <tr>
                                    <td colspan="3" class="text-end"><strong>Tax:</strong></td>
                                    <td>${this.formatCurrency(order.taxAmount)}</td>
                                </tr>
                                ` : ''}
                                ${order.shippingAmount ? `
                                <tr>
                                    <td colspan="3" class="text-end"><strong>Shipping:</strong></td>
                                    <td>${this.formatCurrency(order.shippingAmount)}</td>
                                </tr>
                                ` : ''}
                                <tr>
                                    <td colspan="3" class="text-end"><strong>Total:</strong></td>
                                    <td><strong>${this.formatCurrency(order.totalAmount)}</strong></td>
                                </tr>
                            </tfoot>
                        </table>
                    </div>
                    
                    ${order.notes ? `
                    <div class="mt-3">
                        <h6>Notes</h6>
                        <p>${order.notes}</p>
                    </div>
                    ` : ''}
                `;
                
                // Add action buttons
                if (order.status !== 'CANCELLED' && order.status !== 'COMPLETED' && order.status !== 'REFUNDED') {
                    actionButtons.innerHTML = `
                        <button type="button" class="btn btn-primary update-status-modal-btn" data-id="${order.id}" data-status="${order.status}">
                            Update Status
                        </button>
                        <button type="button" class="btn btn-danger cancel-order-modal-btn" data-id="${order.id}">
                            Cancel Order
                        </button>
                    `;
                    
                    // Add event listeners for action buttons
                    document.querySelector('.update-status-modal-btn').addEventListener('click', function() {
                        const orderId = this.getAttribute('data-id');
                        const currentStatus = this.getAttribute('data-status');
                        modal.hide();
                        orderService.showUpdateStatusModal(orderId, currentStatus);
                    });
                    
                    document.querySelector('.cancel-order-modal-btn').addEventListener('click', function() {
                        const orderId = this.getAttribute('data-id');
                        modal.hide();
                        orderService.confirmCancelOrder(orderId);
                    });
                }
            } else {
                modalContent.innerHTML = `
                    <div class="alert alert-danger">
                        Failed to load order details: ${response.error.message}
                    </div>
                `;
            }
        } catch (error) {
            console.error('Error fetching order details:', error);
            modalContent.innerHTML = `
                <div class="alert alert-danger">
                    Failed to load order details. Please try again later.
                </div>
            `;
        }
    }

    /**
     * Show update status modal
     */
    showUpdateStatusModal(orderId, currentStatus) {
        const modalHtml = `
            <div class="modal fade" id="updateStatusModal" tabindex="-1" aria-labelledby="updateStatusModalLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="updateStatusModalLabel">Update Order Status</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <form id="update-status-form">
                                <div class="mb-3">
                                    <label for="order-status" class="form-label">Status</label>
                                    <select class="form-select" id="order-status" required>
                                        <option value="PENDING" ${currentStatus === 'PENDING' ? 'selected' : ''}>Pending</option>
                                        <option value="PROCESSING" ${currentStatus === 'PROCESSING' ? 'selected' : ''}>Processing</option>
                                        <option value="SHIPPED" ${currentStatus === 'SHIPPED' ? 'selected' : ''}>Shipped</option>
                                        <option value="DELIVERED" ${currentStatus === 'DELIVERED' ? 'selected' : ''}>Delivered</option>
                                        <option value="COMPLETED" ${currentStatus === 'COMPLETED' ? 'selected' : ''}>Completed</option>
                                        <option value="CANCELLED" ${currentStatus === 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                                        <option value="REFUNDED" ${currentStatus === 'REFUNDED' ? 'selected' : ''}>Refunded</option>
                                    </select>
                                </div>
                            </form>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                            <button type="button" class="btn btn-primary" id="update-status-btn">Update Status</button>
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
        const modal = new bootstrap.Modal(document.getElementById('updateStatusModal'));
        modal.show();
        
        // Add event listener for update button
        document.getElementById('update-status-btn').addEventListener('click', function() {
            const status = document.getElementById('order-status').value;
            orderService.updateOrderStatus(orderId, status, modal);
        });
        
        // Remove the modal from the DOM when it's hidden
        document.getElementById('updateStatusModal').addEventListener('hidden.bs.modal', function() {
            document.body.removeChild(modalContainer);
        });
    }

    /**
     * Update order status
     */
    async updateOrderStatus(orderId, status, modal) {
        try {
            const response = await apiService.put(`/orders/${orderId}/status?status=${status}`);
            if (response.success) {
                // Close the modal
                modal.hide();
                
                // Show success message
                this.showSuccessMessage('Order status updated successfully');
                
                // Refresh orders
                this.fetchOrders();
            } else {
                this.showErrorMessage('Failed to update order status: ' + response.error.message);
            }
        } catch (error) {
            console.error('Error updating order status:', error);
            this.showErrorMessage('Failed to update order status. Please try again later.');
        }
    }

    /**
     * Confirm cancel order
     */
    confirmCancelOrder(orderId) {
        this.showConfirmation(
            'Are you sure you want to cancel this order?',
            () => this.cancelOrder(orderId)
        );
    }

    /**
     * Cancel order
     */
    async cancelOrder(orderId) {
        try {
            const response = await apiService.put(`/orders/${orderId}/cancel`);
            if (response.success) {
                // Show success message
                this.showSuccessMessage('Order cancelled successfully');
                
                // Refresh orders
                this.fetchOrders();
            } else {
                this.showErrorMessage('Failed to cancel order: ' + response.error.message);
            }
        } catch (error) {
            console.error('Error cancelling order:', error);
            this.showErrorMessage('Failed to cancel order. Please try again later.');
        }
    }

    /**
     * Get status badge class
     */
    getStatusBadgeClass(status) {
        switch (status) {
            case 'PENDING':
                return 'warning';
            case 'PROCESSING':
                return 'info';
            case 'SHIPPED':
                return 'primary';
            case 'DELIVERED':
                return 'success';
            case 'COMPLETED':
                return 'success';
            case 'CANCELLED':
                return 'danger';
            case 'REFUNDED':
                return 'secondary';
            default:
                return 'secondary';
        }
    }

    /**
     * Format currency
     */
    formatCurrency(amount) {
        return '$' + parseFloat(amount).toFixed(2);
    }

    /**
     * Format date
     */
    formatDate(dateString) {
        const date = new Date(dateString);
        return date.toLocaleDateString();
    }

    /**
     * Show success message
     */
    showSuccessMessage(message) {
        if (typeof notificationService !== 'undefined') {
            notificationService.showSuccess(message);
        } else {
            console.log('Success:', message);
        }
    }

    /**
     * Show error message
     */
    showErrorMessage(message) {
        if (typeof notificationService !== 'undefined') {
            notificationService.showError(message);
        } else {
            console.error('Error:', message);
        }
    }

    /**
     * Show warning message
     */
    showWarningMessage(message) {
        if (typeof notificationService !== 'undefined') {
            notificationService.showWarning(message);
        } else {
            console.warn('Warning:', message);
        }
    }

    /**
     * Show info message
     */
    showInfoMessage(message) {
        if (typeof notificationService !== 'undefined') {
            notificationService.showInfo(message);
        } else {
            console.log('Info:', message);
        }
    }

    /**
     * Show confirmation dialog
     */
    showConfirmation(message, onConfirm) {
        if (typeof notificationService !== 'undefined') {
            notificationService.showConfirmation(message, onConfirm);
        } else {
            if (confirm(message)) {
                onConfirm();
            }
        }
    }

    /**
     * Show loading modal
     */
    showLoading(message) {
        if (typeof notificationService !== 'undefined') {
            return notificationService.showLoading(message);
        } else {
            console.log('Loading:', message);
            return null;
        }
    }

    /**
     * Hide loading modal
     */
    hideLoading() {
        if (typeof notificationService !== 'undefined') {
            notificationService.hideLoading();
        }
    }

    /**
     * Render order timeline
     */
    renderOrderTimeline(order) {
        const statuses = ['PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'COMPLETED'];
        const currentStatusIndex = statuses.indexOf(order.status);
        
        let timelineHtml = `
            <div class="mb-3">
                <h6>Order Progress</h6>
                <div class="timeline">
        `;
        
        statuses.forEach((status, index) => {
            const isCompleted = index <= currentStatusIndex;
            const isCurrent = index === currentStatusIndex;
            const isCancelled = order.status === 'CANCELLED' || order.status === 'REFUNDED';
            
            let statusClass = 'text-muted';
            let iconClass = 'bi-circle';
            
            if (isCancelled && status === 'CANCELLED') {
                statusClass = 'text-danger';
                iconClass = 'bi-x-circle-fill';
            } else if (isCompleted) {
                statusClass = 'text-success';
                iconClass = 'bi-check-circle-fill';
            } else if (isCurrent) {
                statusClass = 'text-primary';
                iconClass = 'bi-arrow-right-circle-fill';
            }
            
            timelineHtml += `
                <div class="timeline-item ${statusClass}">
                    <i class="bi ${iconClass} me-2"></i>
                    <span>${this.getStatusDisplayName(status)}</span>
                </div>
            `;
        });
        
        timelineHtml += `
                </div>
            </div>
        `;
        
        return timelineHtml;
    }

    /**
     * Get status display name
     */
    getStatusDisplayName(status) {
        switch (status) {
            case 'PENDING':
                return 'Order Placed';
            case 'PROCESSING':
                return 'Processing';
            case 'SHIPPED':
                return 'Shipped';
            case 'DELIVERED':
                return 'Delivered';
            case 'COMPLETED':
                return 'Completed';
            case 'CANCELLED':
                return 'Cancelled';
            case 'REFUNDED':
                return 'Refunded';
            default:
                return status;
        }
    }

    /**
     * Toggle select all orders
     */
    toggleSelectAll(checked) {
        const checkboxes = document.querySelectorAll('.order-checkbox');
        checkboxes.forEach(checkbox => {
            checkbox.checked = checked;
        });
        this.updateBulkActions();
    }

    /**
     * Update bulk actions visibility
     */
    updateBulkActions() {
        const selectedCheckboxes = document.querySelectorAll('.order-checkbox:checked');
        const bulkActions = document.getElementById('bulk-actions');
        const selectedCount = document.getElementById('selected-count');
        
        if (selectedCheckboxes.length > 0) {
            bulkActions.style.display = 'block';
            selectedCount.textContent = `${selectedCheckboxes.length} order${selectedCheckboxes.length > 1 ? 's' : ''} selected`;
        } else {
            bulkActions.style.display = 'none';
        }
    }

    /**
     * Get selected order IDs
     */
    getSelectedOrderIds() {
        const selectedCheckboxes = document.querySelectorAll('.order-checkbox:checked');
        return Array.from(selectedCheckboxes).map(checkbox => checkbox.getAttribute('data-id'));
    }

    /**
     * Show bulk update status modal
     */
    showBulkUpdateStatusModal() {
        const selectedIds = this.getSelectedOrderIds();
        if (selectedIds.length === 0) {
            this.showWarningMessage('Please select orders to update');
            return;
        }

        const modalHtml = `
            <div class="modal fade" id="bulkUpdateStatusModal" tabindex="-1" aria-labelledby="bulkUpdateStatusModalLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="bulkUpdateStatusModalLabel">Bulk Update Status</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <p>Update status for <strong>${selectedIds.length}</strong> selected order${selectedIds.length > 1 ? 's' : ''}.</p>
                            <form id="bulk-update-status-form">
                                <div class="mb-3">
                                    <label for="bulk-order-status" class="form-label">New Status</label>
                                    <select class="form-select" id="bulk-order-status" required>
                                        <option value="">Select status...</option>
                                        <option value="PENDING">Pending</option>
                                        <option value="PROCESSING">Processing</option>
                                        <option value="SHIPPED">Shipped</option>
                                        <option value="DELIVERED">Delivered</option>
                                        <option value="COMPLETED">Completed</option>
                                        <option value="CANCELLED">Cancelled</option>
                                        <option value="REFUNDED">Refunded</option>
                                    </select>
                                </div>
                            </form>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                            <button type="button" class="btn btn-primary" id="bulk-update-status-confirm-btn">Update Status</button>
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
        const modal = new bootstrap.Modal(document.getElementById('bulkUpdateStatusModal'));
        modal.show();

        // Add event listener for confirm button
        document.getElementById('bulk-update-status-confirm-btn').addEventListener('click', () => {
            const status = document.getElementById('bulk-order-status').value;
            if (!status) {
                notificationService.showWarning('Please select a status');
                return;
            }
            this.bulkUpdateStatus(selectedIds, status, modal);
        });

        // Remove the modal from the DOM when it's hidden
        document.getElementById('bulkUpdateStatusModal').addEventListener('hidden.bs.modal', () => {
            document.body.removeChild(modalContainer);
        });
    }

    /**
     * Bulk update status
     */
    async bulkUpdateStatus(orderIds, status, modal) {
        const loadingModal = this.showLoading('Updating order statuses...');
        
        try {
            const promises = orderIds.map(orderId => 
                apiService.put(`/orders/${orderId}/status?status=${status}`)
            );
            
            const results = await Promise.allSettled(promises);
            const successful = results.filter(result => result.status === 'fulfilled' && result.value.success).length;
            const failed = results.length - successful;
            
            modal.hide();
            this.hideLoading();
            
            if (successful > 0) {
                this.showSuccessMessage(`Successfully updated ${successful} order${successful > 1 ? 's' : ''}`);
                if (failed > 0) {
                    this.showWarningMessage(`${failed} order${failed > 1 ? 's' : ''} failed to update`);
                }
                this.fetchOrders();
            } else {
                this.showErrorMessage('Failed to update any orders');
            }
        } catch (error) {
            this.hideLoading();
            this.showErrorMessage('An error occurred while updating orders');
        }
    }

    /**
     * Confirm bulk cancel
     */
    confirmBulkCancel() {
        const selectedIds = this.getSelectedOrderIds();
        if (selectedIds.length === 0) {
            this.showWarningMessage('Please select orders to cancel');
            return;
        }

        this.showConfirmation(
            `Are you sure you want to cancel ${selectedIds.length} order${selectedIds.length > 1 ? 's' : ''}?`,
            () => this.bulkCancelOrders(selectedIds)
        );
    }

    /**
     * Bulk cancel orders
     */
    async bulkCancelOrders(orderIds) {
        const loadingModal = this.showLoading('Cancelling orders...');
        
        try {
            const promises = orderIds.map(orderId => 
                apiService.put(`/orders/${orderId}/cancel`)
            );
            
            const results = await Promise.allSettled(promises);
            const successful = results.filter(result => result.status === 'fulfilled' && result.value.success).length;
            const failed = results.length - successful;
            
            this.hideLoading();
            
            if (successful > 0) {
                this.showSuccessMessage(`Successfully cancelled ${successful} order${successful > 1 ? 's' : ''}`);
                if (failed > 0) {
                    this.showWarningMessage(`${failed} order${failed > 1 ? 's' : ''} failed to cancel`);
                }
                this.fetchOrders();
            } else {
                this.showErrorMessage('Failed to cancel any orders');
            }
        } catch (error) {
            this.hideLoading();
            this.showErrorMessage('An error occurred while cancelling orders');
        }
    }

    /**
     * Export orders to CSV
     */
    exportToCSV() {
        const orders = this.currentOrders;
        if (!orders || orders.length === 0) {
            this.showWarningMessage('No orders to export');
            return;
        }

        const headers = ['Order Number', 'Customer', 'Date', 'Status', 'Total', 'Delivery Method'];
        const csvContent = [
            headers.join(','),
            ...orders.map(order => [
                order.orderNumber,
                order.customer ? `${order.customer.firstName} ${order.customer.lastName}` : 'Guest',
                this.formatDate(order.orderDate),
                order.status,
                order.totalAmount,
                order.deliveryMethod || 'N/A'
            ].join(','))
        ].join('\n');

        const blob = new Blob([csvContent], { type: 'text/csv' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `orders_${new Date().toISOString().split('T')[0]}.csv`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);

        this.showSuccessMessage('Orders exported to CSV successfully');
    }

    /**
     * Export orders to PDF
     */
    exportToPDF() {
        this.showInfoMessage('PDF export feature will be implemented in the next update');
        // This would typically use a library like jsPDF or make an API call to generate PDF
    }
}

// Initialize order service when DOM is ready
let orderService;
document.addEventListener('DOMContentLoaded', function() {
    orderService = new OrderService();
}); 