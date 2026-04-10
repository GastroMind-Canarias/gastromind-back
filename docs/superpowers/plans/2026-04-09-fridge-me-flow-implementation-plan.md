# Fridge Me Flow Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement `/me` fridge and fridge-item flows based on JWT household context, with owner-only fridge mutation and admin-by-id unrestricted fridge CRUD.

**Architecture:** Add application use cases for authenticated household resolution and `/me` orchestration. Keep controllers thin and delegate to use cases while preserving existing domain services/repositories. Enforce role boundaries in REST layer and in use-case ownership checks where needed.

**Tech Stack:** Java, Spring Boot, Spring Security, JPA, JUnit/Spring tests.

---

### Task 1: Add authenticated household/fridge resolver use case

**Files:**
- Create: `src/main/java/com/gastromind/api/application/usecases/ResolveAuthenticatedHouseholdContextUseCase.java`
- Modify: `src/main/java/com/gastromind/api/domain/ports/out/FridgeRepository.java`
- Modify: `src/main/java/com/gastromind/api/infrastructure/adapters/out/persistence/jpa/repositories/FridgeJpaRepository.java`
- Modify: `src/main/java/com/gastromind/api/infrastructure/adapters/out/persistence/jpa/FridgeAdapter.java`
- Test: `src/test/java/com/gastromind/api/application/usecases/ResolveAuthenticatedHouseholdContextUseCaseTest.java`

- [ ] **Step 1: Write the failing test**
```java
@Test
void shouldResolveHouseholdAndFridgeFromAuthenticationPrincipal() { /* expects resolved householdId + fridge */ }
```

- [ ] **Step 2: Run test to verify it fails**
Run: `./mvnw -Dtest=ResolveAuthenticatedHouseholdContextUseCaseTest test`
Expected: FAIL because use case does not exist yet.

- [ ] **Step 3: Write minimal implementation**
```java
public record HouseholdContext(User user, String householdId, Fridge fridge) {}
```

- [ ] **Step 4: Run test to verify it passes**
Run: `./mvnw -Dtest=ResolveAuthenticatedHouseholdContextUseCaseTest test`
Expected: PASS.

- [ ] **Step 5: Commit**
```bash
git add src/main/java/com/gastromind/api/application/usecases/ResolveAuthenticatedHouseholdContextUseCase.java src/main/java/com/gastromind/api/domain/ports/out/FridgeRepository.java src/main/java/com/gastromind/api/infrastructure/adapters/out/persistence/jpa/repositories/FridgeJpaRepository.java src/main/java/com/gastromind/api/infrastructure/adapters/out/persistence/jpa/FridgeAdapter.java src/test/java/com/gastromind/api/application/usecases/ResolveAuthenticatedHouseholdContextUseCaseTest.java
git commit -m "feat: add authenticated household fridge context resolver"
```

### Task 2: Implement `/fridges/me` use cases and controller endpoints

**Files:**
- Create: `src/main/java/com/gastromind/api/application/usecases/GetMyFridgeUseCase.java`
- Create: `src/main/java/com/gastromind/api/application/usecases/CreateMyFridgeUseCase.java`
- Create: `src/main/java/com/gastromind/api/application/usecases/UpdateMyFridgeUseCase.java`
- Create: `src/main/java/com/gastromind/api/application/usecases/DeleteMyFridgeUseCase.java`
- Modify: `src/main/java/com/gastromind/api/infrastructure/adapters/in/rest/controllers/FridgeController.java`
- Test: `src/test/java/com/gastromind/api/infrastructure/adapters/in/rest/controllers/FridgeControllerMeSecurityTest.java`

- [ ] **Step 1: Write the failing tests**
```java
@Test void memberCanGetMyFridge_butCannotMutateMyFridge() {}
@Test void ownerCanCreateUpdateDeleteMyFridge() {}
@Test void adminCanUseIdCrud() {}
```

- [ ] **Step 2: Run tests to verify they fail**
Run: `./mvnw -Dtest=FridgeControllerMeSecurityTest test`
Expected: FAIL with missing `/me` endpoints and security mismatches.

- [ ] **Step 3: Write minimal implementation**
```java
@GetMapping("/me") @PreAuthorize("isAuthenticated()")
@PutMapping("/me") @PreAuthorize("hasRole('OWNER')")
```

- [ ] **Step 4: Run tests to verify they pass**
Run: `./mvnw -Dtest=FridgeControllerMeSecurityTest test`
Expected: PASS.

- [ ] **Step 5: Commit**
```bash
git add src/main/java/com/gastromind/api/application/usecases/GetMyFridgeUseCase.java src/main/java/com/gastromind/api/application/usecases/CreateMyFridgeUseCase.java src/main/java/com/gastromind/api/application/usecases/UpdateMyFridgeUseCase.java src/main/java/com/gastromind/api/application/usecases/DeleteMyFridgeUseCase.java src/main/java/com/gastromind/api/infrastructure/adapters/in/rest/controllers/FridgeController.java src/test/java/com/gastromind/api/infrastructure/adapters/in/rest/controllers/FridgeControllerMeSecurityTest.java
git commit -m "feat: add owner-scoped fridge me endpoints"
```

### Task 3: Implement `/fridge-items/me` use cases and controller endpoints

