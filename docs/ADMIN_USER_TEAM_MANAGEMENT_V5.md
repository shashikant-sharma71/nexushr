# Admin User & Team Management (V5)

## Authorization
All `/api/admin/users/**` endpoints require the ADMIN role. HR, MANAGER and EMPLOYEE accounts must receive HTTP 403. Requests without a valid authenticated session must not receive protected data.

## Supported operations
- `GET /api/admin/users`: list user accounts with limited employee/team summary fields.
- `POST /api/admin/users`: create an account and associated employee profile.
- `PUT /api/admin/users/{id}/role`: change a user's role, subject to last-admin and manager-team safeguards.
- `PATCH /api/admin/users/{id}/active`: activate/deactivate, subject to safeguards.
- `PUT /api/admin/users/{id}/team`: assign or unassign an employee's manager.
- `PUT /api/admin/users/{id}/password`: admin password reset.
- `GET /api/admin/users/audit`: read recent admin audit entries.

## Operational note
This version uses an administrator-provided initial password. It does not send email invitations or enforce a first-login password change. Do not describe email-based onboarding as implemented until an email provider, expiring invitation tokens, and forced password-change flow have been built and tested.

## Verification cases
1. ADMIN can execute the operations above.
2. HR, MANAGER and EMPLOYEE receive HTTP 403 for every `/api/admin/users/**` operation.
3. Unauthenticated requests receive HTTP 401.
4. The last active ADMIN cannot be demoted or deactivated.
5. A manager with assigned employees cannot be deactivated/demoted until reassignment.
6. Password reset invalidates previously issued access tokens after the token-version safeguard is enabled.
