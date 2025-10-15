
INSERT INTO customer (first_name, last_name, email, registration_date, total_attendances, current_streak, loyalty_free, active)
VALUES
    ('Luca', 'Casamayor', 'luca@example.com', CURRENT_TIMESTAMP, 4, 1, FALSE, TRUE),
    ('María', 'Gómez', 'maria.gomez@example.com', CURRENT_TIMESTAMP, 5, 0, TRUE, TRUE),
    ('Carlos', 'Pérez', 'carlos.perez@example.com', CURRENT_TIMESTAMP, 1, 1, FALSE, TRUE),
    ('Ana', 'Torres', 'ana.torres@example.com', CURRENT_TIMESTAMP, 2, 2, FALSE, TRUE);


INSERT INTO event (title, description, type, start_date_time, end_date_time, status, active)
VALUES
    ('Romeo y Julieta', 'Obra clásica de Shakespeare', 'THEATER', DATEADD('DAY', 1, CURRENT_TIMESTAMP), DATEADD('HOUR', 2, DATEADD('DAY', 1, CURRENT_TIMESTAMP)), 'SCHEDULED', TRUE),
    ('Concierto RockFest', 'Festival de rock con bandas locales', 'CONCERT', DATEADD('DAY', 3, CURRENT_TIMESTAMP), DATEADD('HOUR', 4, DATEADD('DAY', 3, CURRENT_TIMESTAMP)), 'SCHEDULED', TRUE),
    ('Charla Motivacional', 'Conferencia con speakers invitados', 'CONFERENCE', DATEADD('DAY', 5, CURRENT_TIMESTAMP), DATEADD('HOUR', 3, DATEADD('DAY', 5, CURRENT_TIMESTAMP)), 'SCHEDULED', TRUE),
    ('Obra Infantil', 'Espectáculo para niños y familias', 'THEATER', DATEADD('DAY', 7, CURRENT_TIMESTAMP), DATEADD('HOUR', 2, DATEADD('DAY', 7, CURRENT_TIMESTAMP)), 'SCHEDULED', TRUE);


INSERT INTO ticket_option (event_id, name, price, capacity, sold)
VALUES
    (1, 'General', 5000.00, 100, 0),
    (1, 'VIP', 9000.00, 30, 5),
    (2, 'Campo', 6000.00, 200, 10),
    (2, 'Platea', 8000.00, 100, 8),
    (3, 'General', 4000.00, 150, 0),
    (3, 'Meet & Greet', 7000.00, 50, 0),
    (4, 'General', 3000.00, 120, 0),
    (4, 'VIP Familiar', 5000.00, 40, 0);


INSERT INTO reservation (customer_id, event_id, status, attendee_name, attended_by, created_by_admin, loyalty_free, total, created_at, paid_at, active)
VALUES
    (1, 1, 'PAID', 'Luca Casamayor', 'Admin1', TRUE, FALSE, 10000.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    (2, 2, 'PENDING', 'María Gómez', 'Admin2', TRUE, TRUE, 10000.00, CURRENT_TIMESTAMP, NULL, TRUE),
    (3, 3, 'PAID', 'Carlos Pérez', 'Admin3', TRUE, FALSE, 8000.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    (4, 4, 'PENDING', 'Ana Torres', 'Admin1', TRUE, FALSE, 6000.00, CURRENT_TIMESTAMP, NULL, TRUE);


INSERT INTO reservation_item (reservation_id, ticket_option_id, quantity, unit_price)
VALUES
    (1, 1, 2, 5000.00),
    (2, 3, 1, 6000.00),
    (3, 5, 2, 4000.00),
    (4, 7, 2, 3000.00);
