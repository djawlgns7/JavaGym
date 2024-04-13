package repository;

import domain.Item;
import domain.member.MemberSchedule;
import domain.trainer.Reservation;
import domain.trainer.TrainerSchedule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static connection.ConnectionUtils.*;
import static util.MemberUtil.setRemain;

public class ReservationRepository {

    public List<Reservation> findReservation(int trainerNum) {
        String sql = "SELECT r.r_no, m.m_no, t.t_no, m.m_name, m.m_phone, r.r_date, r.r_time " +
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
            List<Reservation> list = new ArrayList<>();
            int count = 0;
            while (rs.next()) {
                Reservation reservation = new Reservation();

                reservation.setSequence(++count);
                reservation.setMemberNum(rs.getInt("m_no"));
                reservation.setTrainerNum(rs.getInt("t_no"));
                reservation.setMemberName(rs.getString("m_name"));
                reservation.setMemberPhone(rs.getString("m_phone"));
                reservation.setReservationDate(rs.getDate("r_date"));
                reservation.setReservationTime(rs.getInt("r_time"));

                list.add(reservation);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }
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

    public void saveReservation(int memberNum, int trainerNum, LocalDate reservationDate, int reservationTime){
        String sql = "insert into reservation (m_no, t_no, r_date, r_time) values(?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, memberNum);
            pstmt.setInt(2, trainerNum);
            pstmt.setString(3, reservationDate.toString());
            pstmt.setInt(4, reservationTime);

            pstmt.executeUpdate();

            setRemain(memberNum, Item.PT_TICKET, -1);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, null);
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

    public void updateReservation(Reservation reservation) {
        String sql = "update reservation set r_date = ?, r_time = ? where r_no = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setDate(1, reservation.getReservationDate());
            pstmt.setInt(2, reservation.getReservationTime());
            System.out.println(reservation.getReservationTime());
            System.out.println(reservation.getReservationDate());
            System.out.println(reservation.getReservationNum());
            pstmt.setInt(3, reservation.getReservationNum());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    public void insertReservation(Reservation reservation) {
        String sql = "insert into reservation (m_no, t_no, r_date, r_time) values(?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, reservation.getMemberNum());
            pstmt.setInt(2, reservation.getTrainerNum());
            pstmt.setDate(3, reservation.getReservationDate());
            pstmt.setInt(4, reservation.getReservationTime());

            pstmt.executeUpdate();

            setRemain(reservation.getMemberNum(), Item.PT_TICKET, -1);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }
}