**Files:**
- Create: `src/main/java/com/gastromind/api/application/usecases/ListMyFridgeItemsUseCase.java`
- Create: `src/main/java/com/gastromind/api/application/usecases/CreateMyFridgeItemUseCase.java`
- Create: `src/main/java/com/gastromind/api/application/usecases/UpdateMyFridgeItemUseCase.java`
- Create: `src/main/java/com/gastromind/api/application/usecases/DeleteMyFridgeItemUseCase.java`
- Create: `src/main/java/com/gastromind/api/application/usecases/ConsumeMyFridgeItemUseCase.java`
- Create: `src/main/java/com/gastromind/api/application/usecases/MarkMyFridgeItemConsumedUseCase.java`
- Create: `src/main/java/com/gastromind/api/application/usecases/ListMyExpiringFridgeItemsUseCase.java`
- Create: `src/main/java/com/gastromind/api/application/usecases/ListMyFridgeItemsByCategoryUseCase.java`
- Modify: `src/main/java/com/gastromind/api/infrastructure/adapters/in/rest/controllers/FridgeItemController.java`
- Test: `src/test/java/com/gastromind/api/infrastructure/adapters/in/rest/controllers/FridgeItemControllerMeSecurityTest.java`

- [ ] **Step 1: Write failing tests**
```java
@Test void memberCanCrudMyFridgeItems() {}
@Test void operationsAreScopedToOwnFridge() {}
```

- [ ] **Step 2: Run tests to verify failure**
Run: `./mvnw -Dtest=FridgeItemControllerMeSecurityTest test`
Expected: FAIL because `/me` item routes are missing.

- [ ] **Step 3: Write minimal implementation**
```java
@GetMapping("/me") @PreAuthorize("isAuthenticated()")
@PostMapping("/me") @PreAuthorize("isAuthenticated()")
```

- [ ] **Step 4: Run tests to verify pass**
Run: `./mvnw -Dtest=FridgeItemControllerMeSecurityTest test`
Expected: PASS.

- [ ] **Step 5: Commit**
```bash
git add src/main/java/com/gastromind/api/application/usecases/ListMyFridgeItemsUseCase.java src/main/java/com/gastromind/api/application/usecases/CreateMyFridgeItemUseCase.java src/main/java/com/gastromind/api/application/usecases/UpdateMyFridgeItemUseCase.java src/main/java/com/gastromind/api/application/usecases/DeleteMyFridgeItemUseCase.java src/main/java/com/gastromind/api/application/usecases/ConsumeMyFridgeItemUseCase.java src/main/java/com/gastromind/api/application/usecases/MarkMyFridgeItemConsumedUseCase.java src/main/java/com/gastromind/api/application/usecases/ListMyExpiringFridgeItemsUseCase.java src/main/java/com/gastromind/api/application/usecases/ListMyFridgeItemsByCategoryUseCase.java src/main/java/com/gastromind/api/infrastructure/adapters/in/rest/controllers/FridgeItemController.java src/test/java/com/gastromind/api/infrastructure/adapters/in/rest/controllers/FridgeItemControllerMeSecurityTest.java
git commit -m "feat: add member-scoped fridge item me endpoints"
```

### Task 4: Restrict global routes to admin and verify regressions

**Files:**
- Modify: `src/main/java/com/gastromind/api/infrastructure/adapters/in/rest/controllers/FridgeController.java`
- Modify: `src/main/java/com/gastromind/api/infrastructure/adapters/in/rest/controllers/FridgeItemController.java`
- Test: `src/test/java/com/gastromind/api/infrastructure/adapters/in/rest/controllers/AdminGlobalFridgeAccessTest.java`

- [ ] **Step 1: Write failing test**
```java
@Test void nonAdminCannotUseGlobalFridgeOrFridgeItemRoutes() {}
```

- [ ] **Step 2: Run test to verify it fails**
Run: `./mvnw -Dtest=AdminGlobalFridgeAccessTest test`
Expected: FAIL due to currently open global routes.

- [ ] **Step 3: Write minimal implementation**
```java
@PreAuthorize("hasRole('ADMIN')")
```

- [ ] **Step 4: Run tests to verify pass**
Run: `./mvnw -Dtest=AdminGlobalFridgeAccessTest test`
Expected: PASS.

- [ ] **Step 5: Commit**
```bash
git add src/main/java/com/gastromind/api/infrastructure/adapters/in/rest/controllers/FridgeController.java src/main/java/com/gastromind/api/infrastructure/adapters/in/rest/controllers/FridgeItemController.java src/test/java/com/gastromind/api/infrastructure/adapters/in/rest/controllers/AdminGlobalFridgeAccessTest.java
git commit -m "feat: restrict global fridge routes to admin"
```

### Task 5: Full verification and docs update

**Files:**
- Modify: `docs/superpowers/specs/2026-04-09-fridge-me-flow-design.md` (if behavioral notes need correction after implementation)

- [ ] **Step 1: Run targeted tests**
Run: `./mvnw -Dtest=ResolveAuthenticatedHouseholdContextUseCaseTest,FridgeControllerMeSecurityTest,FridgeItemControllerMeSecurityTest,AdminGlobalFridgeAccessTest test`
Expected: PASS.

- [ ] **Step 2: Run project verification**
Run: `./mvnw test`
Expected: PASS with zero failing tests.

- [ ] **Step 3: Run lint/diagnostics check**
Run: Cursor `ReadLints` for touched files.
Expected: No new lint errors.

- [ ] **Step 4: Final commit (only if requested)**
```bash
git add .
git commit -m "feat: implement household-scoped fridge me flow and admin id crud"
```
