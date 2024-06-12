use blog;
alter table categories add user_id bigint not null;
-- 下面这行中的 '#' 替换成分类的创建者 id
update categories set user_id = #;
alter table categories add foreign key(user_id) references users(id);
