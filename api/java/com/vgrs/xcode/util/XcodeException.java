/*
 * (c) VeriSign Inc., 2005, All rights reserved
 */

package com.vgrs.xcode.util;

/**
 * This object implements all error conditions from the IDN SDK.
 */
public class XcodeException extends Exception {
	private static final long serialVersionUID = 128648304940948516L;
	private final int code;
	private final String message;
	private final Throwable parent;


	/**
	 * Construct a xcode exception with an error code and detailed message
	 * 
	 * @param code
	 *        an error code
	 * @param message
	 *        detailed message
	 */
	public XcodeException ( int code, String message ) {
		this( code, message, null );
	}


	/**
	 * Construct a xcode exception chaining the supplied XcodeExcpetion and
	 * another Throwable object.
	 * 
	 * @param x
	 *        a XcodeException object
	 * @param parent
	 *        a Throwable object
	 */
	public XcodeException ( XcodeException x, Throwable parent ) {
		this( x.code, x.message, parent );
	}


	/**
	 * Construct a xcode exception chaining the supplied XcodeExcpetion and detail
	 * message.
	 * 
	 * @param x
	 *        a XcodeException object
	 * @param message
	 *        detailed message
	 */
	public XcodeException ( XcodeException x, String message ) {
		this( x.code, x.message + message, x.parent );
	}


	/**
	 * Construct a xcode exception with an error code, and chaining the supplied
	 * XcodeExcpetion and a Throwable object detail message.
	 * 
	 * @param code
	 *        An XcodeException object
	 * @param message
	 *        A detailed message
	 * @param parent
	 *        A Throwable object
	 */
	public XcodeException ( int code, String message, Throwable parent ) {
		super( message );
		this.code = code;
		this.message = message;
		this.parent = parent;
	}


	/**
	 * Obtain error code of this XcodeExcpetion
	 * 
	 * @return int error code
	 */
	public int getCode () {
		return this.code;
	}


	/**
	 * Obtain detailed message of this XcodeExcpetion
	 * 
	 * @return String error message
	 */
	@Override
	public String getMessage () {
		String output = new String();
		if ( this.message != null ) {
			output += this.message;
		}
		if ( this.parent != null ) {
			output += "\n -> " + this.parent.getClass().getName() + ": ";

			final String msg = this.parent.getMessage();
			if ( msg != null ) {
				output += msg;
			}
		}
		return output;
	}
}