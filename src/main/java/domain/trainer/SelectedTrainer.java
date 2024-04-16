package domain.trainer;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SelectedTrainer {
    public static Trainer currentTrainer;

    public static void setCurrentTrainer(Trainer currentTrainer) {
        SelectedTrainer.currentTrainer = currentTrainer;
    }

}
