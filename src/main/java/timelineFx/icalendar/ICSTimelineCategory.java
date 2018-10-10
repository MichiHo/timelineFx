package timelineFx.icalendar;

import java.util.List;
import java.util.Vector;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import timelineFx.data.TimelineCategory;
import timelineFx.data.TimelineItem;

public class ICSTimelineCategory implements TimelineCategory{
	private Calendar calendar;
	private List<TimelineItem> items;
	
	public ICSTimelineCategory(Calendar cal) {
		calendar = cal;
		items = new Vector<TimelineItem>();
	}
	
	@Override
	public String getName() {
		Property p = calendar.getProperty("NAME");
		if(p!=null) {
			return p.getValue();
		} else {
			return "";
		}
	}

	@Override
	public List<TimelineItem> getItems() {
		return items;
	}
	
	
}
