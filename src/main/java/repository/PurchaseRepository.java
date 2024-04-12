package repository;

import domain.member.UsingLocker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static connection.ConnectionUtils.*;

public class PurchaseRepository {

    /**
     * 헬스장 이용권 구매 이력이 없는 회원의 헬스장 이용권을 설정할 때에만 사용해야 함! (최초 한 번만)
     */
    public void setFirstGymTicket(int memberNum, int ticket) {
        String sql = "insert into purchase (i_no, m_no, p_remain) values (4, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberNum);
            pstmt.setInt(2, ticket);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    /**
     * PT 이용권 구매 이력이 없는 회원의 PT 이용권을 설정할 때에만 사용해야 함! (최초 한 번만)
     */
    public void setFirstPtTicket(int memberNum, int trainerNum, int ticket) {
        String sql = "insert into purchase (i_no, m_no, t_no, p_remain) values (1, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberNum);
            pstmt.setInt(2, trainerNum);
            pstmt.setInt(3, ticket);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    /**
     * 운동복 구매 이력이 없는 회원의 운동복 이용권을 설정할 때에만 사용해야 함! (최초 한 번만)
     */
    public void setFirstClothes(int memberNum, int period) {
        String sql = "insert into purchase (i_no, m_no, p_remain) values (9, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberNum);
            pstmt.setInt(2, period);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    /**
     * 사물함 구매 이력이 없는 회원의 사물함 이용권을 설정할 때에만 사용해야 함! (최초 한 번만)
     */
    public void setFirstLocker(int memberNum, int lockerNum, int period) {
        String sql = "insert into purchase (i_no, m_no, p_locker_no, p_remain) values (13, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberNum);
            pstmt.setInt(2, lockerNum);
            pstmt.setInt(3, period);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    public void deleteLocker(int memberNum) {
        String sql = "delete from purchase where m_no = ? and p_locker_no is not null";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberNum);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    public void deletePtTicketAndTrainer(int memberNum) {
        String sql = "delete from purchase where m_no = ? and t_no is not null";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberNum);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    public boolean isUsingLockerNum(int lockerNum) {
        String sql = "select count(*) from purchase where p_locker_no = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, lockerNum);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                if (rs.getInt(1) == 1) {
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    public List<UsingLocker> findAllUsingLocker() {
        String sql = "select m_no, m_name, p_locker_no, p_remain from purchase join member using(m_no) where p_locker_no is not null";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            List<UsingLocker> list = new ArrayList<>();
            int count = 0;
            while (rs.next()) {
                UsingLocker usingLocker = new UsingLocker();
                usingLocker.setCount(++count);
                usingLocker.setMemberNum(rs.getInt("m_no"));
                usingLocker.setMemberName(rs.getString("m_name"));
                usingLocker.setLockerNum(rs.getInt("p_locker_no"));
                usingLocker.setLockerPeriod(rs.getInt("p_remain"));
                list.add(usingLocker);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }
}