# FitLife - Testing Guide (After Fixes)

This guide helps you verify all the fixes and improvements made to the fitLife application.

## Pre-Testing Setup

### Start the Backend
```bash
cd fitlife-backend
./mvnw spring-boot:run
# Server runs on http://localhost:8090
```

### Start the Frontend
```bash
cd fitlife-frontend
npm install  # if needed
npm start
# Application runs on http://localhost:4200
```

### Open in Browser
Navigate to: http://localhost:4200

---

## 1. Authentication & User Management Tests

### Test 1.1: User Registration
**Steps:**
1. Go to registration page (`/register`)
2. Fill in:
   - Full Name: "Test User"
   - Email: "test@fitlife.app"
   - Password: "password123"
   - Confirm Password: "password123"
   - Check "I agree to Terms"
3. Click "Create Account"

**Expected Results:**
✅ Sweet success toast: "Registration successful"
✅ Redirected to dashboard
✅ User is logged in
✅ Header shows user initials

**If Fails:**
❌ Check if email already exists (use different email)
❌ Verify MySQL is running
❌ Check browser console for errors

---

### Test 1.2: User Login
**Steps:**
1. Logout first (in header menu)
2. Go to login page (`/login`)
3. Enter credentials:
   - Email: test@fitlife.app
   - Password: password123
4. Click "Sign in"

**Expected Results:**
✅ Success toast: "Login successful"
✅ Redirected to dashboard
✅ Current user displayed in header

---

### Test 1.3: Error Handling - Invalid Login
**Steps:**
1. Go to login page
2. Enter:
   - Email: test@fitlife.app
   - Password: wrongpassword
3. Click "Sign in"

**Expected Results:**
✅ Error toast: "Invalid email or password"
✅ Stay on login page (not redirected)
✅ Form remains filled

---

## 2. Workout CRUD Tests

### Test 2.1: Create Workout ✅
**Steps:**
1. Go to Workouts page
2. Click "Add Workout"
3. Fill details:
   - Exercise Name: "Push-ups"
   - Exercise Type: "Strength"
   - Sets: 3
   - Reps: 20
   - Weight (lbs): 0 (no weights, bodyweight)
   - Notes: "Easy workout"
4. Click "Save"

**Expected Results:**
✅ Success toast: "Workout added successfully"
✅ Workout appears in list with today's date
✅ Estimated calories calculated (≈180 = 3 × 20 × 3)

---

### Test 2.2: Update Workout ✅
**Steps:**
1. In workouts list, click the workout you just created
2. Change:
   - Sets: 4
   - Reps: 25
3. Click "Save"

**Expected Results:**
✅ Success toast: "Workout updated successfully"
✅ Workout list updates with new values
✅ Calories recalculated (≈300 = 4 × 25 × 3)

---

### Test 2.3: Delete Workout ✅
**Steps:**
1. In workouts list, find a workout
2. Click delete icon
3. Confirm if prompted

**Expected Results:**
✅ Success toast: "Workout deleted successfully"
✅ Workout removed from list
✅ Statistics update immediately

---

### Test 2.4: Workout Exercise Type Logic
**Steps:**
1. Create multiple workouts with different types:
   - Strength: Shows sets, reps, weight
   - Cardio: Shows duration, distance
   - HIIT: Shows sets, reps, duration
   - Flexibility: Shows only duration

**Expected Results:**
✅ Only relevant fields show for each type
✅ Irrelevant fields auto-cleared to null
✅ Calories calculated appropriately

---

## 3. Goal CRUD Tests

### Test 3.1: Create Goal ✅
**Steps:**
1. Go to Goals page
2. Click "Add Goal"
3. Fill:
   - Title: "Lose 5 kg"
   - Goal Type: "weight"
   - Target Value: 75
   - Current Value: 80
   - Unit: "kg"
   - Deadline: (pick 8 weeks from today)
4. Click "Save"

**Expected Results:**
✅ Success toast: "Goal created successfully"
✅ Goal appears in "Active Goals"
✅ Progress bar shows 20% (80/75)

---

### Test 3.2: Update Goal Progress
**Steps:**
1. Click the goal you created
2. Update Current Value: 75 (equal to target)
3. Click "Save"

