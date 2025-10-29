CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE ingredients (
                             id UUID PRIMARY KEY,
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
                        id UUID PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        calories DOUBLE PRECISION,
                        carbohydrates DOUBLE PRECISION,
                        fat DOUBLE PRECISION,
                        protein DOUBLE PRECISION,
                        prepared_at TIMESTAMP
);

CREATE TABLE meals (
                       id UUID PRIMARY KEY,
                       date_time TIMESTAMP,
                       meal_type VARCHAR(255)
);

CREATE TABLE recipes (
                         id UUID PRIMARY KEY,
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

CREATE TABLE user_ingredients (
                                  id UUID PRIMARY KEY,
                                  user_id UUID NOT NULL,
                                  ingredient_id UUID NOT NULL,

                                  quantity DOUBLE PRECISION NOT NULL,
                                  unit VARCHAR(50) NOT NULL,
                                  expiration_date DATE,
                                  calories DOUBLE PRECISION,
                                  protein DOUBLE PRECISION,
                                  fat DOUBLE PRECISION,
                                  carbohydrates DOUBLE PRECISION,

                                  CONSTRAINT fk_user
                                      FOREIGN KEY(user_id)
                                          REFERENCES users(id)
                                          ON DELETE CASCADE,

                                  CONSTRAINT fk_ingredient
                                      FOREIGN KEY(ingredient_id)
                                          REFERENCES ingredients(id)
                                          ON DELETE CASCADE
);
