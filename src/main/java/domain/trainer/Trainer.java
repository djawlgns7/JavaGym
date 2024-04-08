package domain.trainer;

import domain.Gender;
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
    private double height;
    private double weight;
    private byte[] photo;
    private Gender gender;
    private WorkingHour workingHour;
}