**Expected Results:**
✅ Success toast: "Goal updated successfully"
✅ Goal moves to "Completed Goals" (auto-complete when currentValue >= targetValue)
✅ Progress shows 100%

---

### Test 3.3: Delete Goal ✅
**Steps:**
1. Click delete icon on a goal
2. Confirm

**Expected Results:**
✅ Success toast: "Goal deleted successfully"
✅ Goal removed from list

---

## 4. Nutrition/Meal Tests (MOST CRITICAL - NEWLY FIXED)

### Test 4.1: Create Meal ✅
**Steps:**
1. Go to Nutrition page
2. Click "Add Meal"
3. Fill:
   - Food: "Chicken Breast" (or search for it)
   - Meal Type: "Lunch"
   - Quantity: 150
   - Unit: "g"
   - (Macros auto-populate: 165 cal, 26g protein, 0g carbs, 5.4g fat)
4. Click "Save"

**Expected Results:**
✅ Success toast: "Meal added successfully"
✅ Meal appears in today's meals
✅ Macros update: shows calories, protein, carbs, fat
✅ Remaining macros calculate correctly

---

### Test 4.2: Update Meal (NEWLY FIXED - KEY TEST) 🎯
**Steps:**
1. Go to Nutrition page
2. Click on a meal you created
3. Change:
   - Quantity: 200 (from 150)
   - Can change meal type to "Dinner"
4. Click "Update"

**Expected Results:**
✅ Success toast: "Meal updated successfully"
✅ Meal updated in list with new values
✅ Macros recalculate (200g = 220 cal, 38.5g protein, etc.)
✅ Daily totals update
✅ Remaining macros update

**⚠️ THIS IS THE MAIN FIX - Ensure this works!**

---

### Test 4.3: Delete Meal ✅
**Steps:**
1. Click delete icon on a meal
2. Confirm

**Expected Results:**
✅ Success toast: "Meal deleted successfully"
✅ Meal removed from today's list
✅ Macro totals recalculate

---

### Test 4.4: Multiple Meals - Daily Totals
**Steps:**
1. Add several meals throughout the day
2. Check "Today's Totals" section

**Expected Results:**
✅ All macros sum correctly
✅ Show: Total Calories, Protein, Carbs, Fat
✅ Show: Remaining macros (goal - total)
✅ Show: % of daily goals (0-100%)
✅ Progress rings visualize remaining capacity

---

## 5. Error Handling Tests

### Test 5.1: Backend Error Messages
**Steps:**
1. Open API in Postman or similar
2. Try to:
   - DELETE /api/workouts/99999 (non-existent)
   - PUT /api/meals/99999 (non-existent)

**Expected Results:**
✅ 404 Not Found (not 400)
✅ Message: "Workout not found" or "Meal not found"

---

### Test 5.2: Authorization Checks
**Steps:**
1. User A creates a workout
2. User B logs in
3. User B tries to update User A's workout (via DevTools)

**Expected Results:**
✅ Backend returns error toast
✅ Status: 401 Unauthorized or 403 Forbidden
✅ Message about unauthorized access

---

### Test 5.3: Validation Errors
**Steps:**
1. Try to create workout with:
   - Empty exercise name
   - Negative sets/reps
   - Invalid date

**Expected Results:**
✅ Error toast with validation message
✅ Form field highlighted/marked as invalid
✅ Submit button disabled

---

## 6. UI/UX Tests

### Test 6.1: Toast Notifications Behavior
**Steps:**
1. Perform any successful operation
2. Watch success toast appear
3. Wait 3 seconds

**Expected Results:**
✅ Green success toast appears top-right
✅ Auto-dismisses after 3 seconds
✅ Can dismiss manually by clicking X
✅ Multiple toasts stack

---

### Test 6.2: Loading States
**Steps:**
1. Create a new resource
2. Watch loading spinner on button

**Expected Results:**
✅ Button shows spinner while loading
✅ Button disabled during request
✅ Spinner disappears when done

---

### Test 6.3: Dashboard Statistics
**Steps:**
1. Add some workouts and meals
2. Go to Dashboard

