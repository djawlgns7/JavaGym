package domain.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PtTicket implements Available {
    private int time; // 횟수
    private int price;
}