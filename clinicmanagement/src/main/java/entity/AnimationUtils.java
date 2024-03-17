package entity;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class AnimationUtils {

	public static void createFadeTransition(Node node, Double from, Double to) {
		FadeTransition fade = new FadeTransition();
		fade.setNode(node);
		fade.setDuration(Duration.seconds(1));
		fade.setFromValue(from);
		fade.setToValue(to);
		fade.play();
	}
}