**Expected Results:**
✅ Shows today's statistics:
   - Total workouts
   - Total calories burned
   - Macro breakdown
✅ Shows weekly chart:
   - Workouts per day
   - Trend visualization

---

## 7. Responsive Design Tests

### Test 7.1: Mobile View
**Steps:**
1. Open DevTools (F12)
2. Toggle device toolbar
3. Test on iPhone 12 Pro (390×844)

**Expected Results:**
✅ All pages responsive
✅ Navigation collapses to hamburger menu
✅ Forms fit on screen
✅ Charts/tables remain readable
✅ Touch-friendly button sizes

---

### Test 7.2: Tablet View
**Steps:**
1. Test on iPad (768×1024)

**Expected Results:**
✅ Layout adjusts for wider screen
✅ Sidebar visible on tablets
✅ Two-column layouts work

---

## 8. Navigation Tests

### Test 8.1: Protected Routes
**Steps:**
1. Logout
2. Try to directly visit `/dashboard` in URL

**Expected Results:**
✅ Redirected to `/login`
✅ Cannot access protected pages without token

---

### Test 8.2: Navigation Menu
**Steps:**
1. Click through all menu items
2. Navigation bar, sidebar links

**Expected Results:**
✅ All links work
✅ Current page highlighted
✅ Protected pages redirect after logout

---

## 9. Performance Tests

### Test 9.1: Page Load Time
**Steps:**
1. Open DevTools → Network tab
2. Navigate to each page
3. Check load times

**Expected Results:**
ℹ️ Dashboard: < 2 seconds
ℹ️ Workouts: < 1 second
ℹ️ Nutrition: < 1 second
ℹ️ Goals: < 1 second

---

### Test 9.2: Large Data Sets
**Steps:**
1. Add 50+ workouts
2. Add 30+ meals
3. Navigate pages

**Expected Results:**
✅ No lag or freezing
✅ Smooth scrolling
✅ Charts render properly

---

## 10. Environment Tests

### Test 10.1: Production Build
**Steps:**
```bash
cd fitlife-frontend
ng build --configuration production
# Check dist/ folder created
```

**Expected Results:**
✅ Build completes without errors
✅ Bundle size < 500KB
✅ No console warnings

---

## Checklist - All Tests Passing? ✅

- [ ] Test 1.1: Registration works
- [ ] Test 1.2: Login works
- [ ] Test 1.3: Error messages work
- [ ] Test 2.1: Create workout
- [ ] Test 2.2: Update workout
- [ ] Test 2.3: Delete workout
- [ ] Test 2.4: Exercise type logic
- [ ] Test 3.1: Create goal
- [ ] Test 3.2: Update goal (auto-complete)
- [ ] Test 3.3: Delete goal
- [ ] **Test 4.1: Create meal**
- [ ] **Test 4.2: UPDATE MEAL (KEY!) 🎯**
- [ ] **Test 4.3: Delete meal**
- [ ] Test 4.4: Daily totals
- [ ] Test 5.1: HTTP status codes
- [ ] Test 5.2: Authorization
- [ ] Test 5.3: Validation
- [ ] Test 6.1: Toast notifications
- [ ] Test 6.2: Loading states
- [ ] Test 6.3: Dashboard stats
- [ ] Test 7.1: Mobile responsive
- [ ] Test 7.2: Tablet responsive
- [ ] Test 8.1: Protected routes
- [ ] Test 8.2: Navigation
- [ ] Test 9.1: Page load time
- [ ] Test 9.2: Large datasets

---

## If Something Fails

1. **Check Backend Logs**: Look for stack traces
2. **Check Browser Console**: F12 → Console tab
3. **Check Network Tab**: F12 → Network tab
4. **Restart Services**: Kill and restart backend + frontend
5. **Clear Browser Cache**: Ctrl+Shift+Delete (or Cmd+Shift+Delete on Mac)
6. **Clear localStorage**: 
   ```javascript
   localStorage.clear()
   location.reload()
   ```

---

**Happy Testing! 🚀**

All features should now work smoothly. The meal update functionality is the most critical fix - verify that thoroughly!
