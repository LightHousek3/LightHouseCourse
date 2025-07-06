/**
 * LightHouse Main JavaScript File
 * This file provides UI enhancements and animations
 */

// DOM Content Loaded - Initialize all event listeners when page is ready
document.addEventListener('DOMContentLoaded', () => {
    // initializeUI();
    setupFormValidation();
    setupAnimations();
    setupInteractions();
    initializeSlideshow();
});

/**
 * Initialize UI components
 */
function initializeUI() {
    // Navbar dropdown functionality
    const dropdownToggleList = document.querySelectorAll('.dropdown-toggle');
    dropdownToggleList.forEach(dropdownToggle => {
        dropdownToggle.addEventListener('click', (e) => {
            e.preventDefault();
            const parent = dropdownToggle.parentElement;
            const dropdownMenu = parent.querySelector('.dropdown-menu');

            // Close other open dropdowns
            document.querySelectorAll('.dropdown-menu.show').forEach(menu => {
                if (menu !== dropdownMenu) {
                    menu.classList.remove('show');
                    menu.parentElement.classList.remove('show');
                }
            });

            // Toggle current dropdown
            dropdownMenu.classList.toggle('show');
            parent.classList.toggle('show');
        });
    });

    // Close dropdowns when clicking outside
    document.addEventListener('click', (e) => {
        if (!e.target.closest('.dropdown')) {
            document.querySelectorAll('.dropdown-menu.show').forEach(menu => {
                menu.classList.remove('show');
                menu.parentElement.classList.remove('show');
            });
        }
    });

    // Payment options (for checkout page)
    const paymentOptions = document.querySelectorAll('.payment-option');
    paymentOptions.forEach(option => {
        option.addEventListener('click', () => {
            // Remove active class from all options
            paymentOptions.forEach(po => po.classList.remove('active'));

            // Add active class to clicked option
            option.classList.add('active');

            // Check the radio button
            const radio = option.querySelector('input[type=radio]');
            if (radio)
                radio.checked = true;
        });
    });

    // Initialize tooltips if Bootstrap is available
    if (typeof bootstrap !== 'undefined' && bootstrap.Tooltip) {
        const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]');
        [...tooltipTriggerList].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl));
    }

    // Navbar scroll effect
    window.addEventListener('scroll', () => {
        const navbar = document.querySelector('.navbar');
        if (navbar) {
            if (window.scrollY > 50) {
                navbar.classList.add('navbar-scrolled');
            } else {
                navbar.classList.remove('navbar-scrolled');
            }
        }
    });
}

/**
 * Set up form validation
 */
function setupFormValidation() {
    const forms = document.querySelectorAll('.needs-validation');

    forms.forEach(form => {
        form.addEventListener('submit', event => {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();

                // Highlight all invalid fields with enhanced styling
                const invalidFields = form.querySelectorAll(':invalid');
                invalidFields.forEach(field => {
                    field.classList.add('animate__animated', 'animate__shakeX');
                    // Add pulse effect to the border
                    field.style.borderColor = 'var(--danger)';
                    field.style.boxShadow = '0 0 0 0.25rem rgba(250, 82, 82, 0.25)';

                    setTimeout(() => {
                        field.classList.remove('animate__animated', 'animate__shakeX');
                    }, 1000);
                });
            }

            form.classList.add('was-validated');
        }, false);
    });

    // Enhanced focus effects for form inputs
    const formControls = document.querySelectorAll('.form-control, .form-select');
    formControls.forEach(input => {
        const formGroup = input.closest('.form-group, .mb-3');

        input.addEventListener('focus', () => {
            if (formGroup) {
                formGroup.classList.add('input-focused');

                const label = formGroup.querySelector('label');
                if (label) {
                    label.style.color = 'var(--primary-color)';
                }
            }

            // Remove the transform animation that causes jumping
            // input.style.transform = 'translateY(-2px)';
        });

        input.addEventListener('blur', () => {
            if (formGroup) {
                formGroup.classList.remove('input-focused');

                const label = formGroup.querySelector('label');
                if (label) {
                    label.style.color = '';
                }
            }

            // Remove animation
            // input.style.transform = '';
        });
    });
}

/**
 * Set up animations for page elements
 */
function setupAnimations() {
    // Add fade-in animation to main content
    const mainContent = document.querySelector('main');
    if (mainContent) {
        mainContent.style.opacity = '0';
        mainContent.style.transition = 'opacity 0.5s ease-in-out';

        setTimeout(() => {
            mainContent.style.opacity = '1';
        }, 100);
    }

    // Add scroll animations for sections
    const animatedSections = document.querySelectorAll('.animate-on-scroll');

    const checkIfInView = () => {
        animatedSections.forEach(section => {
            const sectionTop = section.getBoundingClientRect().top;
            const windowHeight = window.innerHeight;

            if (sectionTop < windowHeight * 0.75) {
                section.classList.add('in-view');
            }
        });
    };

    // Run on page load
    checkIfInView();

    // Run on scroll
    window.addEventListener('scroll', checkIfInView);
}

