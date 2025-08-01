/**
 * Main JavaScript for My Bakery Management System
 * Handles UI interactions, page navigation, and data loading
 */

// API service for making authenticated requests
const apiService = {
    /**
     * Make an authenticated API request
     * @param {string} endpoint - API endpoint
     * @param {Object} options - Fetch options
     * @returns {Promise} - Promise with response data
     */
    request: async function(endpoint, options = {}) {
        const token = window.auth.getAuthToken();
        
        const defaultOptions = {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        };
        
        const fetchOptions = { ...defaultOptions, ...options };
        
        try {
            const response = await fetch(`${API_BASE_URL}${endpoint}`, fetchOptions);
            
            // Handle unauthorized errors (token expired)
            if (response.status === 401) {
                try {
                    // Try to refresh the token
                    await window.auth.refreshToken();
                    
                    // Retry the request with new token
                    const newToken = window.auth.getAuthToken();
                    fetchOptions.headers.Authorization = `Bearer ${newToken}`;
                    
                    const retryResponse = await fetch(`${API_BASE_URL}${endpoint}`, fetchOptions);
                    
                    if (!retryResponse.ok) {
                        throw new Error(`API error: ${retryResponse.status}`);
                    }
                    
                    return await retryResponse.json();
                } catch (refreshError) {
                    // If refresh fails, logout
                    console.error('Token refresh failed:', refreshError);
                    window.auth.logout();
                    throw new Error('Session expired. Please login again.');
                }
            }
            
            if (!response.ok) {
                throw new Error(`API error: ${response.status}`);
            }
            
            return await response.json();
        } catch (error) {
            console.error('API request failed:', error);
            throw error;
        }
    },
    
    /**
     * GET request
     * @param {string} endpoint - API endpoint
     * @returns {Promise} - Promise with response data
     */
    get: function(endpoint) {
        return this.request(endpoint, { method: 'GET' });
    },
    
    /**
     * POST request
     * @param {string} endpoint - API endpoint
     * @param {Object} data - Request body data
     * @returns {Promise} - Promise with response data
     */
    post: function(endpoint, data) {
        return this.request(endpoint, {
            method: 'POST',
            body: JSON.stringify(data)
        });
    },
    
    /**
     * PUT request
     * @param {string} endpoint - API endpoint
     * @param {Object} data - Request body data
     * @returns {Promise} - Promise with response data
     */
    put: function(endpoint, data) {
        return this.request(endpoint, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    },
    
    /**
     * DELETE request
     * @param {string} endpoint - API endpoint
     * @returns {Promise} - Promise with response data
     */
    delete: function(endpoint) {
        return this.request(endpoint, { method: 'DELETE' });
    }
};

// Page navigation
function initPageNavigation() {
    const navLinks = document.querySelectorAll('.nav-link[data-page]');
    const pageTitle = document.getElementById('page-title');
    
    navLinks.forEach(link => {
        link.addEventListener('click', function(event) {
            event.preventDefault();
            
            // Get page ID from data attribute
            const pageId = this.getAttribute('data-page');
            
            // Update active nav link
            navLinks.forEach(navLink => navLink.classList.remove('active'));
            this.classList.add('active');
            
            // Update page title
            pageTitle.textContent = this.textContent.trim();
            
            // Show selected page content
            const pageContents = document.querySelectorAll('.page-content');
            pageContents.forEach(content => content.classList.remove('active'));
            
            const selectedPage = document.getElementById(`${pageId}-page`);
            if (selectedPage) {
                selectedPage.classList.add('active');
                
                // Load page data if needed
                loadPageData(pageId);
            }
        });
    });
}

// Load data for specific page
function loadPageData(pageId) {
    switch (pageId) {
        case 'dashboard':
            loadDashboardData();
            break;
        case 'products':
            loadProductsData();
            break;
        case 'inventory':
            loadInventoryData();
            break;
        case 'orders':
            loadOrdersData();
            break;
        case 'customers':
            loadCustomersData();
            break;
        case 'reports':
            loadReportsData();
            break;
        case 'promotions':
            loadPromotionsData();
            break;
        case 'users':
            loadUsersData();
            break;
        case 'settings':
            loadSettingsData();
            break;
    }
}

// Dashboard data loading
function loadDashboardData() {
    // Show loading indicators
    document.getElementById('total-orders').innerHTML = '<div class="spinner-border spinner-border-sm" role="status"></div>';
    document.getElementById('total-revenue').innerHTML = '<div class="spinner-border spinner-border-sm" role="status"></div>';
    document.getElementById('low-stock').innerHTML = '<div class="spinner-border spinner-border-sm" role="status"></div>';
    document.getElementById('pending-orders').innerHTML = '<div class="spinner-border spinner-border-sm" role="status"></div>';
    
    // Load dashboard summary data
    apiService.get('/analytics/dashboard-summary')
        .then(data => {
            document.getElementById('total-orders').textContent = data.totalOrders || 0;
            document.getElementById('total-revenue').textContent = formatCurrency(data.totalRevenue || 0);
            document.getElementById('low-stock').textContent = data.lowStockCount || 0;
            document.getElementById('pending-orders').textContent = data.pendingOrdersCount || 0;
            
            // Load charts
            loadSalesChart(data.salesData);
            loadProductsChart(data.topProducts);
        })
        .catch(error => {
            console.error('Failed to load dashboard summary:', error);
            showErrorMessage('Failed to load dashboard data. Please try again later.');
        });
    
    // Load recent orders
    apiService.get('/orders/recent')
        .then(orders => {
            const tableBody = document.getElementById('recent-orders-table');
            tableBody.innerHTML = '';
            
            if (orders.length === 0) {
                const row = document.createElement('tr');
                row.innerHTML = '<td colspan="5" class="text-center">No recent orders</td>';
                tableBody.appendChild(row);
                return;
            }
            
            orders.forEach(order => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${order.orderNumber}</td>
                    <td>${order.customerName}</td>
                    <td>${formatDate(order.orderDate)}</td>
                    <td><span class="badge badge-${getStatusBadgeClass(order.status)}">${order.status}</span></td>
                    <td>${formatCurrency(order.total)}</td>
                `;
                tableBody.appendChild(row);
            });
        })
        .catch(error => {
            console.error('Failed to load recent orders:', error);
            const tableBody = document.getElementById('recent-orders-table');
            tableBody.innerHTML = '<tr><td colspan="5" class="text-center text-danger">Failed to load orders</td></tr>';
        });
    
    // Load low stock items
    apiService.get('/inventory/low-stock')
        .then(items => {
            const tableBody = document.getElementById('low-stock-table');
            tableBody.innerHTML = '';
            
            if (items.length === 0) {
                const row = document.createElement('tr');
                row.innerHTML = '<td colspan="5" class="text-center">No low stock items</td>';
                tableBody.appendChild(row);
                return;
            }
            
            items.forEach(item => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${item.productName}</td>
                    <td>${item.category}</td>
                    <td class="low-stock">${item.currentStock}</td>
                    <td>${item.minStock}</td>
                    <td><button class="btn btn-sm btn-outline-primary restock-btn" data-product-id="${item.productId}">Restock</button></td>
                `;
                tableBody.appendChild(row);
            });
            
            // Add event listeners to restock buttons
            document.querySelectorAll('.restock-btn').forEach(button => {
                button.addEventListener('click', function() {
                    const productId = this.getAttribute('data-product-id');
                    showRestockModal(productId);
                });
            });
        })
        .catch(error => {
            console.error('Failed to load low stock items:', error);
            const tableBody = document.getElementById('low-stock-table');
            tableBody.innerHTML = '<tr><td colspan="5" class="text-center text-danger">Failed to load inventory data</td></tr>';
        });
}

// Load sales chart
function loadSalesChart(salesData) {
    const ctx = document.getElementById('sales-chart').getContext('2d');
    
    // Destroy existing chart if it exists
    if (window.salesChart) {
        window.salesChart.destroy();
    }
    
    // Create new chart
    window.salesChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: salesData.map(item => item.date),
            datasets: [{
                label: 'Sales',
                data: salesData.map(item => item.amount),
                backgroundColor: 'rgba(54, 162, 235, 0.2)',
                borderColor: 'rgba(54, 162, 235, 1)',
                borderWidth: 2,
                tension: 0.4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: function(value) {
                            return '$' + value;
                        }
                    }
                }
            },
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            return '$' + context.raw;
                        }
                    }
                }
            }
        }
    });
}

// Load products chart
function loadProductsChart(topProducts) {
    const ctx = document.getElementById('products-chart').getContext('2d');
    
    // Destroy existing chart if it exists
    if (window.productsChart) {
        window.productsChart.destroy();
    }
    
    // Create new chart
    window.productsChart = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: topProducts.map(product => product.name),
            datasets: [{
                data: topProducts.map(product => product.quantity),
                backgroundColor: [
                    'rgba(255, 99, 132, 0.7)',
                    'rgba(54, 162, 235, 0.7)',
                    'rgba(255, 206, 86, 0.7)',
                    'rgba(75, 192, 192, 0.7)',
                    'rgba(153, 102, 255, 0.7)'
                ],
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'right'
                }
            }
        }
    });
}

