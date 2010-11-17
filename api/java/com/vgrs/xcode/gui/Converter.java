/*
 * (c) VeriSign Inc., 2005, All rights reserved
 */

package com.vgrs.xcode.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.StringTokenizer;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import com.vgrs.xcode.common.Hex;
import com.vgrs.xcode.common.Native;
import com.vgrs.xcode.common.Unicode;
import com.vgrs.xcode.common.unicodedata.UnicodeData;
import com.vgrs.xcode.ext.Convert;
import com.vgrs.xcode.idna.Idna;
import com.vgrs.xcode.idna.Punycode;
import com.vgrs.xcode.idna.Race;
import com.vgrs.xcode.idna.contextualrule.ContextualRulesRegistry;
import com.vgrs.xcode.util.XcodeError;
import com.vgrs.xcode.util.XcodeException;

/**
 * A Graphical User Interface for running IDNA conversions.
 */
public class Converter extends JFrame implements ActionListener, CaretListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1640196849605222530L;
	private static final String TITLE = "IDN Conversion Tool";
	private static final String DELIMITER = "\n";
	private static final Font DEFAULT_FONT =
			new Font( "Monospaced", Font.PLAIN, 12 );
	private static final String ICON_URL = "images/VeriSign-Small.gif";
	private static final int ANIM_WIDTH = 55;
	private static final int ANIM_HEIGHT = 23;
	private static final int ANIM_FPS = 10;
	private static final String[] ANIM_URLS =
			{
					"images/Cheetah/Cheetah-1.gif", "images/Cheetah/Cheetah-2.gif",
					"images/Cheetah/Cheetah-3.gif", "images/Cheetah/Cheetah-4.gif",
					"images/Cheetah/Cheetah-5.gif", "images/Cheetah/Cheetah-6.gif",
					"images/Cheetah/Cheetah-7.gif", "images/Cheetah/Cheetah-8.gif",
			};
	private static ClassLoader loader = Converter.class.getClassLoader();

	// Menubar
	private final Container content;
	private JCheckBoxMenuItem useStd3AsciiRules;
	private JCheckBoxMenuItem registrationProtocol;
	private JCheckBoxMenuItem maskAceErrors;
	private ButtonGroup radioInput;
	private ButtonGroup radioOutput;
	private Animation anim;

	// GUI
	private final JPanel io;
	private final JTextArea input;
	private final JTextArea output;
	private final JLabel status;

	// IDNA
	private Convert convert = null;
	private Race race;
	private Punycode punycode;

	private Idna iRace;
	private Idna iPunycode;
	private int totalRecords;
	private int errorRecords;
	private int lastLineCount = 1;
	private String lastInputText = null;


	/**
	 * Start the GUI
	 */
	public static void main ( String aArgs[] ) {
		new Converter();
	}


	/**
	 * Create a Converter object which will display on the screen
	 */
	@SuppressWarnings("deprecation")
	public Converter () {
		super( TITLE );
		this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

		setJMenuBar( this.initMenuBar() );

		this.initIdnaObjects();

		this.content = this.getContentPane();
		this.content.setLayout( new BorderLayout() );

		this.io = new JPanel();
		this.io.setLayout( new BoxLayout( this.io, BoxLayout.X_AXIS ) );

		this.input = new JTextArea();
		this.input.setBorder( new EtchedBorder( EtchedBorder.LOWERED ) );
		this.input.addCaretListener( this );

		this.output = new JTextArea();
		this.output.setBorder( new EtchedBorder( EtchedBorder.LOWERED ) );
		this.output.setEditable( false );

		this.io.add( this.input );
		this.io.add( this.output );

		this.content.add( new JScrollPane( this.io ) );

		this.status = new JLabel( loadIcon( ICON_URL ), SwingConstants.LEFT );
		this.content.add( this.status, BorderLayout.SOUTH );

		initFontSupport();

		this.pack();
		this.setLocation( new Point( 500, 100 ) );
		this.setSize( 800, 600 );
		this.show();
	}


	private JMenuBar initMenuBar () {
		final JMenuBar menubar = new JMenuBar();

		final JMenu options = new JMenu( "Options" );
		options.setMnemonic( 'O' );
		this.useStd3AsciiRules = new JCheckBoxMenuItem( "Apply DNS Rules", true );
		this.useStd3AsciiRules.addActionListener( this );
		options.add( this.useStd3AsciiRules );

		this.registrationProtocol =
				new JCheckBoxMenuItem( "Apply Idna Registration Protocol", true );
		this.registrationProtocol.addActionListener( this );
		options.add( this.registrationProtocol );
		this.maskAceErrors = new JCheckBoxMenuItem( "Mask Ace Errors", false );
		this.maskAceErrors.addActionListener( this );
		options.add( this.maskAceErrors );
		options.addSeparator();
		final JMenuItem explicit = new JMenuItem( "Convert" );
		explicit.addActionListener( this );
		options.add( explicit );
		menubar.add( options );

		final JMenu inputtype = new JMenu( "Input Type" );
		this.radioInput = new ButtonGroup();
		initInputEncodingMenu( inputtype, this.radioInput );
		menubar.add( inputtype );

		final JMenu outputtype = new JMenu( "Output Type" );
		this.radioOutput = new ButtonGroup();
		initOutputEncodingMenu( outputtype, this.radioOutput );
		menubar.add( outputtype );

		this.anim = new Animation( ANIM_URLS, ANIM_WIDTH, ANIM_HEIGHT, ANIM_FPS );

		menubar.add( Box.createHorizontalGlue() );
		menubar.add( this.anim );

		return menubar;
	}


	private void initIdnaObjects () {
		initAce();
		initIdna();
		initConvert();
	}


	private void initAce () {
		this.race = new Race( this.useStd3AsciiRules.getState() );
		this.punycode = new Punycode( this.useStd3AsciiRules.getState() );
	}


	private void initIdna () {
		try {
			this.iRace =
					new Idna( this.race, !this.maskAceErrors.getState(),
							this.registrationProtocol.getState() );
			this.iPunycode =
					new Idna( this.punycode, !this.maskAceErrors.getState(),
							this.registrationProtocol.getState() );
			UnicodeData.init();
			ContextualRulesRegistry.init();
		}
		catch ( final XcodeException x ) {
			JOptionPane.showMessageDialog( this, x.getMessage(),
					"Unable to initialize Idna routines", JOptionPane.WARNING_MESSAGE );
		}
	}


	private void initConvert () {
		this.convert = new Convert( this.iRace, this.iPunycode );
	}


	/**
	 * Implemented via contract with CaretListener. Conditionally executes the
	 * conversion if input panel content has changed.
	 * 
	 * @param aCaretEvent
	 *        A CaretEvent
	 */
	public void caretUpdate ( CaretEvent aCaretEvent ) {
		final int thisLineCount = this.input.getLineCount();
		final String thisInputText = this.input.getText().trim();
		if ( thisLineCount != this.lastLineCount
				&& !thisInputText.equals( this.lastInputText ) ) {
			convert();
			this.lastInputText = thisInputText;
		}
		this.lastLineCount = thisLineCount;
	}


	/**
	 * Implemented via contract with ActionListener. Updates internal data
	 * structures when option flags change. Runs conversion when appropriate.
	 * 
	 * @param aActionEvent
	 *        An ActionEvent
	 */
	public void actionPerformed ( ActionEvent aActionEvent ) {
		final Object source = aActionEvent.getSource();
		if ( source instanceof JCheckBoxMenuItem ) {
			final JCheckBoxMenuItem item = (JCheckBoxMenuItem) source;
			if ( item.equals( this.useStd3AsciiRules ) ) {
				initAce();
			}

			else if ( item.equals( this.registrationProtocol ) ) {
				// No specific initialization needed.}
			}
			else if ( item.equals( this.maskAceErrors ) ) {
				// No specific initialization needed.}
			}
			initIdna();
			initConvert();
			convert();
		}
		else if ( source instanceof JRadioButtonMenuItem ) {
			convert();
		}
		else if ( source instanceof JMenuItem ) {
			final JMenuItem item = (JMenuItem) source;
			if ( item.getText().equals( "Convert" ) ) {
				convert();
			}
		}
	}


	private void convert () {
		this.output.setText( "" );
		this.status.setText( "One moment please..." );
		this.update( this.getGraphics() );
		this.totalRecords = 0;
		this.errorRecords = 0;
		final StringTokenizer tokens =
				new StringTokenizer( this.input.getText(), DELIMITER, true );
		String token;
		this.output.setText( "" );
		while ( tokens.hasMoreTokens() ) {
			token = tokens.nextToken();
			if ( token.equals( DELIMITER ) ) {
				this.output.append( DELIMITER );
			}
			else {
				this.output.append( convert( token ) );
			}
			this.anim.next();
		}
		this.status.setText( "    " + this.totalRecords + " records    "
				+ this.errorRecords + " errors" );
	}


	private String convert ( String aInput ) {
		String output;

		String input = aInput;
		if ( input == null ) {
			return "";
		}
		input = input.trim();
		if ( input.length() == 0 || input.charAt( 0 ) == '#' ) {
			return input;
		}

		this.totalRecords++;
		String inputType = getSelection( this.radioInput );
		String outputType = getSelection( this.radioOutput );
		final String originalOutputType = outputType;

		try {
			if ( inputType.equals( "RACE" ) ) {
				// No specific initialization needed.
			}
			else if ( inputType.equals( "Punycode" ) ) {
				// No specific initialization needed.
			}
			else if ( inputType.equals( "Native" ) ) {
				inputType = "UnicodeBigUnmarked";
			}
			else if ( inputType.equals( "UTF-32" ) ) {
				input = new String( Unicode.decode( Hex.decodeInts( input ) ) );
				inputType = "UnicodeBigUnmarked";
			}
			else if ( inputType.equals( "UTF-16" ) ) {
				input = new String( Hex.decodeChars( input ) );
				inputType = "UnicodeBigUnmarked";
			}
			else {
				// new String(byte[]) is a dangerous method, so we avoid it
				// here.
				final byte[] b = Hex.decodeBytes( input );
				final char[] c = new char[b.length];
				for ( int i = 0; i < c.length; i++ ) {
					c[ i ] = (char) (b[ i ] & 0xff);
				}
				input = new String( c );
			}

			if ( outputType.equals( "RACE" ) ) {
				// No specific initialization needed.
			}
			else if ( outputType.equals( "Punycode" ) ) {
				// No specific initialization needed.
			}
			else if ( outputType.equals( "Native" ) ) {
				outputType = "UnicodeBigUnmarked";
			}
			else if ( outputType.equals( "UTF-32" ) ) {
				outputType = "UnicodeBigUnmarked";
			}
			else if ( outputType.equals( "UTF-16" ) ) {
				outputType = "UnicodeBigUnmarked";
			}

			if ( !inputType.equals( outputType ) ) {
				if ( inputType.equals( "UnicodeBigUnmarked" ) ) {
					input = unpackUtf16( input );
				}
				output = this.convert.execute( input, inputType, outputType );
				if ( outputType.equals( "UnicodeBigUnmarked" ) ) {
					output = packUtf16( output );
				}
			}
			else {
				output = input;
			}

			if ( originalOutputType.equals( "RACE" ) ) {
				// No specific initialization needed.
			}
			else if ( originalOutputType.equals( "Punycode" ) ) {
				// No specific initialization needed.
			}
			else if ( originalOutputType.equals( "Native" ) ) {
				// No specific initialization needed.
			}
			else if ( originalOutputType.equals( "UTF-32" ) ) {
				output = Hex.encode( Unicode.encode( output.toCharArray() ) );
			}
			else {
				output = Hex.encode( output.toCharArray() );
			}
		}
		catch ( final XcodeException x ) {
			this.errorRecords++;
			return "Error:" + x.getCode() + " " + x.getMessage();
		}

		return output;
	}


	private String getSelection ( ButtonGroup aButtonGroup ) {
		final Enumeration<AbstractButton> e = aButtonGroup.getElements();
		while ( e.hasMoreElements() ) {
			final AbstractButton ab = e.nextElement();
			if ( ab.isSelected() ) {
				return ab.getText();
			}
		}
		return null;
	}


	/**
	 * Reassemble UTF-16 data which has been mangled during encoding. If a String
	 * object is used to hold data encoded using the "UnicodeBigUnmarked" encoding
	 * type, then each 16-bit value will be spread across two 16-bit char
	 * primitives. This method reassembles those two 16-bit values into a single
	 * UTF-16 character.
	 * 
	 * @param aEncoding
	 *        The String holding the encoded data
	 */
	public static String packUtf16 ( String aEncoding ) throws XcodeException {
		final byte[] b = Native.getEncoding( aEncoding );
		if ( b.length % 2 != 0 ) {
			throw XcodeError.NATIVE_INVALID_ENCODING();
		}
		final char[] c = new char[b.length / 2];
		for ( int i = 0; i < c.length; i++ ) {
			c[ i ] = (char) ((b[ i * 2 ] & 0xff) << 8 | b[ i * 2 + 1 ] & 0xff);
		}
		return new String( c );
	}


	/**
	 * Disassemble UTF-16 data in preparation for decoding. Java native encoding
	 * methods use only 8 bits of data at a time. When decoding UTF-16 data using
	 * the "UnicodeBigUnmarked" encoding type, it is necessary to break each
	 * 16-bit piece of data into two 16-bit character primitives.
	 * 
	 * @param aUtf16String
	 *        The String holding the data to be encoded
	 */
	public static String unpackUtf16 ( String aUtf16String ) {
		final char[] a = aUtf16String.toCharArray();
		final char[] b = new char[a.length * 2];
		for ( int i = 0; i < a.length; i++ ) {
			b[ i * 2 ] = (char) (a[ i ] >> 8);
			b[ i * 2 + 1 ] = (char) (a[ i ] & 0xff);
		}
		return new String( b );
	}


	private void applyRadioButton ( JMenu aJMenu, ButtonGroup aButtonGroup,
			String aTitle, boolean aState ) {
		final JRadioButtonMenuItem tmp = new JRadioButtonMenuItem( aTitle, aState );
		aJMenu.add( tmp );
		aButtonGroup.add( tmp );
		tmp.addActionListener( this );
	}


	private void initInputEncodingMenu ( JMenu aJMenu, ButtonGroup aButtonGroup ) {
		applyRadioButton( aJMenu, aButtonGroup, "RACE", false );
		applyRadioButton( aJMenu, aButtonGroup, "Punycode", true );
		applyRadioButton( aJMenu, aButtonGroup, "Native", false );
		final JMenu hex = new JMenu( "Hex" );
		applyRadioButton( hex, aButtonGroup, "UTF-32", false );
		applyRadioButton( hex, aButtonGroup, "UTF-16", false );
		applyRadioButton( hex, aButtonGroup, "UTF-8", false );
		applyRadioButton( hex, aButtonGroup, "Big5", false );
		applyRadioButton( hex, aButtonGroup, "EUC_CN", false );
		applyRadioButton( hex, aButtonGroup, "EUC_JP", false );
		applyRadioButton( hex, aButtonGroup, "EUC_KR", false );
		applyRadioButton( hex, aButtonGroup, "EUC_TW", false );
		applyRadioButton( hex, aButtonGroup, "GBK", false );
		applyRadioButton( hex, aButtonGroup, "ISO2022JP", false );
		applyRadioButton( hex, aButtonGroup, "ISO2022KR", false );
		applyRadioButton( hex, aButtonGroup, "KOI8_R", false );
		applyRadioButton( hex, aButtonGroup, "SJIS", false );
		aJMenu.add( hex );
	}


	private void initOutputEncodingMenu ( JMenu aJMenu, ButtonGroup aButtonGroup ) {
		applyRadioButton( aJMenu, aButtonGroup, "RACE", false );
		applyRadioButton( aJMenu, aButtonGroup, "Punycode", false );
		applyRadioButton( aJMenu, aButtonGroup, "Native", false );
		final JMenu hex = new JMenu( "Hex" );
		applyRadioButton( hex, aButtonGroup, "UTF-32", true );
		applyRadioButton( hex, aButtonGroup, "UTF-16", false );
		applyRadioButton( hex, aButtonGroup, "UTF-8", false );
		applyRadioButton( hex, aButtonGroup, "Big5", false );
		applyRadioButton( hex, aButtonGroup, "EUC_CN", false );
		applyRadioButton( hex, aButtonGroup, "EUC_JP", false );
		applyRadioButton( hex, aButtonGroup, "EUC_KR", false );
		applyRadioButton( hex, aButtonGroup, "EUC_TW", false );
		applyRadioButton( hex, aButtonGroup, "GBK", false );
		applyRadioButton( hex, aButtonGroup, "ISO2022JP", false );
		applyRadioButton( hex, aButtonGroup, "ISO2022KR", false );
		applyRadioButton( hex, aButtonGroup, "KOI8_R", false );
		applyRadioButton( hex, aButtonGroup, "SJIS", false );
		aJMenu.add( hex );
	}


	private ImageIcon loadIcon ( String aResource ) {
		return new ImageIcon( loader.getResource( aResource ) );
	}


	private void initFontSupport () {
		this.input.setFont( DEFAULT_FONT );
		this.output.setFont( DEFAULT_FONT );
	}

}