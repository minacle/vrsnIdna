/*
 * (c) VeriSign Inc., 2005, All rights reserved
 */

package com.vgrs.xcode.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;

import javax.swing.JLabel;

/**
 * A class used to implement a browser like animation that only runs during
 * calculation. After much experimenting with Java's Thread Priority support,
 * and much failure, we've abandoned that approach. Instead, this object offers
 * a next() method which allows callers to explicitly show the next image. This
 * way a single Thread can control it's own process as well as the animation.
 * After instantiating, a call to the Animation's next() method should be placed
 * somewhere in the main calculation loop.
 */
public class Animation extends JLabel {

	private static final long serialVersionUID = -680655689678056739L;

	/**
	 * All images loaded are scaled to a certain dimension using the scaling
	 * algorithm specified by this constant. <br>
	 * This variable is hard coded to the value <i>Image.SCALE_FAST </i>
	 */
	static public final int SCALE_TYPE = Image.SCALE_FAST;

	// Internals
	static private ClassLoader loader = Animation.class.getClassLoader();

	static private Toolkit kit = Toolkit.getDefaultToolkit();

	// Object attributes
	private final MediaTracker tracker;

	private final Image[] images;

	private final int length;

	private final int width;

	private final int height;

	private int frame;

	private long last;

	private final int delay;

	private Image offImage;

	private Graphics offGraphics;


	/**
	 * Construct an Animation object.
	 * 
	 * @param aSource
	 *        An array of resources which point to slides in the animation.
	 * @param aWidth
	 *        The width of the slides
	 * @param aHeight
	 *        The height of the slides
	 * @param aFramesPerSecond
	 *        The Frames-Per-Second to show
	 */
	public Animation ( String[] aSource, int aWidth, int aHeight,
			int aFramesPerSecond ) {
		super();
		this.width = aWidth;
		this.height = aHeight;
		this.setSize( aWidth, aHeight );
		this.setPreferredSize( new Dimension( aWidth, aHeight ) );
		this.length = aSource.length;
		this.images = new Image[this.length];
		this.tracker = new MediaTracker( this );
		for ( int i = 0; i < this.length; i++ ) {
			this.images[ i ] = loadImage( aSource[ i ], aWidth, aHeight );
			this.tracker.addImage( this.images[ i ], 0 );
		}
		this.frame = 0;
		this.last = 0;
		this.delay = aFramesPerSecond > 0 ? 1000 / aFramesPerSecond : 100;

		this.offImage = null;
		this.offGraphics = null;
	}


	/**
	 * Show the next slide in the animation.
	 */
	public void next () {
		final long click = System.currentTimeMillis();
		if ( click - this.last > this.delay ) {
			this.frame++;
			paint( this.getGraphics() );
			this.last = click;
		}
	}


	/**
	 * Display the Animation
	 * 
	 * @param aGraphics
	 *        A Graphics object on which to draw
	 */
	@Override
	public void paint ( Graphics aGraphics ) {
		update( aGraphics );
	}


	/**
	 * A double-buffered display implementation
	 * 
	 * @param aGraphics
	 *        A Graphics object on which to draw
	 */
	@Override
	public void update ( Graphics aGraphics ) {
		// Create the off-buffer
		final Dimension d = this.getSize();
		if ( this.offGraphics == null || d.width != this.width
				|| d.height != this.height ) {
			this.setSize( this.width, this.height );
			this.offImage = createImage( this.width, this.height );
			this.offGraphics = this.offImage.getGraphics();
		}

		// Erase the off-buffer
		this.offGraphics.setColor( getBackground() );
		this.offGraphics.fillRect( 0, 0, this.width, this.height );
		this.offGraphics.setColor( Color.black );

		// Paint the image onto the off-buffer
		paintFrame( this.offGraphics );

		// Paint the off-buffer onto the screen
		aGraphics.drawImage( this.offImage, 0, 0, null );
	}


	/**
	 * Wait until all frames have been loaded, then draw
	 * 
	 * @param aGraphics
	 *        A Graphics object on which to draw
	 */
	public void paintFrame ( Graphics aGraphics ) {
		if ( this.tracker.statusID( 0, true ) == MediaTracker.COMPLETE ) {
			aGraphics.drawImage( this.images[ this.frame % this.length ], 0, 0, null );
		}
	}


	private Image loadImage ( String aImageResource, int aWidth, int aHeight ) {
		Image image = kit.getImage( loader.getResource( aImageResource ) );
		if ( image == null ) {
			image = kit.getImage( aImageResource );
		}
		if ( image == null ) {
			return image;
		}
		return image.getScaledInstance( aWidth, aHeight, SCALE_TYPE );
	}

}