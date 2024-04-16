package util;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AnimationUtil {

    private static FadeTransition currentFadeOut = null;
    private static FadeTransition currentFadeIn = null;

    public static void animateTabFade(Node newNode, Node oldNode) {
        if (currentFadeOut != null && currentFadeOut.getStatus() == Animation.Status.RUNNING) {
            currentFadeOut.stop(); // 현재 진행 중인 페이드 아웃 중단
            oldNode.setVisible(false); // 바로 숨김 처리
        }
        if (currentFadeIn != null && currentFadeIn.getStatus() == Animation.Status.RUNNING) {
            currentFadeIn.stop(); // 현재 진행 중인 페이드 인 중단
        }

        // 페이드 아웃
        currentFadeOut = new FadeTransition(Duration.seconds(0.4), oldNode);
        currentFadeOut.setFromValue(1.0);
        currentFadeOut.setToValue(0.0);
        currentFadeOut.setOnFinished(event -> oldNode.setVisible(false));

        // 페이드 인
        currentFadeIn = new FadeTransition(Duration.seconds(0.4), newNode);
        currentFadeIn.setFromValue(0.0);
        currentFadeIn.setToValue(1.0);
        currentFadeIn.setOnFinished(event -> newNode.setVisible(true));

        newNode.setVisible(true);

        // 애니메이션 재생
        currentFadeOut.play();
        currentFadeIn.play();
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