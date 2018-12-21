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
	
	private String name;
	private String description;
	private String ID;
	private LocalDateTime start;
	private LocalDateTime end;
	
	/**
	 * @param name			Displayed Name
	 * @param description	
	 * @param id			ID (should be unique within a category)
	 * @param start			StartDate
	 * @param end			If EndDate = StartDate, this Item is marked as 
	 * 						without duration
	 */
	public TimelineItem(String name, String description, String id,
			LocalDateTime start, LocalDateTime end) {
		this.name = name;
		this.description = description;
		ID = id;
		this.start = start;
		this.end = end;
	}
	
	/**
	 * @param id ID (should be unique within a category)
	 */
	public TimelineItem(String id) {
		this.ID = id;
		name = "";
		description = "";
		start = LocalDateTime.now();
		end = LocalDateTime.now();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getStart() {
		return start;
	}

	/**
	 * Set the start date of this Item. If a date after the EndDate is set, the
	 * EndDate is set to it as well, resulting in an item without duration
	 * @param start StartDate
	 */
	public void setStart(LocalDateTime start) {
		if(start.isAfter(this.end)) {
			end = start;
		} 
		this.start = start;
	}

	public LocalDateTime getEnd() {
		return end;
	}

	/**
	 * Set the end date of this Item. If a date before the StartDate is set, the
	 * StartDate is set to it as well, resulting in an item without duration
	 * @param end EndDate
	 */
	public void setEnd(LocalDateTime end) {
		if(end.isBefore(start)) {
			start=end;
		}
		this.end = end;
	}

	/**
	 * Get the ID, which should be unique within the Category this item is in
	 * @return
	 */
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
