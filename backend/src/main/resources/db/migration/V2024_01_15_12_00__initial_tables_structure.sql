CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       username VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL
);

CREATE TABLE ingredients (
                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             name VARCHAR(255) NOT NULL,
                             quantity DOUBLE PRECISION NOT NULL,
                             unit VARCHAR(100) NOT NULL,
                             expiration_date DATE,
                             calories DOUBLE PRECISION,
                             protein DOUBLE PRECISION,
                             fat DOUBLE PRECISION,
                             carbohydrates DOUBLE PRECISION
);

CREATE TABLE dishes (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        name VARCHAR(255) NOT NULL,
                        calories DOUBLE PRECISION,
                        carbohydrates DOUBLE PRECISION,
                        fat DOUBLE PRECISION,
                        protein DOUBLE PRECISION,
                        prepared_at TIMESTAMP
);

CREATE TABLE meals (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       date_time TIMESTAMP,
                       meal_type VARCHAR(255)
);

CREATE TABLE recipes (
                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         name VARCHAR(255) NOT NULL,
                         description VARCHAR(255),
                         cooking_time_minutes INT NOT NULL,
                         instructions TEXT NOT NULL
);

CREATE TABLE dish_ingredients (
                                  dish_id UUID NOT NULL,
                                  ingredient_id UUID NOT NULL,
                                  PRIMARY KEY (dish_id, ingredient_id),
                                  FOREIGN KEY (dish_id) REFERENCES dishes(id),
                                  FOREIGN KEY (ingredient_id) REFERENCES ingredients(id)
);

CREATE TABLE meal_dishes (
                             meal_id UUID NOT NULL,
                             dish_id UUID NOT NULL,
                             PRIMARY KEY (meal_id, dish_id),
                             FOREIGN KEY (meal_id) REFERENCES meals(id),
                             FOREIGN KEY (dish_id) REFERENCES dishes(id)
);

CREATE TABLE recipe_dishes (
                               recipe_id UUID NOT NULL,
                               dish_id UUID NOT NULL,
                               PRIMARY KEY (recipe_id, dish_id),
                               FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
                               FOREIGN KEY (dish_id) REFERENCES dishes(id) ON DELETE CASCADE
);

