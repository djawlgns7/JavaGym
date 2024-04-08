package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
}