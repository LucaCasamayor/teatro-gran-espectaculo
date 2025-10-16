INSERT INTO customer (first_name, last_name, email, registration_date, total_attendances, current_streak, loyalty_free, active)
VALUES
    ('Luca', 'Casamayor', 'luca@example.com', CURRENT_TIMESTAMP, 4, 1, FALSE, TRUE),
    ('María', 'Gómez', 'maria.gomez@example.com', CURRENT_TIMESTAMP, 5, 0, TRUE, TRUE),
    ('Carlos', 'Pérez', 'carlos.perez@example.com', CURRENT_TIMESTAMP, 1, 1, FALSE, TRUE),
    ('Ana', 'Torres', 'ana.torres@example.com', CURRENT_TIMESTAMP, 2, 2, FALSE, TRUE);

INSERT INTO event (title, description, type, start_date_time, end_date_time, status, active)
VALUES
    ('Romeo y Julieta', 'Obra clásica de Shakespeare con escenografía moderna.', 'THEATER', DATEADD('DAY', 1, CURRENT_TIMESTAMP), DATEADD('HOUR', 2, DATEADD('DAY', 1, CURRENT_TIMESTAMP)), 'SCHEDULED', TRUE),
    ('RockFest Córdoba', 'Recital con bandas locales e invitados internacionales.', 'CONCERT', DATEADD('DAY', 3, CURRENT_TIMESTAMP), DATEADD('HOUR', 5, DATEADD('DAY', 3, CURRENT_TIMESTAMP)), 'SCHEDULED', TRUE),
    ('Charla Motivacional 2025', 'Conferencia sobre liderazgo y desarrollo personal.', 'CONFERENCE', DATEADD('DAY', 5, CURRENT_TIMESTAMP), DATEADD('HOUR', 3, DATEADD('DAY', 5, CURRENT_TIMESTAMP)), 'SCHEDULED', TRUE),
    ('La Familia Loca', 'Obra de teatro cómica para toda la familia.', 'THEATER', DATEADD('DAY', 7, CURRENT_TIMESTAMP), DATEADD('HOUR', 2, DATEADD('DAY', 7, CURRENT_TIMESTAMP)), 'SCHEDULED', TRUE),
    ('IndieWave Festival', 'Recital de música indie con múltiples escenarios.', 'CONCERT', DATEADD('DAY', 10, CURRENT_TIMESTAMP), DATEADD('HOUR', 6, DATEADD('DAY', 10, CURRENT_TIMESTAMP)), 'SCHEDULED', TRUE),
    ('Charla TechTalks', 'Conferencia de tecnología con acceso opcional a meet & greet.', 'CONFERENCE', DATEADD('DAY', 12, CURRENT_TIMESTAMP), DATEADD('HOUR', 4, DATEADD('DAY', 12, CURRENT_TIMESTAMP)), 'SCHEDULED', TRUE);


INSERT INTO ticket_option (event_id, name, price, capacity, sold)
VALUES
    (1, 'General', 5000.00, 100, 10),
    (1, 'VIP', 9000.00, 30, 5),
    (4, 'General', 4500.00, 120, 12),
    (4, 'VIP', 8000.00, 25, 4),
    (2, 'Campo', 7000.00, 300, 20),
    (2, 'Platea', 9000.00, 150, 15),
    (2, 'Palco', 12000.00, 40, 8),
    (5, 'Campo', 6000.00, 400, 25),
    (5, 'Platea', 8500.00, 200, 10),
    (5, 'Palco', 11000.00, 30, 5),
    (3, 'General', 4000.00, 200, 18),
    (3, 'Meet & Greet', 7500.00, 50, 5),
    (6, 'General', 5000.00, 180, 10),
    (6, 'Meet & Greet', 9000.00, 40, 8);


INSERT INTO reservation (customer_id, event_id, status, attendee_name, attended_by, created_by_admin, loyalty_free, total, created_at, paid_at, active)
VALUES
    (1, 1, 'PAID', 'Luca Casamayor', 'Admin1', TRUE, FALSE, 10000.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    (2, 2, 'PENDING', 'María Gómez', 'Admin2', TRUE, TRUE, 9000.00, CURRENT_TIMESTAMP, NULL, TRUE),
    (3, 3, 'PAID', 'Carlos Pérez', 'Admin3', TRUE, FALSE, 7500.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    (4, 5, 'PENDING', 'Ana Torres', 'Admin1', TRUE, FALSE, 12000.00, CURRENT_TIMESTAMP, NULL, TRUE);


INSERT INTO reservation_item (reservation_id, ticket_option_id, quantity, unit_price)
VALUES
    (1, 2, 1, 9000.00),
    (1, 1, 1, 5000.00),
    (2, 6, 1, 9000.00),
    (3, 12, 1, 7500.00),
    (4, 9, 2, 6000.00);