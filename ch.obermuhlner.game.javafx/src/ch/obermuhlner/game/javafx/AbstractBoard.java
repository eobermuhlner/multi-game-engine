package ch.obermuhlner.game.javafx;

import java.text.DecimalFormat;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public abstract class AbstractBoard {

	private static final DecimalFormat DOUBLE_FORMAT = new DecimalFormat("##0.0");

	private BorderPane borderPane;
	
	protected StringProperty lastMoveProperty = new SimpleStringProperty();
	protected DoubleProperty scoreProperty = new SimpleDoubleProperty();
	protected StringProperty nextPlayerProperty = new SimpleStringProperty();
	protected StringProperty winnerProperty = new SimpleStringProperty();

	protected void setupBoard(Node board) {
		borderPane = new BorderPane();
		
		GridPane dataPane = new GridPane();
		borderPane.setRight(dataPane);
		
		borderPane.setCenter(board);
		
		int rowIndex = 0;
		{
			dataPane.add(new Label("Last Move"), 0, rowIndex);
			TextField textfieldLastMove = new TextField();
			dataPane.add(textfieldLastMove, 1, rowIndex);
			Bindings.bindBidirectional(textfieldLastMove.textProperty(), lastMoveProperty);
			rowIndex++;
		}
		{
			dataPane.add(new Label("Score"), 0, rowIndex);
			TextField textfieldScore = new TextField();
			dataPane.add(textfieldScore, 1, rowIndex);
			Bindings.bindBidirectional(textfieldScore.textProperty(), scoreProperty, DOUBLE_FORMAT);
			rowIndex++;
		}
		{
			dataPane.add(new Label("Player"), 0, rowIndex);
			TextField textfieldNextPlayer = new TextField();
			dataPane.add(textfieldNextPlayer, 1, rowIndex);
			Bindings.bindBidirectional(textfieldNextPlayer.textProperty(), nextPlayerProperty);
			rowIndex++;
		}
		{
			dataPane.add(new Label("Winner"), 0, rowIndex);
			TextField textfieldWinner = new TextField();
			dataPane.add(textfieldWinner, 1, rowIndex);
			Bindings.bindBidirectional(textfieldWinner.textProperty(), winnerProperty);
			rowIndex++;
		}
	}

	public Node getBoard() {
		return borderPane;
	}
}
