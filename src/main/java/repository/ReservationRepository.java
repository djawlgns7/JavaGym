package repository;

import domain.Item;
import domain.member.MemberSchedule;
import domain.reservation.ReservationInformation;
import domain.trainer.Reservation;
import domain.trainer.TrainerSchedule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static connection.ConnectionUtils.*;
import static util.MemberUtil.setRemain;

public class ReservationRepository {

    public List<Reservation> findReservation(int trainerNum) {
        String sql = "SELECT r.r_no, m.m_no, t.t_no, m.m_name, m.m_phone, r.r_date, r.r_time " +
                "FROM reservation r " +
                "JOIN member m ON r.m_no = m.m_no " +
                "JOIN trainer t ON r.t_no = t.t_no " +
                "WHERE r.t_no = ? " +
                "ORDER BY r.m_no ASC, r.r_date ASC, r.r_time ASC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, trainerNum);
            rs = pstmt.executeQuery();
            List<Reservation> list = new ArrayList<>();
            while (rs.next()) {
                Reservation reservation = new Reservation();

                reservation.setReservationNum(rs.getInt("r_no"));
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
     * 오늘치 예약도 결과로 잡히도록 수정(지훈)
     */
    public List<MemberSchedule> findMemberSchedule(int memberNum) {
        String sql = "SELECT r_no, r_date, r_time, r.t_no, t_name " +
                "FROM reservation r join member m join trainer t on r.m_no = m.m_no and r.t_no = t.t_no " +
                "where m.m_no = ? and (r_date > ? or (r_date = ? and r_time >= ?)) order by r_date, r_time asc";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        LocalDate today = LocalDate.now();
        int currentTime = LocalTime.now().getHour();

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberNum);
            pstmt.setString(2, today.toString());
            pstmt.setString(3, today.toString());
            pstmt.setInt(4, currentTime);

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

            setRemain(num, Item.PT_TICKET, +1);
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

    public void updateReservation(Reservation reservation) {
        String sql = "update reservation set r_date = ?, r_time = ? where r_no = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setDate(1, reservation.getReservationDate());
            pstmt.setInt(2, reservation.getReservationTime());
            pstmt.setInt(3, reservation.getReservationNum());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    public void insertReservation(Reservation reservation){
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

    //예약 클래스 배열 안에 특정 예약이 존재하는지 확인해줌
    public boolean isReservationExist(List<ReservationInformation> reservation, int dDay, int rTime){
        boolean result = false;

        for(int i = 0; i < reservation.size(); i++){
            boolean tempResult = reservation.get(i).isExist(dDay, rTime);
            if(tempResult){
                result = true;
            }
        }
        return result;
    }

    //예약 클래스 배열 내의 특정 예약 정보를 없앰
    public void removeReservation(List<ReservationInformation> reservation, int dDay, int rTime){
        for(int i = 0; i < reservation.size(); i++){
            if(reservation.get(i).isExist(dDay, rTime)){
                reservation.remove(i);
            }
        }
    }

    public boolean checkReservation(int trainerNum, LocalDate reservationDate, int reservationTime) {
        String sql = "SELECT COUNT(*) FROM reservation " +
                "WHERE t_no = ? AND r_date = ? AND r_time = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, trainerNum);
            pstmt.setDate(2, java.sql.Date.valueOf(reservationDate));
            pstmt.setInt(3, reservationTime);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    public List<Reservation> findByMemberName(String memberName) {
        String sql = "SELECT r.r_no, m.m_no, t.t_no, m.m_name, m.m_phone, r.r_date, r.r_time " +
                "FROM reservation r " +
                "JOIN member m ON r.m_no = m.m_no " +
                "JOIN trainer t ON r.t_no = t.t_no " +
                "where m.m_name = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberName);
            rs = pstmt.executeQuery();
            List<Reservation> list = new ArrayList<>();
            while (rs.next()) {
                Reservation reservation = new Reservation();

                reservation.setReservationNum(rs.getInt("r_no"));
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

    public List<Integer> findReservationHours(int trainerNum, Date date) {
        String sql = "SELECT r_time FROM reservation WHERE t_no = ? AND r_date = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            List<Integer> list = new ArrayList<>();
            pstmt.setInt(1, trainerNum);
            pstmt.setDate(2, (java.sql.Date) date);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getInt("r_time"));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }
}