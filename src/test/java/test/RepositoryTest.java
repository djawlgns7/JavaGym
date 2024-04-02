package test;

import domain.Admin;
import domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import repository.AdminRepository;
import repository.MemberRepository;

import java.util.List;

public class RepositoryTest {

    public static MemberRepository memberRepository = new MemberRepository();
    public static AdminRepository adminRepository = new AdminRepository();

    @Test
    void findMemberById() {
        Member findMember = memberRepository.findByNum(1006);
        Assertions.assertThat(findMember.getId()).isEqualTo(1006);
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