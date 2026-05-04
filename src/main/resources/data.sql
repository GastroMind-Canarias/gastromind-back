-- Habilitar extensiAn para UUIDs si no existe
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE EXTENSION IF NOT EXISTS "unaccent";

-- Semilla idempotente (WHERE NOT EXISTS). Los UNIQUE en entidades JPA evitan duplicados logicos en runtime.
-- Si Hibernate acaba de crear la tabla, ON CONFLICT(name) puede fallar hasta que exista el Andice Anico.

-- 1. HOUSEHOLD (una sola fila demo por nombre)
INSERT INTO household (id, name)
SELECT gen_random_uuid(), 'Familia GastroMind'
WHERE NOT EXISTS (SELECT 1 FROM household h WHERE h.name = 'Familia GastroMind' LIMIT 1);

-- 2. CATEGORIES
INSERT INTO category (id, name)
SELECT gen_random_uuid(), v.name
FROM (VALUES
    ('LActeos y Huevos'),
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
    ('CrustAceos'),
    ('Huevos'),
    ('Pescado'),
    ('Cacahuetes'),
    ('Soja'),
    ('Frutos de cAscara'),
    ('Apio'),
    ('Mostaza'),
    ('SAsamo'),
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
       (SELECT id FROM category WHERE name = 'LActeos y Huevos' LIMIT 1)
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

-- 8. STORES (basicos Espana + Canarias)
INSERT INTO store (id, name, name_norm)
SELECT gen_random_uuid(), v.name, v.name_norm
FROM (VALUES
    ('Mercadona', 'mercadona'),
    ('Carrefour', 'carrefour'),
    ('Lidl', 'lidl'),
    ('Dia', 'dia'),
    ('Alcampo', 'alcampo'),
    ('Eroski', 'eroski'),
    ('Consum', 'consum'),
    ('El Corte Ingles', 'corte ingles el'),
    ('Hipercor', 'hipercor'),
    ('Ahorramas', 'ahorramas'),
    ('Hiperdino', 'hiperdino')
) AS v(name, name_norm)
WHERE NOT EXISTS (SELECT 1 FROM store s WHERE lower(s.name) = lower(v.name));

UPDATE store
SET name_norm = lower(regexp_replace(unaccent(name), '[^a-z0-9 ]', ' ', 'g'))
WHERE name_norm IS NULL;

-- 9. STORE ALIASES utiles
INSERT INTO store_alias (id, store_id, alias, alias_norm)
SELECT gen_random_uuid(), s.id, v.alias, v.alias_norm
FROM (VALUES
    ('LIDL SUPERMERCADOS S.A.U.', 'lidl'),
    ('CARREFOUR MARKET', 'carrefour market'),
    ('SUPERMERCADOS DINO', 'dino'),
    ('HIPER DINO', 'dino hiper'),
    ('EL CORTE INGLES SUPERMERCADO', 'corte ingles el supermercado')
) AS v(alias, alias_norm)
JOIN store s ON lower(s.name) = lower(
    CASE
        WHEN v.alias_norm LIKE 'lidl%' THEN 'Lidl'
        WHEN v.alias_norm LIKE 'carrefour%' THEN 'Carrefour'
        WHEN v.alias_norm LIKE '%dino%' THEN 'Hiperdino'
        ELSE 'El Corte Ingles'
    END
)
WHERE NOT EXISTS (
    SELECT 1
    FROM store_alias sa
    WHERE sa.store_id = s.id AND lower(sa.alias_norm) = lower(v.alias_norm)
);
