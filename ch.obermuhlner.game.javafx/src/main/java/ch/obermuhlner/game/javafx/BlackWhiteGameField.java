package ch.obermuhlner.game.javafx;

import java.util.ArrayList;
import java.util.List;

import ch.obermuhlner.game.engine.GameEngine.Side;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class BlackWhiteGameField extends GameField {
	
	private int size;

	private Node background;
	private Node whiteStone;
	private Node blackStone;
	private Node glow;
	
	private ObjectProperty<Side> sideProperty = new SimpleObjectProperty<>(Side.None);

	public BlackWhiteGameField(int size, Paint backgroundPaint) {
		this(size, backgroundPaint, 0.45);
	}
	
	public BlackWhiteGameField(int size, Paint backgroundPaint, double stoneRadius) {
		this(size, 
				new Rectangle(size, size, backgroundPaint),
				new Circle(size * stoneRadius, Color.WHITE),
				new Circle(size * stoneRadius, Color.BLACK));
	}

	public BlackWhiteGameField(int size, Node background, Node whiteStone, Node blackStone) {
		this.size = size;
		this.background = background;
		this.whiteStone = whiteStone;
		this.blackStone = blackStone;

		setGlow(Color.GREEN);
		
		setSkin(new SkinBase<BlackWhiteGameField>(this) {
			// just an empty skin seems to be enough
		});
		
		sideProperty.addListener((observable, oldValue, newValue) -> {
			updateField(newValue, !isDisabled());
		});
		disabledProperty().addListener((observable, oldValue, newValue) -> {
			updateField(sideProperty.get(), !newValue);
		});
		
		updateField(sideProperty.get(), true);
	}

	private void updateField(Side side, boolean enabled) {
		List<Node> nodes = new ArrayList<>();
		
		nodes.add(background);
		
		if (enabled) {
			nodes.add(glow);
		}
		
		switch (side) {
		case White:
			nodes.add(whiteStone);
			break;
		case Black:
			nodes.add(blackStone);
			break;
		case None:
			// nothing
		}

		setField(nodes);
	}

	public void setSide(Side side) {
		sideProperty.set(side);
	}
	
	public Side getSide() {
		return sideProperty.get();
	}
	
	public ObjectProperty<Side> sideProperty() {
		return sideProperty;
	}

	public void setGlow(Color color) {
		RadialGradient glowGradient = new RadialGradient(0.0, 0.0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
				new Stop(0.0, color), new Stop(1.0, Color.TRANSPARENT));
		glow = new Circle(size * 0.5, glowGradient);		
	}
}
