ALTER TABLE recipes ADD COLUMN IF NOT EXISTS dish_id UUID;

ALTER TABLE recipes
    ADD CONSTRAINT fk_recipes_dish
        FOREIGN KEY (dish_id)
            REFERENCES dishes(id)
            ON DELETE SET NULL;
