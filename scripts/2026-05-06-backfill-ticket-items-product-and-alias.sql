-- Backfill seguro para endurecer ticket_items.product_id NOT NULL
-- Ejecutar una sola vez antes de desplegar la restriccion en entornos con datos historicos.

CREATE EXTENSION IF NOT EXISTS "unaccent";

CREATE TABLE IF NOT EXISTS product_alias (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id uuid NOT NULL REFERENCES product(id),
    alias varchar(200) NOT NULL,
    alias_norm varchar(200) NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_product_alias_alias_norm ON product_alias(alias_norm);
CREATE INDEX IF NOT EXISTS idx_product_alias_product_id ON product_alias(product_id);

WITH missing_lines AS (
    SELECT
        ti.id AS ticket_item_id,
        trim(regexp_replace(coalesce(ti.line_product_name, ''), '\s+', ' ', 'g')) AS raw_name,
        lower(trim(regexp_replace(coalesce(ti.line_product_name, ''), '\s+', ' ', 'g'))) AS name_norm
    FROM ticket_items ti
    WHERE ti.product_id IS NULL
),
normalized_lines AS (
    SELECT
        ticket_item_id,
        CASE
            WHEN name_norm = '' THEN 'producto sin identificar'
            ELSE name_norm
        END AS name_norm,
        CASE
            WHEN raw_name = '' THEN 'Producto sin identificar'
            ELSE raw_name
        END AS raw_name
    FROM missing_lines
),
inserted_products AS (
    INSERT INTO product (id, name, is_essential, needs_review, review_note)
    SELECT DISTINCT
        gen_random_uuid(),
        nl.name_norm,
        false,
        true,
        'Creado por backfill de ticket_items sin product_id'
    FROM normalized_lines nl
    WHERE NOT EXISTS (
        SELECT 1 FROM product p WHERE lower(p.name) = nl.name_norm
    )
    RETURNING id, name
),
all_products AS (
    SELECT p.id, lower(p.name) AS name_norm
    FROM product p
),
resolved_lines AS (
    SELECT
        nl.ticket_item_id,
        ap.id AS product_id,
        nl.raw_name,
        nl.name_norm
    FROM normalized_lines nl
    JOIN all_products ap ON ap.name_norm = nl.name_norm
)
UPDATE ticket_items ti
SET product_id = rl.product_id
FROM resolved_lines rl
WHERE ti.id = rl.ticket_item_id
  AND ti.product_id IS NULL;

INSERT INTO product_alias (id, product_id, alias, alias_norm)
SELECT DISTINCT
    gen_random_uuid(),
    rl.product_id,
    rl.raw_name,
    rl.name_norm
FROM (
    SELECT
        trim(regexp_replace(coalesce(ti.line_product_name, ''), '\s+', ' ', 'g')) AS raw_name,
        lower(trim(regexp_replace(coalesce(ti.line_product_name, ''), '\s+', ' ', 'g'))) AS name_norm,
        ti.product_id
    FROM ticket_items ti
    WHERE ti.product_id IS NOT NULL
) rl
WHERE rl.name_norm <> ''
  AND NOT EXISTS (
    SELECT 1 FROM product_alias pa WHERE pa.alias_norm = rl.name_norm
);

ALTER TABLE ticket_items
    ALTER COLUMN product_id SET NOT NULL;
