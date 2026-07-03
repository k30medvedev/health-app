INSERT INTO users (full_name, email, created_at) VALUES
    ('Ivan Petrov', 'ivan.petrov@example.com', CURRENT_TIMESTAMP),
    ('Maria Sidorova', 'maria.sidorova@example.com', CURRENT_TIMESTAMP);

INSERT INTO analyses (user_id, test_name, result_value, unit, reference_range, taken_at, created_at) VALUES
    (1, 'Hemoglobin', '145', 'g/L', '130-160', CURRENT_DATE, CURRENT_TIMESTAMP),
    (1, 'Glucose', '5.1', 'mmol/L', '3.9-5.6', CURRENT_DATE, CURRENT_TIMESTAMP),
    (2, 'Cholesterol', '4.8', 'mmol/L', '<5.2', CURRENT_DATE, CURRENT_TIMESTAMP);
