package networking;

/**
 * This excepition is thrown by the Message class when it is deserialized and
 * the checksums do not match
 */
public class MessageCorruptException extends Exception {
	private static final long serialVersionUID = 1L;

	public MessageCorruptException(String descrip) {
		super(descrip);
	}
}
