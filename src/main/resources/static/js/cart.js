/**
 * Cart Service - Handles all cart-related operations
 */
class CartService {
    constructor() {
        this.cart = null;
        this.cartItemCount = 0;
        this.init();
    }

    /**
     * Initialize the cart service
     */
    init() {
        this.setupEventListeners();
        // Don't automatically load cart during initialization
        // Cart will be loaded when needed or after authentication
    }

    /**
     * Initialize cart after authentication
     */
    async initAfterAuth() {
        try {
            await this.loadCart();
        } catch (error) {
            console.error('Error initializing cart after auth:', error);
        }
    }

    /**
     * Safe API call wrapper
     */
    async safeApiCall(method, endpoint, data = null) {
        if (typeof apiService === 'undefined') {
            throw new Error('API service not available');
        }
        
        switch (method.toLowerCase()) {
            case 'get':
                return await apiService.get(endpoint);
            case 'post':
                return await apiService.post(endpoint, data);
            case 'put':
                return await apiService.put(endpoint, data);
            case 'delete':
                return await apiService.delete(endpoint);
            default:
                throw new Error(`Unsupported API method: ${method}`);
        }
    }

    /**
     * Safe modal hide wrapper
     */
    safeHideModal(modalId) {
        const modalElement = document.getElementById(modalId);
        if (modalElement) {
            const modal = bootstrap.Modal.getInstance(modalElement);
            if (modal) {
                modal.hide();
            }
        }
    }

    /**
     * Set up event listeners for cart functionality
     */
    setupEventListeners() {
        // Cart preview modal events
        document.getElementById('proceed-to-checkout-btn')?.addEventListener('click', () => {
            this.proceedToCheckout();
        });

        document.getElementById('view-cart-btn')?.addEventListener('click', () => {
            this.viewCart();
        });

        // Add to cart modal events
        document.getElementById('decrease-quantity')?.addEventListener('click', () => {
            this.decreaseQuantity();
        });

        document.getElementById('increase-quantity')?.addEventListener('click', () => {
            this.increaseQuantity();
        });

        document.getElementById('add-to-cart-btn')?.addEventListener('click', () => {
            this.addToCart();
        });

        // Quantity input change
        document.getElementById('quantity-input')?.addEventListener('change', (e) => {
            this.validateQuantity(e.target);
        });

        // Header cart button
        document.getElementById('cart-preview-btn')?.addEventListener('click', () => {
            this.showCartPreview();
        });
    }

    /**
     * Load cart from server
     */
    async loadCart() {
        try {
            const response = await this.safeApiCall('get', '/cart');
            if (response && response.success) {
                this.cart = response.data;
                this.updateCartBadge();
                this.updateCartPreview();
                
                // Update cart page content if it's currently displayed
                if (document.getElementById('cart-content')) {
                    displayCartContent();
                }
            } else {
                // Initialize empty cart if no cart found
                this.cart = { items: [] };
                this.updateCartBadge();
            }
        } catch (error) {
            console.error('Error loading cart:', error);
            // Initialize empty cart on error
            this.cart = { items: [] };
            this.updateCartBadge();
        }
    }

    /**
     * Update cart badge with item count
     */
    updateCartBadge() {
        const badge = document.getElementById('cart-badge');
        const headerBadge = document.getElementById('header-cart-badge');
        
        // Only count active items (not saved for later)
        this.cartItemCount = this.cart?.items?.filter(item => !item.savedForLater)?.length || 0;
        
        // Update sidebar cart badge
        if (badge) {
            if (this.cartItemCount > 0) {
                badge.textContent = this.cartItemCount;
                badge.style.display = 'inline';
            } else {
                badge.style.display = 'none';
            }
        }
        
        // Update header cart badge
        if (headerBadge) {
            if (this.cartItemCount > 0) {
                headerBadge.textContent = this.cartItemCount;
                headerBadge.style.display = 'inline';
            } else {
                headerBadge.style.display = 'none';
            }
        }
    }

    /**
     * Add item to cart
     */
    async addItemToCart(productId, quantity) {
        try {
            const response = await this.safeApiCall('post', '/cart/items', {
                productId: productId,
                quantity: quantity
            });

            if (response.success) {
                this.cart = response.data;
                this.updateCartBadge();
                this.updateCartPreview();
                
                // Update cart page content if it's currently displayed
                if (document.getElementById('cart-content')) {
                    displayCartContent();
                }
                
                this.showSuccessMessage('Item added to cart successfully!');
                return true;
            } else {
                this.showErrorMessage('Failed to add item to cart: ' + response.error.message);
                return false;
            }
        } catch (error) {
            console.error('Error adding item to cart:', error);
            this.showErrorMessage('Failed to add item to cart');
            return false;
        }
    }

