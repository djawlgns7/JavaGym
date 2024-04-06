package domain.trainer;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

/**
 * 관리자 페이지에서 사용
 */
@Getter @Setter
public class TrainerSchedule {
    private int sequence;
    private String memberName;
    private Date reservationDate;
    private int reservationTime;
}