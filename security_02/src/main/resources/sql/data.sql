INSERT INTO user_security (email, pwd, rol)
SELECT 'jsm_admin@jsm.com', 'abcd', 'admin'
WHERE NOT EXISTS (
    SELECT 1 FROM user_security WHERE email = 'jsm_admin@jsm.com'
);

INSERT INTO user_security (email, pwd, rol)
SELECT 'jsm_user@jsm.com', 'abcdef', 'user'
WHERE NOT EXISTS (
    SELECT 1 FROM user_security WHERE email = 'jsm_user@jsm.com'
);
