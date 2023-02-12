package org.ncd.raspserver.model;

public class MyException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;
	
	private String warningMessage;
	
	public MyException(String warningMessage) {
		super(warningMessage);
		this.warningMessage = warningMessage;
	}
	

	public String getWarningMessage() {
		return warningMessage;
	}

	public void setWarningMessage(String warningMessage) {
		this.warningMessage = warningMessage;
	}

}
