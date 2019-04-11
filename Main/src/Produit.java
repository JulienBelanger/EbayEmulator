import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.math.BigDecimal;
import javafx.scene.control.ComboBox;
import java.util.HashMap;
import java.util.Map;

public class Produit implements Table
{

	int id;
	int acc_id;
	public enum Categories { CARS, HEALTH, CLOTHING, ELECTRONICS, ART, PETS, APPLIANCES};
	Categories categorie;
	String titre;
	String description;
	BigDecimal price;
	Statement stmt;
	String sql;
	public static int actualId = 0;

	public Produit(int acc_id, Categories categorie, String titre, String description, BigDecimal price, Statement stmt)
	{
		this.id = 0;
		this.acc_id = acc_id;
		this.categorie = categorie;
		this.titre = titre;
		this.description = description;
		this.price = price;
		this.stmt = stmt;
	}

	public static void buyerLook(Statement stmt, ComboBox cb, String where_condition)
	{	//selectionner tous les produits still on market (dont aucune offre n'a été acceté ou aucune offre n'a été faite (left join, on garde les produits si aucune offre faite) et dont une estimation (nécéssairement la dernière) a été acceptée (ne pas laisser l'acheteur voir les produits encore en attente))
		String sql;
		try
		{

			if(where_condition != " And "){
				sql = "select produit.* from produit join (select * from estimation where accepted = true) as foo on produit.id = foo.prd_id join compte on produit.acc_id = compte.id join utilisateur on compte.usr_id = utilisateur.id where produit.id not in (select prd_id from offre where accepted = true) " + where_condition;
			}
			else
			{
				sql = "select produit.* from produit join (select * from estimation where accepted = true) as foo on produit.id = foo.prd_id  where produit.id not in (select prd_id from offre where accepted = true);";
			}
			ResultSet rs = stmt.executeQuery(sql);
			Map<String, String> resDict = new HashMap<String, String>();
			String daString;
			while(rs.next())
			{

				daString = "id:" + rs.getString("id") + ",titre:" + rs.getString("titre") + ",description:" + rs.getString("description") + ",price:"+rs.getString("price");
				System.out.println("current select: " + daString);
				cb.getItems().addAll(daString);
			}
		}catch(Exception e){
			e.printStackTrace(); 
			System.err.println(e.getClass().getName()+": "+e.getMessage());
			System.exit(0);}
	}

	public static BigDecimal getPrice(Statement stmt, int prd_id)
	{
		try
		{
			String sql = "select price from estimation where prd_id = " + prd_id + " AND accepted = true;";
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next())
			{
				return rs.getBigDecimal("price");
			}
		}catch(Exception e){
			e.printStackTrace(); 
			System.err.println(e.getClass().getName()+": "+e.getMessage());
			System.exit(0);}
		return null;
	}

	public static void createTable(Statement stmt)
	{
		try
		{
			String sql;
			sql = "CREATE TYPE CATEGORIES AS ENUM ('CARS', 'HEALTH', 'CLOTHING', 'ELECTRONICS', 'ART', 'PETS', 'APPLIANCES');";
			stmt.executeUpdate(sql);
			sql = "CREATE TABLE produit (id INT PRIMARY KEY, acc_id INTEGER REFERENCES compte(id), categorie CATEGORIES NOT NULL, titre VARCHAR (30), description TEXT, price MONEY);";
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
			this.id = Produit.actualId+1;
			Produit.actualId++;
			sql = "INSERT INTO produit (id, acc_id, categorie, titre, description, price) VALUES" + "(" + id + ", '" + acc_id + "', '" + categorie.name() + "', '" + titre + "', '" + description + "', " + price + ");";
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
			sql = "DELETE FROM produit WHERE id = " + id + ";";
			stmt.executeUpdate(sql);
			
		}catch(Exception e){
			e.printStackTrace(); 
			System.err.println(e.getClass().getName()+": "+e.getMessage());};
	}

	public static int getdbId(Statement stmt)
	{
		try
		{
			String sql = "SELECT MAX(id) FROM produit;";
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
		Produit.actualId = id;
	}

}
