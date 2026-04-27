INSERT INTO users (id, email, password, role)
VALUES (1, 'student@test.com', '123', 'STUDENT')
ON CONFLICT (id) DO NOTHING;

INSERT INTO users (id, email, password, role)
VALUES (2, 'employer@test.com', '123', 'EMPLOYER')
ON CONFLICT (id) DO NOTHING;

INSERT INTO employer (id, user_id, company_name)
VALUES (1, 2, 'University Lab')
ON CONFLICT (id) DO NOTHING;

INSERT INTO student_profile (id, user_id, name, phone, skills)
VALUES (1, 1, 'John Doe', '+123456', 'Java, Spring')
ON CONFLICT (id) DO NOTHING;

INSERT INTO vacancy (id, title, description, employer_id, created_at)
VALUES (1, 'Java Intern', 'Work with Spring Boot', 1, NOW())
ON CONFLICT (id) DO NOTHING;

