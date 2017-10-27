package ticket.server.exception;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class OffTimeException extends Exception {

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

	private static final long serialVersionUID = 2498365781290569998L;

	public OffTimeException(LocalTime beginLocalTime, LocalTime endLocalTime) {
		super("off time:"
				+ beginLocalTime.format(formatter)
				+ " :"
				+ endLocalTime.format(formatter));
	}
}
