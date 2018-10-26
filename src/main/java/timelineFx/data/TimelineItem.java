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
public interface TimelineItem {
	/**
	 * The name of this item. Must not be unique
	 * @return
	 */
	public String getName();
	
	/**
	 * Change the non-unique name of this item.
	 * @param name
	 */
	public void setName(String name);
	
	/**
	 * ID for this Item. Must be unique within a 
	 * Timeline
	 * @return
	 */
	public String getID();
	
	public String getDescription();
	
	public void setDescription(String description);
	
	public LocalDateTime getStart();
	
	public void setStart(LocalDateTime start);
	
	/**
	 * If this event covers a Range in Time, true
	 * is returned.
	 * @return
	 */
	public boolean hasDuration();
	
	/**
	 * Returns the end-date of this Item. If the Item
	 * has no Duration, the start-datetime is returned.
	 * @return
	 */
	public LocalDateTime getEnd();
	
	public void setEnd(LocalDateTime end);
}
