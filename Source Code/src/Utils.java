import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import org.jdatepicker.impl.DateComponentFormatter;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;


class Utils {
	
	static void showInfo( String msg ) {
		System.out.println( " [i] " + msg );
	}
	
	static void showWarning( String msg ) {
		System.out.println( " [!] " + msg );
	}
	
	static void showException( String msg ) {
		System.err.println( " [E] " + msg );
	}
	
	static void showError( String msg ) {
		System.err.println( " [X] " + msg );
	}
	
	static void showSQLmessage( String msg ) {
		System.out.println( " [SQL] " + msg );
	}
	
	static String HTMLFormatter( String string, int size, String font, String color ) {
		return "<html><font face = \"" + font + "\" size = " + size + " color = \"" + color + "\">" + string + "</font></html>";
	}
	
	static int[] calculateCenterScreenPosition( int width, int heigth ) {
		return new int[]{ ( int ) ( getMonitorResolution()[0] - width ) / 2, ( int ) ( getMonitorResolution()[1] - heigth ) / 2 };
	}
	
	static double[] getMonitorResolution() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		return new double[] { screenSize.getWidth(), screenSize.getHeight() };
	}
	
	static boolean isInteger( String string ) {
		try {
			Integer.parseInt( string );
			return true;
		} catch ( NumberFormatException exception ) {
			return false;
		}
	}
	
	static boolean isNegativeInteger( String string ) {
		return string.charAt( 0 ) == '-';
	}
	
	static boolean runFormatIsCorrect( String run ) {
		Matcher matcher = Pattern.compile("^([0-9]{2})[.]{0,1}([0-9]{3})[.]{0,1}([0-9]{3})-{0,1}([0-9k])$").matcher( run );
		if ( !matcher.find() )
			return false;
		
		return true;
	}
	
	static String thousandsSeparator( String n ) {
		if ( ( n.length() - ( n.charAt(0) == '-' ? 1 : 0 ) ) < 4 )
			return n;
		else
			return thousandsSeparator( n.substring( 0, n.length() - 3 ) ) + "." + n.substring( n.length() - 3, n.length() );
	}
	
	static String thousandsSeparatorFloat( String n ) {
		n = n.replace( '.', ',' );
		int l = n.indexOf( ',' );
		if ( l < 1 )
			return thousandsSeparator( n );
		return thousandsSeparator( n.substring( 0, l ) ) + n.substring( l );
	}
	
	static String formatInputRUN( String run ) {
		Matcher matcher = Pattern.compile("^([0-9]{2})[.]{0,1}([0-9]{3})[.]{0,1}([0-9]{3})-{0,1}([0-9k])$").matcher( run );	
		if ( !matcher.matches() )
			return null;
		return matcher.group( 1 ) + matcher.group( 2 ) + matcher.group( 3 ) + matcher.group( 4 );
	}
	
	static String formatOutputRUN( String run ) {	
		return thousandsSeparator( run.substring( 0, run.length() - 1 ) ) + "-" + run.substring( run.length() - 1 );
	}
	
	static String normalizeString( String string ) {
		char[] s = string.toCharArray();
		s[0] = Character.toUpperCase( s[0] );
		
		for ( int i = 1; i < s.length-1; i++ )
			if ( s[i] == ' ' || s[i] == '-' )
				s[i+1] = Character.toUpperCase( s[i+1] );
		return String.valueOf( s );
	}
	
	static String readStringGUI( JFrame window, String title, String msg, String defaultString ) {
		String string;
		while ( true ) {
			string = showInputDialogGUI( window, title, msg, "input", defaultString );
			if ( string == null || string.length() > 0 )
				break;
			showWarnGUI( window, "Formato de entrada incorrecta!" );
		}
		return string != null ? string.trim().toLowerCase() : string;
	}
	
	static String readNumberStringGUI( JFrame window, String title, String msg, String defaultString ) {
		String string;
		while ( true ) {
			string = showInputDialogGUI( window, title, msg, "input", defaultString );
			if ( string == null || isInteger( string ) && !isNegativeInteger( string ) )
				break;
			showWarnGUI( window, "Formato de numero incorrecto!" );
		}
		return string != null ? string.trim().toLowerCase() : string;
	}
	
	static String[] readDateGUI( JFrame window, String title, String okButton, String icon ) {
		
		JPanel panel = new JPanel();
		panel.setLayout( new GridLayout( 2, 2 ) );
		
		panel.add( new JLabel( "Fecha Inicio: " ) );
		JDatePickerImpl initDate = getDatePicker();
		panel.add( initDate );
		
		panel.add( new JLabel( "Fecha Termino: " ) );
		JDatePickerImpl endDate = getDatePicker();
		panel.add( endDate );
		
		if ( JOptionPane.showOptionDialog( window, panel, title,
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
				new ImageIcon( "resources/" + icon + ".png" ), new String[] { okButton, "Cancelar" }, 0 ) == 1 )
			return null;
		
		String init = null, end = null;
		if ( initDate.getModel().isSelected() )
			init = formatDate( initDate.getModel().getYear(), initDate.getModel().getMonth(), initDate.getModel().getDay() );
		if ( endDate.getModel().isSelected() )
			end = formatDate( endDate.getModel().getYear(), endDate.getModel().getMonth(), endDate.getModel().getDay() );
		
		return new String[] { init, end };
	}
	
	private static String formatDate( int year, int month, int day ) {
		return year + "-" + ( month + 1 ) + "-" + day;
	}
	
	private static JDatePickerImpl getDatePicker() {
		UtilDateModel initialDate = new UtilDateModel();
		return new JDatePickerImpl( new JDatePanelImpl( initialDate, getProperties() ), new DateComponentFormatter() );
	}
	
	private static Properties getProperties() {
		Properties properties = new Properties();
		properties.put("text.year", "Año");
		properties.put("text.month", "Mes");
		properties.put("text.today", "Hoy");
		return properties;
	}

	static String[] readSaleGUI( JFrame window, String title, String okButton, String icon, String[] defaults ) {
		
		JPanel panel = new JPanel();
		panel.setLayout( new GridLayout( 2, 2 ) );
		InputState[] checkers = new InputState[] { InputState.EMPTY_STRING, InputState.EMPTY_STRING };
		String[] verifiableInputs = new String[] { "Run cliente", "Run vendedor" };
		
		JLabel runCustomerLabel = new JLabel( "RUN Cliente: " );
		JTextField runCustomer = new JTextField( 12 );
		panel.add( runCustomerLabel );
		panel.add( runCustomer );
		addRUNChecker( runCustomerLabel, runCustomer, checkers, 0 );
		
		JLabel runSellerLabel = new JLabel( "RUN Vendedor: " );
		JTextField runSeller = new JTextField( 12 );
		panel.add( runSellerLabel );
		panel.add( runSeller );
		addRUNChecker( runSellerLabel, runSeller, checkers, 1 );
		
		if ( defaults != null ) {
			runCustomer.setText( defaults[ 0 ] );
			runSeller.setText( defaults[ 1 ] );
		}
		
		do {
			if ( JOptionPane.showOptionDialog( window, panel, title,
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
					new ImageIcon( "resources/" + icon + ".png" ), new String[] { okButton, "Cancelar" }, 0 ) == 1 )
				return null;
		} while ( !checkInputState( checkers, verifiableInputs ) );
		
		return new String[] { formatInputRUN( runCustomer.getText().toLowerCase() ), formatInputRUN( runSeller.getText().toLowerCase() ) };
	}

	static String[] readProductGUI( JFrame window, String title, String okButton, String icon, String[] categories, String[] defaults ) {
		
		JPanel panel = new JPanel();
		panel.setLayout( new GridLayout( 6, 2 ) );
		InputState[] checkers = new InputState[] { InputState.EMPTY_STRING, InputState.EMPTY_STRING, InputState.EMPTY_STRING, InputState.EMPTY_STRING, InputState.EMPTY_STRING };
		String[] verifiableInputs = new String[] { "Nombre", "Stock", "Stock minimo", "Precio neto", "Casillero" };
		
		JLabel nameLabel = new JLabel( "Nombre: " );
		JTextField name = new JTextField( 20 );
		panel.add( nameLabel );
		panel.add( name );
		addStringChecker( nameLabel, name, checkers, 0 );
		
		JLabel stockLabel = new JLabel( "Stock: " );
		JTextField stock = new JTextField( 6 );
		panel.add( stockLabel );
		panel.add( stock );
		addNumberChecker( stockLabel, stock, checkers, 1 );
		
		JLabel stockMinLabel = new JLabel( "Stock minimo: " );
		JTextField stockMin = new JTextField( 6 );
		panel.add( stockMinLabel );
		panel.add( stockMin );
		addNumberChecker( stockMinLabel, stockMin, checkers, 2 );
		
		JLabel priceLabel = new JLabel( "Precio neto: " );
		JTextField price = new JTextField( 10 );
		panel.add( priceLabel );
		panel.add( price );
		addNumberChecker( priceLabel, price, checkers, 3 );
		
		panel.add( new JLabel( Utils.HTMLFormatter( "Categoria: ", 3, "Arial", "#298A08") ) );
		JComboBox<String> category = new JComboBox<>( categories );
		category.setSelectedIndex( 0 );
		panel.add( category );
		
		JLabel boxLabel =  new JLabel( "Casillero: " );
		JTextField box = new JTextField( 10 );
		panel.add( boxLabel );
		panel.add( box );
		addStringChecker( boxLabel, box, checkers, 4 );
		
		if ( defaults != null ) {
			name.setText( defaults[ 0 ] );
			stock.setText( defaults[ 1 ] );
			stockMin.setText( defaults[ 2 ] );
			price.setText( defaults[ 3 ] );
			category.setSelectedItem( defaults[ 4 ] );
			box.setText( defaults[ 5 ] );
		}
		
		do {
			if ( JOptionPane.showOptionDialog( window, panel, title,
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
					new ImageIcon( "resources/" + icon + ".png" ), new String[] { okButton, "Cancelar" }, 0 ) == 1 )
				return null;
		} while ( !checkInputState( checkers, verifiableInputs ) );
		
		return new String[] { name.getText().trim().toLowerCase(), stock.getText(), stockMin.getText(), price.getText(), ( ( String ) category.getSelectedObjects()[0] ).toLowerCase(), box.getText().trim().toLowerCase() };
	}
	
	static void showMessageDialogGUI( JFrame window, String title, String msg, String icon ) {
		JOptionPane.showMessageDialog( window, msg, title,
				JOptionPane.INFORMATION_MESSAGE, new ImageIcon( "resources/" + icon + ".png" ) );
	}
	
	static String showInputDialogGUI( JFrame window, String title, String msg, String icon, String defaultInput ) {
		return ( String ) JOptionPane.showInputDialog( window, msg, title,
				JOptionPane.OK_CANCEL_OPTION, new ImageIcon( "resources/" + icon + ".png" ), null, defaultInput );
	}
	
	static String showSelectionDialogGUI( JFrame window, String title, String msg, String icon, String[] options, String defaultInput ) {
		return ( String ) JOptionPane.showInputDialog( window, msg, title,
				JOptionPane.OK_CANCEL_OPTION, new ImageIcon( "resources/" + icon + ".png" ), options, defaultInput );
	}
	
	static int showOptionDialogGUI( JFrame window, String title, String msg, String icon, String[] options, String defaultOption ) {
		return JOptionPane.showOptionDialog( window, msg, title,
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
				new ImageIcon( "resources/" + icon + ".png" ), options, defaultOption );
	}
	
	static void showWarnGUI( JFrame window, String msg ) {
		showMessageDialogGUI( window, "¡Advertencia!",
				HTMLFormatter( msg, 4, "Arial", "#DF7401"),
				"warning" );
	}
	
	static void showErrorGUI( JFrame window, String msg ) {
		showMessageDialogGUI( window, "¡Error!",
				HTMLFormatter( msg, 4, "Arial", "#DF0101"),
				"error" );
	}
	
	static void showConfirmationGUI( JFrame window, String msg ) {
		showMessageDialogGUI( window, "Operacion finalizada",
				HTMLFormatter( msg, 4, "Arial", "#31B404"),
				"check" );
	}
	
	static void addNumberChecker( JLabel label, JTextField field, InputState[] checkers, int index ) {
		String text = label.getText();
		field.getDocument().addDocumentListener( new DocumentListener() {
			public void changedUpdate( DocumentEvent event ) {}
			public void removeUpdate( DocumentEvent event ) {
				if ( field.getText().length() < 1  ) {
					checkers[ index ] = InputState.EMPTY_STRING;
					setLabelDefault( label, text );
					return;
				}
				check();
			}
			public void insertUpdate( DocumentEvent event ) {
				check();
			}
			private void check() {
				if ( !isInteger( field.getText() ) ) {
					checkers[ index ] = InputState.NOT_NUMBER;
					setLabelIncorrect( label, text );
				} else if ( isNegativeInteger( field.getText() ) ) {
					checkers[ index ] = InputState.NEGATIVE_NUMBER;
					setLabelIncorrect( label, text );
				} else {
					checkers[ index ] = InputState.VALID;
					setLabelCorrect( label, text );
				}
			}
		});
	}
	
	static void addStringChecker( JLabel label, JTextField field, InputState[] checkers, int index ) {
		String text = label.getText();
		field.getDocument().addDocumentListener( new DocumentListener() {
			public void changedUpdate( DocumentEvent event ) {}
			public void removeUpdate( DocumentEvent event ) {
				if ( field.getText().length() < 1  ) {
					checkers[ index ] = InputState.EMPTY_STRING;
					setLabelDefault( label, text );
				}
			}
			public void insertUpdate( DocumentEvent event ) {
				checkers[ index ] = InputState.VALID;
				setLabelCorrect( label, text );
			}
		});
	}
	
	static void addRUNChecker( JLabel label, JTextField field, InputState[] checkers, int index ) {
		String text = label.getText();
		field.getDocument().addDocumentListener( new DocumentListener() {
			public void changedUpdate( DocumentEvent event ) {}
			public void removeUpdate( DocumentEvent event ) {
				if ( field.getText().length() < 1  ) {
					checkers[ index ] = InputState.EMPTY_STRING;
					setLabelDefault( label, text );
				}
				check();
			}
			public void insertUpdate( DocumentEvent event ) {
				check();
			}
			private void check() {
				if ( !runFormatIsCorrect(  field.getText() ) ) {
					checkers[ index ] = InputState.INVALID_RUN;
					setLabelIncorrect( label, text );
				} else {
					checkers[ index ] = InputState.VALID;
					setLabelCorrect( label, text );
				}
			}
		});
	}
	
	static boolean checkInputState( InputState[] checkers, String[] fields ) {
		for ( int i = 0; i < checkers.length; i++ )
			switch ( checkers[ i ] ) {
				case VALID:
					break;
				case EMPTY_STRING :
					showWarnGUI( null, "Campo de texto \"" + fields[ i ] + "\" vacio!" );
					return false;
				case NOT_NUMBER :
					showWarnGUI( null, "Campo de texto \"" + fields[ i ] + "\" debe ser un numero!" );
					return false;
				case NEGATIVE_NUMBER :
					showWarnGUI( null, "Campo de texto \"" + fields[ i ] + "\" debe ser positivo!" );
					return false;
				case INVALID_RUN :
					showWarnGUI( null, "Campo de texto \"" + fields[ i ] + "\" debe ser un R.U.N. valido!" );
					return false;
				case INVALID :
					showWarnGUI( null, "Campo de texto \"" + fields[ i ] + "\" invalido!" );
					return false;
			}
		return true;
	}
	
	static void setLabelCorrect( JLabel label, String text ) {
		label.setText( Utils.HTMLFormatter( text, 3, "Arial", "#298A08") );
	}
	
	static void setLabelIncorrect( JLabel label, String text ) {
		label.setText( Utils.HTMLFormatter( text, 3, "Arial", "#FF0000") );
	}
	
	static void setLabelDefault( JLabel label, String text ) {
		label.setText( Utils.HTMLFormatter( text, 3, "Arial", "#000000") );
	}
	
	static JMenuItem createMenuItem( String text, String color, String icon ) {
		return new JMenuItem( Utils.HTMLFormatter( text, 3, "Arial", color ), new ImageIcon( "resources/" + icon + ".png" ) );
	}
	
	static void createTable( String title, String[] titles, String[][] data, int width, int height, int rowHeight ) {
		JFrame windows = new JFrame( title );
		int[] windowPos = calculateCenterScreenPosition( width, height );
		
		@SuppressWarnings("serial")
		DefaultTableModel model = new DefaultTableModel( titles, 0 ) { public boolean isCellEditable( int row, int column ) { return false; } };;
		JTable table = new JTable( model );
		table.setPreferredScrollableViewportSize( new Dimension( width, height ) );
		JScrollPane scroll = new JScrollPane( table );
		table.setFont( new Font( "Calibri", Font.PLAIN, 16 ) );
		table.setRowHeight( rowHeight );
		windows.add( scroll );
		windows.setLocation( windowPos[0], windowPos[1] );
		windows.setIconImage( new ImageIcon( "resources/icon.png" ).getImage() );
		windows.setSize( width, height );
		windows.setResizable( false );
		
		for(int i = 0; i < data.length; i++ ) {
			model.addRow( data[i] );
		}
		
		windows.setVisible( true );
	}
}