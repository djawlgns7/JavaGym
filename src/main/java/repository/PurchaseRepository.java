package repository;

import domain.Item;

import java.sql.*;

import static connection.ConnectionUtils.*;

public class PurchaseRepository {

    /**
     * 회원 번호, 아이템 타입 -> 남은 기간
     */
    public static Integer getRemain(int memberNum, Item item) {
        Connection conn = null;
        CallableStatement cstmt = null;

        try {
            conn = getConnection();
            cstmt = conn.prepareCall("{call getProductRemain(?, ?, ?)}");

            cstmt.setInt(1, memberNum);
            cstmt.setString(2, item.toString());
            cstmt.registerOutParameter(3, Types.INTEGER);

            cstmt.execute();
            Integer remain = cstmt.getInt(3);
            if (remain != null) {
                return remain;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, cstmt, null);
        }
    }
}