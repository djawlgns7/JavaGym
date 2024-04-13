package domain.trainer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter @Setter
public class Reservation {
    private int sequence;
    private int reservationNum;
    private int memberNum;
    private int trainerNum;
    private String memberName;
    private String memberPhone;
    private Date reservationDate;
    private int reservationTime;


    // 선택 상태를 위한 BooleanProperty 추가
    private final BooleanProperty selected = new SimpleBooleanProperty();

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public boolean isSelected() {
        return selected.get();
    }

}