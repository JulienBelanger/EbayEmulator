import java.sql.Statement;
import java.sql.ResultSet;

public interface Table {
	
	public void insert();

	public void delete();

	public static boolean exists(Statement stmt, String table){
		try
		{
			String sql = "SELECT Count(to_regclass('"+table+"'));";
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				if(rs.getInt("count")==1){
					return true;
				}
			}
		}catch(Exception e){
			e.printStackTrace(); 
			System.err.println(e.getClass().getName()+": "+e.getMessage());
			System.exit(0);}
		return false;
	}

	public static void dropType(Statement stmt, String type)
	{
		try
		{
			String sql = "DROP TYPE " + type + ";";
			stmt.executeUpdate(sql);	
		}catch(Exception e){
			e.printStackTrace(); 
			System.err.println(e.getClass().getName()+": "+e.getMessage());
			System.exit(0);}
		
	}

}