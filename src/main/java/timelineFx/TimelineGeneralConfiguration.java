package timelineFx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import timelineFx.view.TimelineViewConfiguration;

/**
 * Class for all other configuration-details of the application, except
 * those for rendering of the timeline. Visual details are stored
 * in {@link TimelineViewConfiguration}.
 * 
 * @author Michael Hochmuth
 *
 */
public class TimelineGeneralConfiguration {
	public static enum GridSnapMode {
		SECONDS,MINUTES,HOURS,DAYS,MONTHS,YEARS, MINOR_TICKS, MAJOR_TICKS
	}
	
	/**
	 * Determine to which unit the cursor should snap on the time axis
	 * while editing items.
	 */
	private ObjectProperty<GridSnapMode> gridSnapMode = 
			new SimpleObjectProperty<>(GridSnapMode.MINOR_TICKS);

	public final ObjectProperty<GridSnapMode> gridSnapModeProperty() {
		return this.gridSnapMode;
	}
	

	public final GridSnapMode getGridSnapMode() {
		return this.gridSnapModeProperty().get();
	}
	

	public final void setGridSnapMode(final GridSnapMode gridSnapMode) {
		this.gridSnapModeProperty().set(gridSnapMode);
	}
	
	
	
}
