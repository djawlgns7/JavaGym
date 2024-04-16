package domain.trainer;

import domain.Gender;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Data;
import java.sql.Date;

@Data
public class Trainer {
    private Integer num;
    private String id;
    private String name;
    private String password;
    private String phone;
    private Date birthDate;
    private byte[] photo;
    private Gender gender;
    private WorkingHour workingHour;
    private Double height;
    private Double weight;

    // 선택 상태를 위한 BooleanProperty 추가
    private final BooleanProperty selected = new SimpleBooleanProperty();

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public boolean isSelected() {
        return selected.get();
    }
}