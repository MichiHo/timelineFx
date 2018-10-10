package timelineFx.data;

import java.time.LocalDateTime;

public interface TimelineItem {
	
	public String getName();
	
	public void setName(String name);
	
	public String getDescription();
	
	public void setDescription(String description);
	
	public LocalDateTime getStart();
	
	public void setStart(LocalDateTime start);
	
	public boolean hasDuration();
	
	public LocalDateTime getEnd();
	
	public void setEnd(LocalDateTime end);
}
