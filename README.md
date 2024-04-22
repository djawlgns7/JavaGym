# 실행하기 위해 필요한 사항들

## ConnectionConst.java 파일을 자신의 MySQL에 맞게 수정

<details>
  <summary><h2>데이터베이스에 해당 테이블을 생성하고, 해당 값들을 삽입할 것</h2></summary>
  <h3>테이블 생성문</h3>
  ```sql
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
  
  <h3>insert문</h3>
  
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
  ```
</details>

## JavaGym
### Platforms & Languages

### Front
![JavaFX](https://img.shields.io/badge/JavaFX-FFA500.svg?&style=for-the-badge&logo=Java&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-1572B6.svg?&style=for-the-badge&logo=CSS3&logoColor=white)
![Scenebuilder](https://img.shields.io/badge/SceneBuiler-F0AD4E.svg?&style=for-the-badge&logo=scenebuilder&logoColor=white)

### Back
![Java](https://img.shields.io/badge/Java-007396.svg?&style=for-the-badge&logo=Java&logoColor=white)
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

