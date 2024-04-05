package test;

import repository.MemberRepository;
import service.member.MemberService;

public class LoginTest {

    public static MemberRepository repository = new MemberRepository();
    public static MemberService service = new MemberService(repository);

    public static void main(String[] args) {
    }
}