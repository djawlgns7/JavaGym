package repository;

import domain.member.MemberSchedule;
import domain.trainer.TrainerSchedule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
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

    /**
     * 회원의 PT 예약 정보를 가져온다. 이미 지난 예약 내역은 가져오지 않는다. (조회 시점 기준 예약 정보)
     */
    public List<MemberSchedule> findMemberSchedule(int memberNum) {
        String sql = "SELECT r_no, r_date, r_time, r.t_no, t_name " +
                "FROM reservation r join member m join trainer t on r.m_no = m.m_no and r.t_no = t.t_no " +
                "where m.m_no = ? and r_date > now()";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberNum);
            rs = pstmt.executeQuery();
            List<MemberSchedule> list = new ArrayList<>();
            int count = 0;
            while (rs.next()) {
                MemberSchedule schedule = new MemberSchedule();
                schedule.setSequence(++count);
                schedule.setReservationNum(rs.getInt("r_no"));
                schedule.setReservationDate(rs.getDate("r_date"));
                schedule.setReservationTime(rs.getInt("r_time"));
                schedule.setTrainerNum(rs.getInt("r.t_no"));
                schedule.setTrainerName(rs.getString("t_name"));

                list.add(schedule);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    public void deleteReservation(int num) {
        String sql = "delete from reservation where r_no = ?";

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

    /**
     * 오늘을 기준으로 가장 최근 예약일을 얻는다.
     * 회원 입장 시 검증에 사용한다. (성진)
     */
    public Date getTodayReservationDate(int memberNum) {
        String sql = "select r_date from reservation where m_no = ? and r_date <= now() order by 1 desc limit 1";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberNum);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDate("r_date");
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }
}