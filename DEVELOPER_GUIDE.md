# FitLife Development Quick Reference

## Project Structure

```
fitLife/
├── fitlife-backend/              (Spring Boot - Java 21)
│   ├── src/main/java/com/fitlife/
│   │   ├── controller/           (REST endpoints)
│   │   ├── service/              (Business logic)
│   │   ├── model/                (JPA entities)
│   │   ├── repository/           (Data access layer)
│   │   ├── dto/                  (Data transfer objects)
│   │   ├── config/               (Configuration classes)
│   │   └── security/             (JWT, authentication)
│   ├── application.properties    (Backend config - uses env vars)
│   └── pom.xml                   (Maven dependencies)
│
├── fitlife-frontend/             (Angular 21.2 - TypeScript)
│   ├── src/app/
│   │   ├── pages/                (Route components)
│   │   ├── services/             (HTTP + business logic)
│   │   ├── guards/               (Route guards)
│   │   ├── interceptors/         (HTTP interceptors)
│   │   ├── shared/               (Reusable components)
│   │   └── layout/               (Layout components)
│   ├── environments/             (Config per environment)
│   ├── angular.json              (Angular CLI config)
│   └── package.json              (NPM dependencies)
│
└── docs/                          (Documentation)
    └── FIXES_SUMMARY.md          (All improvements made)
```

## Key Services & Their Responsibilities

### Backend Services

**AuthService**
- User registration (creates user, returns JWT)
- User login (validates credentials, returns JWT)
- Handles default user values on registration

**WorkoutService**  
- CRUD for workouts
- Smart field clearing based on exercise type
- Calorie estimation (8 cal/min cardio, 3 cal per set×rep strength)
- Prevents users from accessing other users' workouts

**GoalService**
- CRUD for fitness goals
- Auto-complete when `currentValue >= targetValue`
- Authorization checks per user

**MealService** ✨ (Recently completed)
- CRUD for meals (now including UPDATE)
- Macro tracking (calories, protein, carbs, fat)
- Authorization checks per user

### Frontend Services

**AuthService**
- Manages authentication state (signals)
- JWT token lifecycle management
- User profile synchronization
- BMI calculations

**WorkoutService**
- Manages workout list (signals)
- Field visibility logic by exercise type
- Analytics: today's workouts, weekly stats, total calories

**GoalService**
- Manages goals list (signals)
- Filtering: active vs completed
- Progress calculations: overall %,weeks remaining

**NutritionService**
- Manages meals list (signals)
- Macro calculations: today's totals, remaining, percentages
- Food database with 20 common items + search
- Daily macro goals: 2500 cal, 150g protein, 300g carbs, 85g fat

**ToastService**
- Shows notifications: success, error, info, warning
- Auto-dismiss after 3 seconds

## API Endpoints

### Authentication
```
POST   /api/auth/register       → AuthResponse { token, user }
POST   /api/auth/login          → AuthResponse { token, user }
```

### Users
```
GET    /api/users/profile       → User
PUT    /api/users/profile       → User
```

### Workouts
```
GET    /api/workouts            → Workout[]
POST   /api/workouts            → Workout
PUT    /api/workouts/{id}       → Workout
DELETE /api/workouts/{id}       → 204 No Content
```

### Goals
```
GET    /api/goals               → Goal[]
POST   /api/goals               → Goal
PUT    /api/goals/{id}          → Goal
DELETE /api/goals/{id}          → 204 No Content
```

### Meals
```
GET    /api/meals               → Meal[]
POST   /api/meals               → Meal
PUT    /api/meals/{id}          → Meal  ✨ RECENTLY ADDED
DELETE /api/meals/{id}          → 204 No Content
```

## HTTP Status Codes Used

- **200** OK - Successful GET, POST, PUT
- **204** No Content - Successful DELETE
- **400** Bad Request - Validation errors
- **401** Unauthorized - Missing/invalid JWT or access denied
- **403** Forbidden - User trying to access other user's resource
- **404** Not Found - Resource doesn't exist
- **409** Conflict - Duplicate/duplicate email

## Running the Application

### Backend
```bash
cd fitlife-backend

# Development (listens on http://localhost:8090)
./mvnw spring-boot:run

# Build JAR
./mvnw clean package

# Run JAR
java -jar target/fitlife-0.0.1-SNAPSHOT.jar
```

### Frontend
```bash
cd fitlife-frontend

# Install dependencies (first time only)
npm install

# Development server (http://localhost:4200)
npm start
# or
ng serve

# Build for production
ng build --configuration production
# or 
npm run build

# Run tests
npm test
```

## Database Schema

All entities use:
- `id` (Long, auto-increment primary key)
- `createdAt` (LocalDateTime, auto-set on creation)
- `updatedAt` (LocalDateTime, auto-updated)
- `userId` (Foreign key to users table)

## Common Development Tasks

### Add a new API endpoint

1. **Backend**:
   - Add method to Service class
   - Add endpoint method to Controller class
   - Add DTO if needed
   - Update GlobalExceptionHandler if new error types

2. **Frontend**:
   - Add HTTP method to Service
   - Call service from component
   - Add toast notification for success/error
   - Update UI template

### Debug HTTP requests

1. **Backend**: Check application logs for request/response details
2. **Frontend**: 
   - Open DevTools (F12)
   - Network tab to see HTTP calls
   - Console for errors

## Testing Workflow

```bash
# Run all tests
npm test

# Run specific test file
npm test -- --include='**/nutrition.service.spec.ts'

# Run with coverage
ng test --code-coverage
```

## Debugging Tips

### Backend Issues
- Check MySQL connection (application.properties)
- Verify JWT_SECRET is set
- Look for 401 errors = JWT token issues
- Look for 404 errors = incorrect endpoint/entity not found

### Frontend Issues  
- Check if service is injected properly
- Verify API URL in environment.ts
- Check browser console for errors
- Use Angular DevTools extension in Chrome

## Known Limitations

1. No password reset feature
2. No user profile picture uploads
3. No social/friend system
4. No push notifications
5. Macro goals hardcoded (2500/150/300/85) - not customizable per user
6. No data export
7. No offline mode

## Performance Notes

- Workouts/goals/meals loaded on component initialization
- All data stored in signals (Angular 17+ reactive state)
- No pagination (works fine for typical user data size)
- JWT tokens expire after 24 hours

## Security Considerations

✅ **Implemented**:
- JWT authentication on all protected routes
- BCrypt password hashing
- CORS configuration
- Authorization checks per user
- SQL injection prevention (JPA parameterized queries)

⚠️ **Future Improvements**:
- Add rate limiting
- Add HTTPS only
- Add CSRF protection
- Add request logging/audit trail
- Add two-factor authentication
- Implement OAuth2 providers (Google, etc.)

---

**Last Updated:** May 2026
**Fixes Completed:** 10+ comprehensive improvements
**Test Coverage:** Manual testing recommended for all features
