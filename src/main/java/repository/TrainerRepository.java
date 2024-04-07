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
import java.util.ArrayList;
import java.util.List;

import static connection.ConnectionUtils.close;
import static connection.ConnectionUtils.getConnection;

public class TrainerRepository {

    public Trainer save(Trainer trainer) {
        String sql = "insert into trainer(t_id, t_name, t_pw, t_phone, t_birthdate, t_sex, t_working_Hour) values(?, ?, ?, ?, ?, ?, ?)";

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

            pstmt.executeUpdate();
            return trainer;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    //트레이너의 번호와 사진을 입력하면 데이터베이스에 저장
    public void savePhoto(int trainerNo, File photoFile){
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

                InputStream inputStream = new ByteArrayInputStream(photoBytes);
                Image trainerPhoto = new Image(inputStream);

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
                trainer.setWorkingHour(WorkingHour.valueOf(rs.getString("t_working_Hour")));

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
                trainer.setWorkingHour(WorkingHour.valueOf(rs.getString("t_working_Hour")));
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
                trainer.setWorkingHour(WorkingHour.valueOf(rs.getString("t_working_Hour")));
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

    public List<Trainer> findAllTrainer() {
        String sql = "select t_no, t_id, t_name, t_phone, t_birthdate, t_sex, t_working_hour from trainer";

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
                trainer.setPhone(rs.getString("t_phone"));
                trainer.setBirthDate(rs.getDate("t_birthdate"));
                trainer.setGender(Gender.valueOf(rs.getString("t_sex")));
                trainer.setWorkingHour(WorkingHour.valueOf(rs.getString("t_working_Hour")));

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
        String sql = "update trainer set t_id = ?, t_name = ?, t_phone = ?, t_birthdate = ?, t_sex = ?, t_working_hour = ? where t_no = ?";
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
            pstmt.setInt(7, trainer.getNum());
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
}
