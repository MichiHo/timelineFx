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
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
	 * If true, items that are only partially inside the view are
	 * still shown
	 */
	private BooleanProperty showProtudingItems = new SimpleBooleanProperty(true);
	
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
	private DoubleProperty timeBarWidth = new SimpleDoubleProperty(20.0);
	
	/**
	 * The width, in pixels, to which each unit of the Timebar-Grid
	 * tries to snap.
	 */
	private DoubleProperty gridUnitWidth = new SimpleDoubleProperty(100.0);
	
	/**
	 * Distance, in pixels, between the timebar and the first categorie's items
	 */
	private DoubleProperty timebarItemDistance = new SimpleDoubleProperty(10.0);
	
	/**
	 * The width, in pixels, that each painted item takes up at least.
	 * Items with no duration will always have this width.
	 */
	private DoubleProperty minItemWidth = new SimpleDoubleProperty(5.0);
	
	/**
	 * The amount, in pixels, by which the real item-length protudes
	 * downwards from the item's continued label.
	 */
	private DoubleProperty itemFlag = new SimpleDoubleProperty(10.0);

	/**
	 * Distance, in pixels, between layers of Items within one category
	 * (on the y-axis)
	 */
	private DoubleProperty itemDistance = new SimpleDoubleProperty(7.0);

	/**
	 * Padding between Item borders and the Text
	 */
	private DoubleProperty itemPadding = new SimpleDoubleProperty(5.0);
	
	
	private DoubleProperty itemFontSize = new SimpleDoubleProperty(12.0);
	
	private StringProperty itemFontName = new SimpleStringProperty("Corbel");

	/**
	 * The amount, between 0.0 and 1.0, to which the rect under the
	 * item-text that protudes the real time-range of it is shaded
	 * to the background.s
	 */
	private DoubleProperty itemColorShade = new SimpleDoubleProperty(0.5);
	
	/**
	 * Distance, in pixels, between the Items of two categories
	 */
	private DoubleProperty categoryDistance = new SimpleDoubleProperty(20.0);
	
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
	
	public final BooleanProperty showProtudingItemsProperty() {
		return this.showProtudingItems;
	}
	

	public final boolean isShowProtudingItems() {
		return this.showProtudingItemsProperty().get();
	}
	

	public final void setShowProtudingItems(final boolean showProtudingItems) {
		this.showProtudingItemsProperty().set(showProtudingItems);
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

	public final DoubleProperty timebarItemDistanceProperty() {
		return this.timebarItemDistance;
	}
	

	public final double getTimebarItemDistance() {
		return this.timebarItemDistanceProperty().get();
	}
	

	public final void setTimebarItemDistance(final double timebarItemDistance) {
		this.timebarItemDistanceProperty().set(timebarItemDistance);
	}

	public final DoubleProperty minItemWidthProperty() {
		return this.minItemWidth;
	}
	

	public final double getMinItemWidth() {
		return this.minItemWidthProperty().get();
	}
	

	public final void setMinItemWidth(final double minItemWidth) {
		this.minItemWidthProperty().set(minItemWidth);
	}

	public final DoubleProperty itemFontSizeProperty() {
		return this.itemFontSize;
	}
	

	public final double getItemFontSize() {
		return this.itemFontSizeProperty().get();
	}
	

	public final void setItemFontSize(final double itemFontSize) {
		this.itemFontSizeProperty().set(itemFontSize);
	}
	

	public final StringProperty itemFontNameProperty() {
		return this.itemFontName;
	}
	

	public final String getItemFontName() {
		return this.itemFontNameProperty().get();
	}
	

	public final void setItemFontName(final String itemFontName) {
		this.itemFontNameProperty().set(itemFontName);
	}

	public final DoubleProperty itemFlagProperty() {
		return this.itemFlag;
	}
	

	public final double getItemFlag() {
		return this.itemFlagProperty().get();
	}
	

	public final void setItemFlag(final double itemFlag) {
		this.itemFlagProperty().set(itemFlag);
	}
	

	public final DoubleProperty itemDistanceProperty() {
		return this.itemDistance;
	}
	

	public final double getItemDistance() {
		return this.itemDistanceProperty().get();
	}
	

	public final void setItemDistance(final double itemDistance) {
		this.itemDistanceProperty().set(itemDistance);
	}
	

	public final DoubleProperty itemPaddingProperty() {
		return this.itemPadding;
	}
	

	public final double getItemPadding() {
		return this.itemPaddingProperty().get();
	}
	

	public final void setItemPadding(final double itemPadding) {
		this.itemPaddingProperty().set(itemPadding);
	}
	

	public final DoubleProperty categoryDistanceProperty() {
		return this.categoryDistance;
	}
	

	public final double getCategoryDistance() {
		return this.categoryDistanceProperty().get();
	}
	

	public final void setCategoryDistance(final double categoryDistance) {
		this.categoryDistanceProperty().set(categoryDistance);
	}

	public final DoubleProperty itemColorShadeProperty() {
		return this.itemColorShade;
	}
	

	public final double getItemColorShade() {
		return this.itemColorShadeProperty().get();
	}
	

	public final void setItemColorShade(final double itemColorShade) {
		this.itemColorShadeProperty().set(itemColorShade);
	}
	
	
	
	
	
	
	
	
	
	
	
}
