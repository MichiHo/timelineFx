package timelineFx.data;

import java.time.LocalDateTime;

/**
 * A single Item in a Timeline, a Event in Time,
 * with start-date and possibly end-date (if it is to
 * be a non-duration event, <code>hasDuration()</code> should
 * return false.
 * 
 * @author Michael Hochmuth
 *
 */
public class TimelineItem {
	/**
	 * @param name
	 * @param description
	 * @param iD
	 * @param start
	 * @param end
	 */
	public TimelineItem(String name, String description, String id,
			LocalDateTime start, LocalDateTime end) {
		this.name = name;
		this.description = description;
		ID = id;
		this.start = start;
		this.end = end;
	}
	
	public TimelineItem(String id) {
		this.ID = id;
		name = "";
		description = "";
		start = LocalDateTime.now();
		end = LocalDateTime.now();
	}

	private String name;
	private String description;
	private String ID;
	private LocalDateTime start;
	private LocalDateTime end;
	
	
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getStart() {
		return start;
	}

	public void setStart(LocalDateTime start) {
		if(start.isAfter(this.end)) {
			end = start;
		} 
		this.start = start;
	}

	public LocalDateTime getEnd() {
		return end;
	}

	public void setEnd(LocalDateTime end) {
		if(end.isBefore(start)) {
			start=end;
		}
		this.end = end;
	}

	public String getID() {
		return ID;
	}

	/**
	 * The name of this item. Must not be unique
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Change the non-unique name of this item.
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * If this event covers a Range in Time, true
	 * is returned.
	 * @return
	 */
	public boolean hasDuration() {
		return !start.equals(end);
	}
}
