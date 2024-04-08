package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static connection.ConnectionUtils.*;

public class PurchaseRepository {

    // 특정 회원의 상품 남은 횟수/기간 수정
    public void updateRemain(int remain, int itemNum, int memberNum) {
        String sql = "update purchase set p_remain = ? where i_no = ? and m_no = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, remain);
            pstmt.setInt(2, itemNum);
            pstmt.setInt(3, memberNum);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }
}