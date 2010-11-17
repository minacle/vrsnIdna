
package com.vgrs.xcode.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Create an iterator over an InputStream object.
 * 
 * @author John Colosi
 * @version 1.0
 */
public class InputIterator implements Iterator<Object> {
	// Attributes
	private InputStream r = null;
	private int last = -1;
	private boolean eof = false;


	// Constructor
	public InputIterator ( InputStream r ) {
		this.r = r;
	}


	// Methods
	private int read () throws IOException {
		int i = -1;
		if ( this.last >= 0 ) {
			i = this.last;
			this.last = -1;
		}
		else {
			i = this.r.read();
		}
		if ( i == 0x0d ) {
			i = this.r.read();
			if ( i == 0x0a ) {
				i = this.r.read();
				if ( i < 0 ) {
					this.eof = true;
					this.r.close();
				}
				else {
					this.last = i;
				}
			}
			else {
				this.last = i;
			}
			return -1;
		}
		if ( i == 0x0a ) {
			i = this.r.read();
			if ( i < 0 ) {
				this.eof = true;
				this.r.close();
			}
			else {
				this.last = i;
			}
			return -1;
		}
		if ( i < 0 ) {
			this.eof = true;
			this.r.close();
			return -1;
		}
		return i;
	}


	/**
	 * Determine if the last line has already been read.
	 * 
	 * @return <code>true</code> if the InputStream has been read completely.
	 */
	public boolean hasNext () {
		return !this.eof;
	}


	/**
	 * Retrieve the next line of content.
	 * 
	 * @return The next line of content in a String object.
	 * @throws NoSuchElementException
	 *         If the InputStream has been read completely.
	 */
	public Object next () throws NoSuchElementException {
		if ( this.eof ) {
			throw new NoSuchElementException();
		}

		int i;
		final StringBuffer buffer = new StringBuffer();
		try {
			while ( (i = this.read()) >= 0 ) {
				buffer.append( (char) i );
			}
		}
		catch ( final IOException x ) {
			throw new NoSuchElementException();
		}
		return buffer.toString();
	}


	/**
	 * Removes from the underlying collection the last element returned by the
	 * iterator. (Not Supported)
	 * 
	 * @throws UnsupportedOperationException
	 *         This exception is always thrown.
	 */
	public void remove () throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
}