// Products page implementation
function loadProductsData() {
    console.log('Loading products data...');
    
    // Update products page content
    const productsPage = document.getElementById('products-page');
    productsPage.innerHTML = `
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h3>Products Management</h3>
            <div>
                <button id="add-category-btn" class="btn btn-outline-secondary me-2">
                    <i class="bi bi-folder-plus me-2"></i>Add Category
                </button>
                <button id="add-product-btn" class="btn btn-primary">
                    <i class="bi bi-plus-circle me-2"></i>Add New Product
                </button>
            </div>
        </div>
        
        <!-- Categories Section -->
        <div class="card mb-4">
            <div class="card-header">
                <h5 class="mb-0">Categories</h5>
            </div>
            <div class="card-body">
                <div class="row" id="categories-container">
                    <div class="col-12 text-center">
                        <div class="spinner-border text-primary" role="status">
                            <span class="visually-hidden">Loading categories...</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Products Section -->
        <div class="card mb-4">
            <div class="card-header">
                <div class="row align-items-center">
                    <div class="col">
                        <h5 class="mb-0">Products List</h5>
                    </div>
                    <div class="col-md-4">
                        <input type="text" id="product-search" class="form-control" placeholder="Search products...">
                    </div>
                </div>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-striped table-hover">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Name</th>
                                <th>Category</th>
                                <th>Price</th>
                                <th>Cost</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody id="products-table">
                            <tr>
                                <td colspan="7" class="text-center">
                                    <div class="spinner-border text-primary" role="status">
                                        <span class="visually-hidden">Loading...</span>
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    `;
    
    // Add event listeners
    document.getElementById('add-product-btn').addEventListener('click', showAddProductModal);
    document.getElementById('add-category-btn').addEventListener('click', showAddCategoryModal);
    
    // Add event listener for the search input
    document.getElementById('product-search').addEventListener('input', function() {
        const searchTerm = this.value.toLowerCase();
        filterProducts(searchTerm);
    });
    
    // Fetch data
    fetchCategories();
    fetchProducts();
}

// Fetch products from API
function fetchProducts() {
    apiService.get('/products')
        .then(response => {
            if (response.success) {
                displayProducts(response.data);
            } else {
                showErrorMessage('Failed to load products: ' + response.error.message);
                document.getElementById('products-table').innerHTML = 
                    '<tr><td colspan="6" class="text-center text-danger">Failed to load products</td></tr>';
            }
        })
        .catch(error => {
            console.error('Error fetching products:', error);
            document.getElementById('products-table').innerHTML = 
                '<tr><td colspan="6" class="text-center text-danger">Failed to load products</td></tr>';
        });
}

// Display products in the table
function displayProducts(products) {
    const tableBody = document.getElementById('products-table');
    
    if (!products || products.length === 0) {
        tableBody.innerHTML = '<tr><td colspan="6" class="text-center">No products found</td></tr>';
        return;
    }
    
    tableBody.innerHTML = '';
    
    products.forEach(product => {
        const row = document.createElement('tr');
        row.setAttribute('data-product-id', product.id);
        
        row.innerHTML = `
            <td>${product.id}</td>
            <td>${product.name}</td>
            <td>${product.category ? product.category.name : 'N/A'}</td>
            <td>${formatCurrency(product.price)}</td>
            <td>${formatCurrency(product.costPrice || 0)}</td>
            <td>
                <span class="badge ${product.active ? 'bg-success' : 'bg-danger'}">
                    ${product.active ? 'Active' : 'Inactive'}
                </span>
                ${product.featured ? '<span class="badge bg-info ms-1">Featured</span>' : ''}
            </td>
            <td>
                <div class="btn-group btn-group-sm" role="group">
                    <button type="button" class="btn btn-outline-primary view-product-btn" data-product-id="${product.id}">
                        <i class="bi bi-eye"></i>
                    </button>
                    <button type="button" class="btn btn-outline-secondary edit-product-btn" data-product-id="${product.id}">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button type="button" class="btn btn-outline-danger delete-product-btn" data-product-id="${product.id}">
                        <i class="bi bi-trash"></i>
                    </button>
                </div>
            </td>
        `;
        
        tableBody.appendChild(row);
    });
    
    // Add event listeners to action buttons
    document.querySelectorAll('.view-product-btn').forEach(button => {
        button.addEventListener('click', function() {
            const productId = this.getAttribute('data-product-id');
            viewProduct(productId);
        });
    });
    
    document.querySelectorAll('.edit-product-btn').forEach(button => {
        button.addEventListener('click', function() {
            const productId = this.getAttribute('data-product-id');
            editProduct(productId);
        });
    });
    
    document.querySelectorAll('.delete-product-btn').forEach(button => {
        button.addEventListener('click', function() {
            const productId = this.getAttribute('data-product-id');
            confirmDeleteProduct(productId);
        });
    });
}

// Filter products based on search term
function filterProducts(searchTerm) {
    const rows = document.querySelectorAll('#products-table tr');
    
    rows.forEach(row => {
        const text = row.textContent.toLowerCase();
        if (text.includes(searchTerm)) {
            row.style.display = '';
        } else {
            row.style.display = 'none';
        }
    });
}

