package entity;

import java.io.Serializable;

/**
 * @author Administrator
 */
public class Result implements Serializable {

	private boolean success;
	private String message;

	public Result(Boolean success, String message) {
		this.success = success;
		this.message = message;
	}

	public boolean getSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
