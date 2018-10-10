package timelineFx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.CalendarParser;
import net.fortuna.ical4j.data.CalendarParserFactory;
import net.fortuna.ical4j.data.ContentHandler;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.VEvent;
import timelineFx.icalendar.ICSContentHandler;

public class Rumtesten {
public static void main(String[] args) throws FileNotFoundException, IOException, ParserException {
	CalendarParser parser = CalendarParserFactory.getInstance().createParser();
	Calendar calendar = new Calendar();
	
	ContentHandler handler = new ICSContentHandler();
	
	parser.parse(new FileReader(new File("testfiles/testfile.ics")), handler);
		
	
	//System.out.println("Calendar "+cal.toString());
	
	//CalendarOutputter out = new CalendarOutputter();
	
}
}
