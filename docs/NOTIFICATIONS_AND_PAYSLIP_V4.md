# V4: Announcement read receipts and downloadable payslips

## Announcement notifications

- Publishing an announcement creates one inbox notification for each user matching the selected audience (or every user for `ALL`).
- Each notification is associated with its announcement and its read state is per user.
- `GET /api/notifications/unread-count` returns the authenticated user's unread count.
- `PATCH /api/notifications/announcement/{announcementId}/read` marks only that user's announcement notification as read.
- `PATCH /api/notifications/{id}/read` continues to mark an individual inbox item as read.
- The frontend polls unread count every 10 seconds and refreshes immediately after read/publish actions. The unread indicator is shown on both Announcements and Notifications navigation items, and unread announcements appear on the dashboard.

## Payslip PDF

- Generating a payslip immediately downloads a formatted PDF; existing payroll history rows also have a Download PDF action.
- The PDF includes employee identity, role, department, joining date, pay period, earnings, deductions, gross pay, net pay, amount in words, and authorization footer.
- The logo is currently a generated NexusHR monogram. The company address, HR email, and website in the PDF are development placeholders and must be replaced with the organization's verified legal/company details before real payroll use.
- Current salary calculations are illustrative demo calculations, not statutory payroll or tax advice. Validate PF, TDS, allowances, and all deductions with the organization's payroll/accounting policy before production use.

## Database

- V4 migration adds nullable `announcement_id` to notifications with a foreign key to announcements and indexes for unread queries.
- Local H2 development uses Hibernate `ddl-auto=update`; production PostgreSQL uses Flyway migrations.
