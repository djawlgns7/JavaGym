package domain.member;

import domain.Gender;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Data;
import java.sql.Date;

@Data
public class Member {

    /**
     * java.util.Date 사용
     */

    private Integer num;
    private String name;
    private String password;
    private Gender gender;
    private String email;
    private Date birthDate;
    private String phone;
    private Date enrolDate;

    // 선택 상태를 위한 BooleanProperty 추가
    private final BooleanProperty selected = new SimpleBooleanProperty();

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public boolean isSelected() {
        return selected.get();
    }
}