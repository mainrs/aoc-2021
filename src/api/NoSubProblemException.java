package api;

public class NoSubProblemException extends RuntimeException {
	public NoSubProblemException() {
		super();
	}

	public NoSubProblemException(Exception e) {
		super(e);
	}
}
