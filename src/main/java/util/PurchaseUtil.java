package util;

import domain.Item;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import static connection.ConnectionUtils.close;
import static connection.ConnectionUtils.getConnection;

public class PurchaseUtil {
    public static void purchaseItem(int memberNum, Item item, int quantity) {
        int itemNum = 0;

        switch (item) {
            case PT_TICKET:
                switch (quantity) {
                    case 10:
                        itemNum = 1;
                        break;
                    case 20:
                        itemNum = 2;
                        break;
                    case 30:
                        itemNum = 3;
                        break;
                }
                break;
            case GYM_TICKET:
                switch (quantity) {
                    case 1:
                        itemNum = 4;
                        break;
                    case 30:
                        itemNum = 5;
                        break;
                    case 90:
                        itemNum = 6;
                        break;
                    case 180:
                        itemNum = 7;
                        break;
                    case 360:
                        itemNum = 8;
                        break;
                }
                break;
            case CLOTHES:
                switch (quantity) {
                    case 30:
                        itemNum = 9;
                        break;
                    case 90:
                        itemNum = 10;
                        break;
                    case 180:
                        itemNum = 11;
                        break;
                    case 360:
                        itemNum = 12;
                        break;
                }
                break;
            case LOCKER:
                switch (quantity) {
                    case 30:
                        itemNum = 13;
                        break;
                    case 90:
                        itemNum = 14;
                        break;
                    case 180:
                        itemNum = 15;
                        break;
                    case 360:
                        itemNum = 16;
                        break;
                }
                break;
        }

        Connection conn = null;
        CallableStatement cstmt = null;

        try {
            conn = getConnection();
            cstmt = conn.prepareCall("{call purchaseItem(?, ?)}");

            cstmt.setInt(1, itemNum);
            cstmt.setInt(2, memberNum);

            cstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, cstmt, null);
        }
    }
}
