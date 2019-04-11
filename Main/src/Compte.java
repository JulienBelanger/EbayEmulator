import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.math.BigDecimal;


public class Compte implements Table
{
	int id;
	int usr_id;
	String username;
	String password;
	Statement stmt;
	String sql;
	public static int actualId = 0;

	public Compte(int usr_id, String username, String password, Statement stmt)
	{
		this.id = 0;
		this.usr_id = usr_id;
		this.username = username;
		this.password = password;
		this.stmt = stmt;
	}

	public static void createTable(Statement stmt)
	{	
		try
		{
			String sql; 
			sql = "CREATE TABLE compte (id INT PRIMARY KEY, usr_id INTEGER REFERENCES utilisateur(id) ON DELETE CASCADE, username VARCHAR (30) UNIQUE, password VARCHAR (30), CONSTRAINT usernameNotNull CHECK (NOT (( username IS NULL  OR  username = '' ) AND ( password IS NULL  OR  password = '' ))));";
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
			this.id = Compte.actualId+1;
			Compte.actualId++;
			sql = "INSERT INTO compte (id, usr_id, username, password) VALUES" + "(" + id + ", '" + usr_id + "', '" + username + "', '" + password + "');";
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
			sql = "DELETE FROM compte WHERE id = " + id + ";";
			stmt.executeUpdate(sql);
		}catch(Exception e){
			e.printStackTrace(); 
			System.err.println(e.getClass().getName()+": "+e.getMessage());
			System.exit(0);}
	}

	public static int getUsr(Statement stmt, String username)
	{
		try
		{
			String sql;
			sql = "SELECT usr_id FROM compte where username = '" + username +"';";
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next())
			{
				return rs.getInt("usr_id");
			}
		}catch(Exception e){
			e.printStackTrace(); 
			System.err.println(e.getClass().getName()+": "+e.getMessage());
			System.exit(0);}
		return -1;
	}

	public static boolean exists(Statement stmt, String username)
	{
		try
		{
			String sql;
			sql = "SELECT * FROM compte where username = '" + username + "';";
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next())
			{
				return true;
			}
			
		}catch(Exception e){
			e.printStackTrace(); 
			System.err.println(e.getClass().getName()+": "+e.getMessage());
			System.exit(0);}
		
		return false;
	}

	public static boolean exists(Statement stmt, String username, String password)
	{
		try
		{
			String sql;
			sql = "SELECT * FROM compte where username = '" + username + "' AND password = '" + password + "';";
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next())
			{
				return true;
			}
			
		}catch(Exception e){
			e.printStackTrace(); 
			System.err.println(e.getClass().getName()+": "+e.getMessage());
			System.exit(0);}
		
		return false;
	}

	public static int getId(Statement stmt, String username)
	{
		try
		{
			String sql = "SELECT id from compte where username = '" + username + "';";
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next())
			{
				return(rs.getInt("id"));
			}
		}catch(Exception e){
			e.printStackTrace(); 
			System.err.println(e.getClass().getName()+": "+e.getMessage());
			System.exit(0);}
		return -1;
	}

	public static int getdbId(Statement stmt)
	{
		try
		{
			String sql = "SELECT MAX(id) FROM compte;";
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next())
			{
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
		Compte.actualId = id;
	}

	public static String dance(Statement stmt)
	{
		String res = "";
		try
		{
			String sql = "Select username, (v1.AVG - v2.AVG)/v2.stddev as STD_distance_from_the_mean from ( (Select AVG(count) as avg, username from (select prd_id, count(*) from estimation group by prd_id) as loo join produit on loo.prd_id = produit.id join compte on produit.acc_id = compte.id  group by username) as v1 cross join (Select STDDEV(avg), AVG(avg) from (select AVG(count) as avg from (select prd_id, count(*) from estimation group by prd_id) as foo join produit on foo.prd_id = produit.id join compte on produit.acc_id = compte.id group by username) as too) as v2);";
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next())
			{
				res = res + "|" + rs.getString("username") + "|" + rs.getFloat("std_distance_from_the_mean") + "|\n";
				System.out.println("|" + rs.getString("username") + "|" + rs.getFloat("std_distance_from_the_mean") + "|");
			}
		}catch(Exception e){
			e.printStackTrace(); 
			System.err.println(e.getClass().getName()+": "+e.getMessage());
			System.exit(0);}
		return res;
	}

}