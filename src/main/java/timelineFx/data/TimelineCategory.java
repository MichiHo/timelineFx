package timelineFx.data;

import java.util.List;
import java.util.Vector;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import timelineFx.data.TimelineCategory;
import timelineFx.data.TimelineItem;
import timelineFx.icalendar.ICSTimelineItem;

public class TimelineCategory {
	private Calendar calendar;
	private List<TimelineItem> items;
	
	public TimelineCategory(Calendar cal) {
		calendar = cal;
		items = new Vector<TimelineItem>();
		for(CalendarComponent c : calendar.getComponents()) {
			if(c instanceof VEvent) {
				items.add(new ICSTimelineItem((VEvent)c));
			}
		}
	}
	
	
	public String getName() {
		Property p = calendar.getProperty("NAME");
		if(p!=null) {
			return p.getValue();
		} else {
			return "";
		}
	}

	
	public List<TimelineItem> getItems() {
		return items;
	}
	
	
}
