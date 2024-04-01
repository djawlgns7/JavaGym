alter table member modify m_phonenumber varchar(10) nsot null unique;
alter table trainer modify t_phonenumber varchar(10) not null unique;

ALTER TABLE member
modify COLUMN m_password m_password VARCHAR(60) NOT NULL;

alter table trainer modify t_password varchar(60) not null;
--------

create table enterylog(    -- (지훈추가)
                          e_id int(8) primary key auto_increment,
                          m_id int(4) not null,
                          m_datetime timestamp default (current_timestamp) not null,
                          constraint foreign key(m_id) references member(m_id)
);

ALTER TABLE payment DROP FOREIGN KEY payment_ibfk_1;
ALTER TABLE payment DROP FOREIGN KEY payment_ibfk_2;
ALTER TABLE supplement DROP FOREIGN KEY supplement_ibfk_1;
ALTER TABLE reservation DROP FOREIGN KEY reservation_ibfk_1;
ALTER TABLE reservation DROP FOREIGN KEY reservation_ibfk_2;
ALTER TABLE enterylog DROP FOREIGN KEY enterylog_ibfk_1;



ALTER TABLE payment
    ADD CONSTRAINT FK_payment_member FOREIGN KEY (m_id) REFERENCES member(m_id) ON DELETE CASCADE,
    ADD CONSTRAINT FK_payment_trainer FOREIGN KEY (t_id) REFERENCES trainer(t_id) ON DELETE CASCADE;

ALTER TABLE supplement
    ADD CONSTRAINT FK_supplement_member FOREIGN KEY (m_id) REFERENCES member(m_id) ON DELETE CASCADE;

ALTER TABLE reservation
    ADD CONSTRAINT FK_reservation_member FOREIGN KEY (m_id) REFERENCES member(m_id) ON DELETE CASCADE,
    ADD CONSTRAINT FK_reservation_trainer FOREIGN KEY (t_id) REFERENCES trainer(t_id) ON DELETE CASCADE;

ALTER TABLE enterylog
    ADD CONSTRAINT FK_enterylog_member FOREIGN KEY (m_id) REFERENCES member(m_id) ON DELETE CASCADE;


alter table member
drop m_email2;

alter table member
    change column m_email1 m_email varchar(50) not null unique;