// Show add product modal
function showAddProductModal() {
    // Create modal HTML
    const modalHTML = `
        <div class="modal fade" id="productModal" tabindex="-1" aria-labelledby="productModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="productModalLabel">Add New Product</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form id="product-form">
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="product-name" class="form-label">Product Name</label>
                                    <input type="text" class="form-control" id="product-name" required>
                                </div>
                                <div class="col-md-6">
                                    <label for="product-category" class="form-label">Category</label>
                                    <select class="form-select" id="product-category">
                                        <option value="">Select Category</option>
                                        <!-- Categories will be loaded dynamically -->
                                    </select>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="product-price" class="form-label">Price</label>
                                    <div class="input-group">
                                        <span class="input-group-text">$</span>
                                        <input type="number" class="form-control" id="product-price" step="0.01" min="0" required>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <label for="product-cost-price" class="form-label">Cost Price</label>
                                    <div class="input-group">
                                        <span class="input-group-text">$</span>
                                        <input type="number" class="form-control" id="product-cost-price" step="0.01" min="0">
                                    </div>
                                </div>
                            </div>
                            <div class="mb-3">
                                <label for="product-description" class="form-label">Description</label>
                                <textarea class="form-control" id="product-description" rows="3"></textarea>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="product-sku" class="form-label">SKU</label>
                                    <input type="text" class="form-control" id="product-sku">
                                </div>
                                <div class="col-md-6">
                                    <label for="product-barcode" class="form-label">Barcode</label>
                                    <input type="text" class="form-control" id="product-barcode">
                                </div>
                            </div>
                            <div class="form-check form-switch mb-3">
                                <input class="form-check-input" type="checkbox" id="product-active" checked>
                                <label class="form-check-label" for="product-active">Active</label>
                            </div>
                            <div class="form-check form-switch mb-3">
                                <input class="form-check-input" type="checkbox" id="product-featured">
                                <label class="form-check-label" for="product-featured">Featured</label>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="button" class="btn btn-primary" id="save-product-btn">Save Product</button>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    // Add modal to the DOM
    const modalContainer = document.createElement('div');
    modalContainer.innerHTML = modalHTML;
    document.body.appendChild(modalContainer);
    
    // Initialize the modal
    const modal = new bootstrap.Modal(document.getElementById('productModal'));
    modal.show();
    
    // Load categories
    loadCategories();
    
    // Add event listener to save button
    document.getElementById('save-product-btn').addEventListener('click', saveProduct);
    
    // Clean up when modal is hidden
    document.getElementById('productModal').addEventListener('hidden.bs.modal', function() {
        document.body.removeChild(modalContainer);
    });
}

// Fetch categories from API
function fetchCategories() {
    apiService.get('/categories')
        .then(response => {
            if (response.success) {
                displayCategories(response.data);
            } else {
                showErrorMessage('Failed to load categories: ' + response.error.message);
                document.getElementById('categories-container').innerHTML = 
                    '<div class="col-12 text-center text-danger">Failed to load categories</div>';
            }
        })
        .catch(error => {
            console.error('Error fetching categories:', error);
            document.getElementById('categories-container').innerHTML = 
                '<div class="col-12 text-center text-danger">Failed to load categories</div>';
        });
}

// Display categories in the categories container
function displayCategories(categories) {
    const container = document.getElementById('categories-container');
    
    if (!categories || categories.length === 0) {
        container.innerHTML = '<div class="col-12 text-center">No categories found</div>';
        return;
    }
    
    container.innerHTML = '';
    
    categories.forEach(category => {
        const categoryCard = document.createElement('div');
        categoryCard.className = 'col-md-3 col-sm-6 mb-3';
        categoryCard.innerHTML = `
            <div class="card h-100">
                <div class="card-body">
                    <h6 class="card-title">${category.name}</h6>
                    <p class="card-text text-muted small">${category.description || 'No description'}</p>
                    <div class="d-flex justify-content-between align-items-center">
                        <span class="badge ${category.active ? 'bg-success' : 'bg-danger'}">
                            ${category.active ? 'Active' : 'Inactive'}
                        </span>
                        <div class="btn-group btn-group-sm">
                            <button type="button" class="btn btn-outline-primary edit-category-btn" data-category-id="${category.id}">
                                <i class="bi bi-pencil"></i>
                            </button>
                            <button type="button" class="btn btn-outline-danger delete-category-btn" data-category-id="${category.id}">
                                <i class="bi bi-trash"></i>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        `;
        container.appendChild(categoryCard);
    });
    
    // Add event listeners to category action buttons
    document.querySelectorAll('.edit-category-btn').forEach(button => {
        button.addEventListener('click', function() {
            const categoryId = this.getAttribute('data-category-id');
            editCategory(categoryId);
        });
    });
    
    document.querySelectorAll('.delete-category-btn').forEach(button => {
        button.addEventListener('click', function() {
            const categoryId = this.getAttribute('data-category-id');
            confirmDeleteCategory(categoryId);
        });
    });
}

// Load categories for the product form
function loadCategories() {
    apiService.get('/categories')
        .then(response => {
            if (response.success) {
                const categorySelect = document.getElementById('product-category');
                categorySelect.innerHTML = '<option value="">Select Category</option>';
                response.data.forEach(category => {
                    const option = document.createElement('option');
                    option.value = category.id;
                    option.textContent = category.name;
                    categorySelect.appendChild(option);
                });
            } else {
                console.error('Failed to load categories:', response.error.message);
            }
        })
        .catch(error => {
            console.error('Error loading categories:', error);
        });
}

// Show add category modal
function showAddCategoryModal() {
    // Create modal HTML
    const modalHTML = `
        <div class="modal fade" id="categoryModal" tabindex="-1" aria-labelledby="categoryModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="categoryModalLabel">Add New Category</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form id="category-form">
                            <div class="mb-3">
                                <label for="category-name" class="form-label">Category Name</label>
                                <input type="text" class="form-control" id="category-name" required>
                            </div>
                            <div class="mb-3">
                                <label for="category-description" class="form-label">Description</label>
                                <textarea class="form-control" id="category-description" rows="3"></textarea>
                            </div>
                            <div class="form-check form-switch mb-3">
                                <input class="form-check-input" type="checkbox" id="category-active" checked>
                                <label class="form-check-label" for="category-active">Active</label>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="button" class="btn btn-primary" id="save-category-btn">Save Category</button>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    // Add modal to the DOM
    const modalContainer = document.createElement('div');
    modalContainer.innerHTML = modalHTML;
    document.body.appendChild(modalContainer);
    
    // Initialize the modal
    const modal = new bootstrap.Modal(document.getElementById('categoryModal'));
    modal.show();
    
    // Add event listener to save button
    document.getElementById('save-category-btn').addEventListener('click', saveCategory);
    
    // Clean up when modal is hidden
    document.getElementById('categoryModal').addEventListener('hidden.bs.modal', function() {
        document.body.removeChild(modalContainer);
    });
}

// Save category (create new or update existing)
function saveCategory() {
    const categoryId = document.getElementById('category-form').getAttribute('data-category-id');
    
    const categoryData = {
        name: document.getElementById('category-name').value,
        description: document.getElementById('category-description').value,
        active: document.getElementById('category-active').checked
    };
    
    // Validate required fields
    if (!categoryData.name) {
        alert('Please enter a category name');
        return;
    }
    
    // Determine if this is a create or update operation
    const isUpdate = !!categoryId;
    const endpoint = isUpdate ? `/categories/${categoryId}` : '/categories';
    const method = isUpdate ? 'put' : 'post';
    
    // Save the category
    apiService[method](endpoint, categoryData)
        .then(response => {
            if (response.success) {
                // Close the modal
                bootstrap.Modal.getInstance(document.getElementById('categoryModal')).hide();
                
                // Refresh the categories list
                fetchCategories();
                
                // Show success message
                alert(`Category ${isUpdate ? 'updated' : 'created'} successfully`);
            } else {
                alert('Failed to save category: ' + response.error.message);
            }
        })
        .catch(error => {
            console.error('Error saving category:', error);
            alert('Failed to save category. Please try again.');
        });
}

// Edit category
function editCategory(categoryId) {
    apiService.get(`/categories/${categoryId}`)
        .then(response => {
            if (response.success) {
                const category = response.data;
                
                // Show the category modal
                showAddCategoryModal();
                
                // Wait for modal to be created, then populate it
                setTimeout(() => {
                    document.getElementById('categoryModalLabel').textContent = 'Edit Category';
                    document.getElementById('category-form').setAttribute('data-category-id', category.id);
                    document.getElementById('category-name').value = category.name;
                    document.getElementById('category-description').value = category.description || '';
                    document.getElementById('category-active').checked = category.active;
                }, 100);
            } else {
                alert('Failed to load category: ' + response.error.message);
            }
        })
        .catch(error => {
            console.error('Error loading category:', error);
            alert('Failed to load category. Please try again.');
        });
}

// Confirm delete category
function confirmDeleteCategory(categoryId) {
    if (confirm('Are you sure you want to delete this category? This action cannot be undone.')) {
        deleteCategory(categoryId);
    }
}

// Delete category
function deleteCategory(categoryId) {
    apiService.delete(`/categories/${categoryId}`)
        .then(response => {
            if (response.success) {
                // Refresh the categories list
                fetchCategories();
                
                // Show success message
                alert('Category deleted successfully');
            } else {
                alert('Failed to delete category: ' + response.error.message);
            }
        })
        .catch(error => {
            console.error('Error deleting category:', error);
            alert('Failed to delete category. Please try again.');
        });
}

// Save product (create new or update existing)
function saveProduct() {
    const productId = document.getElementById('product-form').getAttribute('data-product-id');
    
    const productData = {
        name: document.getElementById('product-name').value,
        description: document.getElementById('product-description').value,
        price: parseFloat(document.getElementById('product-price').value),
        costPrice: parseFloat(document.getElementById('product-cost-price').value) || 0,
        sku: document.getElementById('product-sku').value,
        barcode: document.getElementById('product-barcode').value,
        active: document.getElementById('product-active').checked,
        featured: document.getElementById('product-featured').checked
    };
    
    const categoryId = document.getElementById('product-category').value;
    if (categoryId) {
        productData.category = { id: parseInt(categoryId) };
    }
    
    // Validate required fields
    if (!productData.name || !productData.price) {
        alert('Please fill in all required fields');
        return;
    }
    
    // Determine if this is a create or update operation
    const isUpdate = !!productId;
    const endpoint = isUpdate ? `/products/${productId}` : '/products';
    const method = isUpdate ? 'put' : 'post';
    
    // Save the product
    apiService[method](endpoint, productData)
        .then(response => {
            if (response.success) {
                // Close the modal
                bootstrap.Modal.getInstance(document.getElementById('productModal')).hide();
                
                // Refresh the products list
                fetchProducts();
                
                // Show success message
                alert(`Product ${isUpdate ? 'updated' : 'created'} successfully`);
            } else {
                alert('Failed to save product: ' + response.error.message);
            }
        })
        .catch(error => {
            console.error('Error saving product:', error);
            alert('Failed to save product. Please try again.');
        });
}

