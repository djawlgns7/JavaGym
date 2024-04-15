package repository;

import domain.Gender;
import domain.trainer.Trainer;
import domain.trainer.WorkingHour;
import javafx.scene.image.Image;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static connection.ConnectionUtils.close;
import static connection.ConnectionUtils.getConnection;
import static converter.DateToStringConverter.dateToString;

public class TrainerRepository {

    public Trainer save(Trainer trainer) {
        String sql = "insert into trainer(t_id, t_name, t_pw, t_phone, t_birthdate, t_sex, t_working_hour, t_height, t_weight) values(?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, trainer.getId());
            pstmt.setString(2, trainer.getName());
            pstmt.setString(3, trainer.getPassword());
            pstmt.setString(4, trainer.getPhone());
            pstmt.setDate(5, trainer.getBirthDate());
            pstmt.setString(6, trainer.getGender().toString());
            pstmt.setString(7, trainer.getWorkingHour().toString());
            pstmt.setDouble(8, trainer.getHeight());
            pstmt.setDouble(9, trainer.getWeight());

            pstmt.executeUpdate();
            return trainer;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    // 트레이너의 번호와 사진을 입력하면 데이터베이스에 저장
    public void savePhoto(int trainerNo, File photoFile) {
        String sql = "UPDATE trainer set t_photo = ? where t_no = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);

            FileInputStream fis = new FileInputStream(photoFile);

            pstmt.setBlob(1, fis);
            pstmt.setInt(2, trainerNo);

            pstmt.executeUpdate();

        } catch (SQLException | FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    //트레이너의 번호를 입력하면 저장된 이미지를 이미지 파일로 반환
    public Image getImage(int trainerNo){
        String sql = "select t_photo from trainer where t_no = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, trainerNo);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                byte[] photoBytes;

                photoBytes = rs.getBytes("t_photo");
                Image trainerPhoto;

                if (photoBytes == null) {
                    trainerPhoto = new Image("/image/goTrainer.jpg");
                } else {
                    InputStream inputStream = new ByteArrayInputStream(photoBytes);
                    trainerPhoto = new Image(inputStream);
                }

                return trainerPhoto;

            } else {
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    public Trainer findByNum(Integer num) {
        String sql = "select * from trainer where t_no = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, num);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Trainer trainer = new Trainer();

                trainer.setNum(rs.getInt("t_no"));
                trainer.setId(rs.getString("t_id"));
                trainer.setName(rs.getString("t_name"));
                trainer.setPassword(rs.getString("t_pw"));
                trainer.setPhone(rs.getString("t_phone"));
                trainer.setBirthDate(rs.getDate("t_birthdate"));
                trainer.setGender(Gender.valueOf(rs.getString("t_sex")));
                trainer.setWorkingHour(WorkingHour.valueOf(rs.getString("t_working_hour")));
                trainer.setHeight(rs.getDouble("t_height"));
                trainer.setWeight(rs.getDouble("t_weight"));
                trainer.setPhoto(rs.getBytes("t_photo"));

                return trainer;

            } else {
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    public Trainer findById(String id) {
        String sql = "select * from trainer where t_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Trainer trainer = new Trainer();

                trainer.setNum(rs.getInt("t_no"));
                trainer.setId(rs.getString("t_id"));
                trainer.setName(rs.getString("t_name"));
                trainer.setPassword(rs.getString("t_pw"));
                trainer.setPhone(rs.getString("t_phone"));
                trainer.setBirthDate(rs.getDate("t_birthdate"));
                trainer.setGender(Gender.valueOf(rs.getString("t_sex")));
                trainer.setWorkingHour(WorkingHour.valueOf(rs.getString("t_working_hour")));
                trainer.setHeight(rs.getDouble("t_height"));
                trainer.setWeight(rs.getDouble("t_weight"));
                trainer.setPhoto(rs.getBytes("t_photo"));

                return trainer;
            } else {
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    public Trainer findByPhone(String phone) {
        String sql = "select * from trainer where t_phone = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, phone);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Trainer trainer = new Trainer();

                trainer.setNum(rs.getInt("t_no"));
                trainer.setId(rs.getString("t_id"));
                trainer.setName(rs.getString("t_name"));
                trainer.setPassword(rs.getString("t_pw"));
                trainer.setPhone(rs.getString("t_phone"));
                trainer.setBirthDate(rs.getDate("t_birthdate"));
                trainer.setGender(Gender.valueOf(rs.getString("t_sex")));
                trainer.setWorkingHour(WorkingHour.valueOf(rs.getString("t_working_hour")));
                trainer.setHeight(rs.getDouble("t_height"));
                trainer.setWeight(rs.getDouble("t_weight"));
                trainer.setPhoto(rs.getBytes("t_photo"));

                return trainer;

            } else {
                return null;
            }
        } catch (SQLException e) {
            throw  new RuntimeException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    public Trainer findByName(String trainerName) {
        String sql = "select * from trainer where t_name = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, trainerName);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Trainer trainer = new Trainer();

                trainer.setNum(rs.getInt("t_no"));
                trainer.setId(rs.getString("t_id"));
                trainer.setName(rs.getString("t_name"));
                trainer.setPassword(rs.getString("t_pw"));
                trainer.setPhone(rs.getString("t_phone"));
                trainer.setBirthDate(rs.getDate("t_birthdate"));
                trainer.setGender(Gender.valueOf(rs.getString("t_sex")));
                trainer.setWorkingHour(WorkingHour.valueOf(rs.getString("t_working_hour")));
                trainer.setHeight(rs.getDouble("t_height"));
                trainer.setWeight(rs.getDouble("t_weight"));
                trainer.setPhoto(rs.getBytes("t_photo"));

                return trainer;

            } else {
                return null;
            }
        } catch (SQLException e) {
            throw  new RuntimeException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }


    public List<Trainer> findAllTrainer() {
        String sql = "select t_no, t_id, t_name, t_pw, t_phone, t_birthdate, t_sex, t_working_hour, t_height, t_weight, t_photo from trainer";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Trainer> trainers = new ArrayList<>();

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Trainer trainer = new Trainer();

                trainer.setNum(rs.getInt("t_no"));
                trainer.setId(rs.getString("t_id"));
                trainer.setName(rs.getString("t_name"));
                trainer.setPassword(rs.getString("t_pw"));
                trainer.setPhone(rs.getString("t_phone"));
                trainer.setBirthDate(rs.getDate("t_birthdate"));
                trainer.setGender(Gender.valueOf(rs.getString("t_sex")));
                trainer.setWorkingHour(WorkingHour.valueOf(rs.getString("t_working_hour")));
                trainer.setHeight(rs.getDouble("t_height"));
                trainer.setWeight(rs.getDouble("t_weight"));
                trainer.setPhoto(rs.getBytes("t_photo"));

                trainers.add(trainer);
            }
            return trainers;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    public void updateTrainer(Trainer trainer) {
        String sql = "update trainer set t_id = ?, t_name = ?, t_phone = ?, t_birthdate = ?, t_sex = ?, t_working_hour = ?, t_height = ?, t_weight = ? where t_no = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, trainer.getId());
            pstmt.setString(2, trainer.getName());
            pstmt.setString(3, trainer.getPhone());
            pstmt.setDate(4, trainer.getBirthDate());
            pstmt.setString(5, trainer.getGender().toString());
            pstmt.setString(6, trainer.getWorkingHour().toString());
            pstmt.setDouble(7, trainer.getHeight());
            pstmt.setDouble(8, trainer.getWeight());
            pstmt.setInt(9, trainer.getNum());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    public void deleteTrainer(Integer num) {
        String sql = "delete from trainer where t_no = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, num);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    public int getAge(Trainer trainer) {
        LocalDate today = LocalDate.now();
        String birthString = dateToString(trainer.getBirthDate());
        int birthYear = Integer.parseInt(birthString.substring(0, 2));
        int birthMonth = Integer.parseInt(birthString.substring(2, 4));
        int birthDay = Integer.parseInt(birthString.substring(4, 6));
        int todayYear = today.getYear();
        int todayDay = today.getDayOfYear();

        if (birthYear > 50){
            birthYear += 1900;
        } else {
            birthYear += 2000;
        }

        LocalDate birth = LocalDate.of(birthYear, birthMonth, birthDay);
        birthDay = birth.getDayOfYear();
        int age = todayYear - birthYear - 1;

        if (todayDay >= birthDay){
            age++;
        }

        return age;
    }

    // 트레이너를 입력하면 그 트레이너의 출근 시간을 반환한다
    public int getWorkingHourAdder(Trainer trainer){
        String workingHour = String.valueOf(trainer.getWorkingHour());
        if(workingHour.equals("AM")){
            return 8;
        }else
            return 14;
    }

    // 트레이너를 입력하면 그 트레이너의 출근 시간을 반환한다
    public int getWorkingHourAdder(String workingHour){
        if(workingHour.equals("AM")){
            return 8;
        }else
            return 14;
    }
}
