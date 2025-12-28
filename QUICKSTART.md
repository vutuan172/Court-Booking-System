# Court Booking System - Quick Start Guide

## Initial Setup

1. **Start MySQL Database**
   ```bash
   # Using Docker
   docker run --name mysql-court-booking -e MYSQL_ROOT_PASSWORD=yourpassword -e MYSQL_DATABASE=court_booking -p 3306:3306 -d mysql:8.0
   
   # Or start your local MySQL server
   ```

2. **Update Database Configuration**
   Edit `src/main/resources/application.yml` and set your MySQL credentials

3. **Run the Application**
   ```bash
   mvn spring-boot:run
   ```

## Test the System

### Using Web UI

1. Open browser: `http://localhost:8080`
2. Click "Register" to create account
3. Login with your credentials
4. Browse available courts
5. Make a booking

### Using API (curl examples)

```bash
# Register a new user
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Test User",
    "email": "test@example.com",
    "password": "password123",
    "phoneNumber": "0123456789"
  }'

# Login (get session cookie)
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=test@example.com&password=password123" \
  -c cookies.txt

# Get all courts
curl http://localhost:8080/api/courts

# Get schedules for a court
curl "http://localhost:8080/api/courts/1/schedules?date=2024-12-15"

# Create a booking (requires login)
curl -X POST http://localhost:8080/api/bookings \
  -b cookies.txt \
  -H "Content-Type: application/json" \
  -d '{
    "courtId": 1,
    "scheduleId": 1,
    "note": "Team practice",
    "orderItems": [
      {"serviceItemId": 1, "quantity": 2}
    ]
  }'
```

## Default Test Accounts

**Admin Account:**
- Email: `admin@example.com`
- Password: `strongpassword`

**User Account:**
- Email: `user@example.com`
- Password: `password123`

## Common Operations

### As a User:
1. Browse courts and schedules
2. Create bookings with optional services
3. View your booking history
4. Cancel bookings (within policy)
5. Leave reviews for completed bookings

### As an Admin:
1. Manage courts (CRUD)
2. Manage service items (CRUD)
3. View and manage all bookings
4. Generate revenue reports
5. View top services statistics

## Testing Concurrent Bookings

To test the duplicate booking prevention:

1. Open two terminal windows
2. In both, run this command simultaneously:
   ```bash
   curl -X POST http://localhost:8080/api/bookings \
     -b cookies.txt \
     -H "Content-Type: application/json" \
     -d '{"courtId": 1, "scheduleId": 2}'
   ```
3. One should succeed, one should fail with conflict error

## Troubleshooting

**Can't connect to database:**
- Check MySQL is running: `mysql -u root -p`
- Verify connection string in application.yml
- Check if port 3306 is available

**Application won't start:**
- Check Java version: `java -version` (should be 17+)
- Ensure port 8080 is not in use
- Check application logs for errors

**Login not working:**
- Clear browser cookies
- Use correct email (not username)
- Check user exists in database

## Next Steps

1. Import Postman collection: `Court_Booking_API.postman_collection.json`
2. Read full API documentation in README.md
3. Explore the admin dashboard
4. Test all major workflows
5. Review the code structure

## Support

For issues or questions, check:
- README.md for detailed documentation
- schema.sql for database structure
- Source code comments for implementation details
