use blog;
create table favorites(id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       user_id BIGINT NOT NULL,
                       name VARCHAR(50) NOT NULL,
                       FOREIGN KEY(user_id) REFERENCES users(id)); 

create table favorite_articles(favorite_id BIGINT NOT NULL,
                               article_id BIGINT NOT NULL,
                               FOREIGN KEY(favorite_id) REFERENCES favorites(id), 
                               FOREIGN KEY(article_id) REFERENCES articles(id),
                               PRIMARY KEY(favorite_id, article_id));
