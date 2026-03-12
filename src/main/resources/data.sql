--- NIVEL 1: Tablas Maestras ---
INSERT INTO category (id, name) VALUES
('cat_1', 'Carnes'), ('cat_2', 'Lácteos'), ('cat_3', 'Frutas y Verduras'), ('cat_4', 'Despensa')
ON CONFLICT (id) DO NOTHING;

INSERT INTO unit (id, name) VALUES
('un_1', 'Gramos'), ('un_2', 'Mililitros'), ('un_3', 'Unidades'), ('un_4', 'Kilogramos')
ON CONFLICT (id) DO NOTHING;

INSERT INTO allergen (id, name) VALUES
('all_1', 'Gluten'), ('all_2', 'Lactosa'), ('all_3', 'Frutos de cáscara')
ON CONFLICT (id) DO NOTHING;

INSERT INTO store (id, name) VALUES
('st_1', 'Mercadona'), ('st_2', 'Lidl'), ('st_3', 'Carrefour')
ON CONFLICT (id) DO NOTHING;

INSERT INTO household (id, name, members_count) VALUES
('house_1', 'Familia Gastro', 3)
ON CONFLICT (id) DO NOTHING;

--- NIVEL 2: Entidades Principales ---
INSERT INTO users (id, name, email, password, role, household_id) VALUES
('usr_1', 'Gastro Usuario', 'user@gastro.com', '$2a$10$8.UnVuG9HHgffUDAlk8Kn6W.y.n6mS5.Z2B.e.fVfXw98K', 1, 'house_1')
ON CONFLICT (id) DO NOTHING;

INSERT INTO product (id, name, is_essential, category_id) VALUES
('prod_1', 'Leche Entera', true, 'cat_2'),
('prod_2', 'Pechuga Pollo', true, 'cat_1'),
('prod_3', 'Harina Trigo', false, 'cat_4'),
('prod_4', 'Manzanas', true, 'cat_3')
ON CONFLICT (id) DO NOTHING;

INSERT INTO fridge (id, household_id) VALUES
('fr_1', 'house_1')
ON CONFLICT (id) DO NOTHING;

INSERT INTO recipe (id, title, instructions, prep_time_minutes, servings, difficulty, appliance_needed, created_at) VALUES
('rec_1', 'Pollo a la plancha', '1. Salpimentar. 2. Cocinar 5 min por lado.', 15, 2, 'EASY', 'VITROCERAMICA', CURRENT_TIMESTAMP),
('rec_2', 'Batido de Manzana', '1. Trocear. 2. Batir con leche.', 5, 1, 'EASY', 'BATIDORA', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

--- NIVEL 3: Relaciones y Actividad ---

-- Alérgenos (Ojo: aquí el conflicto se mira por la combinación de ambas llaves si es tabla intermedia)
INSERT INTO user_allergens (user_id, allergen_id) VALUES ('usr_1', 'all_2') ON CONFLICT DO NOTHING;
INSERT INTO product_allergens (product_id, allergen_id) VALUES ('prod_3', 'all_1'), ('prod_1', 'all_2') ON CONFLICT DO NOTHING;

INSERT INTO household_appliances (id, household_id, appliance) VALUES
('app_1', 'house_1', 'HORNO'), ('app_2', 'house_1', 'BATIDORA')
ON CONFLICT (id) DO NOTHING;

INSERT INTO fridge_items (id, fridge_id, product_id, quantity, status, expiration_date) VALUES
('fi_1', 'fr_1', 'prod_1', 1000, 'OPENED', '2026-03-10'),
('fi_2', 'fr_1', 'prod_2', 500, 'GOOD', '2026-03-05')
ON CONFLICT (id) DO NOTHING;

INSERT INTO recipe_ingredients (id, recipe_id, product_id, quantity_required, unit_id) VALUES
('ri_1', 'rec_2', 'prod_1', 200, 'un_2'),
('ri_2', 'rec_2', 'prod_4', 2, 'un_3')
ON CONFLICT (id) DO NOTHING;

INSERT INTO user_favorites (id, user_id, recipe_id) VALUES
('fav_1', 'usr_1', 'rec_2')
ON CONFLICT (id) DO NOTHING;

INSERT INTO usual_purchase (id, user_id, product_id, target_quantity) VALUES
('up_1', 'usr_1', 'prod_1', 2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO ticket (id, users_id, store_id, total_amount, purchase_date) VALUES
('tk_1', 'usr_1', 'st_1', 25.50, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

INSERT INTO ticket_items (id, ticket_id, product_id, quantity, price_unit) VALUES
('ti_1', 'tk_1', 'prod_1', 2, 1.20),
('ti_2', 'tk_1', 'prod_2', 1, 5.50)
ON CONFLICT (id) DO NOTHING;