// View product details
function viewProduct(productId) {
    apiService.get(`/products/${productId}`)
        .then(response => {
            if (response.success) {
                const product = response.data;
                
                // Create modal HTML
                const modalHTML = `
                    <div class="modal fade" id="viewProductModal" tabindex="-1" aria-hidden="true">
                        <div class="modal-dialog modal-lg">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title">Product Details</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                </div>
                                <div class="modal-body">
                                    <div class="row">
                                        <div class="col-md-6">
                                            <h4>${product.name}</h4>
                                            <p class="text-muted">${product.category ? product.category.name : 'No Category'}</p>
                                            <p>${product.description || 'No description available.'}</p>
                                            
                                            <h5>Pricing</h5>
                                            <p>Price: ${formatCurrency(product.price)}</p>
                                            <p>Cost: ${formatCurrency(product.costPrice || 0)}</p>
                                            
                                            <h5>Inventory</h5>
                                            <p>SKU: ${product.sku || 'N/A'}</p>
                                            <p>Barcode: ${product.barcode || 'N/A'}</p>
                                            
                                            <h5>Status</h5>
                                            <p>
                                                <span class="badge ${product.active ? 'bg-success' : 'bg-danger'}">
                                                    ${product.active ? 'Active' : 'Inactive'}
                                                </span>
                                                ${product.featured ? '<span class="badge bg-info ms-2">Featured</span>' : ''}
                                            </p>
                                        </div>
                                        <div class="col-md-6">
                                            <div class="text-center mb-3">
                                                <img src="${product.images && product.images.length > 0 ? product.images[0] : 'https://via.placeholder.com/300x200?text=No+Image'}" 
                                                     class="img-fluid rounded" alt="${product.name}">
                                            </div>
                                            
                                            <h5>Additional Information</h5>
                                            <p>Created: ${formatDate(product.createdAt)}</p>
                                            <p>Last Updated: ${formatDate(product.updatedAt)}</p>
                                        </div>
                                    </div>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                                    <button type="button" class="btn btn-primary edit-from-view" data-product-id="${product.id}">Edit</button>
                                </div>
                            </div>
                        </div>
                    </div>
                `;
                
                // Add modal to the DOM
                const modalContainer = document.createElement('div');
                modalContainer.innerHTML = modalHTML;
                document.body.appendChild(modalContainer);
                
                // Initialize the modal
                const modal = new bootstrap.Modal(document.getElementById('viewProductModal'));
                modal.show();
                
                // Add event listener to edit button
                document.querySelector('.edit-from-view').addEventListener('click', function() {
                    modal.hide();
                    editProduct(this.getAttribute('data-product-id'));
                });
                
                // Clean up when modal is hidden
                document.getElementById('viewProductModal').addEventListener('hidden.bs.modal', function() {
                    document.body.removeChild(modalContainer);
                });
            } else {
                alert('Failed to load product details: ' + response.error.message);
            }
        })
        .catch(error => {
            console.error('Error loading product details:', error);
            alert('Failed to load product details. Please try again.');
        });
}

// Edit product
function editProduct(productId) {
    apiService.get(`/products/${productId}`)
        .then(response => {
            if (response.success) {
                const product = response.data;
                
                // Show the add product modal (reusing the same modal)
                showAddProductModal();
                
                // Set form as edit mode
                const form = document.getElementById('product-form');
                form.setAttribute('data-product-id', product.id);
                
                // Update modal title
                document.getElementById('productModalLabel').textContent = 'Edit Product';
                
                // Fill the form with product data
                document.getElementById('product-name').value = product.name;
                document.getElementById('product-description').value = product.description || '';
                document.getElementById('product-price').value = product.price;
                document.getElementById('product-cost-price').value = product.costPrice || '';
                document.getElementById('product-sku').value = product.sku || '';
                document.getElementById('product-barcode').value = product.barcode || '';
                document.getElementById('product-active').checked = product.active;
                document.getElementById('product-featured').checked = product.featured;
                
                // Set category (after categories are loaded)
                if (product.category) {
                    const categorySelect = document.getElementById('product-category');
                    // Wait for categories to load
                    const checkCategoriesLoaded = setInterval(() => {
                        if (categorySelect.options.length > 1) {
                            categorySelect.value = product.category.id;
                            clearInterval(checkCategoriesLoaded);
                        }
                    }, 100);
                }
            } else {
                alert('Failed to load product for editing: ' + response.error.message);
            }
        })
        .catch(error => {
            console.error('Error loading product for editing:', error);
            alert('Failed to load product for editing. Please try again.');
        });
}

// Confirm delete product
function confirmDeleteProduct(productId) {
    if (confirm('Are you sure you want to delete this product? This action cannot be undone.')) {
        deleteProduct(productId);
    }
}

// Delete product
function deleteProduct(productId) {
    apiService.delete(`/products/${productId}`)
        .then(response => {
            if (response.success) {
                // Remove the product from the table
                const row = document.querySelector(`tr[data-product-id="${productId}"]`);
                if (row) {
                    row.remove();
                }
                
                // Show success message
                alert('Product deleted successfully');
                
                // Refresh the products list if the table is now empty
                const tableBody = document.getElementById('products-table');
                if (tableBody.children.length === 0) {
                    fetchProducts();
                }
            } else {
                alert('Failed to delete product: ' + response.error.message);
            }
        })
        .catch(error => {
            console.error('Error deleting product:', error);
            alert('Failed to delete product. Please try again.');
        });
}

function loadInventoryData() {
    // Implementation will be added later
    console.log('Loading inventory data...');
}

function loadOrdersData() {
    // Implementation will be added later
    console.log('Loading orders data...');
}

function loadCustomersData() {
    console.log('Loading customers data...');
    
    // Update customers page content
    const customersPage = document.getElementById('customers-page');
    customersPage.innerHTML = `
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h3>Customers Management</h3>
            <button id="add-customer-btn" class="btn btn-primary">
                <i class="bi bi-plus-circle me-2"></i>Add New Customer
            </button>
        </div>
        
        <div class="card mb-4">
            <div class="card-header">
                <div class="row align-items-center">
                    <div class="col">
                        <h5 class="mb-0">Customers List</h5>
                    </div>
                    <div class="col-md-4">
                        <input type="text" id="customer-search" class="form-control" placeholder="Search customers...">
                    </div>
                </div>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-striped table-hover">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Name</th>
                                <th>Email</th>
                                <th>Phone</th>
                                <th>Status</th>
                                <th>Addresses</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody id="customers-table">
                            <tr>
                                <td colspan="7" class="text-center">
                                    <div class="spinner-border text-primary" role="status">
                                        <span class="visually-hidden">Loading...</span>
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    `;
    
    // Add event listeners
    document.getElementById('add-customer-btn').addEventListener('click', showAddCustomerModal);
    
    // Add event listener for the search input
    document.getElementById('customer-search').addEventListener('input', function() {
        const searchTerm = this.value.toLowerCase();
        filterCustomers(searchTerm);
    });
    
    // Fetch customers from API
    fetchCustomers();
}

function loadReportsData() {
    // Implementation will be added later
    console.log('Loading reports data...');
}

function loadPromotionsData() {
    // Implementation will be added later
    console.log('Loading promotions data...');
}

function loadUsersData() {
    console.log('Loading users data...');
    
    // Update users page content
    const usersPage = document.getElementById('users-page');
    usersPage.innerHTML = `
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h3>Users Management</h3>
            <button id="add-user-btn" class="btn btn-primary">
                <i class="bi bi-plus-circle me-2"></i>Add New User
            </button>
        </div>
        
        <div class="card mb-4">
            <div class="card-header">
                <div class="row align-items-center">
                    <div class="col">
                        <h5 class="mb-0">Users List</h5>
                    </div>
                    <div class="col-md-4">
                        <input type="text" id="user-search" class="form-control" placeholder="Search users...">
                    </div>
                </div>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-striped table-hover">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Name</th>
                                <th>Email</th>
                                <th>Phone</th>
                                <th>Role</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody id="users-table">
                            <tr>
                                <td colspan="7" class="text-center">
                                    <div class="spinner-border text-primary" role="status">
                                        <span class="visually-hidden">Loading...</span>
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    `;
    
    // Add event listeners
    document.getElementById('add-user-btn').addEventListener('click', showAddUserModal);
    
    // Add event listener for the search input
    document.getElementById('user-search').addEventListener('input', function() {
        const searchTerm = this.value.toLowerCase();
        filterUsers(searchTerm);
    });
    
    // Fetch users from API
    fetchUsers();
}

// Fetch users from API
function fetchUsers() {
    apiService.get('/users')
        .then(response => {
            if (response.success) {
                displayUsers(response.data);
            } else {
                showErrorMessage('Failed to load users: ' + response.error.message);
                document.getElementById('users-table').innerHTML = 
                    '<tr><td colspan="7" class="text-center text-danger">Failed to load users</td></tr>';
            }
        })
        .catch(error => {
            console.error('Error fetching users:', error);
            document.getElementById('users-table').innerHTML = 
                '<tr><td colspan="7" class="text-center text-danger">Failed to load users</td></tr>';
        });
}

