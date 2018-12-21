package timelineFx.view;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cache2k.jcache.provider.generic.storeByValueSimulation.SimpleObjectCopyFactory;
import org.w3c.dom.css.Rect;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import timelineFx.TimelineGeneralConfiguration;
import timelineFx.data.TimelineCategory;
import timelineFx.data.TimelineItem;

/**
 * JavaFX Component for displaying and editing multiple {@link TimelineCategory}s.
 * @author Michael Hochmuth
 *
 */
public class TimelineView extends StackPane{
	private enum DragMode {
		NONE, SELECT_TIME_FRAME, SCROLL
	}
	public enum UIMode {
		VIEW, EDIT
	}
	private static List<ChronoField> fields = Arrays.asList(
			ChronoField.SECOND_OF_MINUTE, ChronoField.MINUTE_OF_HOUR,
			ChronoField.HOUR_OF_DAY,ChronoField.DAY_OF_MONTH,
			ChronoField.MONTH_OF_YEAR,ChronoField.YEAR);
	
	TimelineViewConfiguration conf;
	TimelineGeneralConfiguration generalConf;
	private InvalidationListener confListener = o->reshape();
	
	Color editColor = Color.LIGHTGREEN;
	Color viewChangeColor = Color.CYAN;
	
	// Temporaries for painting
	private long startSec = Long.MIN_VALUE, endSec = Long.MAX_VALUE;
	private ChronoField currentUnit = ChronoField.DAY_OF_MONTH;
	private Map<Double, LocalDateTime> grid = new HashMap<Double,LocalDateTime>();
	/**
	 * The Mode determines how the TimelineView can be interacted with
	 */
	private ObjectProperty<UIMode> uiMode = 
			new SimpleObjectProperty<UIMode>(UIMode.VIEW);
	
	// Temporaries for mouse gestures
	private DragMode dragMode = DragMode.NONE;
	private double dragX1;
	private LocalDateTime dragTime1, dragTime2;
	private Group dragOverlay;
	
	private Pane layerBackground, layerContent, layerContentTitles, layerTop, layerOverlay;
	private Label currentMouseTimeLabel;
	
	List<TimelineCategory> categories = new ArrayList<>();