/**
 * Set up interactive UI elements
 */
function setupInteractions() {
    // Interactive buttons
    const buttons = document.querySelectorAll('.btn');
    buttons.forEach(button => {
        // Add ripple effect
        button.addEventListener('click', function (e) {
            const x = e.clientX - e.target.getBoundingClientRect().left;
            const y = e.clientY - e.target.getBoundingClientRect().top;

            const ripple = document.createElement('span');
            ripple.classList.add('ripple');
            ripple.style.left = `${x}px`;
            ripple.style.top = `${y}px`;

            this.appendChild(ripple);

            setTimeout(() => {
                ripple.remove();
            }, 600);
        });

        // Add hover effects
        button.addEventListener('mouseenter', () => {
            button.style.transform = 'translateY(-3px)';
            button.style.boxShadow = 'var(--box-shadow-hover)';
        });

        button.addEventListener('mouseleave', () => {
            button.style.transform = '';
            button.style.boxShadow = 'var(--box-shadow)';
        });
    });

    // Enhanced form inputs
    const formControls = document.querySelectorAll('.form-control');
    formControls.forEach(input => {
        // Add focus animation
        input.addEventListener('focus', () => {
            input.parentElement.classList.add('input-focused');
        });

        input.addEventListener('blur', () => {
            input.parentElement.classList.remove('input-focused');
        });
    });

    // Card hover effects
    const cards = document.querySelectorAll('.card');
    cards.forEach(card => {
        card.addEventListener('mouseenter', () => {
            card.style.transform = 'translateY(-5px)';
            card.style.boxShadow = 'var(--box-shadow-hover)';
        });

        card.addEventListener('mouseleave', () => {
            card.style.transform = 'translateY(0)';
            card.style.boxShadow = 'var(--box-shadow)';
        });
    });

    // Nav link hover effects
    const navLinks = document.querySelectorAll('.nav-link');
    navLinks.forEach(link => {
        link.addEventListener('mouseenter', () => {
            link.style.color = 'var(--primary-color)';
        });

        link.addEventListener('mouseleave', () => {
            if (!link.classList.contains('active')) {
                link.style.color = '';
            }
        });
    });
}

/**
 * Initialize slideshow for hero section
 */
function initializeSlideshow() {
    const slideshow = document.querySelector('.hero-slideshow');
    if (!slideshow)
        return;

    const slides = slideshow.querySelectorAll('.slide');
    const indicatorsContainer = slideshow.querySelector('.slide-indicators');
    let currentSlide = 0;
    let slideInterval;

    // Create indicators if they don't exist
    if (!indicatorsContainer && slides.length > 1) {
        const indicators = document.createElement('div');
        indicators.className = 'slide-indicators';

        slides.forEach((_, index) => {
            const indicator = document.createElement('div');
            indicator.className = 'slide-indicator';
            if (index === 0)
                indicator.classList.add('active');

            indicator.addEventListener('click', () => {
                goToSlide(index);
                resetInterval();
            });

            indicators.appendChild(indicator);
        });

        slideshow.appendChild(indicators);
    }

    // Show initial slide
    if (slides.length > 0) {
        slides[0].classList.add('active');
    }

    // Function to go to a specific slide
    function goToSlide(index) {
        // Hide all slides
        slides.forEach(slide => {
            slide.classList.remove('active');
        });

        // Update indicators
        const indicators = slideshow.querySelectorAll('.slide-indicator');
        indicators.forEach((indicator, i) => {
            if (i === index) {
                indicator.classList.add('active');
            } else {
                indicator.classList.remove('active');
            }
        });

        // Show the target slide
        currentSlide = index;
        slides[currentSlide].classList.add('active');
    }

    // Function to go to the next slide
    function nextSlide() {
        let next = currentSlide + 1;
        if (next >= slides.length) {
            next = 0;
        }
        goToSlide(next);
    }

    // Function to reset the interval timer
    function resetInterval() {
        clearInterval(slideInterval);
        slideInterval = setInterval(nextSlide, 5000);
    }

    // Start the slideshow if there's more than one slide
    if (slides.length > 1) {
        slideInterval = setInterval(nextSlide, 5000);

        // Add swipe events for mobile
        let startX;

        slideshow.addEventListener('touchstart', (e) => {
            startX = e.touches[0].clientX;
        });

        slideshow.addEventListener('touchend', (e) => {
            const endX = e.changedTouches[0].clientX;
            const diff = startX - endX;

            if (Math.abs(diff) > 50) { // Minimum swipe distance
                if (diff > 0) {
                    // Swipe left - next slide
                    nextSlide();
                } else {
                    // Swipe right - previous slide
                    let prev = currentSlide - 1;
                    if (prev < 0) {
                        prev = slides.length - 1;
                    }
                    goToSlide(prev);
                }
                resetInterval();
            }
        });
    }
}

