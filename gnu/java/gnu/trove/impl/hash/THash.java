///////////////////////////////////////////////////////////////////////////////
// Copyright (c) 2001, Eric D. Friedman All Rights Reserved.
// Copyright (c) 2009, Rob Eden All Rights Reserved.
// Copyright (c) 2009, Jeff Randall All Rights Reserved.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
///////////////////////////////////////////////////////////////////////////////

package gnu.trove.impl.hash;

import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.PrimeFinder;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Base class for hashtables that use open addressing to resolve collisions.
 * Created: Wed Nov 28 21:11:16 2001
 * 
 * @author Eric D. Friedman
 * @author Rob Eden (auto-compaction)
 * @author Jeff Randall
 * @version $Id: THash.java,v 1.1.2.3 2009/10/09 01:44:34 robeden Exp $
 */
abstract public class THash implements Externalizable {

	static final long serialVersionUID = -1792948471915530295L;

	/** the load above which rehashing occurs. */
	protected static final float DEFAULT_LOAD_FACTOR =
			Constants.DEFAULT_LOAD_FACTOR;

	/**
	 * the default initial capacity for the hash table. This is one less than a
	 * prime value because one is added to it when searching for a prime capacity
	 * to account for the free slot required by open addressing. Thus, the real
	 * default capacity is 11.
	 */
	protected static final int DEFAULT_CAPACITY = Constants.DEFAULT_CAPACITY;

	/** the current number of occupied slots in the hash. */
	protected transient int _size;

	/** the current number of free slots in the hash. */
	protected transient int _free;

	/**
	 * Determines how full the internal table can become before rehashing is
	 * required. This must be a value in the range: 0.0 < loadFactor < 1.0. The
	 * default value is 0.5, which is about as large as you can get in open
	 * addressing without hurting performance. Cf. Knuth, Volume 3., Chapter 6.
	 */
	protected float _loadFactor;

	/**
	 * The maximum number of elements allowed without allocating more space.
	 */
	protected int _maxSize;

	/**
	 * The number of removes that should be performed before an auto-compaction
	 * occurs.
	 */
	protected int _autoCompactRemovesRemaining;

	/**
	 * The auto-compaction factor for the table.
	 * 
	 * @see #setAutoCompactionFactor
	 */
	protected float _autoCompactionFactor;

	/** @see #tempDisableAutoCompaction */
	protected transient boolean _autoCompactTemporaryDisable = false;


	/**
	 * Creates a new <code>THash</code> instance with the default capacity and
	 * load factor.
	 */
	public THash () {
		this( DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR );
	}


	/**
	 * Creates a new <code>THash</code> instance with a prime capacity at or near
	 * the specified capacity and with the default load factor.
	 * 
	 * @param initialCapacity
	 *        an <code>int</code> value
	 */
	public THash ( int initialCapacity ) {
		this( initialCapacity, DEFAULT_LOAD_FACTOR );
	}


	/**
	 * Creates a new <code>THash</code> instance with a prime capacity at or near
	 * the minimum needed to hold <tt>initialCapacity</tt> elements with load
	 * factor <tt>loadFactor</tt> without triggering a rehash.
	 * 
	 * @param initialCapacity
	 *        an <code>int</code> value
	 * @param loadFactor
	 *        a <code>float</code> value
	 */
	public THash ( int initialCapacity, float loadFactor ) {
		super();
		this._loadFactor = loadFactor;

		// Through testing, the load factor (especially the default load factor) has
		// been
		// found to be a pretty good starting auto-compaction factor.
		this._autoCompactionFactor = loadFactor;

		setUp( HashFunctions.fastCeil( initialCapacity / loadFactor ) );
	}


	protected int calculateGrownCapacity () {
		return capacity() << 1;
	}


	/** @return the current physical capacity of the hash table. */
	abstract public int capacity ();


	/** Empties the collection. */
	public void clear () {
		this._size = 0;
		this._free = capacity();
	}


