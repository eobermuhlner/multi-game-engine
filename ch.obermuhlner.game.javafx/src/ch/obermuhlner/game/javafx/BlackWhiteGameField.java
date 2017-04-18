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
	
	private Rectangle background;
	private Circle whiteCircle;
	private Circle blackCircle;
	private Circle greenGlow;
	
	private ObjectProperty<Side> sideProperty = new SimpleObjectProperty<>(Side.None);

	public BlackWhiteGameField(int size, Paint backgroundPaint) {
		background = new Rectangle(size, size, backgroundPaint);
		whiteCircle = new Circle(size * 0.4, Color.WHITE);
		blackCircle = new Circle(size * 0.4, Color.BLACK);
		
		RadialGradient greenGlowGradient = new RadialGradient(0.0, 0.0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
				new Stop(0.0, Color.LIGHTGREEN), new Stop(1.0, Color.TRANSPARENT));
		greenGlow = new Circle(size * 0.5, greenGlowGradient);
		
		setSkin(new SkinBase<BlackWhiteGameField>(this) {
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
			nodes.add(greenGlow);
		}
		
		switch (side) {
		case White:
			nodes.add(whiteCircle);
			break;
		case Black:
			nodes.add(blackCircle);
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
}
