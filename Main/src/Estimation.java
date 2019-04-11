import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Time;
import java.math.BigDecimal;


public class Estimation implements Table
{

	int id;
	int prd_id;
	BigDecimal price;
	boolean accepted;
	Statement stmt;
	String sql;
	public static int actualId = 0;

	public Estimation(int prd_id, BigDecimal price, Statement stmt)
	{
		this.id = 0;
		this.prd_id = prd_id;
		this.price = price;
		this.accepted = false;
		this.stmt = stmt;
	}

	public static void createTable(Statement stmt)
	{
		try
		{
			String sql;
			sql = "CREATE TABLE estimation (id INT PRIMARY KEY, prd_id INTEGER REFERENCES produit(id), time TIMESTAMP NOT NULL, price MONEY, accepted BOOLEAN DEFAULT FALSE);";
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
			this.id = Estimation.actualId+1;
			Estimation.actualId++;
			sql = "INSERT INTO estimation (id, prd_id, time, price) VALUES " + "(" + id + ", " + prd_id + ", NOW(), "  + price + ");";
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
			sql = "DELETE FROM estimation WHERE id = " + id + ";";
			stmt.executeUpdate(sql);	
			
		}catch(Exception e){
			e.printStackTrace(); 
			System.err.println(e.getClass().getName()+": "+e.getMessage());
			System.exit(0);}
	}

	public static void accept(Statement stmt, int prd_id)
	{
		try
		{

			String sql = "UPDATE estimation SET accepted = true WHERE id = (select id from estimation where prd_id = " + prd_id + " order by time desc LIMIT 1);";
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
			String sql = "SELECT MAX(id) FROM estimation;";
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
		Estimation.actualId = id;
	}

	public static void main(String[] args)
	{	
		Statement stmt = null;
		BigDecimal bf = new BigDecimal("50");
		Estimation es = new Estimation(12, bf, stmt);
	}

}