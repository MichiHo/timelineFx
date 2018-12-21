package timelineFx;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCombination.Modifier;
import javafx.scene.input.KeyCombination.ModifierValue;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.fortuna.ical4j.data.ParserException;
import timelineFx.data.TimelineCategory;
import timelineFx.icalendar.ICalendarTools;
import timelineFx.view.SetupPane;
import timelineFx.view.TimelineView;
import timelineFx.view.TimelineView.UIMode;

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
	ToggleGroup modeToggles;
	TimelineGeneralConfiguration conf;
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		//globalShortcuts.put(, value)
		
		root = new BorderPane();
		
		
		conf = new TimelineGeneralConfiguration();
		
		timelineView = new TimelineView(conf);
		BorderPane timelinePane = new BorderPane();
		timelinePane.setCenter(timelineView);
		
		
		// MODE SELECT
		modeToggles = new ToggleGroup();
		ToggleButton modeToggleView = new ToggleButton("View");
		ToggleButton modeToggleEdit = new ToggleButton("Edit");
		modeToggles.getToggles().add(modeToggleView);		
		modeToggles.getToggles().add(modeToggleEdit);
		
		timelineView.uiModeProperty().addListener((c,o,n)->{
			switch(n) {
			case EDIT:
				modeToggles.selectToggle(modeToggleEdit);
				
				break;
			case VIEW:
				modeToggles.selectToggle(modeToggleView);
				
				break;
			}
		});
		modeToggles.selectedToggleProperty().addListener((c,o,n)->{
			if(n==modeToggleEdit) timelineView.setUiMode(UIMode.EDIT);
			else if(n==modeToggleView) timelineView.setUiMode(UIMode.VIEW);
		});
		
		timelinePane.setTop(new ToolBar(modeToggleView,modeToggleEdit));
		root.setCenter(timelinePane);
		
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
		scene.getAccelerators().put(
				KeyCombination.valueOf("CTRL+o"), 
				()->openFileDialog(primaryStage));
		primaryStage.setScene(scene);
		primaryStage.setTitle("Timeline");
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
		if(file==null || !file.exists())
			return;
		
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