// Display users in the table
function displayUsers(users) {
    const tableBody = document.getElementById('users-table');
    
    if (!users || users.length === 0) {
        tableBody.innerHTML = '<tr><td colspan="7" class="text-center">No users found</td></tr>';
        return;
    }
    
    tableBody.innerHTML = '';
    
    users.forEach(user => {
        const row = document.createElement('tr');
        row.setAttribute('data-user-id', user.id);
        
        row.innerHTML = `
            <td>${user.id}</td>
            <td>${user.firstName} ${user.lastName}</td>
            <td>${user.email}</td>
            <td>${user.phone || 'N/A'}</td>
            <td>
                <span class="badge ${getRoleBadgeClass(user.role)}">
                    ${user.role}
                </span>
            </td>
            <td>
                <span class="badge ${user.active ? 'bg-success' : 'bg-danger'}">
                    ${user.active ? 'Active' : 'Inactive'}
                </span>
                ${user.emailVerified ? '<span class="badge bg-info ms-1">Verified</span>' : ''}
            </td>
            <td>
                <div class="btn-group btn-group-sm" role="group">
                    <button type="button" class="btn btn-outline-primary view-user-btn" data-user-id="${user.id}">
                        <i class="bi bi-eye"></i>
                    </button>
                    <button type="button" class="btn btn-outline-secondary edit-user-btn" data-user-id="${user.id}">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button type="button" class="btn btn-outline-info manage-user-addresses-btn" data-user-id="${user.id}">
                        <i class="bi bi-geo-alt"></i>
                    </button>
                    <button type="button" class="btn btn-outline-danger delete-user-btn" data-user-id="${user.id}">
                        <i class="bi bi-trash"></i>
                    </button>
                </div>
            </td>
        `;
        
        tableBody.appendChild(row);
    });
    
    // Add event listeners to action buttons
    document.querySelectorAll('.view-user-btn').forEach(button => {
        button.addEventListener('click', function() {
            const userId = this.getAttribute('data-user-id');
            viewUser(userId);
        });
    });
    
    document.querySelectorAll('.edit-user-btn').forEach(button => {
        button.addEventListener('click', function() {
            const userId = this.getAttribute('data-user-id');
            editUser(userId);
        });
    });
    
    document.querySelectorAll('.manage-user-addresses-btn').forEach(button => {
        button.addEventListener('click', function() {
            const userId = this.getAttribute('data-user-id');
            manageUserAddresses(userId);
        });
    });
    
    document.querySelectorAll('.delete-user-btn').forEach(button => {
        button.addEventListener('click', function() {
            const userId = this.getAttribute('data-user-id');
            confirmDeleteUser(userId);
        });
    });
}

// Set up event listeners for user management
function setupUserEventListeners() {
    const userFormModal = new bootstrap.Modal(document.getElementById('userFormModal'));
    const addressModal = new bootstrap.Modal(document.getElementById('addressModal'));
    
    // Add user button
    document.getElementById('add-user-btn').addEventListener('click', () => {
        document.getElementById('userFormModalLabel').textContent = 'Add User';
        document.getElementById('user-form').reset();
        document.getElementById('user-id').value = '';
        document.getElementById('password-hint').style.display = 'none';
        document.getElementById('user-password').required = true;
        userFormModal.show();
    });
    
    // Save user button
    document.getElementById('save-user-btn').addEventListener('click', () => {
        const form = document.getElementById('user-form');
        
        if (form.checkValidity()) {
            const userId = document.getElementById('user-id').value;
            const userData = {
                email: document.getElementById('user-email').value,
                firstName: document.getElementById('user-first-name').value,
                lastName: document.getElementById('user-last-name').value,
                phone: document.getElementById('user-phone').value,
                role: document.getElementById('user-role').value,
                active: document.getElementById('user-active').checked
            };
            
            // Add password only if provided or for new users
            const password = document.getElementById('user-password').value;
            if (password) {
                userData.password = password;
            }
            
            if (userId) {
                // Update existing user
                apiService.put(`/users/${userId}`, userData)
                    .then(response => {
                        userFormModal.hide();
                        fetchUsers();
                    })
                    .catch(error => {
                        console.error('Error updating user:', error);
                        alert('Failed to update user. Please try again.');
                    });
            } else {
                // Create new user
                apiService.post('/users', userData)
                    .then(response => {
                        userFormModal.hide();
                        fetchUsers();
                    })
                    .catch(error => {
                        console.error('Error creating user:', error);
                        alert('Failed to create user. Please try again.');
                    });
            }
        } else {
            form.reportValidity();
        }
    });
    
    // Edit user button (delegated event)
    document.getElementById('users-page').addEventListener('click', event => {
        const editBtn = event.target.closest('.edit-user-btn');
        if (editBtn) {
            const userId = editBtn.getAttribute('data-user-id');
            editUser(userId, userFormModal);
        }
    });
    
    // Delete user button (delegated event)
    document.getElementById('users-page').addEventListener('click', event => {
        const deleteBtn = event.target.closest('.delete-user-btn');
        if (deleteBtn) {
            const userId = deleteBtn.getAttribute('data-user-id');
            confirmDeleteUser(userId);
        }
    });
    
    // Address management button (delegated event)
    document.getElementById('users-page').addEventListener('click', event => {
        const addressBtn = event.target.closest('.address-btn');
        if (addressBtn) {
            const userId = addressBtn.getAttribute('data-user-id');
            const userName = addressBtn.getAttribute('data-user-name');
            manageAddresses(userId, userName, addressModal);
        }
    });
    
    // Add address button
    document.getElementById('add-address-btn').addEventListener('click', () => {
        showAddressForm(true);
    });
    
    // Cancel address button
    document.getElementById('cancel-address-btn').addEventListener('click', () => {
        showAddressForm(false);
    });
    
    // Save address button
    document.getElementById('save-address-btn').addEventListener('click', () => {
        saveAddress(addressModal);
    });
}

// Edit user
function editUser(userId, modal) {
    apiService.get(`/users/${userId}`)
        .then(response => {
            const user = response.data || response;
            
            document.getElementById('userFormModalLabel').textContent = 'Edit User';
            document.getElementById('user-id').value = user.id;
            document.getElementById('user-email').value = user.email;
            document.getElementById('user-password').value = '';
            document.getElementById('user-password').required = false;
            document.getElementById('password-hint').style.display = 'block';
            document.getElementById('user-first-name').value = user.firstName;
            document.getElementById('user-last-name').value = user.lastName;
            document.getElementById('user-phone').value = user.phone || '';
            document.getElementById('user-role').value = user.role;
            document.getElementById('user-active').checked = user.active;
            
            modal.show();
        })
        .catch(error => {
            console.error('Error fetching user details:', error);
            alert('Failed to load user details. Please try again.');
        });
}

// Confirm delete user
function confirmDeleteUser(userId) {
    if (confirm('Are you sure you want to delete this user? This action cannot be undone.')) {
        apiService.delete(`/users/${userId}`)
            .then(response => {
                fetchUsers();
            })
            .catch(error => {
                console.error('Error deleting user:', error);
                alert('Failed to delete user. Please try again.');
            });
    }
}

// Manage addresses
function manageAddresses(userId, userName, modal) {
    document.getElementById('address-user-name').textContent = `${userName}'s Addresses`;
    document.getElementById('address-user-id').value = userId;
    
    // Reset and hide the form
    document.getElementById('address-form').reset();
    showAddressForm(false);
    
    // Show loading indicator
    document.getElementById('addresses-container').innerHTML = `
        <div class="text-center py-4">
            <div class="spinner-border" role="status">
                <span class="visually-hidden">Loading...</span>
            </div>
        </div>
    `;
    
    // Show the modal
    modal.show();
    
    // Fetch addresses
    fetchAddresses(userId);
}

// Fetch addresses
function fetchAddresses(userId) {
    apiService.get(`/users/addresses`)
        .then(response => {
            const addresses = response.data || [];
            renderAddresses(addresses);
        })
        .catch(error => {
            console.error('Error fetching addresses:', error);
            document.getElementById('addresses-container').innerHTML = `
                <div class="alert alert-danger">
                    Failed to load addresses. Please try again.
                </div>
            `;
        });
}

// Render addresses
function renderAddresses(addresses) {
    const container = document.getElementById('addresses-container');
    
    if (addresses.length === 0) {
        container.innerHTML = `
            <div class="alert alert-info">
                No addresses found. Click "Add Address" to create one.
            </div>
        `;
        return;
    }
    
    container.innerHTML = addresses.map(address => `
        <div class="card mb-3" data-address-id="${address.id}">
            <div class="card-body">
                <div class="d-flex justify-content-between">
                    <h6 class="card-title">
                        ${address.type} Address
                        ${address.default ? '<span class="badge bg-primary ms-2">Default</span>' : ''}
                    </h6>
                    <div class="btn-group btn-group-sm">
                        <button class="btn btn-outline-primary edit-address-btn" data-address-id="${address.id}">
                            <i class="bi bi-pencil"></i>
                        </button>
                        <button class="btn btn-outline-danger delete-address-btn" data-address-id="${address.id}">
                            <i class="bi bi-trash"></i>
                        </button>
                    </div>
                </div>
                <p class="card-text">
                    ${address.addressLine1}<br>
                    ${address.addressLine2 ? address.addressLine2 + '<br>' : ''}
                    ${address.city}, ${address.state} ${address.postalCode}<br>
                    ${address.country}
                    ${address.phone ? '<br>Phone: ' + address.phone : ''}
                </p>
            </div>
        </div>
    `).join('');
    
    // Add event listeners for address actions
    setupAddressEventListeners();
}