/**
 * Utility function to toggle visibility of an element
 * @param {string} elementId - The ID of the element to toggle
 */
function toggleVisibility(elementId) {
    const element = document.getElementById(elementId);
    if (element) {
        if (element.style.display === 'none' || element.style.display === '') {
            element.style.display = 'block';
            // Fade in
            element.style.opacity = '0';
            setTimeout(() => {
                element.style.opacity = '1';
            }, 10);
        } else {
            // Fade out
            element.style.opacity = '0';
            setTimeout(() => {
                element.style.display = 'none';
            }, 300);
        }
    }
}

/**
 * Add to cart with animation
 * @param {number} courseId - The ID of the course to add
 */
function addToCart(courseId) {
    // Create a flying element for animation
    const courseCard = document.querySelector(`.course-card[data-course-id="${courseId}"]`);
    const imgCard = courseCard.querySelector('.card-img-top');
    const cartBtn = document.querySelector(`.add-to-cart-btn[data-course-id="${courseId}"]`);

    if (cartBtn && courseCard && imgCard) {
        // Create flying element
        const flyingElement = document.createElement('div');
        const img = document.createElement('img');
        img.src = imgCard.src;
        flyingElement.classList.add('flying-item');
        flyingElement.appendChild(img);

        // Get button position (starting point)
        const btnRect = cartBtn.getBoundingClientRect();
        const startX = btnRect.left + (btnRect.width / 2);
        const startY = btnRect.top + (btnRect.height / 2);

        // Position the flying element at the start
        flyingElement.style.left = `${startX}px`;
        flyingElement.style.top = `${startY}px`;
        flyingElement.style.transform = 'translate(-50%, -50%)';
        flyingElement.style.visibility = 'hidden';

        // Add the flying element to the DOM
        document.body.appendChild(flyingElement);

        // Get cart position (ending point)
        const cart = document.querySelector('.fa-shopping-cart');
        if (!cart) {
            flyingElement.remove();
            return;
        }

        const cartRect = cart.getBoundingClientRect();
        const endX = cartRect.left + (cartRect.width / 2);
        const endY = cartRect.top + (cartRect.height / 2);

        // Start animation after a small delay


        // Make actual AJAX request to add item
        fetch(`${window.contextPath}/cart/add?id=${courseId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        })
                .then(response => {
                    if (response.status === 401) {
                        // If status is 401 (not login), redirect to login
                        window.location.href = "login";
                        throw new Error("User not logged in");
                    }
                    return response.json();
                })
                .then(data => {
                    if (!data.success) {
                        showNotification(data.message || 'Error adding to cart', 'error');
                    } else {
                        flyingElement.style.visibility = 'visible';
                        setTimeout(() => {
                            // Apply animation
                            flyingElement.style.transform = 'translate(-50%, -50%) scale(0.5)';
                            flyingElement.style.left = `${endX}px`;
                            flyingElement.style.top = `${endY}px`;

                            // Remove the element after animation completes
                            setTimeout(() => {
                                flyingElement.remove();
                                // Notify user of success
                                showNotification('Course added to cart!', 'success');

                                // Update cart badge count if needed
                                const cartBadge = document.querySelector('.fa-shopping-cart + .badge');
                                if (cartBadge) {
                                    const currentCount = parseInt(cartBadge.textContent) || 0;
                                    cartBadge.textContent = currentCount + 1;
                                    cartBadge.classList.add('pulse-animation');
                                    setTimeout(() => {
                                        cartBadge.classList.remove('pulse-animation');
                                    }, 1000);
                                }
                            }, 2100);
                        }, 100);
                    }
                })
                        .catch(error => {
                            showNotification('Error adding to cart', 'error');
                            console.error('Error:', error);
                        });
            }
        }

        /**
         * Show notification
         * @param {string} message - The message to display
         * @param {string} type - The type of notification (success, error, info)
         */
        function showNotification(message, type = 'info') {
            const notification = document.createElement('div');
            notification.classList.add('custom-notification', `notification-${type}`);
            notification.innerHTML = `
        <div class="notification-icon">
            ${type === 'success' ? '<i class="fas fa-check-circle"></i>' :
                    type === 'error' ? '<i class="fas fa-exclamation-circle"></i>' :
                    '<i class="fas fa-info-circle"></i>'}
        </div>
        <div class="notification-message">${message}</div>
    `;

            document.body.appendChild(notification);

            // Animate in
            setTimeout(() => {
                notification.classList.add('show');
            }, 10);

            // Automatically remove after delay
            setTimeout(() => {
                notification.classList.remove('show');
                setTimeout(() => {
                    notification.remove();
                }, 300);
            }, 3000);
        } 