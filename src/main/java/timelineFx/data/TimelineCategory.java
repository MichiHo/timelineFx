package timelineFx.data;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javafx.scene.paint.Color;
import timelineFx.data.TimelineCategory;
import timelineFx.data.TimelineItem;
import timelineFx.view.TimelineView;

/**
 * A set of {@link TimelineItem}s associated with a name and other
 * attributes. Displayed with a custom color in a {@link TimelineView}.
 * 
 * @author Michael Hochmuth
 *
 */
public class TimelineCategory {
	private static double defaultColorSaturation = 0.7,
			defaultColorBrightness = 1.0;
	private static List<Color> defaultColors = Arrays.asList(
			Color.hsb(0.0,defaultColorSaturation,defaultColorBrightness),
			Color.hsb(60.0,defaultColorSaturation,defaultColorBrightness),
			Color.hsb(120.0,defaultColorSaturation,defaultColorBrightness),
			Color.hsb(180.0,defaultColorSaturation,defaultColorBrightness),
			Color.hsb(240.0,defaultColorSaturation,defaultColorBrightness),
			Color.hsb(300.0,defaultColorSaturation,defaultColorBrightness));
	private static int defaultColorIndex = 0;
	static Color nextDefaultColor() {
		return defaultColors.get(defaultColorIndex++ %defaultColors.size());
		
	}
	
	private List<TimelineItem> items = new Vector<TimelineItem>();
	private Color color;
	private String name;
	
	
	public TimelineCategory(String name) {
		this.name = name;
		color = nextDefaultColor();
	}
	
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name.trim();
	}

	/**
	 * Sorts the List of Items this Category holds by
	 * their start dates.
	 */
	public void sortByStartDate() {
		items.sort((a,b)->{
			return a.getStart().compareTo(b.getStart());
		});
	}
	
	public List<TimelineItem> getItems() {
		return items;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
	
}