// Set up event listeners for address actions
function setupAddressEventListeners() {
    // Edit address button
    document.querySelectorAll('.edit-address-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            const addressId = btn.getAttribute('data-address-id');
            editAddress(addressId);
        });
    });
    
    // Delete address button
    document.querySelectorAll('.delete-address-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            const addressId = btn.getAttribute('data-address-id');
            confirmDeleteAddress(addressId);
        });
    });
}

// Show/hide address form
function showAddressForm(show) {
    const form = document.getElementById('address-form');
    const container = document.getElementById('addresses-container');
    
    if (show) {
        form.classList.remove('d-none');
        container.classList.add('d-none');
    } else {
        form.classList.add('d-none');
        container.classList.remove('d-none');
    }
}

// Edit address
function editAddress(addressId) {
    apiService.get(`/users/addresses/${addressId}`)
        .then(response => {
            const address = response.data || response;
            
            document.getElementById('address-id').value = address.id;
            document.getElementById('address-line1').value = address.addressLine1;
            document.getElementById('address-line2').value = address.addressLine2 || '';
            document.getElementById('address-city').value = address.city;
            document.getElementById('address-state').value = address.state;
            document.getElementById('address-postal-code').value = address.postalCode;
            document.getElementById('address-country').value = address.country;
            document.getElementById('address-phone').value = address.phone || '';
            document.getElementById('address-type').value = address.type;
            document.getElementById('address-default').checked = address.default;
            
            showAddressForm(true);
        })
        .catch(error => {
            console.error('Error fetching address details:', error);
            alert('Failed to load address details. Please try again.');
        });
}

// Save address
function saveAddress(modal) {
    const form = document.getElementById('address-form');
    
    if (form.checkValidity()) {
        const addressId = document.getElementById('address-id').value;
        const userId = document.getElementById('address-user-id').value;
        
        const addressData = {
            addressLine1: document.getElementById('address-line1').value,
            addressLine2: document.getElementById('address-line2').value,
            city: document.getElementById('address-city').value,
            state: document.getElementById('address-state').value,
            postalCode: document.getElementById('address-postal-code').value,
            country: document.getElementById('address-country').value,
            phone: document.getElementById('address-phone').value,
            type: document.getElementById('address-type').value,
            default: document.getElementById('address-default').checked
        };
        
        if (addressId) {
            // Update existing address
            apiService.put(`/users/addresses/${addressId}`, addressData)
                .then(response => {
                    showAddressForm(false);
                    fetchAddresses(userId);
                })
                .catch(error => {
                    console.error('Error updating address:', error);
                    alert('Failed to update address. Please try again.');
                });
        } else {
            // Create new address
            apiService.post('/users/addresses', addressData)
                .then(response => {
                    showAddressForm(false);
                    fetchAddresses(userId);
                })
                .catch(error => {
                    console.error('Error creating address:', error);
                    alert('Failed to create address. Please try again.');
                });
        }
    } else {
        form.reportValidity();
    }
}

// Confirm delete address
function confirmDeleteAddress(addressId) {
    if (confirm('Are you sure you want to delete this address? This action cannot be undone.')) {
        const userId = document.getElementById('address-user-id').value;
        
        apiService.delete(`/users/addresses/${addressId}`)
            .then(response => {
                fetchAddresses(userId);
            })
            .catch(error => {
                console.error('Error deleting address:', error);
                alert('Failed to delete address. Please try again.');
            });
    }
}

// Get role badge class
function getRoleBadgeClass(role) {
    switch (role) {
        case 'ADMIN':
            return 'danger';
        case 'MANAGER':
            return 'warning';
        case 'STAFF':
            return 'info';
        case 'CUSTOMER':
            return 'success';
        default:
            return 'secondary';
    }
}

// Fetch customers from API
function fetchCustomers() {
    apiService.get('/users')
        .then(response => {
            if (response.success) {
                // Filter only customers
                const customers = response.data.filter(user => user.role === 'CUSTOMER');
                displayCustomers(customers);
            } else {
                showErrorMessage('Failed to load customers: ' + response.error.message);
                document.getElementById('customers-table').innerHTML = 
                    '<tr><td colspan="7" class="text-center text-danger">Failed to load customers</td></tr>';
            }
        })
        .catch(error => {
            console.error('Error fetching customers:', error);
            document.getElementById('customers-table').innerHTML = 
                '<tr><td colspan="7" class="text-center text-danger">Failed to load customers</td></tr>';
        });
}

// Display customers in the table
function displayCustomers(customers) {
    const tableBody = document.getElementById('customers-table');
    
    if (!customers || customers.length === 0) {
        tableBody.innerHTML = '<tr><td colspan="7" class="text-center">No customers found</td></tr>';
        return;
    }
    
    tableBody.innerHTML = '';
    
    customers.forEach(customer => {
        const row = document.createElement('tr');
        row.setAttribute('data-customer-id', customer.id);
        
        row.innerHTML = `
            <td>${customer.id}</td>
            <td>${customer.firstName} ${customer.lastName}</td>
            <td>${customer.email}</td>
            <td>${customer.phone || 'N/A'}</td>
            <td>
                <span class="badge ${customer.active ? 'bg-success' : 'bg-danger'}">
                    ${customer.active ? 'Active' : 'Inactive'}
                </span>
                ${customer.emailVerified ? '<span class="badge bg-info ms-1">Verified</span>' : ''}
            </td>
            <td>
                <span class="badge bg-secondary">${customer.addresses ? customer.addresses.length : 0} addresses</span>
            </td>
            <td>
                <div class="btn-group btn-group-sm" role="group">
                    <button type="button" class="btn btn-outline-primary view-customer-btn" data-customer-id="${customer.id}">
                        <i class="bi bi-eye"></i>
                    </button>
                    <button type="button" class="btn btn-outline-secondary edit-customer-btn" data-customer-id="${customer.id}">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button type="button" class="btn btn-outline-info manage-addresses-btn" data-customer-id="${customer.id}">
                        <i class="bi bi-geo-alt"></i>
                    </button>
                    <button type="button" class="btn btn-outline-danger delete-customer-btn" data-customer-id="${customer.id}">
                        <i class="bi bi-trash"></i>
                    </button>
                </div>
            </td>
        `;
        
        tableBody.appendChild(row);
    });
    
    // Add event listeners to action buttons
    document.querySelectorAll('.view-customer-btn').forEach(button => {
        button.addEventListener('click', function() {
            const customerId = this.getAttribute('data-customer-id');
            viewCustomer(customerId);
        });
    });
    
    document.querySelectorAll('.edit-customer-btn').forEach(button => {
        button.addEventListener('click', function() {
            const customerId = this.getAttribute('data-customer-id');
            editCustomer(customerId);
        });
    });
    
    document.querySelectorAll('.manage-addresses-btn').forEach(button => {
        button.addEventListener('click', function() {
            const customerId = this.getAttribute('data-customer-id');
            manageCustomerAddresses(customerId);
        });
    });
    
    document.querySelectorAll('.delete-customer-btn').forEach(button => {
        button.addEventListener('click', function() {
            const customerId = this.getAttribute('data-customer-id');
            confirmDeleteCustomer(customerId);
        });
    });
}

// Filter customers based on search term
function filterCustomers(searchTerm) {
    const rows = document.querySelectorAll('#customers-table tr');
    
    rows.forEach(row => {
        const text = row.textContent.toLowerCase();
        if (text.includes(searchTerm)) {
            row.style.display = '';
        } else {
            row.style.display = 'none';
        }
    });
}

