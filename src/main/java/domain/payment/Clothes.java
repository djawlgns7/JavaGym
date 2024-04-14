package domain.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Clothes implements Available {
    private int period;
    private int price;
}
