package ch.obermuhlner.game.javafx;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class MultiGameApp extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
        Group root = new Group();
        Scene scene = new Scene(root);

		primaryStage.setScene(scene);
        primaryStage.show();

        BorderPane borderPane = new BorderPane();
        root.getChildren().add(borderPane);
        
        FlowPane startGamesPane = new FlowPane();
        borderPane.setTop(startGamesPane);
        
        TabPane currentGamesPane = new TabPane();
        borderPane.setCenter(currentGamesPane);
        
        {
        	Button buttonTicTacToe = new Button("Tic Tac Toe");
        	startGamesPane.getChildren().add(buttonTicTacToe);
        	buttonTicTacToe.setOnAction(event -> {
        		addGame(currentGamesPane, "Tic Tac Toe", createTicTacToe());
        	});
        }

        {
        	Button buttonGomoku = new Button("Gomoku");
        	startGamesPane.getChildren().add(buttonGomoku);
        	buttonGomoku.setOnAction(event -> {
        		addGame(currentGamesPane, "Gomoku", createGomoku());
        	});
        }

        {
        	Button buttonConnectFour = new Button("Connect Four");
        	startGamesPane.getChildren().add(buttonConnectFour);
        	buttonConnectFour.setOnAction(event -> {
        		addGame(currentGamesPane, "Connect Four", createConnectFour());
        	});
        }
}
	
	@Override
	public void stop() throws Exception {
		// TODO shutdown executor 
		
		super.stop();
	}

	private void addGame(TabPane currentGamesPane, String name, Node gameNode) {
		currentGamesPane.getTabs().add(new Tab(name, gameNode));
	}

	private Node createTicTacToe() {
		TicTacToe ticTacToe = new TicTacToe();
		return ticTacToe.getBoard();
	}

	private Node createGomoku() {
		Gomoku gomoku = new Gomoku();
		return gomoku.getBoard();
	}

	private Node createConnectFour() {
		ConnectFour connectFour = new ConnectFour();
		return connectFour.getBoard();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