// Show add customer modal
function showAddCustomerModal() {
    // Create modal HTML
    const modalHTML = `
        <div class="modal fade" id="customerModal" tabindex="-1" aria-labelledby="customerModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="customerModalLabel">Add New Customer</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form id="customer-form">
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="customer-first-name" class="form-label">First Name</label>
                                    <input type="text" class="form-control" id="customer-first-name" required>
                                </div>
                                <div class="col-md-6">
                                    <label for="customer-last-name" class="form-label">Last Name</label>
                                    <input type="text" class="form-control" id="customer-last-name" required>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="customer-email" class="form-label">Email</label>
                                    <input type="email" class="form-control" id="customer-email" required>
                                </div>
                                <div class="col-md-6">
                                    <label for="customer-phone" class="form-label">Phone</label>
                                    <input type="text" class="form-control" id="customer-phone">
                                </div>
                            </div>
                            <div class="mb-3">
                                <label for="customer-password" class="form-label">Password</label>
                                <input type="password" class="form-control" id="customer-password" required>
                                <small class="text-muted">Minimum 6 characters</small>
                            </div>
                            <div class="form-check form-switch mb-3">
                                <input class="form-check-input" type="checkbox" id="customer-active" checked>
                                <label class="form-check-label" for="customer-active">Active</label>
                            </div>
                            <div class="form-check form-switch mb-3">
                                <input class="form-check-input" type="checkbox" id="customer-email-verified">
                                <label class="form-check-label" for="customer-email-verified">Email Verified</label>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="button" class="btn btn-primary" id="save-customer-btn">Save Customer</button>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    // Add modal to the DOM
    const modalContainer = document.createElement('div');
    modalContainer.innerHTML = modalHTML;
    document.body.appendChild(modalContainer);
    
    // Initialize the modal
    const modal = new bootstrap.Modal(document.getElementById('customerModal'));
    modal.show();
    
    // Add event listener to save button
    document.getElementById('save-customer-btn').addEventListener('click', saveCustomer);
    
    // Clean up when modal is hidden
    document.getElementById('customerModal').addEventListener('hidden.bs.modal', function() {
        document.body.removeChild(modalContainer);
    });
}

// Save customer (create new or update existing)
function saveCustomer() {
    const customerId = document.getElementById('customer-form').getAttribute('data-customer-id');
    
    const customerData = {
        firstName: document.getElementById('customer-first-name').value,
        lastName: document.getElementById('customer-last-name').value,
        email: document.getElementById('customer-email').value,
        phone: document.getElementById('customer-phone').value,
        password: document.getElementById('customer-password').value,
        role: 'CUSTOMER',
        active: document.getElementById('customer-active').checked,
        emailVerified: document.getElementById('customer-email-verified').checked
    };
    
    // Validate required fields
    if (!customerData.firstName || !customerData.lastName || !customerData.email || !customerData.password) {
        alert('Please fill in all required fields');
        return;
    }
    
    if (customerData.password.length < 6) {
        alert('Password must be at least 6 characters long');
        return;
    }
    
    // Determine if this is a create or update operation
    const isUpdate = !!customerId;
    const endpoint = isUpdate ? `/users/${customerId}` : '/users';
    const method = isUpdate ? 'put' : 'post';
    
    // Remove password from update if it's empty (keep existing password)
    if (isUpdate && !customerData.password) {
        delete customerData.password;
    }
    
    // Save the customer
    apiService[method](endpoint, customerData)
        .then(response => {
            if (response.success) {
                // Close the modal
                bootstrap.Modal.getInstance(document.getElementById('customerModal')).hide();
                
                // Refresh the customers list
                fetchCustomers();
                
                // Show success message
                alert(`Customer ${isUpdate ? 'updated' : 'created'} successfully`);
            } else {
                alert('Failed to save customer: ' + response.error.message);
            }
        })
        .catch(error => {
            console.error('Error saving customer:', error);
            alert('Failed to save customer. Please try again.');
        });
}

// View customer details
function viewCustomer(customerId) {
    apiService.get(`/users/${customerId}`)
        .then(response => {
            if (response.success) {
                const customer = response.data;
                
                // Create modal HTML
                const modalHTML = `
                    <div class="modal fade" id="viewCustomerModal" tabindex="-1" aria-hidden="true">
                        <div class="modal-dialog modal-lg">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title">Customer Details</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                </div>
                                <div class="modal-body">
                                    <div class="row">
                                        <div class="col-md-6">
                                            <h4>${customer.firstName} ${customer.lastName}</h4>
                                            <p class="text-muted">${customer.email}</p>
                                            
                                            <h5>Contact Information</h5>
                                            <p>Phone: ${customer.phone || 'N/A'}</p>
                                            <p>Email: ${customer.email}</p>
                                            
                                            <h5>Status</h5>
                                            <p>
                                                <span class="badge ${customer.active ? 'bg-success' : 'bg-danger'}">
                                                    ${customer.active ? 'Active' : 'Inactive'}
                                                </span>
                                                ${customer.emailVerified ? '<span class="badge bg-info ms-2">Email Verified</span>' : ''}
                                            </p>
                                        </div>
                                        <div class="col-md-6">
                                            <h5>Addresses (${customer.addresses ? customer.addresses.length : 0})</h5>
                                            ${customer.addresses && customer.addresses.length > 0 ? 
                                                customer.addresses.map(address => `
                                                    <div class="card mb-2">
                                                        <div class="card-body">
                                                            <p class="mb-1">${address.streetAddress}</p>
                                                            <p class="mb-1">${address.city}, ${address.state} ${address.postalCode}</p>
                                                            <p class="mb-0 text-muted">${address.country}</p>
                                                        </div>
                                                    </div>
                                                `).join('') : 
                                                '<p class="text-muted">No addresses on file</p>'
                                            }
                                            
                                            <h5>Account Information</h5>
                                            <p>Member since: ${formatDate(customer.createdAt)}</p>
                                            <p>Last updated: ${formatDate(customer.updatedAt)}</p>
                                        </div>
                                    </div>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                                </div>
                            </div>
                        </div>
                    </div>
                `;
                
                // Add modal to the DOM
                const modalContainer = document.createElement('div');
                modalContainer.innerHTML = modalHTML;
                document.body.appendChild(modalContainer);
                
                // Initialize the modal
                const modal = new bootstrap.Modal(document.getElementById('viewCustomerModal'));
                modal.show();
                
                // Clean up when modal is hidden
                document.getElementById('viewCustomerModal').addEventListener('hidden.bs.modal', function() {
                    document.body.removeChild(modalContainer);
                });
            } else {
                alert('Failed to load customer: ' + response.error.message);
            }
        })
        .catch(error => {
            console.error('Error loading customer:', error);
            alert('Failed to load customer. Please try again.');
        });
}

// Edit customer
function editCustomer(customerId) {
    apiService.get(`/users/${customerId}`)
        .then(response => {
            if (response.success) {
                const customer = response.data;
                
                // Show the customer modal
                showAddCustomerModal();
                
                // Wait for modal to be created, then populate it
                setTimeout(() => {
                    document.getElementById('customerModalLabel').textContent = 'Edit Customer';
                    document.getElementById('customer-form').setAttribute('data-customer-id', customer.id);
                    document.getElementById('customer-first-name').value = customer.firstName;
                    document.getElementById('customer-last-name').value = customer.lastName;
                    document.getElementById('customer-email').value = customer.email;
                    document.getElementById('customer-phone').value = customer.phone || '';
                    document.getElementById('customer-password').value = ''; // Don't show password
                    document.getElementById('customer-password').required = false; // Not required for edit
                    document.getElementById('customer-active').checked = customer.active;
                    document.getElementById('customer-email-verified').checked = customer.emailVerified;
                }, 100);
            } else {
                alert('Failed to load customer: ' + response.error.message);
            }
        })
        .catch(error => {
            console.error('Error loading customer:', error);
            alert('Failed to load customer. Please try again.');
        });
}

// Confirm delete customer
function confirmDeleteCustomer(customerId) {
    if (confirm('Are you sure you want to delete this customer? This action cannot be undone.')) {
        deleteCustomer(customerId);
    }
}

// Delete customer
function deleteCustomer(customerId) {
    apiService.delete(`/users/${customerId}`)
        .then(response => {
            if (response.success) {
                // Refresh the customers list
                fetchCustomers();
                
                // Show success message
                alert('Customer deleted successfully');
            } else {
                alert('Failed to delete customer: ' + response.error.message);
            }
        })
        .catch(error => {
            console.error('Error deleting customer:', error);
            alert('Failed to delete customer. Please try again.');
        });
}

// Manage customer addresses
function manageCustomerAddresses(customerId) {
    // This will be implemented to show a modal for managing customer addresses
    alert('Address management feature will be implemented in the next update');
}

// Filter users based on search term
function filterUsers(searchTerm) {
    const rows = document.querySelectorAll('#users-table tr');
    
    rows.forEach(row => {
        const text = row.textContent.toLowerCase();
        if (text.includes(searchTerm)) {
            row.style.display = '';
        } else {
            row.style.display = 'none';
        }
    });
}

// Show add user modal
function showAddUserModal() {
    // Create modal HTML
    const modalHTML = `
        <div class="modal fade" id="userModal" tabindex="-1" aria-labelledby="userModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="userModalLabel">Add New User</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form id="user-form">
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="user-first-name" class="form-label">First Name</label>
                                    <input type="text" class="form-control" id="user-first-name" required>
                                </div>
                                <div class="col-md-6">
                                    <label for="user-last-name" class="form-label">Last Name</label>
                                    <input type="text" class="form-control" id="user-last-name" required>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="user-email" class="form-label">Email</label>
                                    <input type="email" class="form-control" id="user-email" required>
                                </div>
                                <div class="col-md-6">
                                    <label for="user-phone" class="form-label">Phone</label>
                                    <input type="text" class="form-control" id="user-phone">
                                </div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="user-password" class="form-label">Password</label>
                                    <input type="password" class="form-control" id="user-password" required>
                                    <small class="text-muted">Minimum 6 characters</small>
                                </div>
                                <div class="col-md-6">
                                    <label for="user-role" class="form-label">Role</label>
                                    <select class="form-select" id="user-role" required>
                                        <option value="CUSTOMER">Customer</option>
                                        <option value="STAFF">Staff</option>
                                        <option value="MANAGER">Manager</option>
                                        <option value="ADMIN">Admin</option>
                                    </select>
                                </div>
                            </div>
                            <div class="form-check form-switch mb-3">
                                <input class="form-check-input" type="checkbox" id="user-active" checked>
                                <label class="form-check-label" for="user-active">Active</label>
                            </div>
                            <div class="form-check form-switch mb-3">
                                <input class="form-check-input" type="checkbox" id="user-email-verified">
                                <label class="form-check-label" for="user-email-verified">Email Verified</label>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="button" class="btn btn-primary" id="save-user-btn">Save User</button>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    // Add modal to the DOM
    const modalContainer = document.createElement('div');
    modalContainer.innerHTML = modalHTML;
    document.body.appendChild(modalContainer);
    
    // Initialize the modal
    const modal = new bootstrap.Modal(document.getElementById('userModal'));
    modal.show();
    
    // Add event listener to save button
    document.getElementById('save-user-btn').addEventListener('click', saveUser);
    
    // Clean up when modal is hidden
    document.getElementById('userModal').addEventListener('hidden.bs.modal', function() {
        document.body.removeChild(modalContainer);
    });
}

