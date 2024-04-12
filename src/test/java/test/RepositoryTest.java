package test;

import org.junit.jupiter.api.Test;
import repository.*;

import java.time.LocalDate;
import java.util.Date;

public class RepositoryTest {

    public static MemberRepository memberRepository = new MemberRepository();
    public static AdminRepository adminRepository = new AdminRepository();
    public static TrainerRepository trainerRepository = new TrainerRepository();
    public static ReservationRepository reservationRepository = new ReservationRepository();
    public static PurchaseRepository purchaseRepository = new PurchaseRepository();

    @Test
    void getTodayReservation() {
        Date reservation = reservationRepository.getTodayReservationDate(1068);
        LocalDate today = LocalDate.now();
        System.out.println("reservation = " + reservation);
        System.out.println(reservation.toString().equals(today.toString()));
    }
}