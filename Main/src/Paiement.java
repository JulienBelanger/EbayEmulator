import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Time;
import java.math.BigDecimal;


public class Paiement implements Table
{

	int id;
	int ach_id;
	int aff_id;
	int prd_id;
	BigDecimal price;
	Statement stmt;
	String sql;
	public static int actualId = 0;

	public Paiement(int ach_id, int prd_id, int aff_id, BigDecimal price, Statement stmt)
	{
		this.id = 0;
		this.ach_id = ach_id;
		this.prd_id = prd_id;
		this.aff_id = aff_id;
		this.price = price;
		this.stmt = stmt;

	}

	public static void createTable(Statement stmt)
	{
		try
		{
			String sql;
			sql = "CREATE TABLE paiement (id INT PRIMARY KEY, ach_id INTEGER REFERENCES compte(id), prd_id INTEGER REFERENCES produit(id), aff_id INTEGER REFERENCES compte(id), time TIMESTAMP, price MONEY);";
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
			this.id = Paiement.actualId+1;
			Paiement.actualId++;
			sql = "INSERT INTO paiement (id, ach_id, prd_id, aff_id, price) VALUES " + "(" + id + ", '" + ach_id + ", '" + prd_id + "', '"+ aff_id + "', "  + price + ");";
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
			sql = "DELETE FROM paiement WHERE id = " + id + ";";
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
			String sql = "SELECT MAX(id) FROM paiement;";
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
		Paiement.actualId = id;
	}

}