	/**
	 * Compresses the hashtable to the minimum prime size (as defined by
	 * PrimeFinder) that will hold all of the elements currently in the table. If
	 * you have done a lot of <tt>remove</tt> operations and plan to do a lot of
	 * queries or insertions or iteration, it is a good idea to invoke this
	 * method. Doing so will accomplish two things:
	 * <p/>
	 * <ol>
	 * <li>You'll free memory allocated to the table but no longer needed because
	 * of the remove()s.</li>
	 * <p/>
	 * <li>You'll get better query/insert/iterator performance because there won't
	 * be any <tt>REMOVED</tt> slots to skip over when probing for indices in the
	 * table.</li>
	 * </ol>
	 */
	public void compact () {
		// need at least one free spot for open addressing
		rehash( PrimeFinder.nextPrime( HashFunctions.fastCeil( size()
				/ this._loadFactor ) + 1 ) );
		computeMaxSize( capacity() );

		// If auto-compaction is enabled, re-determine the compaction interval
		if ( this._autoCompactionFactor != 0 ) {
			computeNextAutoCompactionAmount( size() );
		}
	}


	/**
	 * Computes the values of maxSize. There will always be at least one free slot
	 * required.
	 * 
	 * @param capacity
	 *        an <code>int</code> value
	 */
	protected void computeMaxSize ( int capacity ) {
		// need at least one free slot for open addressing
		this._maxSize =
				Math.min( capacity - 1, (int) (capacity * this._loadFactor) );
		this._free = capacity - this._size; // reset the free element count
	}


	/**
	 * Computes the number of removes that need to happen before the next
	 * auto-compaction will occur.
	 * 
	 * @param size
	 *        an <tt>int</tt> that sets the auto-compaction limit.
	 */
	protected void computeNextAutoCompactionAmount ( int size ) {
		if ( this._autoCompactionFactor != 0 ) {
			// NOTE: doing the round ourselves has been found to be faster than using
			// Math.round.
			this._autoCompactRemovesRemaining =
					(int) ((size * this._autoCompactionFactor) + 0.5f);
		}
	}


	/**
	 * Ensure that this hashtable has sufficient capacity to hold
	 * <tt>desiredCapacity<tt> <b>additional</b> elements without
	 * requiring a rehash.  This is a tuning method you can call
     * before doing a large insert.
	 * 
	 * @param desiredCapacity
	 *        an <code>int</code> value
	 */
	public void ensureCapacity ( int desiredCapacity ) {
		if ( desiredCapacity > (this._maxSize - size()) ) {
			rehash( PrimeFinder.nextPrime( HashFunctions
					.fastCeil( (desiredCapacity + size()) / this._loadFactor ) + 1 ) );
			computeMaxSize( capacity() );
		}
	}


	/**
	 * @see #setAutoCompactionFactor
	 * @return a <<tt>float</tt> that represents the auto-compaction factor.
	 */
	public float getAutoCompactionFactor () {
		return this._autoCompactionFactor;
	}


	/**
	 * Tells whether this set is currently holding any elements.
	 * 
	 * @return a <code>boolean</code> value
	 */
	public boolean isEmpty () {
		return 0 == this._size;
	}


	/**
	 * After an insert, this hook is called to adjust the size/free values of the
	 * set and to perform rehashing if necessary.
	 * 
	 * @param usedFreeSlot
	 *        the slot
	 */
	protected final void postInsertHook ( boolean usedFreeSlot ) {
		if ( usedFreeSlot ) {
			this._free--;
		}

		// rehash whenever we exhaust the available space in the table
		if ( (++this._size > this._maxSize) || (this._free == 0) ) {
			// choose a new capacity suited to the new state of the table
			// if we've grown beyond our maximum size, double capacity;
			// if we've exhausted the free spots, rehash to the same capacity,
			// which will free up any stale removed slots for reuse.
			final int newCapacity =
					this._size > this._maxSize ? PrimeFinder.nextPrime( capacity() << 1 )
							: capacity();
			rehash( newCapacity );
			computeMaxSize( capacity() );
		}
	}


