// Julian Tolentino 9954-2634
// Javier Ramirez 5840-0106

import java.io.* ;
import java.sql.* ;
import java.util.*;



public class JJFunFactory {
	public static void main(String[] args)  throws SQLException{
		Connection sqlcon  = null;
		Statement sqlStatement  = null;
		ResultSet myResultSet  = null;

		// 1st two options: login or sign up
		
		int decision = 0;
		
		try {
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());

			// Connect to the database
			sqlcon= DriverManager.getConnection ("jdbc:oracle:thin:hr/hr@oracle.cise.ufl.edu:1521:orcl", "jnt", "yeah=1738");
	
			// Create a Statement
			sqlStatement = sqlcon.createStatement ();
	
			
			/// 1 == login, 2 == sign up
			decision = intro();
			String loginResult = "", userID = "", user_sts = "";
			if(decision == 1){
				do{
					loginResult = login(sqlcon, sqlStatement, myResultSet);
				}while(loginResult.equals("-"));
				String [] tokens = loginResult.split("-");
				userID = tokens[0];
				user_sts = tokens[1];
			}
			else if(decision == 2){
				// Sign up method
				signUp(sqlcon, sqlStatement, myResultSet);

				
			}
			else if(decision == 3){
				browse(sqlcon, sqlStatement, myResultSet);
			}
			
			

			
			
			// call sqlStatement.executeQuery ()
			//String q = "select Student.ID, name, GPA from Student, Apply where UnivName = \'UF\' and Student.ID = Apply.ID";
			String q = "select * FROM USERS ORDER BY ID ASC";
			
			System.out.println(q);      
			myResultSet = sqlStatement.executeQuery(q);
			
			System.out.println("------------------------------------------------");
			System.out.println("Users of JJ's Fun Factory");
			
			System.out.println("------------------------------------------------");
			System.out.println("ID\tName\t\tAddress\t\t\t\tIs Staff\tEmail\t\t\t\t\t\tPassword");
			
			// Move to next row and & its contents to the html output
			while(myResultSet.next())
			{
			  String id = myResultSet.getObject(1).toString();
			  String name = myResultSet.getObject(2).toString();
			  String address = myResultSet.getObject(3).toString();
			  String is_staff = myResultSet.getObject(4).toString();
			  String email = myResultSet.getObject(5).toString();
			  String password = myResultSet.getObject(6).toString();
			  System.out.println(id+"\t"+name+"\t"+address+"\t"+is_staff+"\t\t"+email+"\t"+password);
			}

			sqlStatement.close();
			
			sqlcon.close();
		}

		catch (SQLException ex)
		{
			System.out.println("SQLException:" + ex.getMessage() + "<BR>");
		}
	} 
	
	// choose login or sign up
	static int intro(){
		int input = 0;
		Scanner in = new Scanner(System.in);
		
		
		do{
			System.out.println("-------------------------------------------------------------------");
			System.out.println("Welcome to JJ's Fun Factory.\nWould you like to login, sign up, or browse products?");
			System.out.println("Login: 1");
			System.out.println("Sign up: 2");
			System.out.println("Browse: 3");
			input = in.nextInt();
			
			if(input != 1 && input != 2 && input != 3){
				System.out.println("Invalid input");
			}

		}
		while(input != 1 && input != 2 && input != 3);
		

		
		return input;
		
	}
	
	// sign up
	static void signUp(Connection sqlcon, Statement sqlStatement, ResultSet myResultSet){
		Scanner in = new Scanner(System.in);
		String q = "select MAX(ID) FROM USERS";
		try{
			myResultSet = sqlStatement.executeQuery(q);
			myResultSet.next();
			
			int maxID = Integer.parseInt(myResultSet.getObject(1).toString()); 
			int newID = maxID + 1;
			System.out.println("Enter name:");
			String name = in.nextLine();
			
			System.out.println("Enter address:");
			String address = in.next();

			System.out.println("Are you staff? (Y/N):");
			
			
			String staff = in.next();
			int staffID = 0;
			do{
				if (staff.equals("Y")){
					staffID = 1;
				}
				else if (staff.equals("N")){
					staffID = 0;
				}
			}while(!staff.equals("Y") && !staff.equals("N"));
			

			
			System.out.println("Enter email:");
			String email = in.next();

			
			System.out.println("Enter Password:");
			String password = in.next();

			String r = "INSERT INTO USERS VALUES(" + newID + ",'" + name + "','" + address + "'," + staffID + ",'" + email + "','" + password + "')";
			myResultSet = sqlStatement.executeQuery(r);
		
			
			
		}
		catch (SQLException ex)
		{
			System.out.println("SQLException:" + ex.getMessage() + "<BR>");
		}
		


	}
	
	static String login(Connection sqlcon, Statement sqlStatement, ResultSet myResultSet){
		Scanner in = new Scanner(System.in);
		String result = ""; 

		try{
			
			System.out.println("Enter name:");
			String name = in.nextLine();
			
			System.out.println("Enter Password:");
			String password = in.next();
			

			
			String r = "SELECT ID, IS_STAFF from USERS WHERE name = '" + name + "' AND password = '" + password + "'";
			myResultSet = sqlStatement.executeQuery(r);

			boolean hasRows = false;
	
			String id = "";
			String is_staff = "";
			
			while(myResultSet.next()){
				hasRows = true;
				id = myResultSet.getObject(1).toString();
				is_staff = myResultSet.getObject(2).toString();
			}
			
			if (!hasRows){
				System.out.println("Name and Password Incorrect.");
			}
			
			result = id + "-" + is_staff;
			
			
			
			
		}
		catch (SQLException ex)
		{
			System.out.println("SQLException:" + ex.getMessage() + "<BR>");
		}
		return result;
	}
	static void browse(Connection sqlcon, Statement sqlStatement, ResultSet myResultSet){
		try{
			String r = "SELECT NAME from PRODUCTS";
			myResultSet = sqlStatement.executeQuery(r);
			while(myResultSet.next())
			{
			  String name = myResultSet.getObject(1).toString();
			  System.out.println(name);
			}
		}
		catch (SQLException ex)
		{
			System.out.println("SQLException:" + ex.getMessage() + "<BR>");
		}
	}

}


