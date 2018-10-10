package timelineFx.icalendar;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

import net.fortuna.ical4j.model.component.VEvent;
import timelineFx.data.TimelineItem;

/**
 * {@link TimelineItem} wrapping a ICS {@link VEvent} object.
 * 
 * @author Michael Hochmuth
 *
 */
public class ICSTimelineItem implements TimelineItem{
	private VEvent event;
	private LocalDateTime start,end;
	
	public ICSTimelineItem(VEvent event) {
		this.event = event;

		start = LocalDateTime.ofInstant(event.getStartDate().getDate().toInstant(), 
				ZoneId.systemDefault());
		end = LocalDateTime.ofInstant(event.getEndDate().getDate().toInstant(), 
				ZoneId.systemDefault());
	}
	
	@Override
	public String getName() {
		return event.getSummary().getValue();
	}

	@Override
	public void setName(String name) {
		event.getSummary().setValue(name);
	}

	@Override
	public String getDescription() {
		return event.getDescription().getValue();
	}

	@Override
	public void setDescription(String description) {
		event.getDescription().setValue(description);
	}

	@Override
	public LocalDateTime getStart() {
		return start;
	}

	@Override
	public void setStart(LocalDateTime start) {
		event.getStartDate().setDate(new net.fortuna.ical4j.model.Date(
				start.toEpochSecond(ZoneOffset.UTC)));
		this.start = start;
	}

	@Override
	public boolean hasDuration() {
		return event.getEndDate().getDate().compareTo(event.getStartDate().getDate())==0;
	}

	@Override
	public LocalDateTime getEnd() {
		return end;
	}

	@Override
	public void setEnd(LocalDateTime end) {

		event.getEndDate().setDate(new net.fortuna.ical4j.model.Date(
				end.toEpochSecond(ZoneOffset.UTC)));
		this.end = end;
	}

	
}
