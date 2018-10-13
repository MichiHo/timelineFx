package timelineFx.view;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import timelineFx.data.TimelineCategory;
import timelineFx.data.TimelineItem;

public class TimelineView extends Pane{
	private enum GridUnit {
		YEARS(365*30*24*60*60),MONTHS(30*24*60*60),DAYS(24*60*60),HOURS(60*60),MINUTES(60),SECONDS(1);
		public final long seconds;
		private GridUnit(long s) {seconds = s;}
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
	
	List<TimelineCategory> categories = new ArrayList<>();

	public TimelineView() {
		this.getStyleClass().add(TimelineCSS.CLASS_VIEWER);
		bindReshape(this.widthProperty());
		setConfiguration(new TimelineViewConfiguration());
		
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
		
		startSec = conf.getViewStart().toEpochSecond(conf.getZoneOffset());
		endSec = conf.getViewEnd().toEpochSecond(conf.getZoneOffset());
		conf.viewStartProperty().addListener(
				(c,o,n)->{startSec = n.toEpochSecond(conf.getZoneOffset());});
		conf.viewEndProperty().addListener(
				(c,o,n)->{endSec = n.toEpochSecond(conf.getZoneOffset());});
		
		conf.addListener(confListener);
		reshape();
	}
	
	public void reshape() {
		long seconds = endSec-startSec;
		if(seconds == 0) return;
		if(seconds<0) {
			long t = startSec;
			startSec = endSec;
			endSec = t;
			seconds *= -1;
		}
		
		getChildren().clear();
		
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
			currentUnit = ChronoField.YEAR;
			increment = chooseIncrement(unitsPerTick, 1,5,10,50,100,250,500,1000,1500);
		}

		System.out.println("choose "+currentUnit+" as unit with increment "+increment+" based on "+unitsPerTick);
		double barY = getHeight()-100.0;
		Line timebarLine = new Line(getLeftOffset(), barY, getWidth()-getRightOffset(), barY);
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
		int i = 0;
		while(!start.isAfter(conf.getViewEnd())) {
			x = timeToX(start);
			grid.put(x, start);
			tick = new Polyline(
					x, barY-conf.getTimeBarWidth()*.5, 
					x+conf.getTimeBarWidth()*.5, barY,
					x, barY+conf.getTimeBarWidth()*.5);
			tick.setStrokeWidth(conf.getTimeBarWidth()*0.3);
			tick.setStroke(Color.WHITE);
			tick.getStyleClass().add(TimelineCSS.CLASS_TIMEBAR_TICK);
			timebar.getChildren().add(tick);
			
			ticklabel = new Label(""+start.get(currentUnit));
			ticklabel.relocate(x-ticklabel.getWidth()/2.0, barY+30);
			ticklabel.getStyleClass().add(TimelineCSS.CLASS_TIMEBAR_TICK_LABEL);
			timebar.getChildren().add(ticklabel);
			
			
			++i;
			start = start.plus(increment, currentUnit.getBaseUnit());
		}
		getChildren().add(timebar);
		
		//double gridUnit = getDisplayWidth()/gridUnits;
		
		
		
		
		
		// Left: Sidebar with category names
		
		// Center: TimelineItems !
		/*
		 * - Determine Visible Items
		 * - Find 2D Layout & calculate overall height
		 * - Add arrowheads for continuing events
		 */
		
		double baseline = barY-conf.getTimebarEventDistance();
		Group confItems;
		double x1, x2;
		for(TimelineCategory cat : categories) {
			confItems = new Group();
			for(TimelineItem item : cat.getItems()) {
				x1 = timeToX(item.getStart());
				x2 = timeToX(item.getEnd());
				if(x1<getRightOffset()) {
					// might leap in from the left
					if(x2 > getRightOffset()) {
						
					}
				} else {
					if(x2 > getWidth()-getRightOffset()) {
						// leap to right
					} else {
						Label l = new Label(item.getName());
						Rectangle r = new Rectangle(x1,baseline,l.getWidth(),l.getHeight());
						r.setFill(Color.GREEN);
						l.relocate(x1, baseline);
						confItems.getChildren().add(r);
						confItems.getChildren().add(l);
					}
				}
				
			}
			getChildren().add(confItems);
		}
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
	
	private void bindReshape(Observable... p) {
		for(Observable pp : p)
			pp.addListener(c->reshape());
	}
	
	/**
	 * Find the smallest of the given increments that's bigger/equal to 
	 * the given value
	 * @param value
	 * @param increments
	 * @return One value from the given increments
	 */
	int chooseIncrement(int value, int... increments) {
		System.out.print("chInc for "+value+" : ");
		int res = 10000;
		for(int i : increments) {
			if(i >= value && i < res) {
				res = i;
				System.out.print(""+res+" ");
			}
		}
		System.out.println();
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
				(long)((x-getLeftOffset())/(getWidth()-getLeftOffset()-getRightOffset()))*(endSec-startSec);
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
	
}