    /**
     * Update cart item quantity
     */
    async updateCartItemQuantity(itemId, quantity) {
        try {
            const response = await this.safeApiCall('put', `/cart/items/${itemId}`, {
                quantity: quantity
            });

            if (response.success) {
                this.cart = response.data;
                this.updateCartBadge();
                this.updateCartPreview();
                
                // Update cart page content if it's currently displayed
                if (document.getElementById('cart-content')) {
                    displayCartContent();
                }
                
                this.showSuccessMessage('Cart updated successfully!');
                return true;
            } else {
                this.showErrorMessage('Failed to update cart: ' + response.error.message);
                return false;
            }
        } catch (error) {
            console.error('Error updating cart item:', error);
            this.showErrorMessage('Failed to update cart');
            return false;
        }
    }

    /**
     * Remove item from cart
     */
    async removeCartItem(itemId) {
        try {
            const response = await this.safeApiCall('delete', `/cart/items/${itemId}`);

            if (response.success) {
                this.cart = response.data;
                this.updateCartBadge();
                this.updateCartPreview();
                
                // Update cart page content if it's currently displayed
                if (document.getElementById('cart-content')) {
                    displayCartContent();
                }
                
                this.showSuccessMessage('Item removed from cart successfully!');
                return true;
            } else {
                this.showErrorMessage('Failed to remove item: ' + response.error.message);
                return false;
            }
        } catch (error) {
            console.error('Error removing cart item:', error);
            this.showErrorMessage('Failed to remove item from cart');
            return false;
        }
    }

    /**
     * Clear cart
     */
    async clearCart() {
        try {
            const response = await this.safeApiCall('delete', '/cart/clear');

            if (response.success) {
                this.cart = response.data;
                this.updateCartBadge();
                this.updateCartPreview();
                
                // Update cart page content if it's currently displayed
                if (document.getElementById('cart-content')) {
                    displayCartContent();
                }
                
                this.showSuccessMessage('Cart cleared successfully!');
                return true;
            } else {
                this.showErrorMessage('Failed to clear cart: ' + response.error.message);
                return false;
            }
        } catch (error) {
            console.error('Error clearing cart:', error);
            this.showErrorMessage('Failed to clear cart');
            return false;
        }
    }

    /**
     * Save item for later
     */
    async saveForLater(itemId) {
        try {
            const response = await this.safeApiCall('put', `/cart/items/${itemId}/save-for-later`);

            if (response.success) {
                this.cart = response.data;
                this.updateCartBadge();
                this.updateCartPreview();
                
                // Update cart page content if it's currently displayed
                if (document.getElementById('cart-content')) {
                    displayCartContent();
                }
                
                this.showSuccessMessage('Item saved for later!');
                return true;
            } else {
                this.showErrorMessage('Failed to save item: ' + response.error.message);
                return false;
            }
        } catch (error) {
            console.error('Error saving item for later:', error);
            this.showErrorMessage('Failed to save item for later');
            return false;
        }
    }

    /**
     * Show add to cart modal
     */
    showAddToCartModal(product) {
        const modal = new bootstrap.Modal(document.getElementById('addToCartModal'));
        
        // Populate modal with product information
        document.getElementById('product-image').src = product.imageUrl || 'https://via.placeholder.com/150x150?text=Product';
        document.getElementById('product-name').textContent = product.name;
        document.getElementById('product-category').textContent = product.category?.name || 'No Category';
        document.getElementById('product-price').textContent = formatCurrency(product.price);
        
        // Reset quantity
        document.getElementById('quantity-input').value = '1';
        
        // Store product data for later use
        document.getElementById('add-to-cart-btn').setAttribute('data-product-id', product.id);
        
        modal.show();
    }

    /**
     * Handle add to cart button click
     */
    async addToCart() {
        const productId = document.getElementById('add-to-cart-btn').getAttribute('data-product-id');
        const quantity = parseInt(document.getElementById('quantity-input').value);

        if (!productId || quantity < 1) {
            this.showErrorMessage('Invalid product or quantity');
            return;
        }

        const success = await this.addItemToCart(parseInt(productId), quantity);
        if (success) {
            // Close modal safely
            this.safeHideModal('addToCartModal');
        }
    }

    /**
     * Decrease quantity in add to cart modal
     */
    decreaseQuantity() {
        const input = document.getElementById('quantity-input');
        const currentValue = parseInt(input.value);
        if (currentValue > 1) {
            input.value = currentValue - 1;
        }
    }

