package timelineFx.icalendar;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang3.tuple.Pair;

import net.fortuna.ical4j.data.ContentHandler;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.ParameterListFactory;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Name;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import timelineFx.data.TimelineCategory;

/**
 * ContentHandler-Implementation picking the information out of ICS files, that i need
 * 
 * @author Michael Hochmuth
 *
 */
public class ICSContentHandler implements ContentHandler {
	enum State {
		NO_CALENDAR,
		CALENDAR,
		CALENDAR_PROPERTY,
		COMPONENT,
		PROPERTY
	}
	static final String CODE_VEVENT = "VEVENT";

	// Temporary Data
	Calendar calendar;
	List<Calendar> calendarList;
	VEvent event;
	String propertyValue, propertyType;
	private String paramName;
	boolean useParam = false;
	boolean useProperty = false;


	State state = State.NO_CALENDAR;
	Map<String, Pair<String, String>> propertyParamMap;
	HashSet<String> comPropertiesOfInterest;
	HashSet<String> calPropertiesOfInterest;


	public ICSContentHandler() {
		calendar = new Calendar();
		calendarList = new Vector<Calendar>();
		comPropertiesOfInterest = new HashSet<String>();
		comPropertiesOfInterest.add("DTSTART");
		comPropertiesOfInterest.add("DTEND");
		comPropertiesOfInterest.add("SUMMARY");
		comPropertiesOfInterest.add("DESCRIPTION");
		comPropertiesOfInterest.add("UID");
		

		calPropertiesOfInterest = new HashSet<String>();
		calPropertiesOfInterest.add("X-WR-CALNAME");

		propertyParamMap = new HashMap<String, Pair<String, String>>();
		//		propertyParamMap.put("DTSTART", Pair.of("VALUE", "DATE"));
		//		propertyParamMap.put("DTEND", Pair.of("VALUE", "DATE"));
	}
	
	public List<Calendar> getCreatedCalendars(){
		return calendarList;
	}

	@Override
	public void startCalendar() {
		if(state!=State.NO_CALENDAR)
			throw new IllegalStateException();
		state = State.CALENDAR;

		deepLog("# Start Calendar");
	}
	@Override
	public void endCalendar() {
		if(state!=State.CALENDAR)
			throw new IllegalStateException();
		state = State.NO_CALENDAR;

		commitCalendar();
		deepLog("# End Calendar");
	}
	@Override
	public void startComponent(String arg0) {
		if(state!=State.CALENDAR)
			throw new IllegalStateException();
		state = State.COMPONENT;

		deepLog("Start Component "+arg0);
		if(arg0.equals(CODE_VEVENT)) {
			event = new VEvent();
		}
	}
	@Override
	public void endComponent(String arg0) {
		if(state!=State.COMPONENT)
			throw new IllegalStateException();
		state = State.CALENDAR;

		commitComponent();
		
		event = null;

		deepLog("End Component "+arg0);
	}
	@Override
	public void startProperty(String type) {
		paramName = "";
		if(state==State.COMPONENT) {
			state = State.PROPERTY;
			if( event!=null && comPropertiesOfInterest.contains(type)) {
				useProperty = true;
				propertyType = type;
				if(!propertyParamMap.containsKey(type)) {
					useParam = true;
				}
			}
		}else if(state==State.CALENDAR) {
			state = State.CALENDAR_PROPERTY;
			if(calPropertiesOfInterest.contains(type)) {
				useProperty = true;
				propertyType = type;
				useParam = true;
			}
		} else
			throw new IllegalStateException();

		deepLog("\tStart Property "+type);
	}
	@Override
	public void endProperty(String type) {
		if(state==State.PROPERTY)
			state = State.COMPONENT;
		else if(state == State.CALENDAR_PROPERTY)
			state = State.CALENDAR;
		else
			throw new IllegalStateException();

		commitProperty();

		useProperty = false;
		useParam = false;
		propertyType = null;
		propertyValue = null;
		deepLog("\tEnd Property "+type);
	}

	@Override
	public void parameter(String first, String second) throws URISyntaxException {
		if(state!=State.PROPERTY && state != State.CALENDAR_PROPERTY)
			throw new IllegalStateException();

		if(useParam || (propertyParamMap.containsKey(propertyType) && 
				propertyParamMap.get(propertyType).equals(Pair.of(first, second)))) {
			useParam = true;
			paramName = second;
		} else
			useParam = false;
		deepLog("\t\tParameter "+first+" || "+second);
	}


	@Override
	public void propertyValue(String value)
			throws URISyntaxException, ParseException, IOException {
		if(state!=State.PROPERTY && state != State.CALENDAR_PROPERTY)
			throw new IllegalStateException();

		if(useParam && propertyValue==null) {
			propertyValue = value;
			useParam = false;
		}
		deepLog("\t\t\tValue="+propertyValue);
	}






	private void deepLog(String m) {
		//System.out.println(m);
	}

	private void commitProperty() {
		if(useProperty) {
			//System.out.println("\t\t"+propertyType+"="+propertyValue);
			Property p;
			if(state==State.COMPONENT) {
				try {
					switch(propertyType) {
					case "DTSTART":
						if(!paramName.isEmpty()) {
							p = new DtStart();
							DtStart t = new DtStart();
							t.getParameters().add(new Value(paramName));
							t.setValue(propertyValue);
						} else
							p = new DtStart(propertyValue);
						break;

					case "DTEND":
						p = new DtEnd(propertyValue);
						break;

					case "SUMMARY":
						p = new Summary(propertyValue);
						break;

					case "DESCRIPTION":
						p = new Description(propertyValue);
						break;

					case "UID":
						p = new Uid(propertyValue);
						break;
						
					default:
						return;
					}
					//FSystem.out.println("\t"+p);
					event.getProperties().add(p);
				} catch(ParseException e) {
					System.out.println("\tEXCEPTION "+e.getMessage());
					return;
				}
			} else if (state == State.CALENDAR) {
				
					switch(propertyType) {
					case "X-WR-CALNAME":
						p = new Name(propertyValue);
						break;
						
					default:
						return;
					}
				calendar.getProperties().add(p);
			}
		}
	}

	private void commitComponent() {
		if(event != null) {
			event.validate();
			calendar.getComponents().add(event);
			
			//System.out.println("\tcommit (now "+calendar.getComponents().size()+" components)");
		}
	}

	private void commitCalendar() {
		//System.out.println("COMMIT CALENDAR "+calendar.getProperties());
		calendarList.add(calendar);
	}
}
