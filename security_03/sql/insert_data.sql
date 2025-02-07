
INSERT INTO user_security (email, pwd) VALUES 
('admin@jsm.com', 'abc'),
('user@jsm.com', '123');

INSERT INTO roles_security (role_name, description, user_id) VALUES 
('ADMIN', 'Administrador del sistema', (SELECT id FROM user_security WHERE email = 'admin@jsm.com')),
('USER', 'Usuario estándar', (SELECT id FROM user_security WHERE email = 'user@jsm.com')),
('SUPERVISOR', 'Supervisor con permisos intermedios', (SELECT id FROM user_security WHERE email = 'admin@jsm.com'));
