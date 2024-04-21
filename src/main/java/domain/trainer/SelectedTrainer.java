package domain.trainer;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SelectedTrainer {
    public static Trainer loginTrainer;

    public static void setLoginTrainer(Trainer loginTrainer) {
        SelectedTrainer.loginTrainer = loginTrainer;
    }

}
