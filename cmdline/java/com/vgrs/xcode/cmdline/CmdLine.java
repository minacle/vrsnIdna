/*
 * (c) VeriSign Inc., 2005, All rights reserved
 */

package com.vgrs.xcode.cmdline;

import com.vgrs.xcode.cmdline.CommandLineArgs;

/**
 * This class is to be extended by all the command line classes in the
 * <code>com.vgrs.xcode.cmdline</code> package to provide a uniform way to parse
 * command line arguments.
 * 
 * @author jcolosi
 * @version 1.0 Aug 10, 2010
 */
abstract public class CmdLine {

	/**
	 * Constant prefix for printing the usage of a command line class.
	 */
	static private final String USAGE_ROOT = "usage: java ";

	/**
	 * Command line argument to specify a file name.
	 */
	static protected final String SWITCH_FILE = "file";

	/**
	 * The fully qualified class name of the command line class.
	 */
	private String commandName;

	/**
	 * Collect command line arguments into a set of data structures which are easy
	 * to interrogate.
	 */
	private CommandLineArgs options;


	/**
	 * @param args
	 *        the command line arguments.
	 */
	public CmdLine ( String[] args ) {
		this.commandName = this.getClass().getName();
		this.options = new CommandLineArgs( args );
	}


	/**
	 * @return Fully qualified class name of the command line class.
	 */
	public String getCommandName () {
		return this.commandName;
	}


	/**
	 * @return <code>CommandLineArgs</code> containing the command line arguments
	 *         parsed into easy to interrogate data structures.
	 */
	public CommandLineArgs getOptions () {
		return this.options;
	}


	/**
	 * @return the usage of this command line class.
	 */
	abstract public String getUsageOptions ();


	/**
	 * @param commandName
	 *        Fully qualified class name of the command line class.
	 */
	public void setCommandName ( String commandName ) {
		this.commandName = commandName;
	}


	/**
	 * @param options
	 *        command line arguments for this class.
	 */
	public void setOptions ( CommandLineArgs options ) {
		this.options = options;
	}


	/**
	 * Print the usage of this command line class.
	 */
	public void showUsage () {
		System.out
				.println( USAGE_ROOT + this.commandName + " " + getUsageOptions() );
	}

}