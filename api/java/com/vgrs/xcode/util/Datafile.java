/*
 * (c) VeriSign Inc., 2005, All rights reserved
 */

package com.vgrs.xcode.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

/**
 * Utility methods for retrieving data from files or resources.
 */
public class Datafile {

	static public final String GZIP_SUFFIX = ".gz";

	static public final String ZIP_SUFFIX = ".zip";

	static private final ClassLoader loader = Datafile.class.getClassLoader();


	/**
	 * Retrieve an Iterator over the lines in <code>file</code>.
	 * 
	 * @param aFile
	 *        A location on local disk which has been gzipped
	 * @return An Iterator over the lines in <code>file</code>
	 * @throws XcodeException
	 *         If the <code>file</code> cannot be read
	 */
	static public Iterator<String> getGzipIterator ( File aFile )
			throws XcodeException {
		try {
			return readData( new GZIPInputStream( getResource( aFile ) ) ).iterator();
		}
		catch ( final IOException e ) {
			throw XcodeError.FILE_IO( ": '" + aFile + "'" );
		}
	}


	/**
	 * Retrieve an Iterator over the lines in <code>resource</code>.
	 * 
	 * @param aResource
	 *        A location on the classpath which has been gzipped
	 * @return An Iterator over the lines in <code>resource</code>
	 * @throws XcodeException
	 *         If the <code>resource</code> cannot be read
	 */
	static public Iterator<String> getGzipIterator ( String aResource )
			throws XcodeException {
		try {
			return readData( new GZIPInputStream( getResource( aResource ) ) )
					.iterator();
		}
		catch ( final IOException e ) {
			throw XcodeError.FILE_IO( ": '" + aResource + "'" );
		}
	}


	/**
	 * Retrieve an Iterator over the lines in <code>file</code>.
	 * 
	 * @param aFile
	 *        A location on local disk
	 * @return An Iterator over the lines in <code>file</code>
	 * @throws XcodeException
	 *         If the <code>file</code> cannot be read
	 */
	static public Iterator<String> getIterator ( File aFile )
			throws XcodeException {
		if ( aFile == null ) {
			throw XcodeError.NULL_ARGUMENT();
		}
		final String filename = aFile.getName();
		if ( filename.length() == 0 ) {
			throw XcodeError.EMPTY_ARGUMENT();
		}
		if ( filename.endsWith( ZIP_SUFFIX ) ) {
			return getZipIterator( aFile );
		}
		else if ( filename.endsWith( GZIP_SUFFIX ) ) {
			return getGzipIterator( aFile );
		}
		else {
			return getTxtIterator( aFile );
		}
	}


	/**
	 * Retrieve an Iterator over the lines in <code>resource</code>.
	 * 
	 * @param aResource
	 *        A location on the classpath
	 * @return An Iterator over the lines in <code>resource</code>
	 * @throws XcodeException
	 *         If the <code>resource</code> cannot be read
	 */
	static public Iterator<String> getIterator ( String aResource )
			throws XcodeException {
		if ( aResource == null ) {
			throw XcodeError.NULL_ARGUMENT();
		}
		if ( aResource.length() == 0 ) {
			throw XcodeError.EMPTY_ARGUMENT();
		}
		if ( aResource.endsWith( ZIP_SUFFIX ) ) {
			return getZipIterator( aResource );
		}
		else if ( aResource.endsWith( GZIP_SUFFIX ) ) {
			return getGzipIterator( aResource );
		}
		else {
			return getTxtIterator( aResource );
		}
	}


	/**
	 * Retrieve an Iterator over the lines in <code>file</code>.
	 * 
	 * @param aFile
	 *        A location on local disk
	 * @return An Iterator over the lines in <code>file</code>
	 * @throws XcodeException
	 *         If the <code>file</code> cannot be read
	 */
	static public Iterator<String> getTxtIterator ( File aFile )
			throws XcodeException {
		try {
			return readData( getResource( aFile ) ).iterator();
		}
		catch ( final IOException e ) {
			throw XcodeError.FILE_IO( ": '" + aFile + "'" );
		}
	}


