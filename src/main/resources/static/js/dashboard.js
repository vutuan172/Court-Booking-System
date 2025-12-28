// Dashboard JavaScript - Court Booking System
(function() {
    'use strict';
    
    // Get user roles from data attributes
    var isAdmin = document.body.dataset.isAdmin === 'true';
    var isCourtOwner = document.body.dataset.isCourtOwner === 'true';
    
    console.log('Dashboard loaded - Admin:', isAdmin, 'Owner:', isCourtOwner);
    
    // Helper function to get auth headers (for future JWT usage)
    function getAuthHeaders() {
        var headers = {
            'Content-Type': 'application/json'
        };
        var token = localStorage.getItem('jwt_token');
        if (token) {
            headers['Authorization'] = 'Bearer ' + token;
        }
        return headers;
    }
    
    // Format price in Vietnamese currency
    function formatPrice(price) {
        if (!price) return '0 ₫';
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(price);
    }
    
    // Tab switching function
    window.switchTab = function(tabName, event) {
        if (event) {
            event.preventDefault();
        }
        
        console.log('Switching to tab:', tabName);
        
        // Hide all tabs
        var tabs = document.querySelectorAll('.tab-content');
        for (var i = 0; i < tabs.length; i++) {
            tabs[i].style.display = 'none';
        }
        
        // Remove active class from all nav tabs
        var navTabs = document.querySelectorAll('.nav-tab');
        for (var i = 0; i < navTabs.length; i++) {
            navTabs[i].classList.remove('active');
            navTabs[i].style.color = '#333';
            navTabs[i].style.fontWeight = 'normal';
            navTabs[i].style.borderBottom = '3px solid transparent';
        }
        
        // Show selected tab
        var selectedTab = document.getElementById(tabName + '-tab');
        if (selectedTab) {
            selectedTab.style.display = 'block';
        }
        
        // Set active nav tab
        var activeNavTab = event ? event.currentTarget : null;
        if (activeNavTab) {
            activeNavTab.classList.add('active');
            activeNavTab.style.color = '#667eea';
            activeNavTab.style.fontWeight = 'bold';
            activeNavTab.style.borderBottom = '3px solid #667eea';
        }
        
        // Load data based on tab
        if (tabName === 'courts') {
            loadCourts();
        } else if (tabName === 'bookings') {
            loadMyBookings();
        } else if (tabName === 'reviews') {
            loadReviews();
        } else if (tabName === 'owner-courts') {
            loadOwnerCourts();
            loadOwnerBookings();
        } else if (tabName === 'revenue') {
            loadRevenue();
        } else if (tabName === 'admin') {
            loadAdminData();
        } else if (tabName === 'manage-courts') {
            loadManageCourts();
        }
    };
    
    // Load available courts
    function loadCourts() {
    var container = document.getElementById('courtsGrid');
    if (!container) return;

    // 1. Loading state
    container.innerHTML =
        '<div style="text-align: center; padding: 40px; color: #999;">Loading courts...</div>';

    fetch('/api/courts')
        .then(function (response) { return response.json(); })
        .then(function (result) {
            var courts = result.data || result;

            // 2. Empty state
            if (!Array.isArray(courts) || courts.length === 0) {
                container.innerHTML =
                    '<div style="text-align: center; padding: 60px; color: #999;">' +
                    '<div style="font-size: 48px; margin-bottom: 20px;">🏟️</div>' +
                    '<h3 style="color: #666;">No courts available at the moment</h3>' +
                    '<p>Check back later or contact admin to add courts.</p>' +
                    '</div>';
                return;
            }

            // 3. Clear loading BEFORE render
            container.innerHTML = '';

            // 4. Render each court immediately
            courts.forEach(function (court) {
                var imgHtml = court.imageUrl
                    ? '<div style="width: 100%; height: 180px; overflow: hidden; margin: -20px -20px 15px -20px; border-radius: 10px 10px 0 0; display: flex; align-items: center; justify-content: center; background: #f5f5f5;">' +
                      '<img src="' + court.imageUrl + '" alt="Court" style="max-width: 100%; max-height: 100%; object-fit: contain;" ' +
                      'onerror="this.parentElement.style.display=\'none\'">' +
                      '</div>'
                    : '';

                var card = document.createElement('div');
                card.className = 'court-card';
                card.innerHTML =
                    imgHtml +
                    '<div class="court-name">' + (court.name || 'Unnamed Court') + '</div>' +
                    '<span class="court-type">' + (court.type || court.courtType || 'N/A') + '</span>' +
                    '<div class="court-location">📍 ' + (court.location || 'Unknown location') + '</div>' +
                    '<div class="court-price">' +
                    formatPrice(court.basePricePerHour || court.pricePerHour || 0) +
                    '/hour</div>' +
                    '<div class="court-review-stats loading-review">Loading reviews...</div>' +
                    '<div style="display: flex; gap: 10px; margin-top: 15px;">' +
                    '<button onclick="viewCourtDetails(' + court.id + ')" class="book-btn" ' +
                    'style="flex: 1; margin: 0; background: #2196f3;">📋 Reviews</button>' +
                    '<a href="/booking?courtId=' + court.id + '" class="book-btn" ' +
                    'style="flex: 1; text-align: center; text-decoration: none; margin: 0; line-height: 40px;">Book Now</a>' +
                    '</div>';

                container.appendChild(card);

                // 5. Load review stats AFTER card is in DOM
                loadCourtReviewStats(court.id, card);
            });
        })
        .catch(function (error) {
            console.error('Error loading courts:', error);
            container.innerHTML =
                '<div style="text-align: center; padding: 40px; color: #f44336;">' +
                '<div style="font-size: 48px; margin-bottom: 20px;">⚠️</div>' +
                '<h3>Error loading courts</h3>' +
                '<p>Please refresh the page or try again later.</p>' +
                '</div>';
        });
}

    // Load court review stats
    function loadCourtReviewStats(courtId, cardElement) {
        fetch('/api/reviews/court/' + courtId + '/stats')
            .then(function(response) { 
                if (!response.ok) throw new Error('Failed to fetch');
                return response.json(); 
            })
            .then(function(result) {
                var stats = result.data || result;
                var reviewDiv = cardElement.querySelector('.court-review-stats');
                if (reviewDiv) {
                    var avgRating = stats.averageRating || 0;
                    var totalReviews = stats.totalReviews || 0;
                    var stars = '';
                    for (var i = 0; i < 5; i++) {
                        stars += i < Math.round(avgRating) ? '⭐' : '☆';
                    }
                    reviewDiv.innerHTML = stars + ' ' + avgRating.toFixed(1) + ' (' + totalReviews + ' reviews)';
                    reviewDiv.classList.remove('loading-review');
                }
            })
            .catch(function(error) {
                var reviewDiv = cardElement.querySelector('.court-review-stats');
                if (reviewDiv) {
                    reviewDiv.innerHTML = '⭐ No reviews yet';
                    reviewDiv.classList.remove('loading-review');
                }
            });
    }
    
    // Load my bookings
    function loadMyBookings() {
        var tbody = document.getElementById('bookingsTableBody');
        if (!tbody) return;
        
        tbody.innerHTML = '<tr><td colspan="8" style="text-align: center; padding: 40px;">Loading bookings...</td></tr>';
        
        fetch('/api/bookings/my-bookings')
            .then(function(response) { return response.json(); })
            .then(function(result) {
                var bookings = result.data || result;
                
                if (!Array.isArray(bookings) || bookings.length === 0) {
                    tbody.innerHTML = '<tr><td colspan="8" style="text-align: center; padding: 60px; color: #999;">' +
                        '<div style="font-size: 48px; margin-bottom: 20px;">📅</div>' +
                        '<h3>No bookings yet</h3>' +
                        '<p>Book a court to get started!</p>' +
                        '</td></tr>';
                    return;
                }
                
                var html = '';
                for (var i = 0; i < bookings.length; i++) {
                    var b = bookings[i];
                    var statusClass = 'status-pending';
                    if (b.status === 'CONFIRMED') statusClass = 'status-confirmed';
                    if (b.status === 'CANCELLED' || b.status === 'CANCELED') statusClass = 'status-cancelled';
                    if (b.status === 'COMPLETED' || b.status === 'DONE') statusClass = 'status-completed';
                    
                    html += '<tr>' +
                        '<td>#' + b.id + '</td>' +
                        '<td>' + (b.courtName || 'N/A') + '</td>' +
                        '<td>' + (b.subCourtName || '-') + '</td>' +
                        '<td>' + (b.scheduleDate || b.date || 'N/A') + '</td>' +
                        '<td>' + (b.scheduleTime || (b.startTime + ' - ' + b.endTime) || 'N/A') + '</td>' +
                        '<td>' + formatPrice(b.totalPrice || 0) + '</td>' +
                        '<td><span class="status-badge ' + statusClass + '">' + b.status + '</span></td>' +
                        '<td>-</td>' +
                        '</tr>';
                }
                tbody.innerHTML = html;
            })
            .catch(function(error) {
                console.error('Error loading bookings:', error);
                tbody.innerHTML = '<tr><td colspan="8" style="text-align: center; padding: 40px; color: #f44336;">Error loading bookings</td></tr>';
            });
    }
    
    // Load owner courts
    function loadOwnerCourts() {
        if (!isCourtOwner) return;
        
        var tbody = document.getElementById('ownerCourtsTableBody');
        if (!tbody) return;
        
        tbody.innerHTML = '<tr><td colspan="7" style="text-align: center; padding: 40px;">Loading your courts...</td></tr>';
        
        fetch('/api/court-owner/my-courts')
            .then(function(response) { return response.json(); })
            .then(function(result) {
                var courts = result.data || result;
                
                if (!Array.isArray(courts) || courts.length === 0) {
                    tbody.innerHTML = '<tr><td colspan="7" style="text-align: center; padding: 60px; color: #999;">' +
                        '<div style="font-size: 48px;">🏗️</div>' +
                        '<h3>No courts yet</h3>' +
                        '<p>Add your first court using the button above!</p>' +
                        '</td></tr>';
                    return;
                }
                
                var html = '';
                for (var i = 0; i < courts.length; i++) {
                    var c = courts[i];
                    html += '<tr>' +
                        '<td>#' + c.id + '</td>' +
                        '<td>' + c.name + '</td>' +
                        '<td><span class="court-type">' + c.type + '</span></td>' +
                        '<td>' + c.location + '</td>' +
                        '<td>' + formatPrice(c.basePricePerHour) + '</td>' +
                        '<td><span class="court-status ' + c.status.toLowerCase() + '">' + c.status + '</span></td>' +
                        '<td>' +
                        '<button onclick="manageSubCourts(' + c.id + ', \'' + c.name.replace(/'/g, "\\'") + '\')" class="action-btn" style="background: #9c27b0; color: white; padding: 6px 12px; border: none; border-radius: 4px; cursor: pointer; margin-right: 5px;">Sub-Courts</button>' +
                        '<button onclick="showOwnerEditCourtModal(' + c.id + ')" class="action-btn" style="background: #2196f3; color: white; padding: 6px 12px; border: none; border-radius: 4px; cursor: pointer; margin-right: 5px;">Edit</button>' +
                        '<button onclick="toggleCourtStatus(' + c.id + ', \'' + c.status + '\')" class="action-btn" style="background: ' + (c.status === 'ACTIVE' ? '#f44336' : '#4caf50') + '; color: white; padding: 6px 12px; border: none; border-radius: 4px; cursor: pointer;">' +
                        (c.status === 'ACTIVE' ? 'Deactivate' : 'Activate') +
                        '</button>' +
                        '</td>' +
                        '</tr>';
                }
                tbody.innerHTML = html;
            })
            .catch(function(error) {
                console.error('Error loading owner courts:', error);
                tbody.innerHTML = '<tr><td colspan="7" style="text-align: center; padding: 40px; color: #f44336;">Error loading courts</td></tr>';
            });
    }
    
    // Load owner bookings
    function loadOwnerBookings() {
        if (!isCourtOwner) return;
        
        var tbody = document.getElementById('ownerBookingsTableBody');
        if (!tbody) return;
        
        tbody.innerHTML = '<tr><td colspan="7" style="text-align: center; padding: 40px;">Loading bookings...</td></tr>';
        
        fetch('/api/court-owner/bookings')
            .then(function(response) { return response.json(); })
            .then(function(result) {
                var bookings = result.data || result;
                
                if (!Array.isArray(bookings) || bookings.length === 0) {
                    tbody.innerHTML = '<tr><td colspan="7" style="text-align: center; padding: 60px; color: #999;">' +
                        'No bookings yet for your courts.' +
                        '</td></tr>';
                    return;
                }
                
                var html = '';
                for (var i = 0; i < bookings.length; i++) {
                    var b = bookings[i];
                    var statusClass = 'status-pending';
                    if (b.status === 'CONFIRMED') statusClass = 'status-confirmed';
                    if (b.status === 'CANCELLED' || b.status === 'CANCELED') statusClass = 'status-cancelled';
                    
                    // Action buttons based on booking status
                    var actionButtons = '';
                    if (b.status === 'PENDING') {
                        actionButtons = 
                            '<button onclick="approveBooking(' + b.id + ')" class="btn-approve" style="background: #4caf50; color: white; padding: 5px 12px; border: none; border-radius: 4px; cursor: pointer; margin-right: 5px; font-size: 12px;">✓ Approve</button>' +
                            '<button onclick="rejectBooking(' + b.id + ')" class="btn-reject" style="background: #f44336; color: white; padding: 5px 12px; border: none; border-radius: 4px; cursor: pointer; font-size: 12px;">✗ Reject</button>';
                    } else {
                        actionButtons = '<span style="color: #999; font-size: 12px;">-</span>';
                    }
                    
                    html += '<tr>' +
                        '<td>#' + b.id + '</td>' +
                        '<td>' + (b.courtName || 'N/A') + '</td>' +
                        '<td>' + (b.userName || b.userEmail || 'N/A') + '</td>' +
                        '<td>' + (b.scheduleDate || b.date || 'N/A') + '</td>' +
                        '<td>' + (b.scheduleTime || 'N/A') + '</td>' +
                        '<td>' + formatPrice(b.totalPrice || 0) + '</td>' +
                        '<td><span class="status-badge ' + statusClass + '">' + b.status + '</span></td>' +
                        '<td>' + actionButtons + '</td>' +
                        '</tr>';
                }
                tbody.innerHTML = html;
            })
            .catch(function(error) {
                console.error('Error loading owner bookings:', error);
                tbody.innerHTML = '<tr><td colspan="8" style="text-align: center; padding: 40px; color: #f44336;">Error loading bookings</td></tr>';
            });
    }
    
    // Load admin data
    function loadAdminData() {
        if (!isAdmin) return;
        console.log('Loading admin data...');
    }
    
    // Load manage courts (admin)
    function loadManageCourts() {
        if (!isAdmin) return;
        console.log('Loading manage courts...');
    }
    
    // Show add court modal for owners
    window.showOwnerAddCourtModal = function() {
        var modal = document.createElement('div');
        modal.style.cssText = 'position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000;';
        modal.innerHTML = 
            '<div style="background: white; padding: 30px; border-radius: 12px; max-width: 600px; width: 90%; max-height: 90vh; overflow-y: auto;">' +
            '<h2 style="margin-bottom: 20px; color: #333;">➕ Add New Court</h2>' +
            '<form id="addCourtForm">' +
            '<div style="margin-bottom: 15px;">' +
            '<label style="display: block; margin-bottom: 5px; font-weight: 600;">Court Name:</label>' +
            '<input type="text" name="name" required style="width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 6px;">' +
            '</div>' +
            '<div style="margin-bottom: 15px;">' +
            '<label style="display: block; margin-bottom: 5px; font-weight: 600;">Court Type:</label>' +
            '<select name="type" required style="width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 6px;">' +
            '<option value="FOOTBALL">Football</option>' +
            '<option value="BADMINTON">Badminton</option>' +
            '<option value="TENNIS">Tennis</option>' +
            '<option value="FUTSAL">Futsal</option>' +
            '</select>' +
            '</div>' +
            '<div style="margin-bottom: 15px;">' +
            '<label style="display: block; margin-bottom: 5px; font-weight: 600;">Location:</label>' +
            '<input type="text" name="location" required style="width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 6px;">' +
            '</div>' +
            '<div style="margin-bottom: 15px;">' +
            '<label style="display: block; margin-bottom: 5px; font-weight: 600;">Price per Hour (VND):</label>' +
            '<input type="number" name="basePricePerHour" required min="0" step="1000" style="width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 6px;">' +
            '</div>' +
            '<div style="margin-bottom: 15px;">' +
            '<label style="display: block; margin-bottom: 5px; font-weight: 600;">Description:</label>' +
            '<textarea name="description" rows="3" style="width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 6px;"></textarea>' +
            '</div>' +
            '<div style="margin-bottom: 15px;">' +
            '<label style="display: block; margin-bottom: 5px; font-weight: 600;">Court Image URL:</label>' +
            '<input type="url" name="imageUrl" placeholder="https://example.com/image.jpg" style="width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 6px;">' +
            '<small style="color: #666; display: block; margin-top: 5px;">📸 Paste a public image URL</small>' +
            '</div>' +
            '<div style="display: flex; gap: 10px; justify-content: flex-end;">' +
            '<button type="button" class="cancel-btn" style="padding: 10px 20px; background: #6c757d; color: white; border: none; border-radius: 6px; cursor: pointer;">Cancel</button>' +
            '<button type="submit" style="padding: 10px 20px; background: #28a745; color: white; border: none; border-radius: 6px; cursor: pointer;">Add Court</button>' +
            '</div>' +
            '</form>' +
            '</div>';
        
        modal.querySelector('.cancel-btn').onclick = function() {
            modal.remove();
        };
        
        modal.querySelector('form').onsubmit = function(e) {
            e.preventDefault();
            var formData = new FormData(e.target);
            var courtData = {
                name: formData.get('name'),
                type: formData.get('type'),
                location: formData.get('location'),
                basePricePerHour: parseFloat(formData.get('basePricePerHour')),
                description: formData.get('description') || '',
                imageUrl: formData.get('imageUrl') || '',
                status: 'ACTIVE'
            };
            
            fetch('/api/court-owner/courts', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(courtData)
            })
            .then(function(response) {
                if (response.ok) {
                    alert('✅ Court added successfully!');
                    modal.remove();
                    loadOwnerCourts();
                    loadCourts();
                } else {
                    return response.json().then(function(error) {
                        alert('❌ Failed to add court: ' + (error.message || 'Unknown error'));
                    });
                }
            })
            .catch(function(error) {
                console.error('Error adding court:', error);
                alert('❌ Error adding court. Please try again.');
            });
        };
        
        document.body.appendChild(modal);
    };
    
    // Show add court modal for admins
    window.showAddCourtModal = function() {
        showOwnerAddCourtModal();
    };
    
    // Filter courts
    window.filterCourts = function() {
        loadCourts();
    };
    
    // Initialize on page load
    document.addEventListener('DOMContentLoaded', function() {
        console.log('✅ Dashboard initialized');
        
        // Check if redirected from booking page
        var urlParams = new URLSearchParams(window.location.search);
        if (urlParams.get('booking') === 'success') {
            // Switch to My Bookings tab
            switchTab('bookings', null);
            // Clean URL
            window.history.replaceState({}, document.title, '/dashboard');
        } else {
            loadCourts();
        }
    });
    
    // Approve booking function
    window.approveBooking = function(bookingId) {
        if (!confirm('Approve this booking?')) return;
        
        fetch('/api/court-owner/bookings/' + bookingId + '/approve', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(function(response) { return response.json(); })
        .then(function(result) {
            if (result.success) {
                alert('✅ Booking approved successfully!');
                loadOwnerBookings();
            } else {
                alert('❌ ' + (result.message || 'Error approving booking'));
            }
        })
        .catch(function(error) {
            console.error('Error approving booking:', error);
            alert('❌ Error approving booking');
        });
    };
    
    // Reject booking function
    window.rejectBooking = function(bookingId) {
        if (!confirm('Reject this booking? The time slot will become available again.')) return;
        
        fetch('/api/court-owner/bookings/' + bookingId + '/reject', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(function(response) { return response.json(); })
        .then(function(result) {
            if (result.success) {
                alert('✅ Booking rejected successfully!');
                loadOwnerBookings();
            } else {
                alert('❌ ' + (result.message || 'Error rejecting booking'));
            }
        })
        .catch(function(error) {
            console.error('Error rejecting booking:', error);
            alert('❌ Error rejecting booking');
        });
    };
    
    // Show edit court modal
    window.showOwnerEditCourtModal = function(courtId) {
        // First, fetch court details
        fetch('/api/courts/' + courtId)
            .then(function(response) { return response.json(); })
            .then(function(result) {
                var court = result.data || result;
                
                var modal = document.createElement('div');
                modal.id = 'editCourtModal';
                modal.style.cssText = 'position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000;';
                modal.innerHTML = 
                    '<div style="background: white; padding: 30px; border-radius: 12px; max-width: 600px; width: 90%; max-height: 90vh; overflow-y: auto;">' +
                    '<h2 style="margin-bottom: 20px; color: #333;">✏️ Edit Court</h2>' +
                    '<form id="editCourtForm">' +
                    '<input type="hidden" name="courtId" value="' + court.id + '">' +
                    '<div style="margin-bottom: 15px;">' +
                    '<label style="display: block; margin-bottom: 5px; font-weight: 600;">Court Name:</label>' +
                    '<input type="text" name="name" value="' + court.name + '" required style="width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 6px;">' +
                    '</div>' +
                    '<div style="margin-bottom: 15px;">' +
                    '<label style="display: block; margin-bottom: 5px; font-weight: 600;">Court Type:</label>' +
                    '<select name="type" required style="width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 6px;">' +
                    '<option value="FOOTBALL" ' + (court.type === 'FOOTBALL' ? 'selected' : '') + '>Football</option>' +
                    '<option value="BADMINTON" ' + (court.type === 'BADMINTON' ? 'selected' : '') + '>Badminton</option>' +
                    '<option value="TENNIS" ' + (court.type === 'TENNIS' ? 'selected' : '') + '>Tennis</option>' +
                    '<option value="FUTSAL" ' + (court.type === 'FUTSAL' ? 'selected' : '') + '>Futsal</option>' +
                    '</select>' +
                    '</div>' +
                    '<div style="margin-bottom: 15px;">' +
                    '<label style="display: block; margin-bottom: 5px; font-weight: 600;">Location:</label>' +
                    '<input type="text" name="location" value="' + court.location + '" required style="width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 6px;">' +
                    '</div>' +
                    '<div style="margin-bottom: 15px;">' +
                    '<label style="display: block; margin-bottom: 5px; font-weight: 600;">Price per Hour (VND):</label>' +
                    '<input type="number" name="basePricePerHour" value="' + court.basePricePerHour + '" required min="0" step="1000" style="width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 6px;">' +
                    '</div>' +
                    '<div style="margin-bottom: 15px;">' +
                    '<label style="display: block; margin-bottom: 5px; font-weight: 600;">Description:</label>' +
                    '<textarea name="description" rows="3" style="width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 6px;">' + (court.description || '') + '</textarea>' +
                    '</div>' +
                    '<div style="margin-bottom: 15px;">' +
                    '<label style="display: block; margin-bottom: 5px; font-weight: 600;">Image URL (optional):</label>' +
                    '<input type="url" name="imageUrl" value="' + (court.imageUrl || '') + '" style="width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 6px;">' +
                    '</div>' +
                    '<div style="display: flex; gap: 10px; justify-content: flex-end;">' +
                    '<button type="button" onclick="closeEditCourtModal()" style="padding: 12px 24px; border: 1px solid #ddd; border-radius: 6px; cursor: pointer; background: white;">Cancel</button>' +
                    '<button type="submit" style="padding: 12px 24px; background: #2196f3; color: white; border: none; border-radius: 6px; cursor: pointer; font-weight: 600;">💾 Save Changes</button>' +
                    '</div>' +
                    '</form>' +
                    '</div>';
                
                document.body.appendChild(modal);
                
                // Handle form submission
                document.getElementById('editCourtForm').addEventListener('submit', function(e) {
                    e.preventDefault();
                    updateCourt(courtId, this);
                });
            })
            .catch(function(error) {
                console.error('Error fetching court details:', error);
                alert('❌ Error loading court details');
            });
    };
    
    // Close edit modal
    window.closeEditCourtModal = function() {
        var modal = document.getElementById('editCourtModal');
        if (modal) {
            modal.remove();
        }
    };
    
    // Update court
    function updateCourt(courtId, form) {
        var formData = new FormData(form);
        var data = {
            name: formData.get('name'),
            type: formData.get('type'),
            location: formData.get('location'),
            basePricePerHour: parseFloat(formData.get('basePricePerHour')),
            description: formData.get('description'),
            imageUrl: formData.get('imageUrl')
        };
        
        fetch('/api/court-owner/courts/' + courtId, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        })
        .then(function(response) { return response.json(); })
        .then(function(result) {
            if (result.success) {
                alert('✅ Court updated successfully!');
                closeEditCourtModal();
                loadOwnerCourts();
            } else {
                alert('❌ ' + (result.message || 'Error updating court'));
            }
        })
        .catch(function(error) {
            console.error('Error updating court:', error);
            alert('❌ Error updating court');
        });
    }
    
    // Toggle court status (activate/deactivate)
    window.toggleCourtStatus = function(courtId, currentStatus) {
        var action = currentStatus === 'ACTIVE' ? 'deactivate' : 'activate';
        var confirmMsg = 'Are you sure you want to ' + action + ' this court?';
        
        if (!confirm(confirmMsg)) {
            return;
        }
        
        fetch('/api/court-owner/courts/' + courtId + '/status', {
            method: 'PATCH'
        })
        .then(function(response) { return response.json(); })
        .then(function(result) {
            if (result.success) {
                alert('✅ Court ' + action + 'd successfully!');
                loadOwnerCourts();
            } else {
                alert('❌ ' + (result.message || 'Error ' + action + 'ing court'));
            }
        })
        .catch(function(error) {
            console.error('Error toggling court status:', error);
            alert('❌ Error ' + action + 'ing court');
        });
    };
    
    // Sub-Court Management Functions
    window.manageSubCourts = function(courtId, courtName) {
        var modal = createSubCourtsModal(courtId, courtName);
        document.body.appendChild(modal);
        
        // Setup event listeners AFTER appending to DOM
        document.getElementById('addSubCourtBtn').onclick = function() {
            document.getElementById('addSubCourtForm').style.display = 'block';
        };
        
        loadSubCourts(courtId);
    };
    
    function createSubCourtsModal(courtId, courtName) {
        var modal = document.createElement('div');
        modal.id = 'subCourtsModal';
        modal.style.cssText = 'position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 9999;';
        
        modal.innerHTML = '<div style="background: white; padding: 30px; border-radius: 8px; width: 800px; max-height: 80vh; overflow-y: auto;">' +
            '<h2 style="margin-top: 0;">Sub-Courts for ' + courtName + '</h2>' +
            '<button id="addSubCourtBtn" style="background: #4caf50; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; margin-bottom: 15px;">➕ Add Sub-Court</button>' +
            '<div id="addSubCourtForm" style="display: none; background: #f5f5f5; padding: 15px; border-radius: 4px; margin-bottom: 15px;">' +
            '<input type="hidden" id="subCourtCourtId" value="' + courtId + '">' +
            '<input type="text" id="subCourtName" placeholder="Sub-Court Name" style="width: 100%; padding: 8px; margin-bottom: 10px; border: 1px solid #ddd; border-radius: 4px;">' +
            '<textarea id="subCourtDescription" placeholder="Description (optional)" style="width: 100%; padding: 8px; margin-bottom: 10px; border: 1px solid #ddd; border-radius: 4px; resize: vertical;" rows="3"></textarea>' +
            '<button onclick="createSubCourt()" style="background: #2196f3; color: white; padding: 8px 16px; border: none; border-radius: 4px; cursor: pointer; margin-right: 10px;">Save</button>' +
            '<button onclick="hideAddSubCourtForm()" style="background: #666; color: white; padding: 8px 16px; border: none; border-radius: 4px; cursor: pointer;">Cancel</button>' +
            '</div>' +
            '<table style="width: 100%; border-collapse: collapse;">' +
            '<thead><tr style="background: #f5f5f5;">' +
            '<th style="padding: 12px; text-align: left; border-bottom: 2px solid #ddd;">ID</th>' +
            '<th style="padding: 12px; text-align: left; border-bottom: 2px solid #ddd;">Name</th>' +
            '<th style="padding: 12px; text-align: left; border-bottom: 2px solid #ddd;">Status</th>' +
            '<th style="padding: 12px; text-align: left; border-bottom: 2px solid #ddd;">Actions</th>' +
            '</tr></thead>' +
            '<tbody id="subCourtsTableBody"></tbody>' +
            '</table>' +
            '<button onclick="closeSubCourtsModal()" style="background: #f44336; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; margin-top: 20px;">Close</button>' +
            '</div>';
        
        return modal;
    }
    
    window.closeSubCourtsModal = function() {
        var modal = document.getElementById('subCourtsModal');
        if (modal) modal.remove();
    };
    
    window.hideAddSubCourtForm = function() {
        document.getElementById('addSubCourtForm').style.display = 'none';
        document.getElementById('subCourtName').value = '';
        document.getElementById('subCourtDescription').value = '';
    };
    
    function loadSubCourts(courtId) {
        var tbody = document.getElementById('subCourtsTableBody');
        if (!tbody) return;
        
        tbody.innerHTML = '<tr><td colspan="4" style="text-align: center; padding: 20px;">Loading...</td></tr>';
        
        fetch('/api/subcourts/court/' + courtId)
            .then(function(response) { return response.json(); })
            .then(function(result) {
                var subCourts = result.data || result;
                
                if (!Array.isArray(subCourts) || subCourts.length === 0) {
                    tbody.innerHTML = '<tr><td colspan="4" style="text-align: center; padding: 20px; color: #999;">No sub-courts yet. Click "Add Sub-Court" to create one.</td></tr>';
                    return;
                }
                
                var html = '';
                for (var i = 0; i < subCourts.length; i++) {
                    var sc = subCourts[i];
                    html += '<tr>' +
                        '<td style="padding: 12px; border-bottom: 1px solid #eee;">#' + sc.id + '</td>' +
                        '<td style="padding: 12px; border-bottom: 1px solid #eee;">' + sc.name + '</td>' +
                        '<td style="padding: 12px; border-bottom: 1px solid #eee;"><span style="padding: 4px 12px; border-radius: 12px; font-size: 12px; background: ' + (sc.isAvailable ? '#4caf50' : '#f44336') + '; color: white;">' + (sc.isAvailable ? 'Available' : 'Unavailable') + '</span></td>' +
                        '<td style="padding: 12px; border-bottom: 1px solid #eee;">' +
                        '<button onclick="toggleSubCourtStatus(' + sc.id + ', ' + sc.isAvailable + ', ' + courtId + ')" style="background: ' + (sc.isAvailable ? '#f44336' : '#4caf50') + '; color: white; padding: 5px 12px; border: none; border-radius: 4px; cursor: pointer; margin-right: 5px; font-size: 12px;">' +
                        (sc.isAvailable ? 'Disable' : 'Enable') +
                        '</button>' +
                        '<button onclick="deleteSubCourt(' + sc.id + ', ' + courtId + ')" style="background: #666; color: white; padding: 5px 12px; border: none; border-radius: 4px; cursor: pointer; font-size: 12px;">Delete</button>' +
                        '</td>' +
                        '</tr>';
                }
                tbody.innerHTML = html;
            })
            .catch(function(error) {
                console.error('Error loading sub-courts:', error);
                tbody.innerHTML = '<tr><td colspan="4" style="text-align: center; padding: 20px; color: #f44336;">Error loading sub-courts</td></tr>';
            });
    }
    
    window.createSubCourt = function() {
        var courtId = document.getElementById('subCourtCourtId').value;
        var name = document.getElementById('subCourtName').value;
        var description = document.getElementById('subCourtDescription').value;
        
        if (!name) {
            alert('Please enter sub-court name');
            return;
        }
        
        fetch('/api/subcourts', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + localStorage.getItem('token')
            },
            body: JSON.stringify({
                courtId: parseInt(courtId),
                name: name,
                description: description,
                isAvailable: true
            })
        })
        .then(function(response) { return response.json(); })
        .then(function(result) {
            if (result.success) {
                alert('✅ Sub-court created successfully!');
                hideAddSubCourtForm();
                loadSubCourts(courtId);
            } else {
                alert('❌ Error: ' + (result.message || 'Failed to create sub-court'));
            }
        })
        .catch(function(error) {
            console.error('Error creating sub-court:', error);
            alert('❌ Error creating sub-court');
        });
    };
    
    window.toggleSubCourtStatus = function(subCourtId, currentStatus, courtId) {
        fetch('/api/subcourts/' + subCourtId, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + localStorage.getItem('token')
            },
            body: JSON.stringify({
                isAvailable: !currentStatus
            })
        })
        .then(function(response) { return response.json(); })
        .then(function(result) {
            if (result.success) {
                loadSubCourts(courtId);
            } else {
                alert('❌ Error updating sub-court status');
            }
        })
        .catch(function(error) {
            console.error('Error updating sub-court:', error);
            alert('❌ Error updating sub-court');
        });
    };
    
    window.deleteSubCourt = function(subCourtId, courtId) {
        if (!confirm('Are you sure you want to delete this sub-court?')) {
            return;
        }
        
        fetch('/api/subcourts/' + subCourtId, {
            method: 'DELETE',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('token')
            }
        })
        .then(function(response) { return response.json(); })
        .then(function(result) {
            if (result.success) {
                alert('✅ Sub-court deleted successfully!');
                loadSubCourts(courtId);
            } else {
                alert('❌ Error deleting sub-court');
            }
        })
        .catch(function(error) {
            console.error('Error deleting sub-court:', error);
            alert('❌ Error deleting sub-court');
        });
    };
    
    // Load Reviews & Feedback
    function loadReviews() {
        var tbody = document.getElementById('reviewsTableBody');
        if (!tbody) return;
        
        tbody.innerHTML = '<tr><td colspan="5" style="text-align: center; padding: 40px;">Loading reviews...</td></tr>';
        
        fetch('/api/bookings/my-bookings')
            .then(function(response) { return response.json(); })
            .then(function(result) {
                var bookings = result.data || result;
                
                if (!Array.isArray(bookings) || bookings.length === 0) {
                    tbody.innerHTML = '<tr><td colspan="5" style="text-align: center; padding: 60px; color: #999;">' +
                        '<div style="font-size: 48px; margin-bottom: 20px;">⭐</div>' +
                        '<h3>No reviews yet</h3>' +
                        '<p>Book a court and leave a review!</p>' +
                        '</td></tr>';
                    return;
                }
                
                var html = '';
                for (var i = 0; i < bookings.length; i++) {
                    var b = bookings[i];
                    if (b.status === 'COMPLETED') {
                        html += '<tr>' +
                            '<td>' + (b.courtName || 'N/A') + '</td>' +
                            '<td><span style="color: #ffc107;">⭐⭐⭐⭐⭐</span></td>' +
                            '<td>Great court! Will book again.</td>' +
                            '<td>' + (b.scheduleDate || 'N/A') + '</td>' +
                            '<td><button class="action-btn btn-review" onclick="alert(\'Review feature coming soon!\')">Edit Review</button></td>' +
                            '</tr>';
                    }
                }
                
                if (html === '') {
                    tbody.innerHTML = '<tr><td colspan="5" style="text-align: center; padding: 60px; color: #999;">No completed bookings to review yet</td></tr>';
                } else {
                    tbody.innerHTML = html;
                }
            })
            .catch(function(error) {
                console.error('Error loading reviews:', error);
                tbody.innerHTML = '<tr><td colspan="5" style="text-align: center; padding: 40px; color: #f44336;">Error loading reviews</td></tr>';
            });
    }
    
    // Load Revenue (Court Owner)
    function loadRevenue() {
        // Load stats
        fetch('/api/court-owner/my-bookings')
            .then(function(response) { return response.json(); })
            .then(function(result) {
                var bookings = result.data || result;
                
                if (!Array.isArray(bookings)) {
                    bookings = [];
                }
                
                var totalRevenue = 0;
                var monthlyRevenue = 0;
                var activeBookings = 0;
                var currentMonth = new Date().getMonth();
                
                // Prepare data for charts
                var last7Days = [];
                var revenueByDay = {};
                var statusCounts = { PENDING: 0, CONFIRMED: 0, CANCELLED: 0, COMPLETED: 0 };
                
                // Get last 7 days
                for (var i = 6; i >= 0; i--) {
                    var date = new Date();
                    date.setDate(date.getDate() - i);
                    var dateStr = date.toISOString().split('T')[0];
                    last7Days.push(dateStr);
                    revenueByDay[dateStr] = 0;
                }
                
                for (var i = 0; i < bookings.length; i++) {
                    var b = bookings[i];
                    totalRevenue += b.totalPrice || 0;
                    
                    // Add to daily revenue if within last 7 days
                    if (b.scheduleDate && revenueByDay.hasOwnProperty(b.scheduleDate)) {
                        revenueByDay[b.scheduleDate] += (b.totalPrice || 0);
                    }
                    
                    if (b.scheduleDate) {
                        var bookingMonth = new Date(b.scheduleDate).getMonth();
                        if (bookingMonth === currentMonth) {
                            monthlyRevenue += b.totalPrice || 0;
                        }
                    }
                    
                    if (b.status === 'CONFIRMED' || b.status === 'PENDING') {
                        activeBookings++;
                    }
                    
                    // Count by status
                    if (statusCounts.hasOwnProperty(b.status)) {
                        statusCounts[b.status]++;
                    }
                }
                
                document.getElementById('ownerTotalRevenue').textContent = formatPrice(totalRevenue);
                document.getElementById('ownerMonthlyRevenue').textContent = formatPrice(monthlyRevenue);
                document.getElementById('ownerTotalBookings').textContent = bookings.length;
                document.getElementById('ownerActiveBookings').textContent = activeBookings;
                
                // Create revenue trend chart
                var revenueCtx = document.getElementById('revenueChart');
                if (revenueCtx && typeof Chart !== 'undefined') {
                    // Destroy existing chart if any
                    if (window.revenueChartInstance) {
                        window.revenueChartInstance.destroy();
                    }
                    
                    var revenueData = last7Days.map(function(date) { return revenueByDay[date]; });
                    var labels = last7Days.map(function(date) {
                        var d = new Date(date);
                        return (d.getMonth() + 1) + '/' + d.getDate();
                    });
                    
                    window.revenueChartInstance = new Chart(revenueCtx, {
                        type: 'line',
                        data: {
                            labels: labels,
                            datasets: [{
                                label: 'Revenue ($)',
                                data: revenueData,
                                borderColor: '#667eea',
                                backgroundColor: 'rgba(102, 126, 234, 0.1)',
                                tension: 0.3,
                                fill: true
                            }]
                        },
                        options: {
                            responsive: true,
                            maintainAspectRatio: true,
                            plugins: {
                                legend: { display: false }
                            },
                            scales: {
                                y: { 
                                    beginAtZero: true,
                                    ticks: {
                                        callback: function(value) {
                                            return '$' + value;
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
                
                // Create booking status chart
                var statusCtx = document.getElementById('bookingStatusChart');
                if (statusCtx && typeof Chart !== 'undefined') {
                    // Destroy existing chart if any
                    if (window.statusChartInstance) {
                        window.statusChartInstance.destroy();
                    }
                    
                    window.statusChartInstance = new Chart(statusCtx, {
                        type: 'doughnut',
                        data: {
                            labels: ['Pending', 'Confirmed', 'Completed', 'Cancelled'],
                            datasets: [{
                                data: [
                                    statusCounts.PENDING,
                                    statusCounts.CONFIRMED,
                                    statusCounts.COMPLETED,
                                    statusCounts.CANCELLED
                                ],
                                backgroundColor: [
                                    '#FFC107',
                                    '#4CAF50',
                                    '#2196F3',
                                    '#F44336'
                                ]
                            }]
                        },
                        options: {
                            responsive: true,
                            maintainAspectRatio: true,
                            plugins: {
                                legend: {
                                    position: 'bottom'
                                }
                            }
                        }
                    });
                }
            })
            .catch(function(error) {
                console.error('Error loading revenue stats:', error);
            });
        
        // Load revenue by court
        var tbody = document.getElementById('revenueByCourtTableBody');
        if (!tbody) return;
        
        tbody.innerHTML = '<tr><td colspan="4" style="text-align: center; padding: 40px;">Loading revenue data...</td></tr>';
        
        fetch('/api/court-owner/my-courts')
            .then(function(response) { return response.json(); })
            .then(function(result) {
                var courts = result.data || result;
                
                if (!Array.isArray(courts) || courts.length === 0) {
                    tbody.innerHTML = '<tr><td colspan="4" style="text-align: center; padding: 60px; color: #999;">No courts yet</td></tr>';
                    return;
                }
                
                // For now, show placeholder data - you'll need to implement backend endpoint for this
                var html = '';
                for (var i = 0; i < courts.length; i++) {
                    var c = courts[i];
                    html += '<tr>' +
                        '<td>' + c.name + '</td>' +
                        '<td>0</td>' +
                        '<td>' + formatPrice(0) + '</td>' +
                        '<td>' + formatPrice(0) + '</td>' +
                        '</tr>';
                }
                tbody.innerHTML = html;
            })
            .catch(function(error) {
                console.error('Error loading revenue by court:', error);
                tbody.innerHTML = '<tr><td colspan="4" style="text-align: center; padding: 40px; color: #f44336;">Error loading revenue data</td></tr>';
            });
    }
    
    // View Court Details - Navigate to dedicated reviews page
    window.viewCourtDetails = function(courtId) {
        window.location.href = '/court-reviews?courtId=' + courtId;
    };

    
})();
