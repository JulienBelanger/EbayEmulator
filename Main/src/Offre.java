import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Time;
import java.math.BigDecimal;


public class Offre implements Table
{

	int id;
	int acc_id;
	int prd_id;
	BigDecimal price;
	boolean accepted;
	Statement stmt;
	String sql;
	public static int actualId = 0;

	public Offre(int acc_id, int prd_id, BigDecimal price, Statement stmt)
	{
		this.id = 0;
		this.acc_id = acc_id;
		this.prd_id = prd_id;
		this.price = price;
		this.accepted = false;
		this.stmt = stmt;

	}

	public static void compare(Statement stmt, int es_id)
	{

	}

	public static void createTable(Statement stmt)
	{
		try
		{
			String sql;
			sql = "CREATE TABLE offre (id INT PRIMARY KEY, acc_id INTEGER REFERENCES compte(id), prd_id INTEGER REFERENCES produit(id), time TIMESTAMP, price MONEY, accepted BOOLEAN DEFAULT FALSE);";
			stmt.executeUpdate(sql);
			
		}catch(Exception e){
			e.printStackTrace(); 
			System.err.println(e.getClass().getName()+": "+e.getMessage());
			System.exit(0);}
	}

	@Override
	public void insert()
	{
		try
		{
			this.id = Offre.actualId+1;
			Offre.actualId++;
			sql = "INSERT INTO offre (id, acc_id, prd_id, price, time) VALUES" + "(" + id + ", " + acc_id + ", " + prd_id + ", "  + price + ", NOW() );";
			stmt.executeUpdate(sql);
			
		}catch(Exception e){
			e.printStackTrace(); 
			System.err.println(e.getClass().getName()+": "+e.getMessage());
			System.exit(0);}
	}

	@Override
	public void delete()
	{
		try
		{
			sql = "DELETE FROM offre WHERE id = " + id + ";";
			stmt.executeUpdate(sql);
			
		}catch(Exception e){
			e.printStackTrace(); 
			System.err.println(e.getClass().getName()+": "+e.getMessage());
			System.exit(0);}
	}


	public void accept()
	{
		try
		{
			this.accepted = true;
			sql = "UPDATE offre SET accepted = true WHERE id = " + id + ";";
			stmt.executeUpdate(sql);
			
		}catch(Exception e){
			e.printStackTrace(); 
			System.err.println(e.getClass().getName()+": "+e.getMessage());
			System.exit(0);}
	}

	public static int getdbId(Statement stmt)
	{
		try
		{
			String sql = "SELECT MAX(id) FROM offre;";
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				int max = rs.getInt("max");
				return max;
			}
		}catch(Exception e){
			e.printStackTrace(); 
			System.err.println(e.getClass().getName()+": "+e.getMessage());
			System.exit(0);}
		return -1;
	}

	public static void setId(int id)
	{
		Offre.actualId = id;
	}

}