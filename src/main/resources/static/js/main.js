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
    // Implementation will be added later
    console.log('Loading customers data...');
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
    // Implementation will be added later
    console.log('Loading users data...');
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