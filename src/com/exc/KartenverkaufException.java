package com.exc;

public class KartenverkaufException extends RuntimeException {
	public KartenverkaufException() {
		super();
	}
	public KartenverkaufException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);	
	}
	public KartenverkaufException(String arg0, Throwable arg1) {
		super(arg0, arg1);	
	}
	public KartenverkaufException(String text) {
		super(text);	
	}
	public KartenverkaufException(String text, int s) {
		super(text);	
	}
	public KartenverkaufException(Throwable arg0) {
		super(arg0);	
	}
}
