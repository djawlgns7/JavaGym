package repository;

import domain.Gender;
import domain.Trainer;
import domain.WorkingHour;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}
