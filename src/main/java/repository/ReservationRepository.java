package repository;

import domain.trainer.TrainerSchedule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static connection.ConnectionUtils.*;

public class ReservationRepository {

    /**
     * (성진)
     * 트레이너 번호 -> 해당 트레이너의 PT 일정
     */
    public List<TrainerSchedule> findTrainerSchedule(int trainerNum) {
        String sql = "SELECT m_name, r_date, r_time " +
                    "FROM reservation r JOIN member m JOIN trainer t ON r.m_no = m.m_no AND r.t_no = t.t_no " +
                    "WHERE r.t_no = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, trainerNum);
            rs = pstmt.executeQuery();
            List<TrainerSchedule> list = new ArrayList<>();
            int count = 0;
            while (rs.next()) {
                TrainerSchedule schedule = new TrainerSchedule();
                schedule.setSequence(++count);
                schedule.setMemberName(rs.getString("m_name"));
                schedule.setReservationDate(rs.getDate("r_date"));
                schedule.setReservationTime(rs.getInt("r_time"));

                list.add(schedule);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }
}