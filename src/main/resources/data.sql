-- Habilitar extensión para UUIDs si no existe
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Semilla idempotente (WHERE NOT EXISTS). Los UNIQUE en entidades JPA evitan duplicados lógicos en runtime.
-- Si Hibernate acaba de crear la tabla, ON CONFLICT(name) puede fallar hasta que exista el índice único.

-- 1. HOUSEHOLD (una sola fila demo por nombre)
INSERT INTO household (id, name)
SELECT gen_random_uuid(), 'Familia GastroMind'
WHERE NOT EXISTS (SELECT 1 FROM household h WHERE h.name = 'Familia GastroMind' LIMIT 1);

-- 2. CATEGORIES
INSERT INTO category (id, name)
SELECT gen_random_uuid(), v.name
FROM (VALUES
    ('Lácteos y Huevos'),
    ('Carnes y Aves'),
    ('Frutas y Verduras'),
    ('Despensa'),
    ('Congelados')
) AS v(name)
WHERE NOT EXISTS (SELECT 1 FROM category c WHERE c.name = v.name);

-- 3. ALLERGENS (Los 14 obligatorios de la UE)
INSERT INTO allergen (id, name)
SELECT gen_random_uuid(), v.name
FROM (VALUES
    ('Gluten'),
    ('Lactosa'),
    ('Crustáceos'),
    ('Huevos'),
    ('Pescado'),
    ('Cacahuetes'),
    ('Soja'),
    ('Frutos de cáscara'),
    ('Apio'),
    ('Mostaza'),
    ('Sésamo'),
    ('Sulfitos'),
    ('Altramuces'),
    ('Moluscos')
) AS v(name)
WHERE NOT EXISTS (SELECT 1 FROM allergen a WHERE a.name = v.name);

-- 4. UNITS
INSERT INTO unit (id, name)
SELECT gen_random_uuid(), v.name
FROM (VALUES
    ('Gramos'),
    ('Mililitros'),
    ('Unidades'),
    ('Kilogramos'),
    ('Litros')
) AS v(name)
WHERE NOT EXISTS (SELECT 1 FROM unit u WHERE u.name = v.name);

-- 5. USERS
INSERT INTO users (id, name, email, password, role, household_id)
SELECT
    gen_random_uuid(),
    'admin',
    'gastromind@gmail.com',
    '$2a$10$fS4mC6.L7SInuT9Nn6/m6.yD6j7kHhXkZq7QGzR4L6y5N6/m6.yD6',
    0,
    (SELECT id FROM household WHERE name = 'Familia GastroMind' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM users u WHERE u.email = 'gastromind@gmail.com' LIMIT 1);

-- 6. PRODUCTS
INSERT INTO product (id, name, is_essential, category_id)
SELECT gen_random_uuid(), 'Leche Entera', true,
       (SELECT id FROM category WHERE name = 'Lácteos y Huevos' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product p WHERE p.name = 'Leche Entera');

INSERT INTO product (id, name, is_essential, category_id)
SELECT gen_random_uuid(), 'Pechuga de Pollo', true,
       (SELECT id FROM category WHERE name = 'Carnes y Aves' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product p WHERE p.name = 'Pechuga de Pollo');

-- 7. PRODUCT_ALLERGENS
INSERT INTO product_allergens (product_id, allergen_id)
SELECT p.id, a.id
FROM product p
JOIN allergen a ON a.name = 'Lactosa'
WHERE p.name = 'Leche Entera'
  AND NOT EXISTS (
    SELECT 1 FROM product_allergens pa
    WHERE pa.product_id = p.id AND pa.allergen_id = a.id
  );
