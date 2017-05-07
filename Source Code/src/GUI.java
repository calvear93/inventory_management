import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;

public class GUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private QueryManager query;
	
	private String[] productsLabels = { "Codigo", "Nombre", "Stock", "Stock Minimo", "Precio Neto", "Precio Bruto", "Categoria", "Casillero" },
		salesLabels = { "Codigo", "Fecha", "Total Neto", "Total Bruto", "Cliente", "Vendedor" };
	private boolean stockouts, closedSales;
	private JCheckBoxMenuItem showStockouts, showClosedSales;
	private String[][] products, sales;
	private JTable productsTable, salesTable;
	private JPanel productsPanel, salesPanel;
	private DefaultTableModel modelProductsTable, modelSalesTable;
	private JComboBox<String> orderProduct, orderSale;
	String filterProduct, searchProduct, filterSale, searchSale;
	private JButton addProductButton, filterProductButton, removeFilterProductButton, searchProductButton,
		addSaleButton, filterSaleButton, removeFilterSaleButton, searchSaleButton;
	private JMenuItem editProduct, deleteProduct, addToSaleSelected, addDescription, seeDescription,
		editSale, cancelSale, generateTicket, seeProducts;
	
	GUI() {
		query = new QueryManager();
		stockouts = false;
		closedSales = false;
	}
	
	void start() {
		Utils.showInfo( "Iniciando recursos graficos..." );
		addWindowListener( new WindowAdapter() { public void windowClosing( WindowEvent event ) { closeGUI( event ); } } );
		createMenu();
		createTabs();
		createWindow( 1024, 768 );
		Utils.showInfo( "Recursos graficos cargados satisfactoriamente." );
	}
	
	private void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu( "Menu" ),
				view = new JMenu( "Vista" ),
				categories = new JMenu( "Categorias" ),
				about_menu = new JMenu( "Informacion" );
		JMenuItem exit = Utils.createMenuItem( "Salir", "#8A0808", "exit16" ),
				add_category = Utils.createMenuItem( "Agregar Categoria", "#04B404", "add16" ),
				delete_category = Utils.createMenuItem( "Eliminar Categoria", "#DF0101", "delete16" ),
				see_categories = Utils.createMenuItem( "Ver Categorias", "#0489B1", "search16" ),
				about = Utils.createMenuItem( "Acerca de...", "#000000", "about16" );
		
		showStockouts = new JCheckBoxMenuItem( Utils.HTMLFormatter( "Mostrar Productos <u>Agotados</u>.", 3, "Arial", "#000000" ) );
		showClosedSales = new JCheckBoxMenuItem( Utils.HTMLFormatter( "Mostrar Ventas <u>Efectuadas</u>.", 3, "Arial", "#000000" ) );

		menu.setMnemonic( KeyEvent.VK_M );
		view.setMnemonic( KeyEvent.VK_V );
		about_menu.setMnemonic( KeyEvent.VK_I );
		
		exit.setToolTipText( "Salir de la aplicacion." );
		exit.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_Q, ActionEvent.CTRL_MASK ) );
		
		exit.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent event ) { closeGUI( null ); } } );
		
		showStockouts.addItemListener( new ItemListener() {  public void itemStateChanged(ItemEvent e) { toggleProducts(); } } );
		showClosedSales.addItemListener( new ItemListener() {  public void itemStateChanged(ItemEvent e) { toggleSales(); } } );
		
		add_category.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent event ) { addCategories( ); } } );
		delete_category.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent event ) { deleteCategory( ); } } );
		see_categories.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent event ) { seeCategories( ); } } );
		
		about.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent event ) { about(); } } );
		
		menu.add( exit );
		
		view.add( showStockouts );
		view.add( showClosedSales );
		
		categories.add( add_category );
		categories.add( delete_category );
		categories.add( see_categories );
		
		about_menu.add( about );
		
		menuBar.add( menu );
		menuBar.add( view );
		menuBar.add( categories );
		menuBar.add( Box.createHorizontalGlue() );
		menuBar.add( about_menu );
		setJMenuBar( menuBar );
	}
	
	private void createTabs() {
		JTabbedPane tabs = new JTabbedPane();
		
		JPanel productsTab = new JPanel();
		configProductsTab( productsTab );
		tabs.addTab( "Productos", null, productsTab, "Visualizar y editar productos." );
		
		JPanel salesTab = new JPanel();
		configSalesTab( salesTab );
		tabs.addTab( "Ventas", null, salesTab, "Visualizar y editar ventas." );
		
		add( tabs );
	}
	
	private void configProductsTab( JPanel panel ) {
		panel.setLayout( new BorderLayout() );
		
		productsPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
		panel.add( productsPanel, BorderLayout.PAGE_START );
		
		addProductButton = new JButton( Utils.HTMLFormatter( "Agregar", 3, "Arial", "#8ACC26" ) );
		addProductButton.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent event ) { addProduct(); } } );
		productsPanel.add( addProductButton );
		
		productsPanel.add( Box.createRigidArea( new Dimension( 32, 3 ) ) );
		filterProductButton = new JButton( Utils.HTMLFormatter( "Filtrar", 3, "Arial", "#086A87" ) );
		filterProductButton.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent event ) { filterProduct(); } } );
		productsPanel.add( filterProductButton );
		
		productsPanel.add( Box.createRigidArea( new Dimension( 2, 3 ) ) );
		removeFilterProductButton = new JButton( Utils.HTMLFormatter( "Quitar filtro", 3, "Arial", "#04B404" ) );
		removeFilterProductButton.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent event ) { removeFilterProduct(); } } );
		productsPanel.add( removeFilterProductButton );
		removeFilterProductButton.setEnabled( false );
		
		productsPanel.add( Box.createRigidArea( new Dimension( 386, 3 ) ) );
		orderProduct = new JComboBox<>( productsLabels );
		orderProduct.setSelectedIndex( 0 );
		productsPanel.add( new JLabel( "Ordenar por: " ) );
		productsPanel.add( orderProduct );
		orderProduct.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent event ) { updateProducts(); } } );
		
		productsPanel.add( Box.createRigidArea( new Dimension( 32, 3 ) ) );
		searchProductButton = new JButton( Utils.HTMLFormatter( "Buscar", 3, "Arial", "#086A87" ) );
		searchProductButton.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent event ) { searchProduct(); } } );
		productsPanel.add( searchProductButton );
		panel.add( productsPanel );
		
		createProductsTable( "Tahoma", 14 );

		panel.add( productsPanel );
	}
	
	private void createProductsTable( String font, int size ) {
		products = query.getProducts( stockouts, String.valueOf( orderProduct.getSelectedObjects()[0] ), filterProduct, searchProduct );
	
		productsTable = new JTable( modelProductsTable );
		productsTable.setPreferredScrollableViewportSize( new Dimension( 980, 604 ) );
		JScrollPane productsScroll = new JScrollPane( productsTable );
		productsTable.setFont( new Font( font, Font.PLAIN, size ) );

		JPopupMenu popup = new JPopupMenu();
		
		editProduct = new JMenuItem( "Editar" ); 
		deleteProduct = new JMenuItem( "Remover" );  
		addToSaleSelected = new JMenuItem( "Agregar a venta seleccionada" ); 
		addDescription = new JMenuItem( "Agregar descripcion" ); 
		seeDescription = new JMenuItem( "Ver descripcion" ); 
		
		editProduct.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent event ) { editProduct(); } } );
		deleteProduct.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent event ) { deleteProduct(); } } );
		
		addDescription.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent event ) { addDescription(); } } );
		seeDescription.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent event ) { seeDescription(); } } );
		
		addToSaleSelected.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent event ) { addProductToSale(); } } );
		
		popup.add( editProduct );
		popup.add( deleteProduct );
		popup.addSeparator();
		popup.add( addToSaleSelected );
		popup.addSeparator();
		popup.add( addDescription );
		popup.add( seeDescription );
		
		productsTable.setComponentPopupMenu( popup );
		productsPanel.add( productsScroll, BorderLayout.CENTER );
		
		updateProductsTable();
	}
	
	@SuppressWarnings("serial")
	private void updateProductsTable() {
		modelProductsTable = new DefaultTableModel( productsLabels, 0 ) { public boolean isCellEditable( int row, int column ) { return false; } };
		for( int i = 0; i < products.length; i++ ) {
			products[i][1] = Utils.normalizeString( products[i][1] );
			products[i][4] = "$" + Utils.thousandsSeparator( products[i][4] );
			products[i][5] = "$" + Utils.thousandsSeparatorFloat( products[i][5] );
			products[i][6] = Utils.normalizeString( products[i][6] );
			products[i][7] = products[i][7].toUpperCase();
			modelProductsTable.addRow( products[i] );
		}
		productsTable.setModel( modelProductsTable );
		
		toggleProductsButtons();
	}
	
	private void updateProducts() {
		products = query.getProducts( stockouts, String.valueOf( orderProduct.getSelectedObjects()[0] ), filterProduct, searchProduct );
		updateProductsTable();
	}
	
	private void checkStock( String code ) {
		if ( query.criticStock( code ) )
			Utils.showWarnGUI( this, "Producto en stock critico! Revise stock." );
	}
	
	private void addProduct() {
		String[][] data = query.getCategories();
		String[] categories = new String[ data.length ];
		for ( int i = 0; i < categories.length; i++ )
			categories[ i ] = Utils.normalizeString( data[ i ][ 0 ] );
		
		String[] product = Utils.readProductGUI( this, "Agregar Producto", "Agregar", "add", categories, null );
		if ( product == null )
			return;
		query.addProduct( product[ 0 ], product[ 1 ], product[ 2 ], product[ 3 ], product[ 4 ], product[ 5 ] );
		
		updateProducts();
	}
	
	private void editProduct() {
		if ( productsTable.getSelectedRow() < 0 )
			return;
		String[][] data = query.getCategories();
		String[] categories = new String[ data.length ];
		for ( int i = 0; i < categories.length; i++ )
			categories[ i ] = Utils.normalizeString( data[ i ][ 0 ] );
		
		String[] selected = {
				( String ) productsTable.getValueAt( productsTable.getSelectedRow(), 1 ),
				( String ) productsTable.getValueAt( productsTable.getSelectedRow(), 2 ),
				( String ) productsTable.getValueAt( productsTable.getSelectedRow(), 3 ),
				( ( String ) productsTable.getValueAt( productsTable.getSelectedRow(), 4 ) ).replace( ".", "" ).substring( 1 ),
				( String ) productsTable.getValueAt( productsTable.getSelectedRow(), 6 ),
				( String ) productsTable.getValueAt( productsTable.getSelectedRow(), 7 ),
		};
		
		String[] product = Utils.readProductGUI( this, "Editar Producto", "Editar", "refresh", categories, selected );
		if ( product == null )
			return;
		query.editProduct( ( String ) productsTable.getValueAt( productsTable.getSelectedRow(), 0 ), product[ 0 ], product[ 1 ], product[ 2 ], product[ 3 ], product[ 4 ], product[ 5 ] );

		String checkCode = ( String ) productsTable.getValueAt( productsTable.getSelectedRow(), 0 );
		updateProducts();
		checkStock( checkCode );
	}
	
	private void deleteProduct( ) {
		if ( productsTable.getSelectedRow() < 0 )
			return;
		
		if( Utils.showOptionDialogGUI( this, "Eliminar", "¿Seguro que desea eliminar este Producto?", "remove", new String[] { "Eliminar", "Cancelar" }, "Cancelar" ) == 1 )
			return;
		
		query.deleteProduct( ( String ) productsTable.getValueAt( productsTable.getSelectedRow(), 0 ) );

		updateProducts();
	}
	
	private void filterProduct() {
		String[][] data = query.getCategories();
		String[] categories = new String[ data.length ];
		for ( int i = 0; i < categories.length; i++ )
			categories[ i ] = Utils.normalizeString( data[ i ][ 0 ] );
		
		filterProduct = Utils.showSelectionDialogGUI( this, "Filtro por categoria", "Seleccione categoria", "refresh", categories, null );
		if ( filterProduct == null )
			return;
		filterProduct = filterProduct.toLowerCase();
		
		removeFilterProductButton.setEnabled( true );

		updateProducts();
	}
	
	private void searchProduct() {		
		searchProduct = Utils.showInputDialogGUI( this, "Busqueda por nombre", "Buscar", "refresh", null );
		if ( searchProduct == null )
			return;
		
		removeFilterProductButton.setEnabled( true );

		updateProducts();
	}
	
	private void removeFilterProduct() {
		filterProduct = null;
		searchProduct = null;
		removeFilterProductButton.setEnabled( false );
		
		updateProducts();
	}
	
	private void addDescription( ) {
		if ( productsTable.getSelectedRow() < 0 )
			return;
		
		String description = Utils.showInputDialogGUI( this, "Editar Descripcion", "Ingrese descripcion", "input",
				query.getDescription( ( String ) productsTable.getValueAt( productsTable.getSelectedRow(), 0 ) ) );
		if ( description == null )
			return;
		
		query.addDescription( ( String ) productsTable.getValueAt( productsTable.getSelectedRow(), 0 ), description );
	}
	
	private void seeDescription( ) {
		if ( productsTable.getSelectedRow() < 0 )
			return;
		
		String description = Utils.HTMLFormatter( "<p style = 'text-align : justify; width : 200px;'>" +
				query.getDescription( ( String ) productsTable.getValueAt( productsTable.getSelectedRow(), 0 ) ) + "</p>", 3, "Tahoma", "#0489B1" );
		if ( description == null )
			return;

		Utils.showMessageDialogGUI( this, "Producto #" + ( String ) productsTable.getValueAt( productsTable.getSelectedRow(), 0 ), description, "info" );
	}
	
	private void addProductToSale() {
		if ( productsTable.getSelectedRow() < 0 || salesTable.getSelectedRow() < 0 )
			return;
		
		String quantity = Utils.readNumberStringGUI( this, "Agregar articulo a Factura", "Ingrese cantidad:", null );
		if( quantity == null )
			return;
		 
		query.addProductToSale( ( String ) salesTable.getValueAt( salesTable.getSelectedRow(), 0 ),
				( String ) productsTable.getValueAt( productsTable.getSelectedRow(), 0 ),
				quantity );
		
		int sel = salesTable.getSelectedRow();
		String checkCode = ( String ) productsTable.getValueAt( productsTable.getSelectedRow(), 0 );
		updateProducts();
		updateSales();
		checkStock( checkCode );
		salesTable.setRowSelectionInterval( sel , sel );
	}
	
	private void configSalesTab( JPanel panel ) {
		panel.setLayout( new BorderLayout() );
		
		salesPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
		panel.add( salesPanel, BorderLayout.PAGE_START );
		
		addSaleButton = new JButton( Utils.HTMLFormatter( "Agregar", 3, "Arial", "#8ACC26" ) );
		addSaleButton.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent event ) { addSale(); } } );
		salesPanel.add( addSaleButton );
		
		salesPanel.add( Box.createRigidArea( new Dimension( 32, 3 ) ) );
		filterSaleButton = new JButton( Utils.HTMLFormatter( "Filtrar", 3, "Arial", "#086A87" ) );
		filterSaleButton.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent event ) { filterSale(); } } );
		salesPanel.add( filterSaleButton );
		
		salesPanel.add( Box.createRigidArea( new Dimension( 2, 3 ) ) );
		removeFilterSaleButton = new JButton( Utils.HTMLFormatter( "Quitar filtro", 3, "Arial", "#04B404" ) );
		removeFilterSaleButton.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent event ) { removeFilterSale(); } } );
		salesPanel.add( removeFilterSaleButton );
		removeFilterSaleButton.setEnabled( false );
		
		salesPanel.add( Box.createRigidArea( new Dimension( 386, 3 ) ) );
		orderSale = new JComboBox<>( salesLabels );
		orderSale.setSelectedIndex( 0 );
		salesPanel.add( new JLabel( "Ordenar por: " ) );
		salesPanel.add( orderSale );
		orderSale.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent event ) { updateSales(); } } );
		
		salesPanel.add( Box.createRigidArea( new Dimension( 32, 3 ) ) );
		searchSaleButton = new JButton( Utils.HTMLFormatter( "Buscar", 3, "Arial", "#086A87" ) );
		searchSaleButton.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent event ) { searchSale(); } } );
		salesPanel.add( searchSaleButton );
		panel.add( salesPanel );
		
		createSalesTable( "Tahoma", 14 );

		panel.add( salesPanel );
	}
	
	private void createSalesTable( String font, int size ) {
		sales = query.getSales( closedSales, String.valueOf( orderSale.getSelectedObjects()[0] ), filterSale, searchSale );
	
		salesTable = new JTable( modelSalesTable );
		salesTable.setPreferredScrollableViewportSize( new Dimension( 980, 604 ) );
		JScrollPane productsScroll = new JScrollPane( salesTable );
		salesTable.setFont( new Font( font, Font.PLAIN, size ) );

		JPopupMenu popup = new JPopupMenu();
		
		editSale = new JMenuItem( "Editar" ); 
		cancelSale = new JMenuItem( "Cancelar" );  
		generateTicket = new JMenuItem( "Generar boleta" ); 
		seeProducts = new JMenuItem( "Ver lista productos" ); 
		
		editSale.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent event ) { editSale(); } } );
		cancelSale.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent event ) { cancelSale(); } } );
		generateTicket.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent event ) { generateTicket(); } } );
		seeProducts.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent event ) { seeProducts(); } } );
		
		popup.add( editSale );
		popup.add( cancelSale );
		popup.addSeparator();
		popup.add( generateTicket );
		popup.addSeparator();
		popup.add( seeProducts );
		
		salesTable.setComponentPopupMenu( popup );
		salesPanel.add( productsScroll, BorderLayout.CENTER );
		
		updateSalesTable();
	}
	
	@SuppressWarnings("serial")
	private void updateSalesTable() {
		modelSalesTable = new DefaultTableModel( salesLabels, 0 ) { public boolean isCellEditable( int row, int column ) { return false; } };
		for( int i = 0; i < sales.length; i++ ) {
			sales[i][2] = "$" + Utils.thousandsSeparator( sales[i][2] );
			sales[i][3] = "$" + Utils.thousandsSeparatorFloat( sales[i][3] );
			sales[i][4] = Utils.formatOutputRUN( sales[i][4] );
			sales[i][5] = Utils.formatOutputRUN( sales[i][5] );
			modelSalesTable.addRow( sales[i] );
		}
		salesTable.setModel( modelSalesTable );
		
		toggleSalesButtons();
	}
	
	private void updateSales() {
		sales = query.getSales( closedSales, String.valueOf( orderSale.getSelectedObjects()[0] ), filterSale, searchSale );
		updateSalesTable();
	}
	
	private void addSale() {
		String[] sale = Utils.readSaleGUI( this, "Crear Venta", "Crear", "add", null );
		if ( sale == null )
			return;
		query.addSale( sale[ 0 ], sale[ 1 ] );
		
		updateSales();
	}
	
	private void editSale() {
		if ( salesTable.getSelectedRow() < 0 )
			return;
		
		String[] selected = {
				( String ) salesTable.getValueAt( salesTable.getSelectedRow(), 4 ),
				( String ) salesTable.getValueAt( salesTable.getSelectedRow(), 5 )
		};

		String[] sale = Utils.readSaleGUI( this, "Crear Venta", "Crear", "add", selected );
		if ( sale == null )
			return;
		
		query.editSale( ( String ) salesTable.getValueAt( salesTable.getSelectedRow(), 0 ), sale[ 0 ], sale[ 1 ] );
		
		updateSales();
	}
	
	private void cancelSale() {
		if ( salesTable.getSelectedRow() < 0 )
			return;
		
		query.cancelSale( ( String ) salesTable.getValueAt( salesTable.getSelectedRow(), 0 ) );
		
		updateSales();
	}
	
	private void seeProducts() {
		if ( salesTable.getSelectedRow() < 0 )
			return;
		String[] titles = { "Codigo", "Nombre", "Precio Neto", "Precio Bruto", "Categoria", "Casillero", "Cantidad"  };
		String[][] productsOf = query.getProductsOf( ( String ) salesTable.getValueAt( salesTable.getSelectedRow(), 0 ) );
		String[][] productsRevamped = new String[ productsOf.length ][ 7 ];
		
		for( int i = 0; i < productsOf.length; i++ ) {
			productsRevamped[i][0] = productsOf[i][0];
			productsRevamped[i][1] = Utils.normalizeString( productsOf[i][1] );
			productsRevamped[i][2] = "$" + Utils.thousandsSeparator( productsOf[i][2] );
			productsRevamped[i][3] = "$" + Utils.thousandsSeparatorFloat( productsOf[i][3] );
			productsRevamped[i][4] = Utils.normalizeString( productsOf[i][4] );
			productsRevamped[i][5] = productsOf[i][5].toUpperCase();
			productsRevamped[i][6] = query.getSaleQuantityOF( productsOf[i][0], ( String ) salesTable.getValueAt( salesTable.getSelectedRow(), 0 ) );
		}
		
		Utils.createTable( "Productos en boleta #" + ( String ) salesTable.getValueAt( salesTable.getSelectedRow(), 0 ), titles, productsRevamped, 720, 360, 22 );
	}
	
	private void filterSale() {
		
		String[] dates = Utils.readDateGUI( this, "Filtro por fecha", "Filtrar", "refresh" );
		
		if ( dates[0] == null && dates[1] == null )
			return;

		searchSale = "";
		if ( dates[0] != null )
			searchSale += " AND fecha >= '" + dates[0] + "'";
		if ( dates[1] != null )
			searchSale += " AND fecha <= '" + dates[1] + "'";
		
		removeFilterSaleButton.setEnabled( true );
		
		updateSales();
	}
	
	private void searchSale() {		
		searchSale = Utils.showInputDialogGUI( this, "Busqueda por RUN", "Buscar", "refresh", null );
		if ( searchSale == null )
			return;
		searchSale = " AND run_cliente ILIKE '%" + Utils.formatInputRUN( searchSale ) + "%' OR run_vendedor ILIKE '%" + Utils.formatInputRUN( searchSale ) + "%'";
		
		removeFilterSaleButton.setEnabled( true );

		updateSales();
	}
	
	private void removeFilterSale() {
		filterSale = null;
		searchSale = null;
		removeFilterSaleButton.setEnabled( false );
		
		updateSales();
	}
	
	private void generateTicket() {
		if ( salesTable.getSelectedRow() < 0 )
			return;
		
		JPanel panel = new JPanel();
		panel.setLayout( new GridLayout( 8, 1 ) );
		panel.add( new JLabel( Utils.HTMLFormatter( "<u>E</u>lectroMusic ltda.</p> - Sandra Iturra.", 3, "Tahoma", "#0489B1") ) );
		panel.add( new JLabel( Utils.HTMLFormatter( "<u>BOLETA ELECTRONICA</u>", 5, "Tahoma", "#0489B1") ) );
		
		panel.add( Box.createRigidArea( new Dimension( 32, 3 ) ) );
		
		panel.add( new JLabel( Utils.HTMLFormatter( "RUN Vendedor: " + ( String ) salesTable.getValueAt( salesTable.getSelectedRow(), 5 ),
				3, "Tahoma", "#74DF00") ) );
		panel.add( new JLabel( Utils.HTMLFormatter( "RUN Cliente: " + ( String ) salesTable.getValueAt( salesTable.getSelectedRow(), 4 ),
				3, "Tahoma", "#74DF00") ) );
		panel.add( new JLabel( Utils.HTMLFormatter( "Total: " + ( String ) salesTable.getValueAt( salesTable.getSelectedRow(), 3 ),
				3, "Tahoma", "#04B404") ) );
		
		panel.add( Box.createRigidArea( new Dimension( 32, 3 ) ) );
		
		panel.add( new JLabel( Utils.HTMLFormatter( "Fecha: " + ( String ) salesTable.getValueAt( salesTable.getSelectedRow(), 1 ),
				3, "Tahoma", "#DF7401") ) );
		
		int option = JOptionPane.showOptionDialog( this, panel, "Impresion Boleta Electronica",
				JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE,
				new ImageIcon( "resources/question.png" ), new String[] { "Imprimir Boleta", "Finalizar Venta", "Cancelar" }, 0 );
		if ( option == 0 )
			printElectronicSale( ( String ) salesTable.getValueAt( salesTable.getSelectedRow(), 0 ) );
		else if ( option == 2 )
			return;
		
		query.finishSale( ( String ) salesTable.getValueAt( salesTable.getSelectedRow(), 0 ) );
	}
	
	private void printElectronicSale( String file ) {
		try {
			PrintWriter writer = new PrintWriter( new FileWriter( file + ".txt" ) );
			String total = ( String ) salesTable.getValueAt( salesTable.getSelectedRow(), 3 );
			writer.println( " ======================================================================================" );
			writer.println( " =============================  BOLETA ELECTRONICA  ===================================" );
			writer.format( " FACTURA: #%s", ( String ) salesTable.getValueAt( salesTable.getSelectedRow(), 0 ) );
			writer.println();
			writer.println( " ==============================  LISTA ARTICULOS  =====================================" );
			writer.format( " %-40s %-12s %-12s %-12s %-8s", "NOMBRE", "PRECIO NETO", "PRECIO BRUTO", "CATEGORIA", "CANTIDAD" );
			writer.println();
			String[][] productsOf = query.getProductsOf( ( String ) salesTable.getValueAt( salesTable.getSelectedRow(), 0 ) );
			for( int i = 0; i < productsOf.length; i++ ) {
				writer.format( " %-40s", Utils.normalizeString( productsOf[i][1] ) );
				writer.format( " %-12s", "$" + Utils.thousandsSeparator( productsOf[i][2] ) );
				writer.format( " %-12s", "$" + Utils.thousandsSeparatorFloat( productsOf[i][3] ) );
				writer.format( " %-12s", Utils.normalizeString( productsOf[i][4] ) );
				writer.format( " %-8s", query.getSaleQuantityOF( productsOf[i][0], ( String ) salesTable.getValueAt( salesTable.getSelectedRow(), 0 ) ) );
				writer.println();
			}
			writer.println( " ======================================================================================" );
			writer.format( " TOTAL:\t\t\t\t%-10s", total.substring( 0, total.indexOf( "," ) ) );
			writer.println();
			writer.println( " ======================================================================================" );
			writer.format( " RUT VENDEDOR:\t%-10s", ( String ) salesTable.getValueAt( salesTable.getSelectedRow(), 5 ) );
			writer.println();
			writer.format( " RUT CLIENTE:\t%-10s", ( String ) salesTable.getValueAt( salesTable.getSelectedRow(), 4 ) );
			writer.println();
			writer.format( " FECHA:\t\t%10s", ( String ) salesTable.getValueAt( salesTable.getSelectedRow(), 1 ) );
			writer.println();
			writer.close();
			Utils.showConfirmationGUI( this, "Boleta Electronica generada satisfactoriamente." );
		} catch ( IOException exception ) {
			exception.printStackTrace();
		}
	}
	
	private void toggleProductsButtons() {
		if ( products.length == 0 ) {
			filterProductButton.setEnabled( false );
			searchProductButton.setEnabled( false );
		} else {
			filterProductButton.setEnabled( true );
			searchProductButton.setEnabled( true );
		}
		if ( stockouts )
			addToSaleSelected.setEnabled( false );
		else 
			addToSaleSelected.setEnabled( true );
	}
	
	private void toggleSalesButtons() {
		if ( sales.length == 0 ) {
			filterSaleButton.setEnabled( false );
			searchSaleButton.setEnabled( false );
		} else {
			filterSaleButton.setEnabled( true );
			searchSaleButton.setEnabled( true );
		}
		if ( closedSales ) {
			cancelSale.setEnabled( false );
			generateTicket.setEnabled( false );
		} else {
			cancelSale.setEnabled( true );
			generateTicket.setEnabled( true );
		}
	}
	
	private void toggleProducts() { stockouts = stockouts ? false : true; toggleProductsButtons(); updateProducts(); }
	
	private void toggleSales() { closedSales = closedSales ? false : true; toggleSalesButtons(); updateSales(); }
	
	private void addCategories() {		
		String category = Utils.readStringGUI( this, "Agregar Categoria", "Ingrese categoria:", null );
		if( category == null )	return;
		
		query.addCategory( category );
	}
	
	private void deleteCategory() {
		String[][] data = query.getCategories();
		String[] categories = new String[ data.length ];
		for ( int i = 0; i < categories.length; i++ )
			categories[ i ] = Utils.normalizeString( data[ i ][ 0 ] );
		
		String category = Utils.showSelectionDialogGUI( this, "Eliminar Categoria", "Seleccione categoria", "remove", categories, null );
		if ( category == null )
			return;
		category = category.toLowerCase();
		 
		query.deleteCategory( category );
	}
	
	private void seeCategories() {
		String[] titles = { Utils.HTMLFormatter( "<u>C</u>ATEGORIAS", 5, "Arial", "#088A08" ) };
		String[][] categories = query.getCategories();
		for ( int i = 0; i < categories.length; i++ )
			categories[ i ][ 0 ] = Utils.normalizeString( categories[ i ][ 0 ] );
		
		Utils.createTable( "Categorias", titles, categories, 640, 260, 22 );
	}
	
	private void createWindow( int width, int height ) {
		int[] windowPos = Utils.calculateCenterScreenPosition( width, height );
		setTitle( "ElectroMusic - BDD Manager" );
		setLocation( windowPos[0], windowPos[1] );
		setIconImage( new ImageIcon( "resources/icon.png" ).getImage() );
		setSize( width, height );
		setResizable( false );
		setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
		setVisible( true );
	}
	
	private void about() {
		JPanel panel = new JPanel();
		panel.setLayout( new GridLayout( 8, 1 ) );
		panel.add( new JLabel( Utils.HTMLFormatter( "Project Leader & Main Developer", 4, "Comic Sans MS", "#088A85") ) );
		panel.add( new JLabel( Utils.HTMLFormatter( "<b>Cristopher Alvear a.k.a. <u>CrawiS</u></b>", 4, "Comic Sans MS", "#FF4000") ) );
		panel.add( new JLabel( Utils.HTMLFormatter( "Project Team ", 4, "Comic Sans MS", "#088A85") ) );
		panel.add( new JLabel( Utils.HTMLFormatter( "Luis Cid a.k.a. <u>Lucho</u>", 4, "Comic Sans MS", "#5FB404") ) );
		panel.add( new JLabel( Utils.HTMLFormatter( "Victor Inostroza a.k.a. <u>Inhox</u>", 4, "Comic Sans MS", "#5FB404") ) );
		panel.add( new JLabel( Utils.HTMLFormatter( "Osvaldo Ramirez a.k.a. <u>El Encuestador</u>", 4, "Comic Sans MS", "#5FB404") ) );
		panel.add( new JLabel( Utils.HTMLFormatter( "Felipe Guzman a.k.a. <u>Rafita</u>", 4, "Comic Sans MS", "#5FB404") ) );
		panel.add( new JLabel( Utils.HTMLFormatter( "Version: 0.3b", 4, "Comic Sans MS", "#01A9DB") ) );
		
		JOptionPane.showOptionDialog( this, panel, "Acerca de",
				JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE,
				new ImageIcon( "resources/info.png" ), new String[] { "CrawiS es Grandioso!" }, 0 );
	}
	
	void closeGUI( WindowEvent evento ) {
		if( Utils.showOptionDialogGUI( this, "Salir", Utils.HTMLFormatter( "¿Seguro que desea salir?", 3, "Arial", "#DF0101"),
				"exit", new String[] { "Salir", "Cancelar" }, "Cancelar" ) == 1 )
			return;
		query.closeConnection();
		Utils.showInfo( "Programa finalizado." );
		if ( evento == null ) System.exit( 0 );
		this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	}
}