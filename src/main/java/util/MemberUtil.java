package util;

import domain.Item;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;

import static connection.ConnectionUtils.close;
import static connection.ConnectionUtils.getConnection;

public class MemberUtil {

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
            return cstmt.getInt(3);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, cstmt, null);
        }
    }

    /**
     * 회원 번호 -> 사물함 번호
     */
    public static Integer getLockerNum(int memberNum) {
        Connection conn = null;
        CallableStatement cstmt = null;

        try {
            conn = getConnection();
            cstmt = conn.prepareCall("{call getLockerNum(?, ?)}");

            cstmt.setInt(1, memberNum);
            cstmt.registerOutParameter(2, Types.INTEGER);

            cstmt.execute();

            return cstmt.getInt(2);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, cstmt, null);
        }
    }

    /**
     * 회원 번호 -> 트레이너 번호
     */
    public static Integer getTrainerNumForMember(int memberNum) {
        Connection conn = null;
        CallableStatement cstmt = null;

        try {
            conn = getConnection();
            cstmt = conn.prepareCall("{call getTrainerNo(?, ?)}");

            cstmt.setInt(1, memberNum);
            cstmt.registerOutParameter(2, Types.INTEGER);

            cstmt.execute();
            return cstmt.getInt(2);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, cstmt, null);
        }
    }

    // 회원의 서비스 잔여 일수 또는 개수를 List에 담아 반환한다.
    public static List<Integer> getRemainAll(int memberNum) {

        Integer remainGym = getRemain(memberNum, Item.GYM_TICKET);
        Integer remainPT = getRemain(memberNum, Item.PT_TICKET);
        Integer remainClothes = getRemain(memberNum, Item.CLOTHES);
        Integer remainLocker = getRemain(memberNum, Item.LOCKER);

        return Arrays.asList(remainGym, remainPT, remainClothes, remainLocker);
    }

}