use blog;

DELIMITER $$
create trigger delete_likes_before_deleting_article before delete on articles for each row
BEGIN
  delete from likes where article_id = old.id;
END;
$$
DELIMITER ;
