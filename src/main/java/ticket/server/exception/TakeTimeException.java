package ticket.server.exception;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TakeTimeException extends Exception {

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private static final long serialVersionUID = 2498365781290569998L;

	public TakeTimeException(Date takeTime, Date takeBeginTime) {
		super("take time:"
				+ LocalDateTime.ofInstant(takeTime.toInstant(), ZoneId.systemDefault()).format(formatter)
				+ " is before merchant open begin time:"
				+ LocalDateTime.ofInstant(takeBeginTime.toInstant(), ZoneId.systemDefault()).format(formatter));
	}
}
