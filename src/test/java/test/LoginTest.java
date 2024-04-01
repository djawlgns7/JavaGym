package test;

import repository.MemberRepository;
import service.MemberService;

public class LoginTest {

    public static MemberRepository repository = new MemberRepository();
    public static MemberService service = new MemberService(repository);

    public static void main(String[] args) {
        System.out.println(service.login("31026950", "1234"));
    }
}