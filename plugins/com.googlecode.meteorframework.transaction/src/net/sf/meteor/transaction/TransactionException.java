package net.sf.meteor.transaction;

public class TransactionException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public TransactionException(String message, Throwable cause) {
		super(message, cause);
	}

	public TransactionException(String message) {
		super(message);
	}

	public TransactionException(Throwable cause) {
		super(cause.getMessage(), cause);
	}
}
