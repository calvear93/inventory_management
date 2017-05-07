import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

public class QueryManager {
	
private static PostgreSQL conection;
	
	public QueryManager() {
		try {
			String[] cfg = readCFG( "postgresql.ini" );
			conection = new PostgreSQL( cfg[0], cfg[1], cfg[2] );
		} catch ( SQLException exception ) {
			Utils.showException( " QueryManager: Conexion a Base de Datos fallida." );
			Utils.showErrorGUI( null, "Conexion a Base de Datos fallida!" );
			exception.printStackTrace();
		} catch ( IOException  exception ) {
			Utils.showErrorGUI( null, "Lectura de configuraciones fallida!<br>FILE NOT FOUND: postgresql.ini" );
			exception.printStackTrace();
		}
	}
	
	String[][] getCategories() {
		String query = "SELECT * FROM categoria;";
		return query( query, "Error al consultar categorias!" );
	}
	
	String[][] getProducts( boolean stockouts, String order, String filter, String search ) {
		order = filterProductOrder( order );
		return stockouts ? getStockouts( order, filter, search ) : getProductsAvailable( order, filter, search );
	}
	
	private String[][] getProductsAvailable( String order, String filter, String search ) {
		String query = "SELECT * FROM productos WHERE stock > 0";
		if ( filter != null )
			query += " AND categoria = '" + filter + "'";
		if ( search != null )
			query += " AND nombre ILIKE '%" + search + "%'";
		query += " ORDER BY " + order;

		return query( query, "Error al consultar productos!" );
	}
	
	private String[][] getStockouts( String order, String filter, String search ) {
		String query = "SELECT * FROM productos WHERE stock = 0";
		if ( filter != null )
			query += " AND categoria = '" + filter + "'";
		if ( search != null )
			query += " AND nombre ILIKE '%" + search + "%'";
		query += " ORDER BY " + order;
		return query( query, "Error al consultar productos agotados!" );
	}
	
	String[][] getSales( boolean closedSales, String order, String filter, String search ) {
		order = filterSaleOrder( order );
		return closedSales ? getClosedSales( order, filter, search ) : getOnGoingSales( order, filter, search );
	}
	
	private String[][] getOnGoingSales( String order, String filter, String search ) {
		String query = "SELECT codigo, fecha, total_neto, total_bruto, run_cliente, run_vendedor FROM boletas WHERE NOT efectuada";
		if ( filter != null )
			query += " AND " + filter;
		if ( search != null )
			query += search;
		query += " ORDER BY " + order;
		
		return query( query, "Error al consultar ventas en curso!" );
	}
	
	private String[][] getClosedSales( String order, String filter, String search ) {
		String query = "SELECT codigo, fecha, total_neto, total_bruto, run_cliente, run_vendedor FROM boletas WHERE efectuada";
		if ( filter != null )
			query += " AND " + filter;
		if ( search != null )
			query += search;
		query += " ORDER BY " + order;
		return query( query, "Error al consultar ventas efectuadas!" );
	}
	
	void addCategory( String category ) {
		String command = "INSERT INTO categoria ( nombre ) VALUES ( '" + category + "' );";
		execute( command, "Error al agregar categoria!", "Categoria agregada satisfactoriamente." );
	}
	
	void addProduct( String name, String stock, String stock_min, String price, String category, String box ) {
		String command = "INSERT INTO producto ( nombre, stock, stock_minimo, precio_neto, categoria, casillero ) VALUES" +
				" ( '" + name + "', " + stock + ", " + stock_min + ", " + price + ", '" + category + "', '" + box + "' );";
		execute( command, "Error al agregar producto!", "Producto agregado satisfactoriamente." );
	}
	
	void addSale( String customer, String seller ) {
		String command = "SELECT crear_boleta ( '" + customer + "', '" + seller + "' )";
		execute( command, "Error al agregar venta!", "Venta creada satisfactoriamente." );
	}

	void editProduct( String code, String name, String stock, String stock_min, String price, String category, String box ) {
		String command = "UPDATE producto SET nombre = '" + name + "', stock = " + stock + ", stock_minimo = " + stock_min +
				", precio_neto = " + price + ", categoria = '" + category + "', casillero = '" + box +
				"' WHERE codigo = '" + code + "';";
		execute( command, "Error al editar producto!", "Producto editado satisfactoriamente." );
	}
	
	void addDescription( String code, String description ) {
		String command = "UPDATE producto SET descripcion = '" + description +
				"' WHERE codigo = '" + code + "';";
		execute( command, "Error al agregar descripcion a producto!", "Descripcion agregada satisfactoriamente." );
	}
	
