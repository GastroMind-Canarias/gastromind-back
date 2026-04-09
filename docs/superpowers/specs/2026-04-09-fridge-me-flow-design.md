# Fridge `/me` Flow and Admin-by-Id CRUD Design

## Context

The project already applies a `households/me` pattern where authenticated users operate on their own household inferred from JWT identity, while administrators keep global access by id.

Current `fridges` and `fridge-items` controllers still expose mostly global CRUD patterns. This design aligns fridge flows with the existing household pattern and preserves hexagonal architecture boundaries.

## Goals

- Add a `/me` flow for fridge and fridge items based on authenticated user household.
- Allow all household members to list/create/update/delete fridge items in their own household fridge.
- Restrict fridge creation/update/deletion in `/me` to household owners only.
- Keep administrators with unrestricted CRUD on fridges by id.
- Keep architecture hexagonal and introduce use cases where orchestration is currently controller/service-bound.

## Non-goals

- No schema redesign of existing fridge/fridge-item entities.
- No changes to authentication mechanism (JWT parsing and principal resolution remain as currently implemented).
- No changes to unrelated domains.

## Roles and Authorization Rules

- `ROLE_ADMIN`
  - Full CRUD on `/api/v1/fridges/{id}`.
  - Full global access for operational endpoints in `fridge-items` by explicit ids/fridge ids.
- `ROLE_OWNER`
  - Can read own fridge via `/api/v1/fridges/me`.
  - Can create/update/delete own fridge via `/api/v1/fridges/me`.
  - Can CRUD fridge items in own fridge via `/api/v1/fridge-items/me/**`.
- `ROLE_MEMBER`
  - Can read own fridge via `/api/v1/fridges/me`.
  - Cannot create/update/delete own fridge.
  - Can CRUD fridge items in own fridge via `/api/v1/fridge-items/me/**`.

## API Design

## Fridge Controller

- `GET /api/v1/fridges` -> admin only (global list).
- `GET /api/v1/fridges/{id}` -> admin only.
- `POST /api/v1/fridges/{id?}` existing global create route remains admin only.
- `PUT /api/v1/fridges/{id}` -> admin only.
- `DELETE /api/v1/fridges/{id}` -> admin only.

New `/me` routes:

- `GET /api/v1/fridges/me` -> authenticated household member/owner/admin with household.
- `POST /api/v1/fridges/me` -> owner only for own household.
- `PUT /api/v1/fridges/me` -> owner only for own household fridge.
- `DELETE /api/v1/fridges/me` -> owner only for own household fridge.

Behavioral notes:

- Household id is resolved from authenticated user loaded by principal from JWT.
- Own fridge lookup uses `FridgeRepository.findByHouseholdId(householdId)`.
- If multiple fridges exist for same household unexpectedly, use first deterministic result and keep behavior documented.

## Fridge Item Controller

Global endpoints:

- Existing global routes stay available only to admins (support/ops mode).

New `/me` routes (authenticated household context):

- `GET /api/v1/fridge-items/me` -> list all items from own household fridge.
- `POST /api/v1/fridge-items/me` -> create item in own household fridge.
- `PUT /api/v1/fridge-items/me/{itemId}` -> update own household item.
- `PUT /api/v1/fridge-items/me/{itemId}/consume` -> partial consume.
- `PUT /api/v1/fridge-items/me/{itemId}/mark-consumed` -> consume all.
- `DELETE /api/v1/fridge-items/me/{itemId}` -> delete own household item.
- `GET /api/v1/fridge-items/me/expiring?days=7` -> expiring items from own fridge.
- `GET /api/v1/fridge-items/me/category/{categoryId}` -> own fridge filtered by category.

## Hexagonal Architecture Changes

## Application Use Cases (new)

Fridge `/me` orchestration:

- `GetMyFridgeUseCase`
- `CreateMyFridgeUseCase`
- `UpdateMyFridgeUseCase`
- `DeleteMyFridgeUseCase`

Fridge item `/me` orchestration:

- `ListMyFridgeItemsUseCase`
- `CreateMyFridgeItemUseCase`
- `UpdateMyFridgeItemUseCase`
- `DeleteMyFridgeItemUseCase`
- `ConsumeMyFridgeItemUseCase`
- `MarkMyFridgeItemConsumedUseCase`
- `ListMyExpiringFridgeItemsUseCase`
- `ListMyFridgeItemsByCategoryUseCase`

Shared identity/context helper:

- `ResolveAuthenticatedHouseholdUseCase` (or an equivalent private shared application component) to:
  - Resolve current user by authentication principal.
  - Ensure household membership exists.
  - Resolve current fridge for that household.

## Ports and Services

- Reuse existing output ports:
  - `UserRepository`
  - `FridgeRepository`
  - `FridgeItemRepository`
- Reuse existing domain services implementations where useful:
  - `FridgeServiceImpl`
  - `FridgeItemServiceImpl`

Controller responsibilities remain thin:

- Authenticate/authorize via annotations and minimal role checks.
- Delegate business flow to use cases.
- Map DTO <-> domain.

## Error Handling

- User without household in `/me` flow -> `ForbiddenException`.
- Household exists but fridge not found -> `NotFoundException`.
- `/me` fridge mutation attempted by non-owner -> `ForbiddenException`.
- Item mutation in `/me` where item does not belong to own fridge -> `ForbiddenException`.
- Invalid payloads continue using validation annotations and existing exception handling.

## Testing Strategy

- Controller/integration authorization tests:
  - Member can CRUD fridge items in `/me`.
  - Member cannot mutate fridge in `/me`.
  - Owner can mutate fridge in `/me`.
  - Admin can perform CRUD on `/fridges/{id}`.
- Application use case tests:
  - Household resolution from authentication principal.
  - Correct fridge ownership scoping for item operations.
  - Proper exception branches for missing household/fridge.
- Regression checks:
  - Existing admin global behavior remains functional.
  - Existing household and auth workflows remain unchanged.

## Rollout Notes

- Keep backward compatibility for existing admin global endpoints.
- Document new `/me` routes in Swagger annotations.
- Prefer incremental implementation in this order:
  1. Authenticated household resolution utility/use case.
  2. Fridge `/me` use cases + controller endpoints.
  3. Fridge-item `/me` use cases + controller endpoints.
  4. Authorization hardening of global routes to admin-only.
  5. Tests.

## Ambiguity Resolution

- For owner checks in `/me`, only `ROLE_OWNER` should mutate own fridge. `ROLE_ADMIN` uses id-based global routes by design.
- `/me` flows rely on household-linked fridge. If none exists, endpoint returns not found and does not auto-create.
