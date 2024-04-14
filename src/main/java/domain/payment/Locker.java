package domain.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Locker implements Available {
    private int period;
    private int num;
    private int price;
}