    /**
     * Increase quantity in add to cart modal
     */
    increaseQuantity() {
        const input = document.getElementById('quantity-input');
        const currentValue = parseInt(input.value);
        if (currentValue < 99) {
            input.value = currentValue + 1;
        }
    }

    /**
     * Validate quantity input
     */
    validateQuantity(input) {
        let value = parseInt(input.value);
        if (isNaN(value) || value < 1) {
            value = 1;
        } else if (value > 99) {
            value = 99;
        }
        input.value = value;
    }

    /**
     * Update cart preview modal content
     */
    updateCartPreview() {
        const content = document.getElementById('cart-preview-content');
        if (!content) return;

        if (!this.cart || !this.cart.items || this.cart.items.length === 0) {
            content.innerHTML = `
                <div class="text-center py-4">
                    <i class="bi bi-cart3 fs-1 text-muted"></i>
                    <h5 class="mt-3">Your cart is empty</h5>
                    <p class="text-muted">Add some products to get started!</p>
                </div>
            `;
            return;
        }

        let itemsHtml = '';
        this.cart.items.forEach(item => {
            itemsHtml += `
                <div class="card mb-3">
                    <div class="card-body">
                        <div class="row align-items-center">
                            <div class="col-md-2">
                                <img src="${item.product.imageUrl || 'https://via.placeholder.com/80x80?text=Product'}" 
                                     alt="${item.product.name}" class="img-fluid rounded">
                            </div>
                            <div class="col-md-4">
                                <h6 class="mb-1">${item.product.name}</h6>
                                <small class="text-muted">${item.product.category?.name || 'No Category'}</small>
                            </div>
                            <div class="col-md-2">
                                <div class="input-group input-group-sm">
                                    <button class="btn btn-outline-secondary" type="button" 
                                            onclick="cartService.updateItemQuantity(${item.id}, ${item.quantity - 1})">-</button>
                                    <input type="number" class="form-control text-center" value="${item.quantity}" 
                                           min="1" max="99" onchange="cartService.updateItemQuantity(${item.id}, this.value)">
                                    <button class="btn btn-outline-secondary" type="button" 
                                            onclick="cartService.updateItemQuantity(${item.id}, ${item.quantity + 1})">+</button>
                                </div>
                            </div>
                            <div class="col-md-2 text-center">
                                <span class="fw-bold">${formatCurrency(item.totalPrice)}</span>
                            </div>
                            <div class="col-md-2 text-end">
                                <button class="btn btn-sm btn-outline-danger" 
                                        onclick="cartService.removeCartItem(${item.id})">
                                    <i class="bi bi-trash"></i>
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            `;
        });

        content.innerHTML = `
            <div class="mb-3">
                ${itemsHtml}
            </div>
            <div class="card">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <h5 class="mb-0">Total</h5>
                        <h5 class="mb-0 text-primary">${formatCurrency(this.cart.totalAmount)}</h5>
                    </div>
                </div>
            </div>
        `;
    }

    /**
     * Update item quantity (called from cart preview)
     */
    async updateItemQuantity(itemId, quantity) {
        await this.updateCartItemQuantity(itemId, parseInt(quantity));
    }

    /**
     * Show cart preview modal
     */
    showCartPreview() {
        this.updateCartPreview();
        const modal = new bootstrap.Modal(document.getElementById('cartPreviewModal'));
        modal.show();
    }

