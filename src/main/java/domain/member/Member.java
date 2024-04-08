package domain.member;

import domain.Gender;
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
}