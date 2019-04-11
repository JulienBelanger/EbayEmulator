import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.math.BigDecimal;


public class Utilisateur implements Table
{
	int id;
	String nom;
	String prenom;
	String username;
	String password;
	Compte compte;
	Statement stmt;
	String sql;

	public static int actualId = 0;


	public Utilisateur(Statement stmt, String nom, String prenom, String username, String password)
	{
		this.id = 0;
		this.nom = nom;
		this.prenom = prenom;
		this.stmt = stmt;
		this.username = username;
		this.password = password;
	}

	public static String getName(Statement stmt, String username)
	{
		try
		{
			String sql = "SELECT utilisateur.prenom from utilisateur join (select * from compte where username ='"+username+"') as foo on utilisateur.id = foo.usr_id;";
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next())
			{
				return rs.getString("prenom");
			}
		}catch(Exception e){
			e.printStackTrace(); 
			System.err.println(e.getClass().getName()+": "+e.getMessage());
			System.exit(0);}
		return "";
	}

	public static void createTable(Statement stmt)
	{
		try
		{
			String sql;
			sql = "CREATE TABLE utilisateur (id INT PRIMARY KEY, nom VARCHAR (30) NOT NULL, prenom VARCHAR (30) NOT NULL);";
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
			// Update table id generator
			this.id = Utilisateur.actualId+1;
			Utilisateur.actualId++;
			// put user into db
			sql = "INSERT INTO utilisateur (id, nom, prenom) VALUES " + "(" + id + ", '" + nom + "', '" + prenom + "');";
			stmt.executeUpdate(sql);
			// Create new compte for new user (1 to many)
			compte = new Compte(id, username, password, stmt);
			compte.insert(); // put it in db
			
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
			sql = "DELETE FROM utilisateur WHERE id = " + id + ";";
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
			String sql = "SELECT MAX(id) FROM Utilisateur;";
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
		Utilisateur.actualId = id;
	}

	public static String UserBMoy(Statement stmt)
	{
		String res = "";
		try
		{
			String sql = "Select prenom, nom, username, count from (Select count(*), username, prenom, nom from offre join produit on offre.prd_id = produit.id join compte on produit.acc_id = compte.id join utilisateur on compte.usr_id = utilisateur.id group by (username, prenom, nom)) as goo where count >= (select AVG(count) from (Select count(*) from offre join produit on offre.prd_id = produit.id join compte on produit.acc_id = compte.id join utilisateur on compte.usr_id = utilisateur.id group by username) as foo);";
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next())
			{	
				res = res + "|" + rs.getString("prenom") + "|" + rs.getString("nom") + "|" + rs.getString("username") + "| \n";
				System.out.println("|" + rs.getString("prenom") + "|" + rs.getString("nom") + "|" + rs.getString("username") + "|");
			}
		}catch(Exception e){
			e.printStackTrace(); 
			System.err.println(e.getClass().getName()+": "+e.getMessage());
			System.exit(0);}
		return res;
	}

}