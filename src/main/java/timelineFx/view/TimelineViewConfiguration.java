package timelineFx.view;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Vector;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import timelineFx.data.DateTimeProperty;

/**
 * Class with all configuration needed (besides the Stylesheet)
 * for showing timelines (partially or whole).
 * 
 * @author Michael Hochmuth
 *
 */
public class TimelineViewConfiguration {
	private List<InvalidationListener> listeners = new Vector<InvalidationListener>();
	/**
	 * If true, events that are only partially inside the view are
	 * still shown
	 */
	private BooleanProperty showProtudingEvents = new SimpleBooleanProperty(true);
	
	/**
	 * First Displayed Time in the current View
	 */
	private DateTimeProperty viewStart = new DateTimeProperty(LocalDateTime.of(2018, 1, 1, 0, 0));
	
	/**
	 * Last Displayed Time in the current View
	 */
	private DateTimeProperty viewEnd = new DateTimeProperty(LocalDateTime.now());
	
	/**
	 * {@link ZoneOffset} to be used to place the {@link LocalDateTime}s
	 */
	private ObjectProperty<ZoneOffset> zoneOffset = 
			new SimpleObjectProperty<ZoneOffset>(ZoneOffset.UTC);

	/**
	 * Width of the SideBar with the category names
	 */
	private DoubleProperty sideBarWidth = new SimpleDoubleProperty(200.0);
	
	/**
	 * Width of the Line that is the TimeBar, in pixels
	 */
	private DoubleProperty timeBarWidth = new SimpleDoubleProperty(50.0);
	
	/**
	 * The width, in pixels, to which each unit of the Timebar-Grid
	 * tries to snap.
	 */
	private DoubleProperty gridUnitWidth = new SimpleDoubleProperty(100.0);
	

	private DoubleProperty timebarEventDistance = new SimpleDoubleProperty(100.0);
	
	
	public TimelineViewConfiguration() {
		InvalidationListener l = o -> notifyListeners(o);
		
		// Add listeners to all observable fields
		for(Field f : this.getClass().getDeclaredFields()) {
			if(Observable.class.isAssignableFrom(f.getType())) {
				try {
					((Observable)f.get(this)).addListener(l);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void notifyListeners(Observable o) {
		for(InvalidationListener l : listeners) {
			l.invalidated(o);
		}
	}
	
	/**
	 * Adds an {@link InvalidationListener} to be notified
	 * of any change to one of this configuration's fields
	 * @param l
	 */
	public void addListener(InvalidationListener l) {
		listeners.add(l);
	}
	
	/**
	 * Removes the given Listener if present, else does nothing.
	 * @param l
	 */
	public void removeListener(InvalidationListener l) {
		listeners.remove(l);
	}
	
	public final BooleanProperty showProtudingEventsProperty() {
		return this.showProtudingEvents;
	}
	

	public final boolean isShowProtudingEvents() {
		return this.showProtudingEventsProperty().get();
	}
	

	public final void setShowProtudingEvents(final boolean showProtudingEvents) {
		this.showProtudingEventsProperty().set(showProtudingEvents);
	}


	public final DateTimeProperty viewStartProperty() {
		return this.viewStart;
	}
	


	public final LocalDateTime getViewStart() {
		return this.viewStartProperty().get();
	}
	


	public final void setViewStart(final LocalDateTime viewStart) {
		this.viewStartProperty().set(viewStart);
	}
	


	public final DateTimeProperty viewEndProperty() {
		return this.viewEnd;
	}
	


	public final LocalDateTime getViewEnd() {
		return this.viewEndProperty().get();
	}
	


	public final void setViewEnd(final LocalDateTime viewEnd) {
		this.viewEndProperty().set(viewEnd);
	}


	public final ObjectProperty<ZoneOffset> zoneOffsetProperty() {
		return this.zoneOffset;
	}
	


	public final ZoneOffset getZoneOffset() {
		return this.zoneOffsetProperty().get();
	}
	


	public final void setZoneOffset(final ZoneOffset zoneOffset) {
		this.zoneOffsetProperty().set(zoneOffset);
	}


	public final DoubleProperty sideBarWidthProperty() {
		return this.sideBarWidth;
	}
	


	public final double getSideBarWidth() {
		return this.sideBarWidthProperty().get();
	}
	


	public final void setSideBarWidth(final double sideBarWidth) {
		this.sideBarWidthProperty().set(sideBarWidth);
	}


	public final DoubleProperty gridUnitWidthProperty() {
		return this.gridUnitWidth;
	}
	


	public final double getGridUnitWidth() {
		return this.gridUnitWidthProperty().get();
	}
	


	public final void setGridUnitWidth(final double gridUnitWidth) {
		this.gridUnitWidthProperty().set(gridUnitWidth);
	}

	public final DoubleProperty timeBarWidthProperty() {
		return this.timeBarWidth;
	}
	

	public final double getTimeBarWidth() {
		return this.timeBarWidthProperty().get();
	}
	

	public final void setTimeBarWidth(final double timeBarWidth) {
		this.timeBarWidthProperty().set(timeBarWidth);
	}

	public final DoubleProperty timebarEventDistanceProperty() {
		return this.timebarEventDistance;
	}
	

	public final double getTimebarEventDistance() {
		return this.timebarEventDistanceProperty().get();
	}
	

	public final void setTimebarEventDistance(final double timebarEventDistance) {
		this.timebarEventDistanceProperty().set(timebarEventDistance);
	}
	
	
	
	
	
	
	
}
