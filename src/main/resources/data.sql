-- Habilitar extensión para UUIDs si no existe
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- 1. HOUSEHOLD
INSERT INTO household (id, name, members_count)
VALUES (gen_random_uuid(), 'Familia GastroMind', 3)
ON CONFLICT DO NOTHING;

-- 2. CATEGORIES (Insertamos todas las principales)
INSERT INTO category (id, name) VALUES
    (gen_random_uuid(), 'Lácteos y Huevos'),
    (gen_random_uuid(), 'Carnes y Aves'),
    (gen_random_uuid(), 'Frutas y Verduras'),
    (gen_random_uuid(), 'Despensa'),
    (gen_random_uuid(), 'Congelados')
ON CONFLICT DO NOTHING;

-- 3. ALLERGENS (Los 14 obligatorios de la UE)
INSERT INTO allergen (id, name) VALUES
    (gen_random_uuid(), 'Gluten'),
    (gen_random_uuid(), 'Lactosa'),
    (gen_random_uuid(), 'Crustáceos'),
    (gen_random_uuid(), 'Huevos'),
    (gen_random_uuid(), 'Pescado'),
    (gen_random_uuid(), 'Cacahuetes'),
    (gen_random_uuid(), 'Soja'),
    (gen_random_uuid(), 'Frutos de cáscara'),
    (gen_random_uuid(), 'Apio'),
    (gen_random_uuid(), 'Mostaza'),
    (gen_random_uuid(), 'Sésamo'),
    (gen_random_uuid(), 'Sulfitos'),
    (gen_random_uuid(), 'Altramuces'),
    (gen_random_uuid(), 'Moluscos')
ON CONFLICT DO NOTHING;

-- 4. UNITS
INSERT INTO unit (id, name) VALUES
    (gen_random_uuid(), 'Gramos'),
    (gen_random_uuid(), 'Mililitros'),
    (gen_random_uuid(), 'Unidades'),
    (gen_random_uuid(), 'Kilogramos')
ON CONFLICT DO NOTHING;

-- 5. USERS (Relacionado con el household creado arriba)
INSERT INTO users (id, name, email, password, role, household_id)
VALUES (
    gen_random_uuid(),
    'admin',
    'gastromind@gmail.com',
    '$2a$10$fS4mC6.L7SInuT9Nn6/m6.yD6j7kHhXkZq7QGzR4L6y5N6/m6.yD6', -- Hash de '12345678'
    0,
    (SELECT id FROM household WHERE name = 'Familia GastroMind' LIMIT 1)
) ON CONFLICT DO NOTHING;

-- 6. PRODUCTS (Relacionados con sus categorías)
INSERT INTO product (id, name, is_essential, category_id) VALUES
(
    gen_random_uuid(),
    'Leche Entera',
    true,
    (SELECT id FROM category WHERE name = 'Lácteos y Huevos' LIMIT 1)
),
(
    gen_random_uuid(),
    'Pechuga de Pollo',
    true,
    (SELECT id FROM category WHERE name = 'Carnes y Aves' LIMIT 1)
) ON CONFLICT DO NOTHING;

-- 7. PRODUCT_ALLERGENS (Relación intermedia)
INSERT INTO product_allergens (product_id, allergen_id) VALUES
(
    (SELECT id FROM product WHERE name = 'Leche Entera' LIMIT 1),
    (SELECT id FROM allergen WHERE name = 'Lactosa' LIMIT 1)
) ON CONFLICT DO NOTHING;