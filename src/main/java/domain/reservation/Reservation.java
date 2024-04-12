package domain.reservation;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Reservation {
    private int dDay;
    private int rTime;

    public boolean isExist(int dDay, int rTime){
        if(dDay == this.dDay && rTime == this.rTime){
            return true;
        }else{
            return false;
        }
    }
}
