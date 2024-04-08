package domain.member;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

/**
 * 특정 회원의 PT 예약 정보를 제공하는 클래스 (성진)
 * 관리자가 사용한다.
 */
@Getter @Setter
public class MemberSchedule {

    private int sequence;
    private int reservationNum;
    private Date reservationDate;
    private int reservationTime;
    private Integer trainerNum;
    private String trainerName;

    // 선택 상태를 위한 BooleanProperty 추가
    private final BooleanProperty selected = new SimpleBooleanProperty();

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }
}