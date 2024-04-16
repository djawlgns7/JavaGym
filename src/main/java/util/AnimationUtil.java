package util;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AnimationUtil {

    public static void animateTabFade(Node newNode, Node oldNode) {
        // 페이드 아웃
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.4), oldNode);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> oldNode.setVisible(false));

        // 페이드 인
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.4), newNode);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        newNode.setVisible(true);

        fadeOut.play();
        fadeIn.play();
    }

    public static void applyFadeIn(Stage stage) {
        // 페이드 인
        Node root = stage.getScene().getRoot();
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        // 초기 투명도
        root.setOpacity(0);
        fadeIn.setOnFinished(event -> root.setOpacity(1));
    }


}
