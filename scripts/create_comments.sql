use blog;

create table comments(id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      user_id BIGINT NOT NULL,
                      article_id BIGINT NOT NULL,
                      content TINYTEXT NOT NULL,
                      response_id BIGINT,
                      FOREIGN KEY(user_id) REFERENCES users(id),
                      FOREIGN KEY(article_id) REFERENCES articles(id),
                      FOREIGN KEY(response_id) REFERENCES comments(id)
                      );
