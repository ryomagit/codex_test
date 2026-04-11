INSERT INTO genres (name, slug) VALUES
    ('Java', 'java'),
    ('JavaScript', 'javascript'),
    ('Python', 'python'),
    ('Databases', 'databases'),
    ('Architecture', 'architecture'),
    ('Algorithms', 'algorithms'),
    ('Security', 'security'),
    ('DevOps', 'devops')
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    slug = VALUES(slug);
