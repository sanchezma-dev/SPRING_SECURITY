
DROP TABLE IF EXISTS roles_security;
DROP TABLE IF EXISTS user_security;

CREATE TABLE user_security (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    pwd VARCHAR(255) NOT NULL
);

CREATE TABLE roles_security (
    id SERIAL PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL,
    description TEXT,
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user_security(id) ON DELETE CASCADE
);
