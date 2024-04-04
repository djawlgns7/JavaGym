package repository;

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

    // insert one member into DB
    public Member save(Member member) {
        String sql = "insert into member(m_name, m_pw, m_sex, m_email, m_birthdate, m_phone) values(?, ?, ?, ?, ?, ?)";

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
            close(conn, pstmt, null);
        }
    }

    public Member findByNum(Integer num) {
        String sql = "select * from member where m_no = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, num);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Member member = new Member();

                member.setNum(rs.getInt("m_no"));
                member.setName(rs.getString("m_name"));
                member.setPassword(rs.getString("m_pw"));
                member.setGender(Gender.valueOf(rs.getString("m_sex")));
                member.setEmail(rs.getString("m_email"));
                member.setBirthDate(rs.getDate("m_birthdate"));
                member.setPhone(rs.getString("m_phone"));
                member.setEnrolDate(rs.getDate("m_enrollment"));

                return member;

            } else {
                throw new NoSuchElementException("member not found m_no=" + num);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    public Member findByPhone(String phone) {
        String sql = "select * from member where m_phone = ?";

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

                member.setNum(rs.getInt("m_no"));
                member.setName(rs.getString("m_name"));
                member.setPassword(rs.getString("m_pw"));
                member.setGender(Gender.valueOf(rs.getString("m_sex")));
                member.setEmail(rs.getString("m_email"));
                member.setBirthDate(rs.getDate("m_birthdate"));
                member.setPhone(rs.getString("m_phone"));
                member.setEnrolDate(rs.getDate("m_enrollment"));

                return member;

            } else {
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, rs);
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

                member.setNum(rs.getInt("m_no"));
                member.setName(rs.getString("m_name"));
                member.setPassword(rs.getString("m_pw"));
                member.setGender(Gender.valueOf(rs.getString("m_sex")));
                member.setEmail(rs.getString("m_email"));
                member.setBirthDate(rs.getDate("m_birthdate"));
                member.setPhone(rs.getString("m_phone"));
                member.setEnrolDate(rs.getDate("m_enrollment"));

                return member;

            } else {
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    public List<Member> findAllMembers() {
        String sql = "select m_no, m_name, m_sex, m_email, m_birthdate, m_phone, m_enrollment from member";

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

                member.setNum(rs.getInt("m_no"));
                member.setName(rs.getString("m_name"));
                member.setGender(Gender.valueOf(rs.getString("m_sex")));
                member.setEmail(rs.getString("m_email"));
                member.setBirthDate(rs.getDate("m_birthdate"));
                member.setPhone(rs.getString("m_phone"));
                member.setEnrolDate(rs.getDate("m_enrollment"));

                members.add(member);
            }

            return members;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    public void updateMember(Member member) {
        String sql = "update member set m_name = ?, m_sex = ?, m_email = ?, m_birthdate = ?, m_phone = ? where m_no = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, member.getName());
            pstmt.setString(2, member.getGender().toString());
            pstmt.setString(3, member.getEmail());
            pstmt.setDate(4, member.getBirthDate());
            pstmt.setString(5, member.getPhone());
            pstmt.setInt(6, member.getNum());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    public void deleteMember(Integer num) {
        String sql = "delete from member where m_no = ?";
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
}