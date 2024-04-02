package service;

import domain.Member;
import org.mindrot.jbcrypt.BCrypt;
import repository.MemberRepository;

public class MemberService {

    private final MemberRepository repository;

    public MemberService(MemberRepository repository) {
        this.repository = repository;
    }

    public Member signUp(Member member) {
        return repository.save(member);
    }


    public Member login(String phone, String password) {

        Member findMember = repository.findByPhone(phone);

        if (findMember != null && BCrypt.checkpw(password, findMember.getPassword())) {
            return findMember;
        }
        return null;
    }
}