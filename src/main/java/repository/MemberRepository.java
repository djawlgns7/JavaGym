package repository;

import domain.Gender;
import domain.member.Member;
import domain.trainer.Trainer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static connection.ConnectionUtils.*;
import static converter.DateToStringConverter.dateToString;

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

    //멤버의 고유 번호로 멤버의 정보를 담은 클래스를 반환
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
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    //멤버의 전화번호로 멤버의 정보를 담은 클래스를 반환
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

    //멤버의 이메일로 멤버의 정보를 담은 클래스를 반환
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

    //모든 멤버들의 정보를 담은 리스트를 반환
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

    //특정 멤버의 정보를 바꿔줌
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

    //특정 멤버의 정보를 지움
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

    public int getAge(Member member) throws ParseException {
        LocalDate today = LocalDate.now();
        String birthString = dateToString(member.getBirthDate());
        int birthYear = Integer.parseInt(birthString.substring(0, 2));
        int birthMonth = Integer.parseInt(birthString.substring(2, 4));
        int birthDay = Integer.parseInt(birthString.substring(4, 6));
        int todayYear = today.getYear();
        int todayDay = today.getDayOfYear();

        if(birthYear > 50){
            birthYear += 1900;
        }else{
            birthYear += 2000;
        }

        LocalDate birth = LocalDate.of(birthYear, birthMonth, birthDay);
        birthDay = birth.getDayOfYear();
        int age = todayYear - birthYear - 1;

        if(todayDay >= birthDay){
            age++;
        }

        return age;
    }

    //특정 멤버가 오늘 예약이 있는지 여부를 반환한다
    public boolean hasReservationToday(int memberNum){
        String sql = "select count(r_date) from reservation where r_date = ? and m_no = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        LocalDate today = LocalDate.now();

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, today.toString());
            pstmt.setInt(2, memberNum);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                int countOfRDate = rs.getInt(1);

                if(countOfRDate == 1){
                    return true;
                }else{
                    return false;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, rs);
        }

        return false;
    }
}