	/**
	 * Retrieve an Iterator over the lines in <code>resource</code>.
	 * 
	 * @param aResource
	 *        A location on the classpath
	 * @return An Iterator over the lines in <code>resource</code>
	 * @throws XcodeException
	 *         If the <code>resource</code> cannot be read
	 */
	static public Iterator<String> getTxtIterator ( String aResource )
			throws XcodeException {
		try {
			return readData( getResource( aResource ) ).iterator();
		}
		catch ( final IOException e ) {
			throw XcodeError.FILE_IO( ": '" + aResource + "'" );
		}
	}


	/**
	 * Retrieve an Iterator over the lines in <code>file</code>.
	 * 
	 * @param aFile
	 *        A location on local disk which has been zipped
	 * @return An Iterator over the lines in <code>file</code>
	 * @throws XcodeException
	 *         If the <code>file</code> cannot be read
	 */
	static public Iterator<String> getZipIterator ( File aFile )
			throws XcodeException {
		try {
			return getZipIterator( new ZipInputStream( getResource( aFile ) ) );
		}
		catch ( final IOException e ) {
			throw XcodeError.FILE_IO( ": '" + aFile + "'" );
		}
	}


	/**
	 * Retrieve an Iterator over the lines in <code>resource</code>.
	 * 
	 * @param aResource
	 *        A location on the classpath which has been zipped
	 * @return An Iterator over the lines in <code>resource</code>
	 * @throws XcodeException
	 *         If the <code>resource</code> cannot be read
	 */
	static public Iterator<String> getZipIterator ( String aResource )
			throws XcodeException {
		try {
			return getZipIterator( new ZipInputStream( getResource( aResource ) ) );
		}
		catch ( final IOException e ) {
			throw XcodeError.FILE_IO( ": '" + aResource + "'" );
		}
	}


	/**
	 * Retrieve an Iterator over the lines in <code>inputstream</code>.
	 * 
	 * @param aStream
	 *        An InputStream which has been zipped
	 * @return An Iterator over the lines in <code>stream</code>
	 * @throws IOException
	 *         If the <code>stream</code> cannot be read
	 */
	static public Iterator<String> getZipIterator ( ZipInputStream aStream )
			throws IOException {
		final ArrayList<String> lines = new ArrayList<String>();
		while ( aStream.getNextEntry() != null ) {
			lines.addAll( readData( aStream ) );
		}
		return lines.iterator();
	}


	/**
	 * Retrieve the FileInputStream associated with <code>file</code>.
	 * 
	 * @param aFile
	 *        A location on local disk
	 * @return A FileInputStream for the <code>file</code>
	 * @throws XcodeException
	 *         If the <code>file</code> cannot be read
	 */
	static private FileInputStream getResource ( File aFile )
			throws XcodeException {
		try {
			return new FileInputStream( aFile );
		}
		catch ( final FileNotFoundException e ) {
			throw XcodeError.FILE_IO( ": '" + aFile + "'" );
		}
	}


	/**
	 * Retrieve the InputStream associated with <code>resource</code>.
	 * 
	 * @param aResource
	 *        A location on the classpath
	 * @return An InputStream for the <code>resource</code>
	 * @throws XcodeException
	 *         If the <code>resource</code> cannot be read
	 */
	static private InputStream getResource ( String aResource )
			throws XcodeException {
		final InputStream inputStream = loader.getResourceAsStream( aResource );
		if ( inputStream == null ) {
			throw XcodeError.FILE_IO( ": '" + aResource + "'" );
		}
		return inputStream;
	}


	/**
	 * Traverse a BufferedReader object yielding an ArrayList of lines.
	 * 
	 * @param aBuffer
	 *        A BufferedReader object
	 * @return List of lines in the <code>buffer</code>
	 * @throws IOException
	 *         If the <code>buffer</code> cannot be read
	 */
	static private ArrayList<String> readData ( BufferedReader aBuffer )
			throws IOException {
		final ArrayList<String> lines = new ArrayList<String>();
		String line = null;
		while ( (line = aBuffer.readLine()) != null ) {
			lines.add( line );
		}
		return lines;
	}


	/**
	 * Traverse an InputStream object yielding an ArrayList of lines.
	 * 
	 * @param aStream
	 *        An InputStream object
	 * @return List of lines in the <code>stream</code>
	 * @throws IOException
	 *         If the <code>stream</code> cannot be read
	 */
	static private ArrayList<String> readData ( InputStream aStream )
			throws IOException {
		return readData( new BufferedReader( new InputStreamReader( aStream ) ) );
	}
}