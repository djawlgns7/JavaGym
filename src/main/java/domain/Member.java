package domain;

import lombok.Data;
import java.sql.Date;

@Data
public class Member {

    private Integer id;
    private String name;
    private String password;
    private Gender gender;
    private String email;
    private Date birthDate;
    private String phone;
    private Date enrolDate;
}