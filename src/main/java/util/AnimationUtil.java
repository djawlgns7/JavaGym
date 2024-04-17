package util;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AnimationUtil {

    private static FadeTransition currentFadeOut = null;
    private static FadeTransition currentFadeIn = null;

    public static void animateTab(Node newNode, Node oldNode) {
        if (currentFadeOut != null && currentFadeOut.getStatus() == Animation.Status.RUNNING) {
            currentFadeOut.stop();
            oldNode.setVisible(false);
        }
        if (currentFadeIn != null && currentFadeIn.getStatus() == Animation.Status.RUNNING) {
            currentFadeIn.stop();
        }

        currentFadeOut = new FadeTransition(Duration.seconds(0.4), oldNode);
        currentFadeOut.setFromValue(1.0);
        currentFadeOut.setToValue(0.0);
        currentFadeOut.setOnFinished(event -> oldNode.setVisible(false));

        currentFadeIn = new FadeTransition(Duration.seconds(0.4), newNode);
        currentFadeIn.setFromValue(0.0);
        currentFadeIn.setToValue(1.0);
        currentFadeIn.setOnFinished(event -> newNode.setVisible(true));

        newNode.setVisible(true);

        currentFadeOut.play();
        currentFadeIn.play();
    }

    public static void applyFadeIn(Stage stage) {
        Node root = stage.getScene().getRoot();
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        root.setOpacity(0);
        fadeIn.setOnFinished(event -> root.setOpacity(1));
    }

    public static void applyFadeInDialog(Node node) {
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.3), node);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        node.getScene().getWindow().setOnShown(event -> fadeIn.play());
    }
}