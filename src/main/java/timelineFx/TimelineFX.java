package timelineFx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarParser;
import net.fortuna.ical4j.data.CalendarParserFactory;
import net.fortuna.ical4j.data.ContentHandler;
import net.fortuna.ical4j.model.Calendar;
import timelineFx.data.TimelineCategory;
import timelineFx.icalendar.ICSContentHandler;
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
		
		CalendarParser parser = CalendarParserFactory.getInstance().createParser();
		ICSContentHandler handler = new ICSContentHandler();
		parser.parse(new FileReader(new File("testfiles/timelineTest.ics")), handler);
		ContentHandler h;
		
		FileInputStream stream = new FileInputStream("testfiles/timelineTest.ics");
		CalendarBuilder b = new CalendarBuilder();
		timelineView.addCategory(new TimelineCategory(b.build(stream)));
		
		root.getStylesheets().add("timelineStyle.css");
		scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setWidth(1300);
		primaryStage.setHeight(700);
		primaryStage.show();
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
