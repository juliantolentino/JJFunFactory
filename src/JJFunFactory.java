// Julian Tolentino 9954-2634
// Javier Ramirez 5840-0106

import java.io.* ;
import java.sql.* ;
import java.util.*;
import java.lang.StringBuilder;



public class JJFunFactory {
	public static void main(String[] args)  throws SQLException{
		Connection sqlcon  = null;
		Statement sqlStatement  = null;
		ResultSet myResultSet  = null;

		
		int decision = 0;
		Scanner in = new Scanner(System.in);
		
		try {
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());

			// Connect to the database
			sqlcon= DriverManager.getConnection ("jdbc:oracle:thin:hr/hr@oracle.cise.ufl.edu:1521:orcl", "jnt", "yeah=1738");
	
			// Create a Statement
			sqlStatement = sqlcon.createStatement ();
			
			
			/// 1 == login, 2 == sign up
			decision = intro();
			String loginResult = "", userID = "", user_sts = "";
			String tokens[];
			
			switch(decision){
			case 1: 
						do{
							loginResult = login(sqlcon, sqlStatement, myResultSet);
						}while(loginResult.equals("-"));
						tokens = loginResult.split("-");
						userID = tokens[0];
						user_sts = tokens[1];
						break;
			case 2:
						// Sign up method
						signUp(sqlcon, sqlStatement, myResultSet);
						do{
							loginResult = login(sqlcon, sqlStatement, myResultSet);
						}while(loginResult.equals("-"));
						tokens = loginResult.split("-");
						userID = tokens[0];
						user_sts = tokens[1];
						break;
			case 3:
						browse(sqlcon, sqlStatement, myResultSet);
					
						do{
							System.out.println("\nSearch: 1 \nLogin: 2\nSignup: 3\nLeave: -1 ");
							decision = in.nextInt();
							switch(decision) {
							case 1:	search(sqlcon, sqlStatement, myResultSet);
									break;
							case 2: 
									do{
										loginResult = login(sqlcon, sqlStatement, myResultSet);
									}while(loginResult.equals("-"));
									tokens = loginResult.split("-");
									userID = tokens[0];
									user_sts = tokens[1];
									break;
							case 3: 
									signUp(sqlcon, sqlStatement, myResultSet);
									do{
										loginResult = login(sqlcon, sqlStatement, myResultSet);
									}while(loginResult.equals("-"));
									tokens = loginResult.split("-");
									userID = tokens[0];
									user_sts = tokens[1];
									break;
									
							case -1: System.out.println("Thanks for visiting our store!");
									break;
							default: System.out.println("Value not an option.");
									break;
							}
						}while(decision != -1 && decision != 2 && decision != 3);
						break;
			}
			
			if (user_sts.equals("0")){
				customer(sqlcon, sqlStatement, myResultSet);
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
			System.out.println("Sign Up Script");
			System.out.println("--------------------------------------------");
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
			
			System.out.println("Login Script");
			System.out.println("--------------------------------------------");
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
			System.out.println("Current Store Selection");
			System.out.println("--------------------------------------------");
			System.out.println("NAME\t\tPRICE\tQUANTITY");
			String r = "SELECT NAME, PRICE, STOCKQUANTITY from PRODUCTS";
			myResultSet = sqlStatement.executeQuery(r);
			while(myResultSet.next())
			{
			  String name = myResultSet.getObject(1).toString();
			  String price = myResultSet.getObject(2).toString();
			  String stockQuantity = myResultSet.getObject(3).toString();

			  System.out.println(name + "\t" + price + "\t" + stockQuantity);
			}
		}
		catch (SQLException ex)
		{
			System.out.println("SQLException:" + ex.getMessage() + "<BR>");
		}
	}
	
	static void search(Connection sqlcon, Statement sqlStatement, ResultSet myResultSet){
		Scanner in = new Scanner(System.in);
		
		try{
			System.out.println("Search Script");
			System.out.println("--------------------------------------------");
			System.out.println("Enter keyword for search:");
			String keyword = in.nextLine();
			keyword = convert(keyword);
			
			String r = "SELECT NAME from PRODUCTS WHERE name LIKE '%" + keyword + "%'";
			myResultSet = sqlStatement.executeQuery(r);
			
			while(myResultSet.next())
			{
			  keyword = myResultSet.getObject(1).toString();
			  System.out.println(keyword);
			}
		}
		catch (SQLException ex)
		{
			System.out.println("SQLException:" + ex.getMessage() + "<BR>");
		}
	}
	static String convert(String str){
		StringBuilder conversion  = new StringBuilder(str);
		for(int i = 0;  i < conversion.length(); i++){
			char temp = conversion.charAt(i);
			if((int)temp >= 97 && (int)temp <= 122){
				temp -= 32;
				conversion.setCharAt(i, temp);
			}
			
		}
		return conversion.toString();
	}
	static void customer(Connection sqlcon, Statement sqlStatement, ResultSet myResultSet){
		int decision = 0;
		Scanner in = new Scanner(System.in);
		
		browse(sqlcon, sqlStatement, myResultSet);
	
		do{
			System.out.println("\nBrowse: 1 \nSearch: 2 \nPlace Order: 3\nCheckout: 4\nEdit or Delete Account: 5\nLeave: -1 ");
			decision = in.nextInt();
			switch(decision) {
			case 1:	browse(sqlcon, sqlStatement, myResultSet);
					break;
			case 2: search(sqlcon, sqlStatement, myResultSet);
					break;
			case 3: placeOrder(sqlcon, sqlStatement, myResultSet);
					break;
			case 4: //checkout
					break;
			case 5: //edit delete account
				break;
			case -1: System.out.println("Thanks for visiting our store!");
					break;
			default: System.out.println("Value not an option.");
					break;
			}
		}while(decision != -1 );
	}
	static void placeOrder(Connection sqlcon, Statement sqlStatement, ResultSet myResultSet){
		Scanner in = new Scanner(System.in);
		
		try{
			System.out.println("Place Order Script");
			System.out.println("--------------------------------------------");
			System.out.println("Enter product name:");
			String name = in.nextLine();
			name = convert(name);
			System.out.println("Enter quantity of product:");
			String quantity = in.next();
			
			
			String q = "select STOCKQUANTITY FROM PRODUCTS WHERE name = '" + name + "'";
			myResultSet = sqlStatement.executeQuery(q);
			boolean hasRows = false;
			while(myResultSet.next()){
				int quantityOfProduct = Integer.parseInt(myResultSet.getObject(1).toString());
				int quantityNeededByUser = Integer.parseInt(quantity);
				quantityOfProduct -= quantityNeededByUser;
				quantity = Integer.toString(quantityOfProduct);
			}
			
			String r = "UPDATE PRODUCTS SET STOCKQUANTITY = '" + quantity + "' WHERE name = '" + name + "' AND STOCKQUANTITY > = '" + quantity + "'";
			
			// TO DO put proper error checking for an invalid number on query
			myResultSet = sqlStatement.executeQuery(r);
			if(myResultSet.next()){
				hasRows = true;
			}
			
			if(!hasRows){
				System.out.println("Order not placed.");
			}

		}
		catch (SQLException ex)
		{
			System.out.println("SQLException:" + ex.getMessage() + "<BR>");
		}

	}

}


