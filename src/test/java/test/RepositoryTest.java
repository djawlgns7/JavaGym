package test;

import domain.Admin;
import domain.Gender;
import domain.member.Member;
import domain.trainer.Trainer;
import domain.trainer.TrainerSchedule;
import org.junit.jupiter.api.Test;
import repository.AdminRepository;
import repository.MemberRepository;
import repository.ReservationRepository;
import repository.TrainerRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class RepositoryTest {

    public static MemberRepository memberRepository = new MemberRepository();
    public static AdminRepository adminRepository = new AdminRepository();
    public static TrainerRepository trainerRepository = new TrainerRepository();
    public static ReservationRepository reservationRepository = new ReservationRepository();

    @Test
    void findMemberById() {
        Member findMember = memberRepository.findByNum(1004);
        assertThat(findMember.getNum()).isEqualTo(1004);
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

    @Test
    void findTrainer() {
        Trainer findTrainer = trainerRepository.findByNum(9000);
        assertThat(findTrainer.getName()).isEqualTo("김트레이너");
    }

    @Test
    void findAllTrainer() {
        List<Trainer> trainers = trainerRepository.findAllTrainer();
        assertThat(trainers.size()).isEqualTo(5);
    }

    @Test
    void findTrainerSchedule() {
        List<TrainerSchedule> schedule = reservationRepository.findTrainerSchedule(9003);
        assertThat(schedule.size()).isEqualTo(3);
    }

    @Test
    void saveTrainer() {
        Trainer trainer = new Trainer();
    }
}