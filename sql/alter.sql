alter table trainer
modify t_name VARCHAR(10) NOT NULL UNIQUE;

alter table purchase
modify p_locker_no INT(3) default null unique;