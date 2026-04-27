INSERT INTO users (id, email, password, role)
VALUES (1, 'student@test.com', '123', 'STUDENT'),
       (2, 'employer@test.com', '123', 'EMPLOYER');

INSERT INTO employer (id, user_id, company_name)
VALUES (1, 2, 'University Lab');

INSERT INTO student_profile (id, user_id, name, phone, skills)
VALUES (1, 1, 'John Doe', '+123456', 'Java, Spring');

INSERT INTO vacancy (id, title, description, employer_id, created_at)
VALUES (1, 'Java Intern', 'Work with Spring Boot', 1, NOW());

SELECT setval(
               pg_get_serial_sequence('student_profile', 'id'),
               (SELECT MAX(id) FROM student_profile)
       );

SELECT setval(
               pg_get_serial_sequence('users', 'id'),
               (SELECT MAX(id) FROM users)
       );

SELECT setval(
               pg_get_serial_sequence('users', 'id'),
               (SELECT MAX(id) FROM employer)
       );

SELECT setval(
               pg_get_serial_sequence('vacancy', 'id'),
               (SELECT MAX(id) FROM vacancy)
       );


CREATE INDEX idx_vacancy_fts
    ON vacancy
        USING GIN (to_tsvector('russian', title || ' ' || coalesce(description, '')));