package messenger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;

public class MainSQL {
	
	public static void main(String[] args)
	{
		String url = "jdbc:mysql://localhost:3306/nutzerdaten";
		String user = "root";
		String password = "";
		Datenbank neu = new Datenbank(url, user, password);
		
		//System.out.println(a1.get(3).get(3));
		//neu.registrieren("Max3", "Lernen2");
		//System.out.println(neu.sqlAbfrage("SELECT Name FROM nutzer WHERE BINARY Name = 'robert';"));
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String input = "";
		
		while(!input.equals("q"))
		{
			try {
				
				if(!neu.getEingeloggt())
				{
					System.out.println("1: login\n2: registrieren");
					input = br.readLine();
					switch(input)
					{
						
						case("1"): 
							System.out.println("name eingeben");
							String name = br.readLine();
							System.out.println("passwort eingeben");
							String passwort = br.readLine();
							neu.login(name, passwort);
							break;
						case("2"): 
							System.out.println("name eingeben");
							String name1 = br.readLine();
							System.out.println("passwort eingeben");
							String passwort1 = br.readLine();
							neu.registrieren(name1, passwort1);
							break;
					}	
				}else{
					System.out.println("1: adressbuch\n2: nachrichten anzeigen\n3: senden\n4: nachrichtenloeschen");
					input = br.readLine();
					switch(input)
					{
						case("1"): neu.adressbuch();
							break;
						case("2"): neu.nachrichtEmpfangen();
							break;
						case("3"): neu.nachrichtSenden();
							break;
						case("4"): neu.loescheNachrichten();
							break;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
