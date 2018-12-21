package timelineFx.view;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import timelineFx.data.TimelineItem;

/**
 * A Panel to paint over the rest to describe Objects.
 * 
 * @author Michael Hochmuth
 *
 */
public class ToolTip extends StackPane{
	
	private Object currentThing = null;
	private Label title, text;
	
	/**
	 * A new, yet hidden ToolTip. Make it visible by defining something
	 * to describe with {@link ToolTip#show(Object, double, double)}.
	 */
	public ToolTip() {
		setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, 
				new CornerRadii(3.0), Insets.EMPTY)));
		setPadding(new Insets(5.0));
		
		BorderPane root = new BorderPane();
		getChildren().add(root);
		
		title = new Label("<title>");
		title.setStyle("-fx-font-weight: bold");
		root.setTop(title);
		
		text = new Label("<text>\n<text>");
		text.setPrefWidth(200.0);
		root.setCenter(text);
		
		hide();
	}
	
	/**
	 * Show a tooltip for the given Object, if it can.
	 * Hides the tooltip otherwise, of if NULL is passed.
	 * The given Position is only used if the Object
	 * changes.
	 * @param thing	Object to describe. In case, it isn't of a
	 * 				supported type, toString() is used instead.
	 * @param x 	Position on Screen
	 * @param y		Position on Screen
	 */
	public void show(Object thing, double x, double y) {
		if(thing == null) {
			hide();
			return;
		}
		
		setVisible(true);
		if(thing == currentThing)
			return;
		
		setTranslateX(x);
		setTranslateY(y);
		
		if(TimelineItem.class.isAssignableFrom(thing.getClass())) {
			TimelineItem item = (TimelineItem)thing;
			title.setText(item.getName());
			StringBuilder builder = new StringBuilder();
			if(!item.getDescription().isEmpty()) {
				builder.append(item.getDescription());
				builder.append('\n');
			}
			if(!item.hasDuration()) {
				builder.append("At: ");
				builder.append(item.getStart().format(DateTimeFormatter
						.ofLocalizedDateTime(FormatStyle.LONG)));
				builder.append('\n');
			} else {
				builder.append("From: ");
				builder.append(item.getStart().format(DateTimeFormatter
						.ofLocalizedDateTime(FormatStyle.LONG)));
				builder.append('\n');
				
				builder.append("To: ");
				builder.append(item.getEnd().format(DateTimeFormatter
						.ofLocalizedDateTime(FormatStyle.LONG)));
				builder.append('\n');
			}
			text.setText(builder.toString());
		} else {
			title.setText("Unsupported Type");
			text.setText(thing.toString());
		}
	}
	
	/**
	 * Hide the tooltip and deregister the currently described
	 * thing
	 */
	public void hide() {
		currentThing = null;
		setVisible(false);
		title.setText("-");
		text.setText("-");
	}
	
}
