import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

class PostgreSQL {
	
	private Connection conection;
	
	PostgreSQL( String host, String user, String passwd ) throws SQLException {
		conection = DriverManager.getConnection( host, user, passwd );
		Utils.showSQLmessage( "Conexion a Base de Datos Creada exitosamente." );
	}
	
	ResultSet injectQuery( String query ) throws SQLException {
		return conection.createStatement().executeQuery( query );
	}
	
	boolean execute( String command ) throws SQLException {
		return conection.createStatement().execute( command );
	}
	
	String[] getVectorOfInjectedQuery( int column, ResultSet resultSet ) throws SQLException {
		ArrayList<String> list = new ArrayList<>();

		while( resultSet.next() )
			list.add( resultSet.getString( column ) );
		resultSet.close();
		return list.toArray( new String[ list.size() ] );
	}
	
	String[][] getMatrixOfInjectedQuery( ResultSet resultSet ) throws SQLException {
		String[] row;
		ArrayList<String[]> list = new ArrayList<>();
		int columns = resultSet.getMetaData().getColumnCount();

		while( resultSet.next() ) {
			row = new String[ columns ];
			for( int i = 0; i < row.length; i++ )
				row[ i ] = resultSet.getString( i + 1 );
			list.add( row );
		}
		
		resultSet.close();
		return list.toArray( new String[ list.size() ][] );
	}
	
	void closeConnection() throws SQLException {
		conection.close();
		Utils.showSQLmessage( "Conexion a Base de Datos Cerrada exitosamente." );
	}
}