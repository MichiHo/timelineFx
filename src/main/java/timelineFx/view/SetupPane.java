package timelineFx.view;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class SetupPane extends VBox{
	private TimelineViewConfiguration conf;
	private TimelineView view;
	public SetupPane(TimelineView view) {
		this.view = view;
		this.conf = view.getConfiguration();
		
		setPadding(new Insets(5.0));
		
		Slider slider;
		Button button;
		
		add(caption("Zoom"));
		
		button = new Button("Zoom to Content");
		button.setOnAction(e -> view.zoomToContent());
		add(button);
		
		add(caption("Measures"));
		add(label("Grid Unit Width"));
		add(slider(1, 1000,conf.gridUnitWidthProperty()));
		
		add(label("Flag"));
		add(slider(0,100,conf.itemFlagProperty()));
		
		add(label("Item Padding"));
		add(slider(0.0,100.0,conf.itemPaddingProperty()));
		
		add(label("Item Font Size"));
		add(slider(0.0,100.0,conf.itemFontSizeProperty()));
		
		add(label("Item Min Width"));
		add(slider(0.0,200.0,conf.minItemWidthProperty()));
		
		add(label("Item-Timebar Distance"));
		add(slider(0.0,400.0,conf.timebarItemDistanceProperty()));
		
		add(label("Item-Item Distance"));
		add(slider(0.0,100.0,conf.itemDistanceProperty()));
		
		add(caption("Colors"));
		
		add(label("Item-Textpane shade"));
		add(slider(0.0,1.0,conf.itemColorShadeProperty()));
		
		
	}
	
	private Label label(String text) {
		Label label = new Label(text);
		label.setPadding(new Insets(5.0,0.0,0.0,0.0));
		return label;
	}
	
	private Slider slider(int min, int max, IntegerProperty prop) {
		Slider sl = new Slider(min, max, min);
		sl.valueProperty().bindBidirectional(prop);
		return sl;
	}
	
	private Slider slider(double min, double max, DoubleProperty prop) {
		Slider sl = new Slider(min, max, min);
		sl.valueProperty().bindBidirectional(prop);
		return sl;
	}
	
	private void add(Node n) {
		getChildren().add(n);
	}
	
	/**
	 * Creates a formatted caption for the Menu
	 * 
	 * @param text
	 * @return
	 */
	private Node caption(String text) {
		Label l = new Label(text);
		l.setStyle("-fx-text-alignment: center; -fx-font-weight: bold");
		Separator s = new Separator();
		s.setPadding(new Insets(8.0, 0.0, 1.0, 0.0));
		return new BorderPane(l, s, null, null, null);
	}
	
	
}
