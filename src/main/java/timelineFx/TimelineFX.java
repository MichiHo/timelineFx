package timelineFx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarParser;
import net.fortuna.ical4j.data.CalendarParserFactory;
import net.fortuna.ical4j.data.ContentHandler;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import timelineFx.data.TimelineCategory;
import timelineFx.icalendar.ICSContentHandler;
import timelineFx.icalendar.ICalendarTools;
import timelineFx.view.SetupPane;
import timelineFx.view.TimelineView;

/**
 * Main Window for the Timeline-Application.
 * 
 * @author Michael Hochmuth
 *
 */
public class TimelineFX extends Application {
		
	Scene scene;
	BorderPane root;
	TimelineView timelineView;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		root = new BorderPane();
		
		timelineView = new TimelineView();
		root.setCenter(timelineView);
		
		FileInputStream stream = new FileInputStream("testfiles/timelineTest.ics");
		CalendarBuilder b = new CalendarBuilder();
		//timelineView.addCategory(new TimelineCategory(b.build(stream)));
		
		
		SetupPane setupPane = new SetupPane(timelineView);
		root.setLeft(setupPane);
		
		// MENU BAR
		MenuBar menuBar = new MenuBar();
		Menu fileMenu = new Menu("File");
		MenuItem menuItem;
		
		menuItem = new MenuItem("Open File");
		menuItem.setOnAction(e->openFileDialog(primaryStage));
		fileMenu.getItems().add(menuItem);
		
		menuBar.getMenus().add(fileMenu);
		root.setTop(menuBar);
		
		root.getStylesheets().add("timelineStyle.css");
		scene = new Scene(root);
		
		primaryStage.setScene(scene);
		primaryStage.setWidth(1300);
		primaryStage.setHeight(700);
		primaryStage.setOnShown(e->timelineView.zoomToContent());
		primaryStage.show();
	}

	private void openFileDialog(Window window) {
		File file;
		FileChooser chooser = new FileChooser();
		chooser.setInitialDirectory(new File("testfiles"));
		chooser.setTitle("Open File");
		chooser.getExtensionFilters().add(
				new ExtensionFilter("Calendar Files", "*.ics"));
		file = chooser.showOpenDialog(window);
		try {
			TimelineCategory c = ICalendarTools.importICS(file);
			timelineView.addCategory(c);
			timelineView.zoomToContent();
		} catch(ParserException e) {
			statusMessage("Can't open file - file format error.");
			return;
		} catch(IOException e) {
			statusMessage("Can't open file - i/o-error");
			return;
		}
			
	}
	
	
	public void statusMessage(String message) {
		
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
