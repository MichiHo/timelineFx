package timelineFx.view;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import timelineFx.data.TimelineCategory;
import timelineFx.data.TimelineItem;

public class TimelineView extends StackPane{
	private enum DragMode {
		NONE, SELECT_TIME_FRAME
	}
	private static List<ChronoField> fields = Arrays.asList(ChronoField.SECOND_OF_MINUTE, ChronoField.MINUTE_OF_HOUR,
			ChronoField.HOUR_OF_DAY,ChronoField.DAY_OF_MONTH,
			ChronoField.MONTH_OF_YEAR,ChronoField.YEAR);
	
	TimelineViewConfiguration conf;
	private InvalidationListener confListener = o->reshape();
	
	// Temporaries for painting
	private long startSec = 0L, endSec = Long.MAX_VALUE;
	private ChronoField currentUnit = ChronoField.DAY_OF_MONTH;
	private Map<Double, LocalDateTime> grid = new HashMap<Double,LocalDateTime>();
	private DragMode dragMode = DragMode.NONE;
	private double dragX1;
	
	private Pane layerBackground, layerContent, layerContentTitles, layerTop, layerOverlay;
	private Label currentMouseTimeLabel;
	
	List<TimelineCategory> categories = new ArrayList<>();

	public TimelineView() {
		this.getStyleClass().add(TimelineCSS.CLASS_VIEWER);
		bindReshape(this.widthProperty());
		setConfiguration(new TimelineViewConfiguration());
		
		currentMouseTimeLabel = new Label();
		currentMouseTimeLabel.relocate(5000, getHeight()-50.0);
		getChildren().add(currentMouseTimeLabel);
		
		this.addEventFilter(MouseEvent.MOUSE_MOVED, e->{
			currentMouseTimeLabel.relocate(e.getX(), getHeight()-50.0);
			currentMouseTimeLabel.setText(xToTime(e.getX()).toString());
		});
		this.addEventFilter(ScrollEvent.SCROLL, e -> {
			scale(e.getX(), e.getDeltaX());
		});
		this.addEventFilter(MouseEvent.DRAG_DETECTED, e -> {
			dragMode = DragMode.SELECT_TIME_FRAME;
			dragX1 = e.getX();
		});
		
		this.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
			if(dragMode==DragMode.SELECT_TIME_FRAME) {
				// SHOW TIME FRAME THING
			}
		});
		
		this.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
			if(dragMode==DragMode.SELECT_TIME_FRAME) {
				
				double x2 = e.getX();
				if(x2<dragX1) {
					double t = x2;
					x2 = dragX1;
					dragX1 = t;
				}
				if(x2!=dragX1) {
					LocalDateTime t1 = xToTime(dragX1);
					LocalDateTime t2 = xToTime(x2);
					conf.setViewStart(t1);
					conf.setViewEnd(t2);
				}
				dragMode = DragMode.NONE;
				dragX1 = 0.0;
			}
		});
		
		
		reshape();
	}
	
	/**
	 * Sets a new configuration to guide this TimelineView's rendering.
	 * It will react instantly to Property-Changes in the Configuration.
	 * @param conf
	 */
	public void setConfiguration(TimelineViewConfiguration conf) {
		if(this.conf != null)
			this.conf.removeListener(confListener);
		
		this.conf = conf;
		
		conf.addListener(confListener);
		reshape();
	}
	
	public void reshape() {
		startSec = conf.getViewStart().toEpochSecond(conf.getZoneOffset());
		endSec = conf.getViewEnd().toEpochSecond(conf.getZoneOffset());
		long measure = System.currentTimeMillis();
		long seconds = endSec-startSec;
		if(seconds == 0) return;
		if(seconds<0) {
			long t = startSec;
			startSec = endSec;
			endSec = t;
			seconds *= -1;
		}
		
		getChildren().clear();
		
		layerBackground = new Pane();
		getChildren().add(layerBackground);
		layerContent = new Pane();
		getChildren().add(layerContent);
		layerContentTitles = new Pane();
		layerContentTitles.setBackground(Background.EMPTY);
		getChildren().add(layerContentTitles);
		layerTop = new Pane();
		layerTop.setBackground(Background.EMPTY);
		getChildren().add(layerTop);
		layerOverlay = new Pane();
		layerOverlay.setBackground(Background.EMPTY);
		getChildren().add(layerOverlay);
		
		Color backgroundColor = Color.LIGHTGOLDENRODYELLOW;
		layerBackground.setBackground(new Background(
				new BackgroundFill(backgroundColor, 
						CornerRadii.EMPTY, Insets.EMPTY)));
		
		// Bottom: Timebar with years/months/etc
		/*
		 * - Generate metric for Time <-> x-axis
		 * - Decide Grid granularity
		 * - Place Years/Months/Days/Hours/Minutes/Seconds
		 */
		Group timebar = new Group();
		int gridUnits = (int)(getDisplayWidth()/conf.getGridUnitWidth());
		if(gridUnits<1) return;
		System.out.println(""+gridUnits+" grid units");
		int unitsPerTick = (int)(seconds/gridUnits);
		System.out.println("would be "+unitsPerTick+" seconds per unit");
		
		int increment = 1;
		if(unitsPerTick < 60) {
			currentUnit = ChronoField.SECOND_OF_MINUTE;
			increment = chooseIncrement(unitsPerTick, 1,5,10,30);
		} else if(	(unitsPerTick/=60) < 60) {
			currentUnit = ChronoField.MINUTE_OF_HOUR;
			increment = chooseIncrement(unitsPerTick, 1,5,10,30);
		} else if(	(unitsPerTick/=60) < 24) {
			currentUnit = ChronoField.HOUR_OF_DAY;
			increment = chooseIncrement(unitsPerTick, 1,3,6,12);
		} else if(	(unitsPerTick/=24) < 30) {
			currentUnit = ChronoField.DAY_OF_MONTH;
			increment = chooseIncrement(unitsPerTick, 1,5,10);
		} else if(	(unitsPerTick/=30) < 12) {
			currentUnit = ChronoField.MONTH_OF_YEAR;
			increment = chooseIncrement(unitsPerTick, 1,3,6);
		} else  {
			unitsPerTick/=365;
			currentUnit = ChronoField.YEAR;
			increment = chooseIncrement(unitsPerTick, 1,5,10,50,
					100,250,500,1000,1500);
		}

		System.out.println("choose "+currentUnit+" as unit with increment "
				+increment+" based on "+unitsPerTick);
		double barY = getHeight()-100.0;
		Line timebarLine = new Line(getLeftOffset(), barY, 
				getWidth()-getRightOffset(), barY);
		timebarLine.getStyleClass().add(TimelineCSS.CLASS_TIMEBAR);
		timebarLine.setStrokeWidth(conf.getTimeBarWidth());
		timebar.getChildren().add(timebarLine);
		
		LocalDateTime start = conf.getViewStart();
		// set all smaller units then the grid unit to zero
		for(int i = fields.indexOf(currentUnit)-1; i >= 0; --i) {
			start = start.with(fields.get(i), fields.get(i).isDateBased()?1L:0L);
		}
		
		// if the thereby made grid-position is actually not visible -> next one
		if(start.isBefore(conf.getViewStart())) {
			start = start.plus(increment,currentUnit.getBaseUnit());
		}
		
		grid.clear();
		double x;
		Polyline tick;
		Label ticklabel;
		while(!start.isAfter(conf.getViewEnd())) {
			x = timeToX(start);
			grid.put(x, start);
			tick = new Polyline(
					x, barY-conf.getTimeBarWidth()*.5, 
					x+conf.getTimeBarWidth()*.5, barY,
					x, barY+conf.getTimeBarWidth()*.5);
			tick.setStrokeWidth(conf.getTimeBarWidth()*0.3);
			tick.setStroke(backgroundColor);
			tick.getStyleClass().add(TimelineCSS.CLASS_TIMEBAR_TICK);
			timebar.getChildren().add(tick);
			
			ticklabel = new Label(""+start.get(currentUnit));
			ticklabel.relocate(x-ticklabel.getWidth()/2.0, barY+30);
			ticklabel.getStyleClass().add(TimelineCSS.CLASS_TIMEBAR_TICK_LABEL);
			timebar.getChildren().add(ticklabel);
			
			
			start = start.plus(increment, currentUnit.getBaseUnit());
		}
		layerTop.getChildren().add(timebar);
		
		//double gridUnit = getDisplayWidth()/gridUnits;
		
		
		
		
		
		// Left: Sidebar with category names
		
		// Center: TimelineItems !
		/*
		 * - Determine Visible Items
		 * - Find 2D Layout & calculate overall height
		 * - Add arrowheads for continuing events
		 */
		
		double baseline = barY-conf.getTimebarItemDistance(), y;
		double x1, x2, x3;
		int layer = 0;
		boolean leapL, leapR;
		Text text;
		Rectangle textRect, itemRect;
		Font itemFont = new Font(conf.getItemFontName(), conf.getItemFontSize());
		
		for(TimelineCategory cat : categories) {
			Color itemRectColor = Color.GREEN;
			Color textRectColor = itemRectColor.interpolate(backgroundColor,
					conf.getItemColorShade());
			
			List<Double> layers = new ArrayList<Double>();
			layers.add(-10000.0);
			
			cat.sortByStartDate();
			for(TimelineItem item : cat.getItems()) {
				leapL = false; leapR = false;
				x1 = timeToX(item.getStart());
				x2 = timeToX(item.getEnd());
				if(!item.hasDuration() || x2-x1 < conf.getMinItemWidth())
					x2 = x1 + conf.getMinItemWidth();
				
				if(x1<getLeftOffset()) {
					if(x2 > getRightOffset()) {
						// leaps BOTH
						x1 = getLeftOffset();
						x2 = getRightOffset();
						leapL = true;
						leapR = true;
					} else if(x2 > getLeftOffset()) {
						// leap in from the left
						x1 = getLeftOffset();
						leapL = true;
					}
				} else if(x1 < getWidth()-getRightOffset()){
					if(x2 > getWidth()-getRightOffset()) {
						// leap to right
						x2 = getWidth()-getRightOffset();
						leapR = true;
					} 
					// else = normal case
				} else continue;
				
				// TODO show leap arrows
				
				text = new Text(item.getName());
				text.setFont(itemFont);
				x3 = text.getBoundsInLocal().getWidth()+x1
						+ 2* conf.getItemPadding();
				
				// Decide Layer
				layer = 0;
				while(layers.get(layer)>x1) {
					layer++;
					if(layer>=layers.size()) {
						layers.add(x3);
						break;
					}
				}
				layers.set(layer, x3);
				
				double itemHeight = conf.getItemFlag() + conf.getItemFontSize()
						+ 2*conf.getItemPadding();
				
				y = baseline -conf.getTimebarItemDistance() 
						- layer*(conf.getItemDistance()	+ itemHeight);
				
				itemRect = new Rectangle(x1, y-itemHeight, x2-x1, 
						itemHeight + baseline-y);
				itemRect.setFill(itemRectColor);
				layerContent.getChildren().add(itemRect);
				
				
				text.relocate(x1+conf.getItemPadding(), 
						y-itemHeight + conf.getItemPadding());
				if(x3 > x2) {
					textRect = new Rectangle(x2,y-itemHeight,x3-x2,
							conf.getItemFontSize()+2*conf.getItemPadding());
					textRect.setFill(textRectColor);
					layerContentTitles.getChildren().add(textRect);
				}
				layerContentTitles.getChildren().add(text);	
				
				
			}
		}
		layerOverlay.getChildren().add(currentMouseTimeLabel);
		System.out.println("reshape in " + 
				(System.currentTimeMillis()-measure)+" ms");
	}
	
	private void registerEventHandlers(TimelineItem item, Node node) {
		
	}
	
	/**
	 * Adds the given {@link TimelineCategory} to the
	 * list of categories this TimelineView shows.
	 * @param cat
	 */
	public void addCategory(TimelineCategory cat) {
		if(!categories.contains(cat)) {
			categories.add(cat);
			reshape();
		}
	}
	
	/**
	 * Zooms on the time-axis to fit all Items in all Categories inside
	 */
	public void zoomToContent() {
		LocalDateTime start = LocalDateTime.MAX;
		LocalDateTime end = LocalDateTime.MIN;
		for(TimelineCategory cat : categories) {
			for(TimelineItem item : cat.getItems()) {
				if(item.getStart().isBefore(start))
					start = item.getStart();
				if(item.getEnd().isAfter(end))
					end = item.getEnd();
			}
		}
		conf.setViewStart(start);
		conf.setViewEnd(end);
		reshape();
	}

	public void scale(double xPivot, double amount) {
		double factor;
		if(xPivot<=getLeftOffset()) 
			factor = 0.0;
		else if(xPivot >= getWidth()-getRightOffset()) 
			factor = 1.0;
		else 
			factor = (xPivot-getRightOffset())/(getWidth()-getRightOffset()-getLeftOffset());
		
		System.out.println("Factor "+factor);
		LocalDateTime newStart = xToTime(getLeftOffset()+factor*amount*100.0);
		LocalDateTime newEnd = xToTime(getWidth()-getRightOffset()-(1.0-factor)*amount*100.0);
		
		conf.setViewStart(newStart);
		conf.setViewEnd(newEnd);

		startSec = conf.getViewStart().toEpochSecond(conf.getZoneOffset());
		endSec = conf.getViewEnd().toEpochSecond(conf.getZoneOffset());
	}
	
	private void bindReshape(Observable... p) {
		for(Observable pp : p)
			pp.addListener(c->reshape());
	}
	
	/**
	 * Find the smallest of the given increment-values that's bigger/equal to 
	 * the given value
	 * @param value 
	 * @param increments Values (in ascending order!)
	 * @return One value from the given increments
	 */
	int chooseIncrement(int value, int... increments) {
		int res = increments[increments.length-1];
		for(int i : increments) {
			if(i >= value && i < res) {
				res = i;
			}
		}
		return res;
	}
	
	double timeToX(LocalDateTime t) {
		if(startSec==endSec) return 0.0;
		
		long s = t.toEpochSecond(conf.getZoneOffset());
		return getLeftOffset() + 
				(getWidth()-getLeftOffset()-getRightOffset())*(s-startSec)/(endSec-startSec);
		
	}
	
	LocalDateTime xToTime(double x) {
		if(startSec==endSec) return LocalDateTime.MIN;
		
		long s = startSec + 
				(long)(((x-getLeftOffset())/(getWidth()-getLeftOffset()-getRightOffset()))
						*(endSec-startSec));
		return LocalDateTime.ofEpochSecond(s, 0, conf.getZoneOffset());
	}
	
	
	double getLeftOffset() {
		return conf.getSideBarWidth();
	}
	
	double getRightOffset() {
		return 10.0;
	}
	
	double getDisplayWidth() {
		return getWidth()-getLeftOffset()-getRightOffset();
	}

	public TimelineViewConfiguration getConfiguration() {
		return conf;
	}
	
}