	public TimelineView(TimelineGeneralConfiguration configuration) {
		generalConf = configuration;
		setConfiguration(new TimelineViewConfiguration());
		endSec = conf.getViewEnd().toEpochSecond(conf.getZoneOffset());
		startSec = conf.getViewStart().toEpochSecond(conf.getZoneOffset());
		
		this.getStyleClass().add(TimelineCSS.CLASS_VIEWER);
		bindReshape(this.widthProperty());
		
		currentMouseTimeLabel = new Label();
		currentMouseTimeLabel.relocate(5000, getHeight()-20.0);
		getChildren().add(currentMouseTimeLabel);
		
		this.widthProperty().addListener((c,o,n)->reshape());
		this.heightProperty().addListener((c,o,n)->reshape());
		
		this.addEventFilter(MouseEvent.MOUSE_MOVED, e->mouseMoved(e));
		this.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> mousePressed(e));
		this.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> mouseDragged(e));
		this.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> mouseReleased(e));
		
		this.addEventFilter(ScrollEvent.SCROLL, e -> {
			scale(e.getX()-getLeftOffset(), e.getDeltaY()*-1);
		});
		this.addEventFilter(MouseEvent.DRAG_DETECTED, e -> {
			if(getUiMode()==UIMode.VIEW && e.isSecondaryButtonDown()) {
				dragMode = DragMode.SELECT_TIME_FRAME;
				dragX1 = e.getX();
			} 
		});
		
		
		
		reshape();
	}
	
	private void mousePressed(MouseEvent e){
		if(e.getButton()==MouseButton.MIDDLE) {
			dragMode = DragMode.SCROLL;
			dragX1 = e.getX();
		}
	}
	
	private void mouseDragged(MouseEvent e) {
		switch(dragMode) {
		case SCROLL:
			double dist = dragX1-e.getX();
			TemporalAmount a = xToTimeAmount(dist);
			
			conf.setViewEnd(conf.getViewEnd().plus(a));
			conf.setViewStart(conf.getViewStart().plus(a));
//			if(e.getX()>dragX1) {
//				// Right scroll
//			} else {
//				// Left Scroll
//				conf.setViewStart(conf.getViewStart().plus(a));
//				conf.setViewEnd(conf.getViewEnd().plus(a));
//			}
			dragX1 = e.getX();
			break;
		case SELECT_TIME_FRAME:
			// SHOW TIME FRAME THING
			double x1 = dragX1, x2 = e.getX();
			if(x2 < x1) {
				x2 = dragX1;
				x1 = e.getX();
			}
			if(dragOverlay==null) {
				dragOverlay = new Group();
				layerOverlay.getChildren().add(dragOverlay);
			}
			dragOverlay.getChildren().clear();
			Rectangle left = new Rectangle(getLeftOffset(), 0, 
					x1-getLeftOffset(), getHeight());
			left.setFill(viewChangeColor.deriveColor(0.0, 1.0, 1.0, 0.4));
			dragOverlay.getChildren().add(left);
			
			Rectangle right = new Rectangle(x2, 0, 
					getWidth()-getRightOffset()-x2, getHeight());
			right.setFill(viewChangeColor.deriveColor(0.0, 1.0, 1.0, 0.4));
			dragOverlay.getChildren().add(right);
		
			break;
		} 
		
	}

	private void mouseReleased(MouseEvent e) {
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
			dragX1 = 0.0;
		} 
		dragMode = DragMode.NONE;
		if(dragOverlay!=null) {
			layerOverlay.getChildren().remove(dragOverlay);
			dragOverlay = null;
		}
	}

	private void mouseMoved(MouseEvent e) {
		currentMouseTimeLabel.relocate(e.getX(), getHeight()-20.0);
		currentMouseTimeLabel.setText(xToTime(e.getX()).toString());
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
	
	/**
	 * Repaints the whole thing from scratch. Might take like >400ms sometimes
	 * but is used heavily
	 */
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
		ToolTip tooltip = new ToolTip();
		
		layerBackground = new Pane();
		layerBackground.addEventFilter(MouseEvent.MOUSE_MOVED, e->{
			tooltip.hide();
			e.consume();
		});
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
		
		layerOverlay.getChildren().add(tooltip);
		
		Color backgroundColor = Color.LIGHTGOLDENRODYELLOW;
		layerBackground.setBackground(new Background(
				new BackgroundFill(backgroundColor, 
						CornerRadii.EMPTY, Insets.EMPTY)));
		
		///////////////////////////////////////////////////////////////////////
		/* Bottom: Timebar with years/months/etc
		 * - Generate metric for Time <-> x-axis
		 * - Decide Grid granularity
		 * - Place Years/Months/Days/Hours/Minutes/Seconds
		 */
		Group timebar = new Group();
		double gridUnits = getDisplayWidth()/conf.getGridUnitWidth();
		if(gridUnits<=0.0) return;
		System.out.println(""+gridUnits+" grid units");
		long unitsPerTick = (int)Math.ceil(seconds/gridUnits);
		System.out.println("would be "+unitsPerTick+" seconds per unit");
		
		long increment = 1;
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
			unitsPerTick/=12;
			currentUnit = ChronoField.YEAR;
			increment = chooseIncrement(unitsPerTick, 1,5,10,50,
					100,250,500,1000,1500);
		}

		System.out.println("choose "+currentUnit+" as unit with increment "
				+increment+" based on "+unitsPerTick);
		double barY = getHeight()-100.0;
		Rectangle timebarRect = new Rectangle(getLeftOffset(), barY, 
				getWidth()-getRightOffset(), conf.getTimeBarWidth());
		timebar.getChildren().add(timebarRect);
		
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
		double x,y,y2;
		Polyline tick;
		Text ticklabel;
		Font minorTickFont = new Font(conf.getItemFontName(), 
				conf.getMinorTickFontSize());
		Font majorTickFont = new Font(conf.getItemFontName(), 
				conf.getMajorTickFontSize());
		y = barY + conf.getTimeBarWidth() + 5.0;
		y2 = y + 5.0 + conf.getMinorTickFontSize();
		while(!start.isAfter(conf.getViewEnd())) {
			x = Math.floor(timeToX(start))+0.5;
			grid.put(x, start);
			tick = new Polyline(x, barY,
					x, barY+conf.getTimeBarWidth()+1);
			tick.setStrokeWidth(1.0);
			tick.setStroke(backgroundColor);
			tick.getStyleClass().add(TimelineCSS.CLASS_TIMEBAR_TICK);
			timebar.getChildren().add(tick);
			
			ticklabel = new Text(""+start.get(currentUnit));
			ticklabel.setFont(minorTickFont);
			ticklabel.relocate(x-ticklabel.getBoundsInLocal().getWidth()/2.0, y);
			ticklabel.getStyleClass().add(TimelineCSS.CLASS_TIMEBAR_TICK_LABEL);
			timebar.getChildren().add(ticklabel);
			
			LocalDateTime next = 
					start.plus(increment, currentUnit.getBaseUnit());
			
			// Major Ticks?
			if(fields.indexOf(currentUnit)+1 < fields.size() && start.get(fields.get(fields.indexOf(currentUnit)+1)) !=
					next.get(fields.get(fields.indexOf(currentUnit)+1))){
				LocalDateTime major = next.with(currentUnit, currentUnit.range().getMinimum());
				if(major.isBefore(conf.getViewEnd())) {
					x = timeToX(major);
					
					tick = new Polyline(x, barY,
							x, barY+conf.getTimeBarWidth() + 1);
					tick.setStrokeWidth(4.0);
					tick.setStroke(backgroundColor);
					tick.getStyleClass().add(TimelineCSS.CLASS_TIMEBAR_TICK);
					timebar.getChildren().add(tick);
					
					String text = "";
					switch(currentUnit) {
					case SECOND_OF_MINUTE:
					case MINUTE_OF_HOUR:
					case HOUR_OF_DAY:
						if(start.getDayOfMonth()==next.getDayOfMonth())
							text = major.format(DateTimeFormatter.ISO_LOCAL_TIME);
						else
							text = major.format(DateTimeFormatter.ISO_LOCAL_DATE);
						break;
					
					case DAY_OF_MONTH:
						if(start.getYear()==next.getYear())
							text = "" + next.getMonth().getDisplayName(
									TextStyle.FULL, conf.getLocale());
						else
							text = "" + next.getYear();
						break;
					
					case MONTH_OF_YEAR:
						text = "" + next.getYear();
						break;
					}
					ticklabel = new Text(text);
					ticklabel.relocate(
							x-ticklabel.getBoundsInLocal().getWidth()/2.0, 
							y2);
					ticklabel.getStyleClass().add(TimelineCSS.CLASS_TIMEBAR_TICK_LABEL);
					ticklabel.setFont(majorTickFont);
					timebar.getChildren().add(ticklabel);

					next = major.plus(increment, currentUnit.getBaseUnit());
				}
				
			}
			
			start = next;
		}
		layerTop.getChildren().add(timebar);
		
		
		///////////////////////////////////////////////////////////////////////
		// Left: Sidebar with category names
		
		Rectangle sidebar = new Rectangle(0.0, 0.0, 
				conf.getSideBarWidth(), getHeight());
		sidebar.setFill(backgroundColor.darker());
		layerTop.getChildren().add(sidebar);
		
		Rectangle rightBar = new Rectangle(getWidth()-conf.getRightBarWidth(),0.0,
				conf.getRightBarWidth(),getHeight());
		rightBar.setFill(backgroundColor.darker());
		layerTop.getChildren().add(rightBar);
		
		// category names come with the items below
		
		
		///////////////////////////////////////////////////////////////////////
		/* Center: TimelineItems !
		 * - Determine Visible Items
		 * - Find 2D Layout & calculate overall height
		 * - Add arrowheads for continuing events
		 */
		
		// lowerY = lowest y of current category, upperY = highest y of c.c.
		double lowerY = barY-conf.getTimebarItemDistance(), upperY = lowerY;
		// x1 = start of event, x2 = end of event, x3 = end of textbox
		double x1, x2, x3;
		
		int layer = 0;
		// For displaying events non-overlapping, layers stores
		// the first free x-coordinate on each height layer
		List<Double> layers = null;
		
		// Used to determine whether to show arrows for protuding events
		boolean leapL, leapR;
		
		// JavaFX-temporaries
		Text text;
		Rectangle textRect, itemRect;
		Font itemFont = Font.font(conf.getItemFontName(), 
				conf.getItemFontSize());
		Font boldFont = Font.font(conf.getItemFontName(), 
				FontWeight.BOLD, 40.0);
		
		for(TimelineCategory cat : categories) {
			Color itemRectColor = cat.getColor();
			Color textRectColor = itemRectColor.interpolate(backgroundColor,
					conf.getItemColorShade());
			
			if(layers!=null) {
				lowerY = upperY-conf.getCategoryDistance();
			}
			layers = new ArrayList<Double>();
			layers.add(-10000.0);
			
			cat.sortByStartDate();
			for(TimelineItem item : cat.getItems()) {
				leapL = false; leapR = false;
				x1 = timeToX(item.getStart());
				x2 = timeToX(item.getEnd());
				if(!item.hasDuration() || x2-x1 < conf.getMinItemWidth())
					x2 = x1 + conf.getMinItemWidth();
				
				if(x1<getLeftOffset()) {
					if(x2 > getWidth()-getRightOffset()) {
						// leaps BOTH
						x1 = getLeftOffset();
						x2 = getWidth()-getRightOffset();
						leapL = true;
						leapR = true;
					} else if(x2 > getLeftOffset()) {
						// leap in from the left
						x1 = getLeftOffset();
						leapL = true;
					} else continue;
				} else if(x1 < getWidth()-getRightOffset()){
					if(x2 > getWidth()-getRightOffset()) {
						// leap to right
						x2 = getWidth()-getRightOffset();
						leapR = true;
					} 
					// else = normal case
				} else continue;
				
				
				
				text = new Text(item.getName());
				text.setFont(itemFont);
				x3 = text.getBoundsInLocal().getWidth()+x1
						+ 2* conf.getItemPadding();
				
				double itemHeight = conf.getItemFlag() + conf.getItemFontSize()
						+ 2*conf.getItemPadding();
				
				// Decide Layer
				layer = 0;
				while(layers.get(layer)>x1) {
					layer++;
					if(layer>=layers.size()) {
						layers.add(x3);
						upperY = lowerY - (layer+1)*
								(conf.getItemDistance() + itemHeight);
						break;
					}
				}
				layers.set(layer, x3);
				
				EventHandler<MouseEvent> mouseOver = e -> {
					tooltip.show(item, e.getX(), e.getY());
					e.consume();
				};
				
				y = lowerY - layer*(conf.getItemDistance() + itemHeight);


				if(leapL) {
					Polygon arrow = new Polygon(
							conf.getSideBarWidth()-5.0,
							y - .5*itemHeight - 0.5*conf.getItemFontSize(),
							conf.getSideBarWidth()-5.0,
							y - .5*itemHeight + 0.5*conf.getItemFontSize(),
							conf.getSideBarWidth()-10.0,
							y - .5*itemHeight);
					arrow.setFill(itemRectColor);
					layerTop.getChildren().add(arrow);
				}
				if(leapR){
					Polygon arrow = new Polygon(
							getWidth()-10.0,
							y - .5*itemHeight - 0.5*conf.getItemFontSize(),
							getWidth()-10.0,
							y - .5*itemHeight + 0.5*conf.getItemFontSize(),
							getWidth()-5.0,
							y - .5*itemHeight);
					arrow.setFill(itemRectColor);
					layerTop.getChildren().add(arrow);
				}
				
				itemRect = new Rectangle(x1, y-itemHeight, x2-x1, 
						itemHeight + lowerY-y);
				itemRect.setFill(itemRectColor);
				//itemRect.setStroke(backgroundColor);
				itemRect.setStrokeWidth(1.0);
				itemRect.addEventFilter(MouseEvent.MOUSE_MOVED, mouseOver);
				layerContent.getChildren().add(itemRect);
				
				
				text.relocate(x1+conf.getItemPadding(), 
						y-itemHeight + conf.getItemPadding());
				if(x3 > x2) {
					textRect = new Rectangle(x2,y-itemHeight,x3-x2,
							conf.getItemFontSize()+2*conf.getItemPadding());
					textRect.setFill(textRectColor);
					textRect.addEventFilter(MouseEvent.MOUSE_MOVED, mouseOver);
					layerContentTitles.getChildren().add(textRect);
				}
				layerContentTitles.getChildren().add(text);	
				
				
			}
			
			Text catText = new Text(cat.getName());
			catText.setFont(boldFont);
			catText.setFill(itemRectColor);
			TextFlow textFlow = new TextFlow(catText);
			textFlow.setLineSpacing(-20.0);
			textFlow.setTextAlignment(TextAlignment.RIGHT);
			BorderPane catNamePane = new BorderPane(textFlow);
			catNamePane.setPrefWidth(getLeftOffset()-40.0);
			
			layerTop.getChildren().add(catNamePane);
			layerTop.layout();
			
			catNamePane.relocate(
					20.0, 
					lowerY-textFlow.getBoundsInLocal().getHeight());
			
			double editorY = lowerY;
			catNamePane.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
				System.out.println("p");
				if(e.getClickCount()==2) {
					TextField a = new TextField(cat.getName());
					if(startEdit(()-> {
						// CANCEL Action
						layerOverlay.getChildren().remove(a);
					})) {
						e.consume();

						a.setEditable(true);
						a.requestFocus();
						layerOverlay.getChildren().add(a);
						a.relocate(20.0, editorY);
						a.addEventFilter(KeyEvent.KEY_TYPED, ev->{
							if(ev.getCode()==KeyCode.ENTER) {
								ev.consume();
								cat.setName(a.getText());
								cancelEdit();
							}
						});
					}
					
				}
			});
			
			
			
		}
		layerOverlay.getChildren().add(currentMouseTimeLabel);
		System.out.println("reshape in " + 
				(System.currentTimeMillis()-measure)+" ms");
	}
	
	private Runnable editCancelAction;
	
	/**
	 * Start a new Edit. This cancels an ongoing Editing process, if present.
	 * @param onCancel Runnable to execute when the new Edit is canceled.
	 * @return false if editing right now isn't allowed
	 */
	private boolean startEdit(Runnable onCancel) {
		if(getUiMode()!=UIMode.EDIT) return false;
		
		cancelEdit();
		editCancelAction = onCancel;
		return true;
	}
	
	/**
	 * If there is an editing operation running, this cancels it.
	 */
	public void cancelEdit() {
		if(editCancelAction!=null) 
			editCancelAction.run();
		editCancelAction = null;
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
		if(categories.isEmpty()) return;
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

	/**
	 * 
	 * @param xPivot
	 * @param amount Negative Zooms in
	 */
	public void scale(double xPivot, double amount) {
		double factor;
		if(xPivot<=getLeftOffset()) 
			factor = 0.0;
		else if(xPivot >= getWidth()-getRightOffset()) 
			factor = 1.0;
		else 
			factor = (xPivot-getRightOffset())/(getWidth()-getRightOffset()-getLeftOffset());
		
		
		Duration a = xToTimeAmount(factor*amount,amount>0);
		Duration b = xToTimeAmount((1.0-factor)*amount,amount>0);
		// To avoid glitching when scrolling at seconds unit
		if(a.getSeconds()==0 && Math.abs(b.getSeconds())<3 
				|| b.getSeconds()==0 && Math.abs(a.getSeconds())<3)
			return;
		
		LocalDateTime newStart = conf.getViewStart().minus(a);
		LocalDateTime newEnd = conf.getViewEnd().plus(b);
		
		conf.setViewStart(newStart);
		conf.setViewEnd(newEnd);

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
	long chooseIncrement(long value, long... increments) {
		long res = increments[increments.length-1];
		for(long i : increments) {
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
		if(startSec>=endSec) return LocalDateTime.now();
		
		long s = startSec + 
				(long)(((x-getLeftOffset())/(getWidth()-getLeftOffset()-getRightOffset()))
						*(endSec-startSec));
		return LocalDateTime.ofEpochSecond(s, 0, conf.getZoneOffset());
	}
	
	Duration xToTimeAmount(double x) {
		return xToTimeAmount(x,false);
		
	}
	
	Duration xToTimeAmount(double x, boolean atLeastOneSecond) {
		if(startSec == endSec) 
			return atLeastOneSecond?Duration.ofSeconds(1):Duration.ZERO;

		long seconds = (long)((endSec-startSec)*(x/getDisplayWidth()));
		if(atLeastOneSecond&&seconds==0) seconds = 1;
		return Duration.ofSeconds(seconds);
	}
	
	double getLeftOffset() {
		return conf.getSideBarWidth();
	}
	
	double getRightOffset() {
		return conf.getRightBarWidth();
	}
	
	double getDisplayWidth() {
		return getWidth()-getLeftOffset()-getRightOffset();
	}

	public TimelineViewConfiguration getConfiguration() {
		return conf;
	}

	public final ObjectProperty<UIMode> uiModeProperty() {
		return this.uiMode;
	}
	

	public final UIMode getUiMode() {
		return this.uiModeProperty().get();
	}
	

	public final void setUiMode(final UIMode uiMode) {
		this.uiModeProperty().set(uiMode);
	}
	
	
}