	String getDescription( String code ) {
		String command = "SELECT descripcion FROM producto WHERE codigo = '" + code + "';";
		return query( command, "Error al consultar descripcion de producto!" )[0][0];
	}
	
	String getSaleQuantityOF( String product, String sale ) {
		String command = "SELECT cantidad FROM venta WHERE producto = '" + product + "' AND boleta = '" + sale + "';";
		return query( command, "Error al cantidad de ventas del producto!" )[0][0];
	}
	
	void editSale( String code, String customer, String seller ) {
		String command = "UPDATE boleta SET run_cliente = '" + customer + "', run_vendedor = '" + seller +
				"' WHERE codigo = '" + code + "';";
		execute( command, "Error al editar boleta!", "Boleta editada satisfactoriamente." );
	}
	
	void addProductToSale( String sale, String product, String quantity ) {
		String command = "SELECT agregar_venta ( '" + sale + "', '" + product + "', " + quantity + " );";
		execute( command, "Error al agregar producto a categoria! Excede cantidad de producto.", "Producto agregado satisfactoriamente." );
	}
	
	void deleteProduct( String code ) {
		String command = "DELETE FROM producto WHERE codigo = '" + code + "';";
		execute( command, "No se puede eliminar este producto!", "Producto eliminado satisfactoriamente." );
	}
	
	void finishSale( String code ) {
		String command = "UPDATE boleta SET efectuada = TRUE WHERE codigo = '" + code + "';";
		execute( command, "Error al finalizar venta!", "Venta finalizada satisfactoriamente." );
	}
	
	void cancelSale( String code ) {
		String command = "SELECT cancelar_boleta ( '" + code + "' );";
		execute( command, "Error al cancelar venta!", "Venta cancelada satisfactoriamente." );
	}
	
	void deleteCategory( String category ) {
		String command = "DELETE FROM categoria WHERE nombre = '" + category + "';";
		execute( command, "Error al eliminar categoria!", "Categoria eliminada satisfactoriamente." );
	}
	
	String[][] getProductsOf( String code ) {
		String query = "SELECT codigo, nombre, precio_neto, precio_neto * 1.19 as precio_bruto, categoria, casillero FROM producto" +
				" WHERE codigo IN (" +
					" SELECT producto FROM venta" +
					" WHERE boleta = '" + code +
				"' );";
		return query( query, "Error al consultar productos!" );
	}
	
	boolean criticStock( String code ) {
		String query = "SELECT stock_critico ( '" + code + "' );";
		if ( query( query, "Error al verificar estado de stock!" )[0][0].charAt(0) == 't' )
			return true;
		return false;
	}
	
	String filterProductOrder( String order ) {
		switch ( order ) {
			case "Stock Minimo" :
				return "stock_minimo";
			case "Precio Neto" :
				return "precio_neto";
			case "Precio Bruto" :
				return "precio_bruto";
		}
		return order;
	}
	
	String filterSaleOrder( String order ) {
		switch ( order ) {
			case "Cliente" :
				return "run_cliente";
			case "Vendedor" :
				return "run_vendedor";
			case "Total Neto" :
				return "total_neto";
			case "Total Bruto" :
				return "total_bruto";
		}
		return order;
	}
	
	void execute( String command, String error, String confirmation ) {
		try {
			conection.execute( command );
			Utils.showConfirmationGUI( null, confirmation );
		} catch ( SQLException exception ) {
			Utils.showException( " QueryManager: Error de comando!" );
			Utils.showWarnGUI( null, error );
			exception.printStackTrace();
		}
	}
	
	String[][] query( String query, String msg ) {
		try {
			return conection.getMatrixOfInjectedQuery( conection.injectQuery( query ) );
		} catch ( SQLException exception ) {
			Utils.showException( " QueryManager: Error de consulta!" );
			Utils.showWarnGUI( null, msg );
			exception.printStackTrace();
		}
		return new String[][] {};
	}
	
	private String[] readCFG( String path ) throws FileNotFoundException, IOException {
		@SuppressWarnings("resource")
		BufferedReader reader = new BufferedReader( new FileReader ( new File ( path ) ) );
		return new String[] { reader.readLine(), reader.readLine(), reader.readLine() };
	}
	
	void closeConnection() {
		try {
			conection.closeConnection();
		} catch (SQLException exception) {
			Utils.showException( " QueryManager: Cierre de conexion fallida." );
			Utils.showErrorGUI( null, "Cierre de conexion a Base de Datos fallida!" );
			exception.printStackTrace();
		}
	}
}