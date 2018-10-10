package timelineFx.data;

import java.util.List;

/**
 * Interface for Data Classes describing one Timeline-Category.
 * 
 * @author Michael Hochmuth
 *
 */
public interface TimelineCategory {

	/**
	 * Name of this Category / Calendar
	 * @return
	 */
	public String getName();
	
	/**
	 * List of all TimelineItems in this Category
	 * @return
	 */
	public List<TimelineItem> getItems();
}
