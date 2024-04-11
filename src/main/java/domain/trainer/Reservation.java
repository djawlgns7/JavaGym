package domain.trainer;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter @Setter
public class Reservation {
    private int Sequence;
    private int MemberNum;
    private int TrainerNum;
    private String MemberName;
    private String MemberPhone;
    private Date ReservationDate;
    private int ReservationTime;

}
