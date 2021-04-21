package ch.obermuhlner.game.javafx;

import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.layout.StackPane;

public class GameField extends Control {

	private StackPane stackPane;
	
	public GameField() {
		stackPane = new StackPane();
		getChildren().add(stackPane);
	}
	
	public void setField(List<Node> nodes) {
		stackPane.getChildren().setAll(nodes);
	}
}
