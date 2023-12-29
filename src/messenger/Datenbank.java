package messenger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

public class Datenbank {
	private boolean eingeloggt = false;
	private String DbUrl, DbUser, DbPassword,User,UserId;
	Datenbank(String url, String user, String password)
	{
		try(Connection conn = DriverManager.getConnection(url, user, password)){
			System.out.println("datenbankverbindung erfolgreich");
			this.DbUrl = url;
			this.DbUser = user;
			this.DbPassword = password;
		}catch(SQLException e)
		{
			System.out.println("konnte nicht auf datenbank zugreifen");;
		}
	}
	void registrieren(String nameRegister, String passwordRegister)
	{
		String query = "SELECT Name FROM nutzer WHERE BINARY Name = '"+nameRegister+"';";
		ArrayList<ArrayList<String>> sqlString = sqlAbfrageRs(query);
		if(!sqlString.isEmpty())
		{
			System.out.println("nutzer existiert bereits");
		}else
		{
			if(!passwordRegister.isEmpty())
			{
				query = "INSERT INTO nutzer(Name, Passwort) VALUES('"+nameRegister+"','"+passwordRegister+"');";
				sqlAbfrage(query);
				System.out.println("Erfolgreich registriert");
			}else
			{
				System.out.println("Kein passwort eingegeben");
			}
		}
	}
	void login(String nameLogin, String passwordLogin)
	{
		String query = "SELECT Name, NutzerID from nutzer WHERE BINARY NAME = '"+nameLogin+"' and BINARY Passwort = '"+passwordLogin+"';";
		ArrayList<String> sqlStrings = sqlAbfrageRs(query).get(0);
		User = sqlStrings.get(0);
		UserId = sqlStrings.get(1);
		if(User.contentEquals(nameLogin))
		{
			eingeloggt = true;
			System.out.println("Willkommen, "+User+"!");
		}
	}
	void nachrichtEmpfangen()
	{
		String query = "SELECT * FROM empfangeneNachricht WHERE EmpfaengerID = "+UserId+";";
		ArrayList<ArrayList<String>> sqlString = sqlAbfrageRs(query);
		for(int i = 0;i<sqlString.size();i++)
		{
			System.out.print(String.format("%-15s","von: "+sucheNutzer(sqlString.get(i).get(1))));
			System.out.print(String.format("%-7s","ID: "+sqlString.get(i).get(1)));
			System.out.println(String.format("%-10s","am: "+datum(sqlString.get(i).get(2))));
			System.out.println(String.format("%-25s",sqlString.get(i).get(3))+"\n");
		}
	}
	void nachrichtSenden()
	{			
		try {
			System.out.println("Nutzerid");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String idSenden = br.readLine();
			System.out.println("Nachricht");
			String nachrichtSenden = br.readLine();
			String time = ""+System.currentTimeMillis()/1000;
			String query = "INSERT INTO `empfangenenachricht`(`AbsenderID`, `EmpfaengerID`, `Zeit`, `Nachricht`) VALUES ('"+UserId+"','"+idSenden+"','"+time+"','"+nachrichtSenden+"')";
			if(sqlAbfrage(query))
			{
				System.out.println("nachricht an "+sucheNutzer(idSenden)+" abgeschickt");
			}else
			{
				System.out.println("fehler beim absenden");
			}
		} catch (IOException e) {
			System.out.println("fehler beim absenden");
		}
		
	}
	void loescheNachrichten()
	{
		String query = "DELETE FROM empfangeneNachricht WHERE EmpfaengerID = '"+UserId+"';";
		sqlAbfrage(query);
	}
	String sucheNutzer(String id)
	{
		String query = "SELECT Name FROM nutzer WHERE NutzerID = '"+id+"';";
		return sqlAbfrageRs(query).get(0).get(0);
	}
	String datum(String zeit)
	{
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
		Date date = new Date(Long.parseLong(zeit)*1000);	
		return date.toString();
		
	}
	void adressbuch()
	{
		String query = "SELECT Name, NutzerID FROM nutzer;";
		ArrayList<ArrayList<String>> sqlString = sqlAbfrageRs(query);
		ArrayList<String> sqlLabels = sqlAbfragenLabels(query);
		System.out.println(String.format("%-15s", sqlLabels.get(0))+String.format("%-15s", sqlLabels.get(1)));
		for(int i = 0;i<sqlString.get(0).size()*15;i++)
		{
			System.out.print("-");
		}
		System.out.println();
		for(int i = 0;i<sqlString.size();i++)
		{
			System.out.println(String.format("%-15s", sqlString.get(i).get(0))+String.format("%-15s", sqlString.get(i).get(1)));
		}
	}

	boolean getEingeloggt()
	{
		return eingeloggt;
	}
	public ArrayList<String> sqlAbfragenLabels(String query)
	{
		try(Connection conn = DriverManager.getConnection(DbUrl, DbUser, DbPassword))
		{
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			ArrayList<String> a1 = new ArrayList();
			for(int i = 1;i<rs.getMetaData().getColumnCount()+1;i++)
			{		
				a1.add(rs.getMetaData().getColumnLabel(i));
			}
			return a1;
		}catch (SQLException e) {
			System.out.println("Keine labels");
		}
		return null;
	}
	public ArrayList<ArrayList<String>> sqlAbfrageRs(String query)
	{
		try(Connection conn = DriverManager.getConnection(DbUrl, DbUser, DbPassword))
		{
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			ArrayList<ArrayList<String>> a2 = new ArrayList();
			while(rs.next()){
				ArrayList<String> a1 = new ArrayList();
				for(int i = 1;i<rs.getMetaData().getColumnCount()+1;i++)
				{
					
						a1.add(rs.getString(i));
				}
				a2.add(a1);
			}
			return a2;
		} catch (SQLException e) {
			System.out.println("Keine nachrichten");
		}
		return null;
	}
	public boolean sqlAbfrage(String query)
	{
		try(Connection conn = DriverManager.getConnection(DbUrl, DbUser, DbPassword))
		{
			Statement stmt = conn.createStatement();
			stmt.execute(query);
			return true;
		}catch(SQLException e)
		{
			System.out.println("ausgabefehler");
		}
		return false;
	}
}