// Save user (create new or update existing)
function saveUser() {
    const userId = document.getElementById('user-form').getAttribute('data-user-id');
    
    const userData = {
        firstName: document.getElementById('user-first-name').value,
        lastName: document.getElementById('user-last-name').value,
        email: document.getElementById('user-email').value,
        phone: document.getElementById('user-phone').value,
        password: document.getElementById('user-password').value,
        role: document.getElementById('user-role').value,
        active: document.getElementById('user-active').checked,
        emailVerified: document.getElementById('user-email-verified').checked
    };
    
    // Validate required fields
    if (!userData.firstName || !userData.lastName || !userData.email || !userData.password) {
        alert('Please fill in all required fields');
        return;
    }
    
    if (userData.password.length < 6) {
        alert('Password must be at least 6 characters long');
        return;
    }
    
    // Determine if this is a create or update operation
    const isUpdate = !!userId;
    const endpoint = isUpdate ? `/users/${userId}` : '/users';
    const method = isUpdate ? 'put' : 'post';
    
    // Remove password from update if it's empty (keep existing password)
    if (isUpdate && !userData.password) {
        delete userData.password;
    }
    
    // Save the user
    apiService[method](endpoint, userData)
        .then(response => {
            if (response.success) {
                // Close the modal
                bootstrap.Modal.getInstance(document.getElementById('userModal')).hide();
                
                // Refresh the users list
                fetchUsers();
                
                // Show success message
                alert(`User ${isUpdate ? 'updated' : 'created'} successfully`);
            } else {
                alert('Failed to save user: ' + response.error.message);
            }
        })
        .catch(error => {
            console.error('Error saving user:', error);
            alert('Failed to save user. Please try again.');
        });
}

// View user details
function viewUser(userId) {
    apiService.get(`/users/${userId}`)
        .then(response => {
            if (response.success) {
                const user = response.data;
                
                // Create modal HTML
                const modalHTML = `
                    <div class="modal fade" id="viewUserModal" tabindex="-1" aria-hidden="true">
                        <div class="modal-dialog modal-lg">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title">User Details</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                </div>
                                <div class="modal-body">
                                    <div class="row">
                                        <div class="col-md-6">
                                            <h4>${user.firstName} ${user.lastName}</h4>
                                            <p class="text-muted">${user.email}</p>
                                            
                                            <h5>Contact Information</h5>
                                            <p>Phone: ${user.phone || 'N/A'}</p>
                                            <p>Email: ${user.email}</p>
                                            
                                            <h5>Role & Status</h5>
                                            <p>
                                                <span class="badge ${getRoleBadgeClass(user.role)}">
                                                    ${user.role}
                                                </span>
                                            </p>
                                            <p>
                                                <span class="badge ${user.active ? 'bg-success' : 'bg-danger'}">
                                                    ${user.active ? 'Active' : 'Inactive'}
                                                </span>
                                                ${user.emailVerified ? '<span class="badge bg-info ms-2">Email Verified</span>' : ''}
                                            </p>
                                        </div>
                                        <div class="col-md-6">
                                            <h5>Addresses (${user.addresses ? user.addresses.length : 0})</h5>
                                            ${user.addresses && user.addresses.length > 0 ? 
                                                user.addresses.map(address => `
                                                    <div class="card mb-2">
                                                        <div class="card-body">
                                                            <p class="mb-1">${address.streetAddress}</p>
                                                            <p class="mb-1">${address.city}, ${address.state} ${address.postalCode}</p>
                                                            <p class="mb-0 text-muted">${address.country}</p>
                                                        </div>
                                                    </div>
                                                `).join('') : 
                                                '<p class="text-muted">No addresses on file</p>'
                                            }
                                            
                                            <h5>Account Information</h5>
                                            <p>Member since: ${formatDate(user.createdAt)}</p>
                                            <p>Last updated: ${formatDate(user.updatedAt)}</p>
                                        </div>
                                    </div>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                                </div>
                            </div>
                        </div>
                    </div>
                `;
                
                // Add modal to the DOM
                const modalContainer = document.createElement('div');
                modalContainer.innerHTML = modalHTML;
                document.body.appendChild(modalContainer);
                
                // Initialize the modal
                const modal = new bootstrap.Modal(document.getElementById('viewUserModal'));
                modal.show();
                
                // Clean up when modal is hidden
                document.getElementById('viewUserModal').addEventListener('hidden.bs.modal', function() {
                    document.body.removeChild(modalContainer);
                });
            } else {
                alert('Failed to load user: ' + response.error.message);
            }
        })
        .catch(error => {
            console.error('Error loading user:', error);
            alert('Failed to load user. Please try again.');
        });
}

// Edit user
function editUser(userId) {
    apiService.get(`/users/${userId}`)
        .then(response => {
            if (response.success) {
                const user = response.data;
                
                // Show the user modal
                showAddUserModal();
                
                // Wait for modal to be created, then populate it
                setTimeout(() => {
                    document.getElementById('userModalLabel').textContent = 'Edit User';
                    document.getElementById('user-form').setAttribute('data-user-id', user.id);
                    document.getElementById('user-first-name').value = user.firstName;
                    document.getElementById('user-last-name').value = user.lastName;
                    document.getElementById('user-email').value = user.email;
                    document.getElementById('user-phone').value = user.phone || '';
                    document.getElementById('user-password').value = ''; // Don't show password
                    document.getElementById('user-password').required = false; // Not required for edit
                    document.getElementById('user-role').value = user.role;
                    document.getElementById('user-active').checked = user.active;
                    document.getElementById('user-email-verified').checked = user.emailVerified;
                }, 100);
            } else {
                alert('Failed to load user: ' + response.error.message);
            }
        })
        .catch(error => {
            console.error('Error loading user:', error);
            alert('Failed to load user. Please try again.');
        });
}

// Confirm delete user
function confirmDeleteUser(userId) {
    if (confirm('Are you sure you want to delete this user? This action cannot be undone.')) {
        deleteUser(userId);
    }
}

// Delete user
function deleteUser(userId) {
    apiService.delete(`/users/${userId}`)
        .then(response => {
            if (response.success) {
                // Refresh the users list
                fetchUsers();
                
                // Show success message
                alert('User deleted successfully');
            } else {
                alert('Failed to delete user: ' + response.error.message);
            }
        })
        .catch(error => {
            console.error('Error deleting user:', error);
            alert('Failed to delete user. Please try again.');
        });
}

// Manage user addresses
function manageUserAddresses(userId) {
    // This will be implemented to show a modal for managing user addresses
    alert('Address management feature will be implemented in the next update');
}

function loadSettingsData() {
    // Implementation will be added later
    console.log('Loading settings data...');
}

// Utility functions
function formatCurrency(amount) {
    return '$' + parseFloat(amount).toFixed(2);
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString();
}

function getStatusBadgeClass(status) {
    switch (status.toLowerCase()) {
        case 'pending':
            return 'pending';
        case 'processing':
            return 'processing';
        case 'completed':
            return 'completed';
        case 'cancelled':
            return 'cancelled';
        default:
            return 'secondary';
    }
}

function showErrorMessage(message) {
    // Implementation for showing error messages
    console.error(message);
    // Could use a toast or alert component
}

function showRestockModal(productId) {
    // Implementation for restock modal
    console.log('Show restock modal for product ID:', productId);
    // Would show a modal for entering restock quantity
}

// Initialize the application when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    // Initialize page navigation
    initPageNavigation();
});