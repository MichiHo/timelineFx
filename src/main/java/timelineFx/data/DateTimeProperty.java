package timelineFx.data;

import java.time.LocalDateTime;

import javafx.beans.property.SimpleObjectProperty;

public class DateTimeProperty extends SimpleObjectProperty<LocalDateTime> {
	public DateTimeProperty(LocalDateTime initialValue) {
		super(initialValue);
	}
	
}
