package domain.trainer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter @Setter
public class Reservation {
    private int Sequence;
    private int ReservationNum;
    private int MemberNum;
    private int TrainerNum;
    private String MemberName;
    private String MemberPhone;
    private Date ReservationDate;
    private int ReservationTime;


    // 선택 상태를 위한 BooleanProperty 추가
    private final BooleanProperty selected = new SimpleBooleanProperty();

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public boolean isSelected() {
        return selected.get();
    }

}