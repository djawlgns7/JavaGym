package repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static connection.ConnectionUtils.*;

public class EntryLogRepository {

    public List<Timestamp> findAllEntryLogs(Integer memberNum) {
        String sql = "select m_datetime from entry_log where m_no = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, memberNum);
            rs = pstmt.executeQuery();

            List<Timestamp> times = new ArrayList<>();
            while (rs.next()) {
                times.add(rs.getTimestamp("m_datetime"));
            }
            return times;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    public void save(Integer memberNum) {
        String sql = "insert into entry_log(m_no) values (?)";
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
}