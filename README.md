<!-- 로고 -->
<h1 align="center" style="display: block; font-size: 2.5em; font-weight: bold; margin-block-start: 1em; margin-block-end: 1em;">
<img align="center" src="https://github.com/djawlgns7/JavaGym/raw/readme/readmeResources/image/logo_without_background.png" style="width:70%;height:70%"/>
</h1>

## 💪발표 자료
- [발표 자료(PPT)](https://github.com/djawlgns7/JavaGym/raw/readme/readmeResources/presentationFile/Javagym.pptx)
- [발표 자료(PDF)](https://github.com/djawlgns7/JavaGym/raw/readme/readmeResources/presentationFile/Javagym.pdf)
- [사용한 기술 상세](링크 추가 예정)

## 💪시연 영상
- 추후 유튜브 링크 추가 예정

## ![](https://raw.githubusercontent.com/aregtech/areg-sdk/master/docs/img/pin.svg)목차
- [소개](#소개)
- [사용 기술 및 툴 소개](#사용-기술-및-툴-소개)
- [실행하기 위해 필요한 사항들](#실행하기-위해-필요한-사항들)

## 💪소개

- **JavaGym**은 헬스장을 이용하는 모든 사람들을 위한 키오스크입니다.  
- **회원**들은 로그인만 하면 헬스장 입장, 헬스장의 상품 구매, PT 예약 등의 기능을 이용 가능합니다.  
- **트레이너**는 자신이 담당하는 회원들의 목록과 예약 목록을 볼 수 있습니다. 예약 등록 및 수정, 삭제 또한 가능합니다.  
- **관리자**는 헬스장에 등록된 트레이너와 회원들의 정보 열람 및 수정이 가능합니다. 수동으로 트레이너와 회원을 등록하는것 또한 가능합니다. 그리고 회원들의 출입 로그를 확인할 수 있습니다.

## 💪사용 기술 및 툴 소개

### Front
![JavaFX-17](https://img.shields.io/badge/JavaFX-FFA500.svg?&style=for-the-badge&logo=Java&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-1572B6.svg?&style=for-the-badge&logo=CSS3&logoColor=white)
![Scenebuilder](https://img.shields.io/badge/SceneBuiler-F0AD4E.svg?&style=for-the-badge&logo=scenebuilder&logoColor=white)

### Back
![Java-17](https://img.shields.io/badge/Java-007396.svg?&style=for-the-badge&logo=Java&logoColor=white)
![Lombok](https://img.shields.io/badge/Lombok-DA2128.svg?&style=for-the-badge&logo=lombok&logoColor=white)
![coolSMS](https://img.shields.io/badge/coolSMS-34DA50.svg?&style=for-the-badge&logo=imessage&logoColor=white)
![jbcrypt](https://img.shields.io/badge/jbcrypt-FE5F50.svg?&style=for-the-badge&logo=letsencrypt&logoColor=white)


### DB
![MySQL](https://img.shields.io/badge/MySQL-4479A1.svg?&style=for-the-badge&logo=MySQL&logoColor=white)
![HikariCP](https://img.shields.io/badge/HikariCP-FFFFFF.svg?&style=for-the-badge&logo=Java&logoColor=black)

### Tools
![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?&style=for-the-badge&logo=Gradle&logoColor=white)
![Git](https://img.shields.io/badge/Git-F05032.svg?&style=for-the-badge&logo=Git&logoColor=white)
![Github](https://img.shields.io/badge/GitHub-181717.svg?&style=for-the-badge&logo=GitHub&logoColor=white)
![discord](https://img.shields.io/badge/Discord-5865F2.svg?&style=for-the-badge&logo=discord&logoColor=white)
![intellijidea](https://img.shields.io/badge/IntelliJ%20IDEA-000000.svg?&style=for-the-badge&logo=intellijidea&logoColor=white)
![Figma](https://img.shields.io/badge/Figma-F24E1E.svg?&style=for-the-badge&logo=Figma&logoColor=white)
![kakaotalk](https://img.shields.io/badge/KakaoTalk-F7E600.svg?&style=for-the-badge&logo=kakaotalk&logoColor=black)

## 💪실행하기 위해 필요한 사항들

### 1. 데이터베이스 생성 및 데이터 추가
<details>
  <summary><h3>데이터베이스에 추가해야 하는 값들</h3></summary>
  
  <details>
    <summary><h3>테이블 생성문</h3></summary>
    
    <pre><code class="language-sql">
    -- 회원 테이블
    CREATE TABLE member(
    m_no INT(4) PRIMARY KEY AUTO_INCREMENT,
    m_name VARCHAR(10) NOT NULL,
    m_pw VARCHAR(60) NOT NULL,
    m_sex ENUM('M', 'F') NOT NULL,
    m_email VARCHAR(60) NOT NULL UNIQUE,
    m_birthdate DATE NOT NULL,
    m_phone VARCHAR(8) NOT NULL UNIQUE,
    m_enrollment DATE DEFAULT (CURRENT_DATE) NOT NULL
    );
    
    ALTER TABLE member AUTO_INCREMENT = 1000;
    
    -- 트레이너 테이블
    CREATE TABLE trainer(
    t_no INT(4) PRIMARY KEY AUTO_INCREMENT,
    t_id VARCHAR(20) NOT NULL UNIQUE,
    t_pw VARCHAR(60) NOT NULL,
    t_name VARCHAR(10) NOT NULL UNIQUE,
    t_phone VARCHAR(10) NOT NULL UNIQUE,
    t_birthdate DATE NOT NULL,
    t_height decimal(5, 1) NOT NULL,
    t_weight decimal(5, 1) NOT NULL,
    t_photo MEDIUMBLOB,
    t_sex ENUM('M', 'F') NOT NULL,
    t_working_hour ENUM('AM', 'PM') NOT NULL
    );
    
    ALTER TABLE trainer AUTO_INCREMENT = 9000;
    
    -- 예약 테이블
    CREATE TABLE reservation(
    r_no INT(8) PRIMARY KEY AUTO_INCREMENT,
    m_no INT(4) NOT NULL,
    t_no INT(4) NOT NULL,
    r_date DATE NOT NULL,
    r_time INT(2) NOT NULL,
    CONSTRAINT FOREIGN KEY(m_no) REFERENCES member(m_no) ON DELETE CASCADE,
    CONSTRAINT FOREIGN KEY(t_no) REFERENCES trainer(t_no) ON DELETE CASCADE
    );
    
    -- 입장 기록 테이블
    CREATE TABLE entry_log(
    e_no INT(8) PRIMARY KEY AUTO_INCREMENT,
    m_no INT(4) NOT NULL,
    m_datetime TIMESTAMP DEFAULT (CURRENT_TIMESTAMP) NOT NULL,
    CONSTRAINT FOREIGN KEY(m_no) REFERENCES member(m_no) ON DELETE CASCADE
    );
    
    -- 관리자 테이블
    CREATE TABLE admin(
    a_id VARCHAR(20) PRIMARY KEY,
    a_pw VARCHAR(60)
    );
    
    -- 상품 정보 테이블
    CREATE TABLE item(
    i_no INT(3) PRIMARY KEY AUTO_INCREMENT,
    i_type ENUM('gym_ticket', 'PT_ticket', 'clothes', 'locker'),
    i_detail INT(3),
    i_price INT(7) NOT NULL
    );
    
    -- 구매 기록 테이블
    CREATE TABLE purchase(
    p_no INT(8) PRIMARY KEY AUTO_INCREMENT,
    i_no INT(3) NOT NULL,
    m_no INT(4) NOT NULL,
    t_no INT(4) DEFAULT NULL,
    p_locker_no INT(3) DEFAULT NULL UNIQUE,
    p_datetime TIMESTAMP DEFAULT (CURRENT_TIMESTAMP) NOT NULL,
    p_remain INT(3) NOT NULL,
    CONSTRAINT FOREIGN KEY(i_no) REFERENCES item(i_no) ON DELETE CASCADE,
    CONSTRAINT FOREIGN KEY(m_no) REFERENCES member(m_no) ON DELETE CASCADE,
    CONSTRAINT FOREIGN KEY(t_no) REFERENCES trainer(t_no) ON DELETE CASCADE
    );
    </code></pre>
  </details>
  <details>
    <summary><h3>insert문</h3></summary>
    
    <pre><code class="language-sql">
    -- admin 데이터
    INSERT INTO admin VALUES
    ('admin', '$2a$10$BtWNTnqX3dEI7jPSoBJyEOFSZRJ4kAhdCFOFB32uKfM0j.y/2HuQ6');
    
    -- item 데이터
    INSERT INTO item (i_type, i_detail, i_price) VALUES
    ('PT_ticket', 10, 700000),  -- PT 10회 이용권
    ('PT_ticket', 20, 1300000),  -- PT 20회 이용권
    ('PT_ticket', 30, 1800000);  -- PT 30회 이용권
    
    INSERT INTO item (i_type, i_detail, i_price) VALUES
    ('gym_ticket', 1, 20000),    -- 체육관 하루 이용권
    ('gym_ticket', 30, 50000),   -- 체육관 30일 이용권
    ('gym_ticket', 90, 150000),  -- 체육관 90일 이용권
    ('gym_ticket', 180, 280000), -- 체육관 180일 이용권
    ('gym_ticket', 360, 510000); -- 체육관 360일 이용권
    
    INSERT INTO item (i_type, i_detail, i_price) VALUES
    ('clothes', 30, 7000),   -- 의류 30일 대여
    ('clothes', 90, 18000),  -- 의류 90일 대여
    ('clothes', 180, 31000), -- 의류 180일 대여
    ('clothes', 360, 52000); -- 의류 360일 대여
    
    INSERT INTO item (i_type, i_detail, i_price) VALUES
    ('locker', 30, 7000),   -- 락커 30일 대여
    ('locker', 90, 18000),   -- 락커 90일 대여
    ('locker', 180, 31000), -- 락커 180일 대여
    ('locker', 360, 52000); -- 락커 360일 대여
    </code></pre>
  </details>
  <details>
    <summary><h3>저장 프로시저와 이벤트 스케쥴러</h3></summary>
    
    <pre><code class="language-sql">
    -- 1. 이용권의 남은 횟수 반환. 없을 경우 null 반환
    delimiter //
    create procedure getProductRemain(in v_m_no int, in v_i_type enum('gym_ticket', 'PT_ticket', 'clothes', 'locker'), out v_remain int)
    begin
    	set @remain = (
    	select p_remain from item join purchase using(i_no)
        where m_no = v_m_no and i_type = v_i_type
        order by p_datetime desc limit 1
        );
        
        select @remain into v_remain;
        
    end //
    delimiter ;
    
    -- 2. 사용하고 있는 락커 번호 반환. 없을 경우 null 반환
    delimiter //
    create procedure getLockerNum(in v_m_no int, out v_p_locker_no int)
    begin
    	set @locker_no = (
    	select p_locker_no from item join purchase using(i_no)
        where m_no = v_m_no and i_type = 'locker'
        order by p_datetime desc limit 1
        );
        
        select @locker_no into v_p_locker_no;
        
    end //
    delimiter ;
    
    -- 3. 락커 번호 배정
    delimiter //
    create procedure setLockerNum(in v_m_no int, in v_p_locker_no int)
    begin
    	set @v_p_no = (
        select p_no from item join purchase using(i_no)
        where m_no = v_m_no and i_type = 'locker'
    	order by p_datetime desc limit 1
        );
    
    	update purchase
        set p_locker_no = v_p_locker_no
        where p_no = @v_p_no;
    end //
    delimiter ;
    
    -- 4. 상품 구매 시 구매 정보와 남은 이용 횟수/기간 업데이트
    delimiter //
    create procedure purchaseItem(in v_i_no int, in v_m_no int)
    begin
    	set @type = (select i_type from item where i_no = v_i_no);
        set @name = (select i_detail from item where i_no = v_i_no);
        call getProductRemain(v_m_no, @type, @remain);
        set @remain = if(@remain is null, 0, @remain);
    
    	insert into purchase(i_no, m_no, p_remain)
        values(
        v_i_no, v_m_no,
        (@remain + @name)
        );
    end //
    delimiter ;
    
    -- 5. 회원의 번호를 입력하면 담당 트레이너의 번호를 반환하는 프로시저 없으면 null
    -- (4/5 추가)
    delimiter //
    create procedure getTrainerNo(in v_m_no int, out v_t_no int)
    begin
    	set @trainer_no = (
    	select t_no from item join purchase using(i_no)
        where m_no = v_m_no and i_type = 'PT_ticket'
        order by p_datetime desc limit 1
        );
        
        select @trainer_no into v_t_no;
    end //
    delimiter ;
    
    -- 6. 회원의 번호와 트레이너 번호를 입력하면 회원의 담당 트레이너를 배정/바꿔줌
    -- 트레이너 번호를 0으로 지정하면 담당 트레이너를 지워줌
    -- (4/18 추가)
    delimiter //
    create procedure setTrainerNo(in v_m_no int, in v_t_no int)
    begin
    	set @v_p_no = (
        select p_no from item join purchase using(i_no)
        where m_no = v_m_no and i_type = 'PT_ticket'
    	order by p_datetime desc limit 1
        );
    
    	set v_t_no = IF(v_t_no = 0, null, v_t_no);
    
    	update purchase
        set t_no = v_t_no
        where p_no = @v_p_no;
    end //
    delimiter ;
    
    -- 7. 이용권 남은 횟수 변경(증가/감소)/if 남은 횟수가 0보다 적어질 경우 0으로 만듦.
    delimiter //
    create procedure setRemain(in v_m_no int, in v_i_type enum('gym_ticket', 'PT_ticket', 'clothes', 'locker'), in v_set_num int)
    begin
        set @v_p_no = (
        select p_no from item join purchase using(i_no)
        where m_no = v_m_no and i_type = v_i_type
    		order by p_datetime desc limit 1
        );
        
    	update purchase
    	set p_remain = p_remain + v_set_num
        where p_no = @v_p_no;
        
        call getProductRemain(v_m_no, v_i_type, @remain);
        
        if @remain <= 0 then
    		update purchase
    		set p_remain = 0
    		where p_no = @v_p_no;
    	end if;
    end //
    delimiter ;
    
    -- 8. 회원 번호와 아이템 유형을 넣으면 해당 아이템 유형을 구매한 이력이 없으면 true(1) 반환해주는 프로시저
    DELIMITER //
    CREATE PROCEDURE isFirstPurchase(IN v_m_no INT, IN v_i_type ENUM('gym_ticket', 'PT_ticket', 'clothes', 'locker'), OUT v_result BOOLEAN)
    BEGIN
        DECLARE purchase_count INT;
    
        SELECT COUNT(*) INTO purchase_count
        FROM item
        JOIN purchase USING(i_no)
        WHERE m_no = v_m_no AND i_type = v_i_type;
    
        IF purchase_count = 0 THEN
            SET v_result = TRUE;
        ELSE
            SET v_result = FALSE;
        END IF;
    END //
    DELIMITER ;
    
    -- 9. 회원이 가지고 있는 락커 번호 삭제
    delimiter //
    create procedure deleteLockerNum(in v_m_no int)
    begin
    	set @v_p_no = (
        select p_no from item join purchase using(i_no)
        where m_no = v_m_no and i_type = 'locker'
    	order by p_datetime desc limit 1
        );
    
    	update purchase
        set p_locker_no = null
        where p_no = @v_p_no;
    end //
    delimiter ;
    
    -- 이벤트 스케쥴러 사용 허용 설정
    SHOW VARIABLES LIKE 'event%';
    SET GLOBAL event_scheduler = ON;
    
    -- 하루마다 회원의 모든 이용권 삭제
    CREATE EVENT itemUpdate
    ON SCHEDULE EVERY 1 DAY
    COMMENT '이용권 하루치 삭제'
    DO
    UPDATE purchase
    SET p_remain = p_remain - 1
    where i_no > 3 and p_remain > 0;
    
    -- 사물함 이용 기간이 0이 된 회원의 사물함 배정 해제
    CREATE EVENT lockerUpdate
    ON SCHEDULE EVERY 1 DAY
    COMMENT '사물함 이용 기간이 끝난 회원의 사물함 배정 해제'
    DO
    UPDATE purchase
    set p_locker_no = null
    where 13 <= i_no and i_no <= 16 and p_remain = 0;
    </code></pre>
  </details>
</details>

### 2. Edit configurations에서 VM옵션 추가
<details>
  <summary><h3>VM옵션 추가 방법</h3></summary>
  
  <img align="center" src="https://github.com/djawlgns7/JavaGym/raw/readme/readmeResources/image/edit_configurations.png" style="width:70%;height:70%"/>
  <h4>(--module-path '자신의 javafx의 lib파일이 깔려있는 경로를 지정' --add-modules=javafx.controls,javafx.fxml,javafx.media)</h4>
</details>

### 3. main/java/connection/ConnectionConst.java 파일을 자신의 데이터베이스에 맞게 수정





# 💪Skills
### Platforms & Languages
![Java](https://img.shields.io/badge/Java-007396.svg?&style=for-the-badge&logo=Java&logoColor=white)
![Spring](https://img.shields.io/badge/Spring-6DB33F.svg?&style=for-the-badge&logo=Spring&logoColor=white)
![Python](https://img.shields.io/badge/Python-3776AB.svg?&style=for-the-badge&logo=Python&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84.svg?&style=for-the-badge&logo=Android&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E.svg?&style=for-the-badge&logo=JavaScript&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript-3178C6.svg?&style=for-the-badge&logo=TypeScript&logoColor=white)
![HTML5](https://img.shields.io/badge/HTML5-E34F26.svg?&style=for-the-badge&logo=HTML5&logoColor=white)

![CSS3](https://img.shields.io/badge/CSS3-1572B6.svg?&style=for-the-badge&logo=CSS3&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1.svg?&style=for-the-badge&logo=MySQL&logoColor=white)
![Oracle](https://img.shields.io/badge/Oracle-F80000.svg?&style=for-the-badge&logo=Oracle&logoColor=white)
![Java](https://img.shields.io/badge/Java-007396.svg?&style=for-the-badge&logo=Java&logoColor=white)
![HTML5](https://img.shields.io/badge/HTML5-E34F26.svg?&style=for-the-badge&logo=Java&logoColor=white)


### Tools

![Git](https://img.shields.io/badge/Git-F05032.svg?&style=for-the-badge&logo=Git&logoColor=white)
![Eclipse IDE](https://img.shields.io/badge/Eclipse%20IDE-2C2255.svg?&style=for-the-badge&logo=Eclipse%20IDE&logoColor=white)
![Visual Studio Code](https://img.shields.io/badge/Visual%20Studio%20Code-007ACC.svg?&style=for-the-badge&logo=Visual%20Studio%20Code&logoColor=white)
![Android Studio](https://img.shields.io/badge/Android%20Studio-3DDC84.svg?&style=for-the-badge&logo=Android%20Studio&logoColor=white)


![Eclipse IDE](https://img.shields.io/badge/Eclipse%20IDE-2C2255.svg?&style=for-the-badge&logo=Eclipse%20IDE&logoColor=white)
![Visual Studio Code](https://img.shields.io/badge/Visual%20Studio%20Code-007ACC.svg?&style=for-the-badge&logo=Visual%20Studio%20Code&logoColor=white)
![Android Studio](https://img.shields.io/badge/Android%20Studio-3DDC84.svg?&style=for-the-badge&logo=Android%20Studio&logoColor=white)

