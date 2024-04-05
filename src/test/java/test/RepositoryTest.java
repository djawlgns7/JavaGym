package test;

import domain.Admin;
import domain.Gender;
import domain.Member;
import org.junit.jupiter.api.Test;
import repository.AdminRepository;
import repository.MemberRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class RepositoryTest {

    public static MemberRepository memberRepository = new MemberRepository();
    public static AdminRepository adminRepository = new AdminRepository();

    @Test
    void findMemberById() {
        Member findMember = memberRepository.findByNum(1006);
        assertThat(findMember.getNum()).isEqualTo(1006);
    }

    @Test
    void deleteMember() {
        memberRepository.deleteMember(1018);
    }

    @Test
    void updateMember() {
        Member member = new Member();
        member.setNum(1021);
        member.setName("test11");
        member.setGender(Gender.M);
        member.setEmail("abccc12@naver.com");
        LocalDate localDate = LocalDate.of(2024, 4, 3);
        member.setBirthDate(Date.valueOf(localDate));
        member.setPhone("20242024");
        memberRepository.updateMember(member);
    }

    @Test
    void findAllMembers() {
        List<Member> allMembers = memberRepository.findAllMembers();
        System.out.println(allMembers);
    }

    @Test
    void findAdmin() {
        Admin admin = adminRepository.findById("admin");
        System.out.println(admin.getId());
    }
}