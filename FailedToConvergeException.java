
public class FailedToConvergeException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private int value = 0;
	
	public FailedToConvergeException() {
		super();
	}
	
	public FailedToConvergeException(String message) {
		super(message);
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
}
