package repository;

import domain.Gender;
import domain.trainer.Trainer;
import domain.trainer.WorkingHour;

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
        String sql = "select t_no, t_id, t_name, t_pw, t_phone, t_birthdate, t_sex, t_working_hour, t_height, t_weight from trainer";

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
}
