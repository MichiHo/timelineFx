package timelineFx.icalendar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Vector;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Summary;
import timelineFx.data.TimelineCategory;
import timelineFx.data.TimelineItem;

/**
 * Class with static methods for importing ICS files as 
 * {@link TimelineCategory}.
 * @author Michael Hochmuth
 *
 */
public class ICalendarTools {
	private static HashMap<String, Integer> properties = new HashMap<>();
	
	/**
	 * Parse the file as ICS file and convert it to a TimelineCategory.
	 * Some data from the ICS file will not be used, only the parts used in
	 * this application.
	 * 
	 * @param file File to parse
	 * @return A Category filled with the data and named after the file
	 * @throws IOException If the file doesn't exist or something.
	 * @throws ParserException If the file isn't a correct ICS file
	 */
	public static TimelineCategory importICS(File file) 
			throws IOException, ParserException {
		FileInputStream stream = new FileInputStream(file);
		CalendarBuilder b = new CalendarBuilder();
		Calendar calendar =  b.build(stream);
		TimelineCategory category = new TimelineCategory(
				file.getName().substring(0, file.getName().lastIndexOf('.')));
		
		Integer v = -1;
		
		for(CalendarComponent c : calendar.getComponents()) {
			if(c instanceof VEvent) {
				category.getItems().add(convertEvent((VEvent)c));
				for(Property p : c.getProperties()) {
					if((v = properties.get(p.getName())) == null)
						v = 0;
					++v;
					properties.put(p.getName(), v);
				}
			}
		}		
		return category;
	}
	
	/**
	 * Converts a ICS-{@link VEvent} to a TimelineItem
	 * @param event
	 * @return
	 */
	public static TimelineItem convertEvent(VEvent event) {
		
		return new TimelineItem(
				str(event.getSummary()),
				str(event.getDescription()),
				str(event.getUid()),
				LocalDateTime.ofInstant(event.getStartDate().getDate().toInstant(), 
						ZoneId.systemDefault()),
				LocalDateTime.ofInstant(event.getEndDate().getDate().toInstant(), 
						ZoneId.systemDefault()));
	}
	
	private static String str(Property p) {
		if(p==null)
			return "";
		return p.getValue();
	}
	
	/**
	 * The class internally counts occurrences of all different properties
	 * present in the parsed ICS-files' events. This function prints the
	 * results to the console.
	 */
	public static void printPropertyStatistics() {
		System.out.println("# Properties:");
		for(String k : properties.keySet()) {
			System.out.println(k + " : " + properties.get(k));
		}
		System.out.println();
	}
}
