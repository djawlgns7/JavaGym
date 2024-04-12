package repository;

import domain.Item;
import domain.member.MemberSchedule;
import domain.reservation.Reservation;
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

    public TrainerSchedule findByName(String name) {
        String sql = "select * from reservation natural join member where m_name = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                TrainerSchedule trainerSchedule = new TrainerSchedule();

                trainerSchedule.setSequence(rs.getInt("r_no"));
                trainerSchedule.setMemberName(rs.getString("r"));
                trainerSchedule.setReservationDate(rs.getDate("r_date"));
                trainerSchedule.setReservationTime(rs.getInt("r_time"));

                return trainerSchedule;
            } else {
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, rs);
        }

    }
    public void delete(int num) {
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

    //예약 클래스 배열 안에 특정 예약이 존재하는지 확인해줌
    public boolean isReservationExist(List<Reservation> reservation, int dDay, int rTime){
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
    public void removeReservation(List<Reservation> reservation, int dDay, int rTime){
        for(int i = 0; i < reservation.size(); i++){
            if(reservation.get(i).isExist(dDay, rTime)){
                reservation.remove(i);
            }
        }
    }
}