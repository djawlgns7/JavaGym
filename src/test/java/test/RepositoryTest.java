package test;

import domain.Admin;
import domain.Member;
import org.junit.jupiter.api.Test;
import repository.AdminRepository;
import repository.MemberRepository;

import java.util.List;

public class RepositoryTest {

    public static MemberRepository repository = new MemberRepository();
    public static AdminRepository adminRepository = new AdminRepository();

    public static void main(String[] args) {
        Member findMember = repository.findByPhone("31026950");
        if (findMember.getName().equals("정성진")) {
            System.out.println("ok");
        } else {
            System.out.println("no");
        }
    }

    @Test
    void findAllMembers() {
        List<Member> allMembers = repository.findAllMembers();
        System.out.println(allMembers);
    }

    @Test
    void findAdmin() {
        Admin admin = adminRepository.findById("admin");
        System.out.println(admin.getId());
    }
}