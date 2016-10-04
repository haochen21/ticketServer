package ticket.server.model.order;

import java.io.Serializable;
import java.net.NetworkInterface;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CartNo implements Serializable {

	private static final int MACHINE_IDENTIFIER;

	private static final short PROCESS_IDENTIFIER;

	private static final int LOW_ORDER_THREE_BYTES = 0x00ffffff;

	private static final AtomicInteger NEXT_COUNTER = new AtomicInteger(new SecureRandom().nextInt());

	private final int timestamp;

	private final int machineIdentifier;

	private final short processIdentifier;

	private final int counter;

	private static final char[] HEX_CHARS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b',
			'c', 'd', 'e', 'f' };

	private static final long serialVersionUID = -5448439004094795956L;

	private final static Logger logger = LoggerFactory.getLogger(CartNo.class);

	public CartNo() {
		this(new Date());
	}

	public CartNo(final Date date) {
		this(dateToTimestampSeconds(date), MACHINE_IDENTIFIER, PROCESS_IDENTIFIER, NEXT_COUNTER.getAndIncrement(),
				false);
	}

	private CartNo(final int timestamp, final int machineIdentifier, final short processIdentifier, final int counter,
			final boolean checkCounter) {
		if ((machineIdentifier & 0xff000000) != 0) {
			throw new IllegalArgumentException(
					"The machine identifier must be between 0 and 16777215 (it must fit in three bytes).");
		}
		if (checkCounter && ((counter & 0xff000000) != 0)) {
			throw new IllegalArgumentException(
					"The counter must be between 0 and 16777215 (it must fit in three bytes).");
		}
		this.timestamp = timestamp;
		this.machineIdentifier = machineIdentifier;
		this.processIdentifier = processIdentifier;
		this.counter = counter & LOW_ORDER_THREE_BYTES;
	}

	public String toHexString() {
		char[] chars = new char[24];
		int i = 0;
		for (byte b : toByteArray()) {
			chars[i++] = HEX_CHARS[b >> 4 & 0xF];
			chars[i++] = HEX_CHARS[b & 0xF];
		}
		return new String(chars);
	}

	public byte[] toByteArray() {
		byte[] bytes = new byte[12];
		bytes[0] = int3(timestamp);
		bytes[1] = int2(timestamp);
		bytes[2] = int1(timestamp);
		bytes[3] = int0(timestamp);
		bytes[4] = int2(machineIdentifier);
		bytes[5] = int1(machineIdentifier);
		bytes[6] = int0(machineIdentifier);
		bytes[7] = short1(processIdentifier);
		bytes[8] = short0(processIdentifier);
		bytes[9] = int2(counter);
		bytes[10] = int1(counter);
		bytes[11] = int0(counter);
		return bytes;
	}

	private static byte int3(final int x) {
		return (byte) (x >> 24);
	}

	private static byte int2(final int x) {
		return (byte) (x >> 16);
	}

	private static byte int1(final int x) {
		return (byte) (x >> 8);
	}

	private static byte int0(final int x) {
		return (byte) (x);
	}

	private static byte short1(final short x) {
		return (byte) (x >> 8);
	}

	private static byte short0(final short x) {
		return (byte) (x);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		CartNo orderId = (CartNo) o;

		if (counter != orderId.counter) {
			return false;
		}
		if (machineIdentifier != orderId.machineIdentifier) {
			return false;
		}
		if (processIdentifier != orderId.processIdentifier) {
			return false;
		}
		if (timestamp != orderId.timestamp) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = timestamp;
		result = 31 * result + machineIdentifier;
		result = 31 * result + (int) processIdentifier;
		result = 31 * result + counter;
		return result;
	}

	@Override
	public String toString() {
		return toHexString();
	}

	static {
		try {
			MACHINE_IDENTIFIER = createMachineIdentifier();
			PROCESS_IDENTIFIER = createProcessIdentifier();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static int dateToTimestampSeconds(final Date time) {
		return (int) (time.getTime() / 1000);
	}

	private static int createMachineIdentifier() {
		// build a 2-byte machine piece based on NICs info
		int machinePiece;
		try {
			StringBuilder sb = new StringBuilder();
			Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
			while (e.hasMoreElements()) {
				NetworkInterface ni = e.nextElement();
				sb.append(ni.toString());
				byte[] mac = ni.getHardwareAddress();
				if (mac != null) {
					ByteBuffer bb = ByteBuffer.wrap(mac);
					try {
						sb.append(bb.getChar());
						sb.append(bb.getChar());
						sb.append(bb.getChar());
					} catch (BufferUnderflowException shortHardwareAddressException) { // NOPMD
						// mac with less than 6 bytes. continue
					}
				}
			}
			machinePiece = sb.toString().hashCode();
		} catch (Throwable t) {
			// exception sometimes happens with IBM JVM, use random
			machinePiece = (new SecureRandom().nextInt());
			logger.warn("Failed to get machine identifier from network interface, using random number instead", t);
		}
		machinePiece = machinePiece & LOW_ORDER_THREE_BYTES;
		return machinePiece;
	}

	// Creates the process identifier. This does not have to be unique per class
	// loader because
	// NEXT_COUNTER will provide the uniqueness.
	private static short createProcessIdentifier() {
		short processId;
		try {
			String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
			if (processName.contains("@")) {
				processId = (short) Integer.parseInt(processName.substring(0, processName.indexOf('@')));
			} else {
				processId = (short) java.lang.management.ManagementFactory.getRuntimeMXBean().getName().hashCode();
			}

		} catch (Throwable t) {
			processId = (short) new SecureRandom().nextInt();
			logger.warn("Failed to get process identifier from JMX, using random number instead", t);
		}

		return processId;
	}

}
