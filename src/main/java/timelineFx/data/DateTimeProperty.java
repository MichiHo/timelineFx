package timelineFx.data;

import java.time.LocalDateTime;

import javafx.beans.property.SimpleObjectProperty;

/**
 * JavaFX-Property specialization for {@link LocalDateTime}.
 * 
 * @author Michael Hochmuth
 *
 */
public class DateTimeProperty extends SimpleObjectProperty<LocalDateTime> {
	
	/**
	 * The constructor of {@link DateTimeProperty}
	 * @param initialValue The initial wrapped value.
	 */
	public DateTimeProperty(LocalDateTime initialValue) {
		super(initialValue);
	}
	
}
