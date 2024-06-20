use blog;

DELIMITER $$
create trigger before_deleting_article before delete on articles for each row
BEGIN
  delete from likes where article_id = old.id;
  delete from comments where article_id = old.id;
  delete from favorite_articles where article_id = old.id;
END;
$$

-- create trigger before_deleting_comment before delete on comments for each row
-- BEGIN
--   update comments set response_id = null where response_id = old.id;
-- END;
-- $$

create trigger before_deleting_favorite before delete on favorites for each row
BEGIN
  delete from favorite_articles where favorite_id = old.id;
END;
$$

create trigger before_deleting_user before delete on users for each row
BEGIN
    delete from favorites where user_id = old.id;
    delete from likes where user_id = old.id;
    delete from comments where user_id = old.id;
    delete from categories where user_id = old.id;
    delete from articles where user_id = old.id;
END;
$$
DELIMITER ;
