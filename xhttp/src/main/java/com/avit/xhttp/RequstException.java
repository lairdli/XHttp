package com.avit.xhttp;

public class RequstException extends Exception {

	/** 
	 * @Fields serialVersionUID : TODO 
	 */ 
	private static final long serialVersionUID = -1792666634717135362L;

	public RequstException() {
		super();
	}

	public RequstException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public RequstException(String detailMessage) {
		super(detailMessage);
	}

	public RequstException(Throwable throwable) {
		super(throwable);
	}	
}
