package repository;

import domain.Admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import static connection.ConnectionUtils.*;
import static connection.ConnectionUtils.getConnection;

public class AdminRepository {

    public Admin findById(String id) {
        String sql = "select * from admin where id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Admin admin = new Admin();
                admin.setId(rs.getString(1));
                admin.setPassword(rs.getString(2));

                return admin;
            } else {
                throw new NoSuchElementException("admin not found admin = " + id);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            clear(conn, pstmt, rs);
        }
    }
}