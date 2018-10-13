package timelineFx;

import java.io.File;
import java.io.FileInputStream;
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
import net.fortuna.ical4j.model.property.DtStart;
import timelineFx.icalendar.ICSContentHandler;

public class Rumtesten {
public static void main(String[] args) throws FileNotFoundException, IOException, ParserException, ParseException {
//	CalendarParser parser = CalendarParserFactory.getInstance().createParser();
//	Calendar calendar = new Calendar();
//	
//	ContentHandler handler = new ICSContentHandler();
//	
//	parser.parse(new FileReader(new File("testfiles/testfile.ics")), handler);
//	
	
	FileInputStream stream = new FileInputStream("testfiles/timelineTest.ics");
	CalendarBuilder b = new CalendarBuilder();
	Calendar cal = b.build(stream);
	
	
	int x = 1;
	if((x+=1)==2) {
		System.out.println("ha");
	}
	System.out.println(x);
}
}