	public void readExternal ( ObjectInput in ) throws IOException,
			ClassNotFoundException {

		// VERSION
		in.readByte();

		// LOAD FACTOR
		final float old_factor = this._loadFactor;
		this._loadFactor = in.readFloat();

		// AUTO COMPACTION LOAD FACTOR
		this._autoCompactionFactor = in.readFloat();

		// If we change the laod factor from the default, re-setup
		if ( old_factor != this._loadFactor ) {
			setUp( (int) Math.ceil( DEFAULT_CAPACITY / this._loadFactor ) );
		}
	}


	/**
	 * Re-enable auto-compaction after it was disabled via
	 * {@link #tempDisableAutoCompaction()}.
	 * 
	 * @param check_for_compaction
	 *        True if compaction should be performed if needed before returning.
	 *        If false, no compaction will be performed.
	 */
	public void reenableAutoCompaction ( boolean check_for_compaction ) {
		this._autoCompactTemporaryDisable = false;

		if ( check_for_compaction && (this._autoCompactRemovesRemaining <= 0)
				&& (this._autoCompactionFactor != 0) ) {

			// Do the compact
			// NOTE: this will cause the next compaction interval to be calculated
			compact();
		}
	}


	/**
	 * Rehashes the set.
	 * 
	 * @param newCapacity
	 *        an <code>int</code> value
	 */
	protected abstract void rehash ( int newCapacity );


	/**
	 * Delete the record at <tt>index</tt>. Reduces the size of the collection by
	 * one.
	 * 
	 * @param index
	 *        an <code>int</code> value
	 */
	protected void removeAt ( int index ) {
		this._size--;

		// If auto-compaction is enabled, see if we need to compact
		if ( this._autoCompactionFactor != 0 ) {
			this._autoCompactRemovesRemaining--;

			if ( !this._autoCompactTemporaryDisable
					&& (this._autoCompactRemovesRemaining <= 0) ) {
				// Do the compact
				// NOTE: this will cause the next compaction interval to be calculated
				compact();
			}
		}
	}


	/**
	 * The auto-compaction factor controls whether and when a table performs a
	 * {@link #compact} automatically after a certain number of remove operations.
	 * If the value is non-zero, the number of removes that need to occur for
	 * auto-compaction is the size of table at the time of the previous compaction
	 * (or the initial capacity) multiplied by this factor.
	 * <p/>
	 * Setting this value to zero will disable auto-compaction.
	 * 
	 * @param factor
	 *        a <tt>float</tt> that indicates the auto-compaction factor
	 */
	public void setAutoCompactionFactor ( float factor ) {
		if ( factor < 0 ) {
			throw new IllegalArgumentException( "Factor must be >= 0: " + factor );
		}

		this._autoCompactionFactor = factor;
	}


	/**
	 * initializes the hashtable to a prime capacity which is at least
	 * <tt>initialCapacity + 1</tt>.
	 * 
	 * @param initialCapacity
	 *        an <code>int</code> value
	 * @return the actual capacity chosen
	 */
	protected int setUp ( int initialCapacity ) {
		int capacity;

		capacity = PrimeFinder.nextPrime( initialCapacity );
		computeMaxSize( capacity );
		computeNextAutoCompactionAmount( initialCapacity );

		return capacity;
	}


	/**
	 * Returns the number of distinct elements in this collection.
	 * 
	 * @return an <code>int</code> value
	 */
	public int size () {
		return this._size;
	}


	/**
	 * Temporarily disables auto-compaction. MUST be followed by calling
	 * {@link #reenableAutoCompaction}.
	 */
	public void tempDisableAutoCompaction () {
		this._autoCompactTemporaryDisable = true;
	}


	/**
	 * This simply calls {@link #compact compact}. It is included for symmetry
	 * with other collection classes. Note that the name of this method is
	 * somewhat misleading (which is why we prefer <tt>compact</tt>) as the load
	 * factor may require capacity above and beyond the size of this collection.
	 * 
	 * @see #compact
	 */
	public final void trimToSize () {
		compact();
	}


	public void writeExternal ( ObjectOutput out ) throws IOException {
		// VERSION
		out.writeByte( 0 );

		// LOAD FACTOR
		out.writeFloat( this._loadFactor );

		// AUTO COMPACTION LOAD FACTOR
		out.writeFloat( this._autoCompactionFactor );
	}
}// THash