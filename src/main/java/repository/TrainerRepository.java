package repository;

import domain.Member;
import domain.WorkingHour;
import domain.Gender;
import domain.Trainer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static connection.ConnectionUtils.*;

public class TrainerRepository {

    //
    public Trainer save(Trainer trainer) {
        String sql = "insert into trainer(t_id, t_name, t_pw, t_phone, t_birthdate, t_sex, t_working_hour) values(?, ?, ?, ?, ?, ?, ?)";

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
        String sql = "select * from member t_id = ?";

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

                pstmt.setString(1, trainer.getId());
                pstmt.setString(2, trainer.getName());
                pstmt.setString(3, trainer.getPassword());
                pstmt.setString(4, trainer.getPhone());
                pstmt.setDate(5, trainer.getBirthDate());
                pstmt.setString(6, trainer.getGender().toString());
                pstmt.setString(7, trainer.getWorkingHour().toString());

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


    public List<Trainer> findAllTrainers() {
        String sql = "select t_id, t_name, t_pw, t_phone, t_birthdate, t_sex, t_working_hour from trainer";

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

                trainers.add(trainer);
            }
            return trainers;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, rs);
        }


    }
}