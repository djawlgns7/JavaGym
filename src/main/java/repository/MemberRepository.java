package repository;

import connection.ConnectionUtils;
import domain.Gender;
import domain.Member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static connection.ConnectionUtils.*;

public class MemberRepository {

    public Member save(Member member) {
        String sql = "insert into member(m_name, m_password, m_sex, m_email, m_birth, m_phonenumber) values(?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, member.getName());
            pstmt.setString(2, member.getPassword());
            pstmt.setString(3, member.getGender().toString());
            pstmt.setString(4, member.getEmail());
            pstmt.setDate(5, member.getBirthDate());
            pstmt.setString(6, member.getPhone());

            pstmt.executeUpdate();
            return member;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            clear(conn, pstmt, null);
        }
    }

    public Member findById(Integer id) {
        String sql = "select * from member where m_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Member member = new Member();

                member.setId(rs.getInt("m_id"));
                member.setName(rs.getString("m_name"));
                member.setPassword(rs.getString("m_password"));
                member.setGender(Gender.valueOf(rs.getString("m_sex")));
                member.setEmail(rs.getString("m_email"));
                member.setBirthDate(rs.getDate("m_birth"));
                member.setPhone(rs.getString("m_phonenumber"));
                member.setEnrolDate(rs.getDate("m_enrolldate"));

                return member;

            } else {
                throw new NoSuchElementException("member not found memberId=" + id);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            clear(conn, pstmt, rs);
        }
    }

    public Member findByPhone(String phone) {
        String sql = "select * from member where m_phonenumber = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, phone);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Member member = new Member();

                member.setId(rs.getInt("m_id"));
                member.setName(rs.getString("m_name"));
                member.setPassword(rs.getString("m_password"));
                member.setGender(Gender.valueOf(rs.getString("m_sex")));
                member.setEmail(rs.getString("m_email"));
                member.setBirthDate(rs.getDate("m_birth"));
                member.setPhone(rs.getString("m_phonenumber"));
                member.setEnrolDate(rs.getDate("m_enrolldate"));

                return member;

            } else {
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            clear(conn, pstmt, rs);
        }
    }

    public Member findByEmail(String email) {
        String sql = "select * from member where m_email = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Member member = new Member();

                member.setId(rs.getInt("m_id"));
                member.setName(rs.getString("m_name"));
                member.setPassword(rs.getString("m_password"));
                member.setGender(Gender.valueOf(rs.getString("m_sex")));
                member.setEmail(rs.getString("m_email"));
                member.setBirthDate(rs.getDate("m_birth"));
                member.setPhone(rs.getString("m_phonenumber"));
                member.setEnrolDate(rs.getDate("m_enrolldate"));

                return member;

            } else {
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            clear(conn, pstmt, rs);
        }
    }

    public List<Member> findAllMembers() {
        String sql = "select m_id, m_name, m_sex, m_email, m_birth, m_phonenumber, m_enrolldate from member";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Member> members = new ArrayList<>();

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Member member = new Member();
                member.setId(rs.getInt("m_id"));
                member.setName(rs.getString("m_name"));
                member.setGender(Gender.valueOf(rs.getString("m_sex")));
                member.setEmail(rs.getString("m_email"));
                member.setBirthDate(rs.getDate("m_birth"));
                member.setPhone(rs.getString("m_phonenumber"));
                member.setEnrolDate(rs.getDate("m_enrolldate"));
                members.add(member);
            }

            return members;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            clear(conn, pstmt, rs);
        }

    }
}