    /**
     * Proceed to checkout
     */
    proceedToCheckout() {
        // Close cart preview modal safely
        this.safeHideModal('cartPreviewModal');
        
        // Create checkout modal HTML
        const checkoutModalHtml = `
            <div class="modal fade" id="checkoutModal" tabindex="-1" aria-labelledby="checkoutModalLabel" aria-hidden="true">
                <div class="modal-dialog modal-lg">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="checkoutModalLabel">Checkout</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <form id="checkout-form">
                                <div class="row mb-3">
                                    <div class="col-md-6">
                                        <h6>Delivery Method</h6>
                                        <div class="form-check">
                                            <input class="form-check-input" type="radio" name="deliveryMethod" id="delivery" value="DELIVERY" checked>
                                            <label class="form-check-label" for="delivery">
                                                Delivery
                                            </label>
                                        </div>
                                        <div class="form-check">
                                            <input class="form-check-input" type="radio" name="deliveryMethod" id="pickup" value="PICKUP">
                                            <label class="form-check-label" for="pickup">
                                                Pickup
                                            </label>
                                        </div>
                                    </div>
                                </div>
                                
                                <div id="delivery-address-section">
                                    <h6>Delivery Address</h6>
                                    <div class="row mb-3">
                                        <div class="col-md-6">
                                            <label for="street" class="form-label">Street</label>
                                            <input type="text" class="form-control" id="street" required>
                                        </div>
                                        <div class="col-md-6">
                                            <label for="city" class="form-label">City</label>
                                            <input type="text" class="form-control" id="city" required>
                                        </div>
                                    </div>
                                    <div class="row mb-3">
                                        <div class="col-md-4">
                                            <label for="state" class="form-label">State</label>
                                            <input type="text" class="form-control" id="state" required>
                                        </div>
                                        <div class="col-md-4">
                                            <label for="zipCode" class="form-label">Zip Code</label>
                                            <input type="text" class="form-control" id="zipCode" required>
                                        </div>
                                        <div class="col-md-4">
                                            <label for="country" class="form-label">Country</label>
                                            <input type="text" class="form-control" id="country" required>
                                        </div>
                                    </div>
                                </div>
                                
                                <h6>Payment Information</h6>
                                <div class="row mb-3">
                                    <div class="col-md-6">
                                        <label for="cardNumber" class="form-label">Card Number</label>
                                        <input type="text" class="form-control" id="cardNumber" required>
                                    </div>
                                    <div class="col-md-3">
                                        <label for="expiryDate" class="form-label">Expiry Date</label>
                                        <input type="text" class="form-control" id="expiryDate" placeholder="MM/YY" required>
                                    </div>
                                    <div class="col-md-3">
                                        <label for="cvv" class="form-label">CVV</label>
                                        <input type="text" class="form-control" id="cvv" required>
                                    </div>
                                </div>
                                
                                <h6>Order Summary</h6>
                                <div class="table-responsive">
                                    <table class="table table-sm">
                                        <thead>
                                            <tr>
                                                <th>Item</th>
                                                <th>Quantity</th>
                                                <th class="text-end">Price</th>
                                            </tr>
                                        </thead>
                                        <tbody id="checkout-items">
                                            <!-- Will be populated by JavaScript -->
                                        </tbody>
                                        <tfoot>
                                            <tr>
                                                <th colspan="2" class="text-end">Subtotal:</th>
                                                <td class="text-end" id="checkout-subtotal">$0.00</td>
                                            </tr>
                                            <tr>
                                                <th colspan="2" class="text-end">Tax:</th>
                                                <td class="text-end" id="checkout-tax">$0.00</td>
                                            </tr>
                                            <tr>
                                                <th colspan="2" class="text-end">Shipping:</th>
                                                <td class="text-end" id="checkout-shipping">$0.00</td>
                                            </tr>
                                            <tr>
                                                <th colspan="2" class="text-end">Total:</th>
                                                <td class="text-end fw-bold" id="checkout-total">$0.00</td>
                                            </tr>
                                        </tfoot>
                                    </table>
                                </div>
                            </form>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                            <button type="button" class="btn btn-primary" id="place-order-btn">Place Order</button>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        // Add modal to the DOM if it doesn't exist
        if (!document.getElementById('checkoutModal')) {
            document.body.insertAdjacentHTML('beforeend', checkoutModalHtml);
            
            // Add event listeners
            document.querySelectorAll('input[name="deliveryMethod"]').forEach(radio => {
                radio.addEventListener('change', (e) => {
                    const deliveryAddressSection = document.getElementById('delivery-address-section');
                    if (e.target.value === 'DELIVERY') {
                        deliveryAddressSection.style.display = 'block';
                        document.getElementById('checkout-shipping').textContent = '$5.00';
                    } else {
                        deliveryAddressSection.style.display = 'none';
                        document.getElementById('checkout-shipping').textContent = '$0.00';
                    }
                    this.updateCheckoutTotal();
                });
            });
            
            document.getElementById('place-order-btn').addEventListener('click', () => {
                this.placeOrder();
            });
        }
        
        // Populate checkout items
        this.populateCheckoutItems();
        
        // Show the modal
        const checkoutModal = new bootstrap.Modal(document.getElementById('checkoutModal'));
        checkoutModal.show();
    }
    
    /**
     * Populate checkout items
     */
    populateCheckoutItems() {
        const checkoutItems = document.getElementById('checkout-items');
        if (!checkoutItems) return;
        
        let itemsHtml = '';
        let subtotal = 0;
        
        this.cart.items.forEach(item => {
            const itemTotal = item.totalPrice || (item.unitPrice * item.quantity);
            subtotal += itemTotal;
            
            itemsHtml += `
                <tr>
                    <td>${item.product.name}</td>
                    <td>${item.quantity}</td>
                    <td class="text-end">${formatCurrency(itemTotal)}</td>
                </tr>
            `;
        });
        
        checkoutItems.innerHTML = itemsHtml;
        
        // Calculate and display totals
        const tax = subtotal * 0.08; // 8% tax
        const shipping = document.querySelector('input[name="deliveryMethod"]:checked').value === 'DELIVERY' ? 5 : 0;
        const total = subtotal + tax + shipping;
        
        document.getElementById('checkout-subtotal').textContent = formatCurrency(subtotal);
        document.getElementById('checkout-tax').textContent = formatCurrency(tax);
        document.getElementById('checkout-shipping').textContent = formatCurrency(shipping);
        document.getElementById('checkout-total').textContent = formatCurrency(total);
    }
    
    /**
     * Update checkout total
     */
    updateCheckoutTotal() {
        const subtotal = parseFloat(document.getElementById('checkout-subtotal').textContent.replace('$', ''));
        const tax = parseFloat(document.getElementById('checkout-tax').textContent.replace('$', ''));
        const shipping = document.querySelector('input[name="deliveryMethod"]:checked').value === 'DELIVERY' ? 5 : 0;
        const total = subtotal + tax + shipping;
        
        document.getElementById('checkout-shipping').textContent = formatCurrency(shipping);
        document.getElementById('checkout-total').textContent = formatCurrency(total);
    }
    
    /**
     * Place order
     */
    async placeOrder() {
        // Get form data
        const deliveryMethod = document.querySelector('input[name="deliveryMethod"]:checked').value;
        
        // Get current user
        const currentUser = window.auth?.getUser();
        if (!currentUser || !currentUser.id) {
            this.showErrorMessage('User not authenticated. Please login again.');
            return;
        }

        // Calculate total amount
        const subtotal = this.cart.items.reduce((sum, item) => sum + (item.totalPrice || 0), 0);
        const tax = subtotal * 0.08; // 8% tax
        const shipping = deliveryMethod === 'DELIVERY' ? 5 : 0;
        const totalAmount = subtotal + tax + shipping;

        // Create order object
        const order = {
            customerId: currentUser.id,
            items: this.cart.items.map(item => ({
                productId: item.product.id,
                quantity: item.quantity,
                unitPrice: item.unitPrice,
                totalPrice: item.totalPrice
            })),
            deliveryMethod: deliveryMethod,
            status: 'PENDING',
            orderDate: new Date().toISOString(),
            totalAmount: totalAmount,
            taxAmount: tax,
            shippingAmount: shipping
        };
        
        // Add shipping address if delivery method is DELIVERY
        if (deliveryMethod === 'DELIVERY') {
            order.shippingAddress = {
                addressLine1: document.getElementById('street').value,
                city: document.getElementById('city').value,
                state: document.getElementById('state').value,
                postalCode: document.getElementById('zipCode').value,
                country: document.getElementById('country').value,
                type: 'SHIPPING'
            };
        }
        
        try {
            const response = await this.safeApiCall('post', '/orders', order);
            
            if (response.success) {
                // Close checkout modal safely
                this.safeHideModal('checkoutModal');
                
                // Clear cart
                await this.clearCart();
                
                // Show success message
                this.showSuccessMessage('Order placed successfully! Your order number is: ' + response.data.orderNumber);
                
                // Navigate to orders page
                loadPageData('orders');
            } else {
                this.showErrorMessage('Failed to place order: ' + response.error.message);
            }
        } catch (error) {
            console.error('Error placing order:', error);
            this.showErrorMessage('Failed to place order. Please try again later.');
        }
    }

    /**
     * View cart page
     */
    viewCart() {
        // Close cart preview modal safely
        this.safeHideModal('cartPreviewModal');
        
        // Navigate to cart page
        loadPageData('cart');
        
        // Update active navigation
        document.querySelectorAll('.nav-link').forEach(link => {
            link.classList.remove('active');
        });
        document.querySelector('[data-page="cart"]').classList.add('active');
        
        // Update page title
        document.getElementById('page-title').textContent = 'Cart';
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
     * Show info message
     */
    showInfoMessage(message) {
        if (typeof notificationService !== 'undefined') {
            notificationService.showInfo(message);
        } else {
            console.log('Info:', message);
        }
    }
}

// Initialize cart service when DOM is ready
let cartService;
document.addEventListener('DOMContentLoaded', function() {
    cartService = new CartService();
}); 