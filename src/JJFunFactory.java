// Julian Tolentino 9954-2634
// Javier Ramirez 5840-0106

import java.io.* ;
import java.sql.* ;
import java.util.*;
import java.lang.StringBuilder;
import java.text.*;
import java.util.Date;



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
							
							while(!in.hasNextInt()){
								System.out.println("Value not an option.");
								in.nextLine();
							}
							
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
				customer(sqlcon, sqlStatement, myResultSet, userID);
			}
			else{
				staff(sqlcon, sqlStatement, myResultSet, userID);
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
			while(!in.hasNextInt()){
				System.out.println("Value not an option.");
				in.nextLine();
			}
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
	
			String staff = "";
			int staffID = 0;
			do{
				staff = in.next();
				if (staff.equals("Y")){
					staffID = 1;
				}
				else if (staff.equals("N")){
					staffID = 0;
				}
				else
					System.out.println("Value not an option.");
					
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
			String r = "SELECT NAME, PRICE, STOCKQUANTITY from PRODUCTS ORDER BY PRICE DESC";
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
	static void customer(Connection sqlcon, Statement sqlStatement, ResultSet myResultSet, String userID){
		int decision = 0;
		Scanner in = new Scanner(System.in);
		
		browse(sqlcon, sqlStatement, myResultSet);
	
		do{
			System.out.println("\nBrowse: 1 \nSearch: 2 \nPlace Order: 3\nCheckout: 4\nUpdate or Delete Account: 5\nLeave: -1 ");
			while(!in.hasNextInt()){
				System.out.println("Value not an option.");
				in.nextLine();
			}
			
			decision = in.nextInt();
			switch(decision) {
			case 1:	browse(sqlcon, sqlStatement, myResultSet);
					break;
			case 2: search(sqlcon, sqlStatement, myResultSet);
					break;
			case 3: placeOrder(sqlcon, sqlStatement, myResultSet, userID);
					break;
			case 4: checkOut(sqlcon, sqlStatement, myResultSet, userID);
					break;
			case 5: updateOrDeleteAccount(sqlcon, sqlStatement, myResultSet, userID);
					break;
			case -1: System.out.println("Thanks for visiting our store!");
					break;
			default: System.out.println("Value not an option.");
					break;
			}
		}while(decision != -1 );
	}
	
	
	static void placeOrder(Connection sqlcon, Statement sqlStatement, ResultSet myResultSet, String userID){
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
			int quantityOfProduct = 0, quantityNeededByUser = 0;
			while(myResultSet.next()){
				quantityOfProduct = Integer.parseInt(myResultSet.getObject(1).toString());
				quantityNeededByUser = Integer.parseInt(quantity);
				if(quantityNeededByUser > quantityOfProduct || quantityNeededByUser < 1){
					System.out.println("Order not placed.");
					return;
				}
				quantityOfProduct -= quantityNeededByUser;
				quantity = Integer.toString(quantityOfProduct);
			}
			
			
			int priceTimesQuantity = 0;
			String s = "select PRICE FROM PRODUCTS WHERE name = '" + name + "'";
			myResultSet = sqlStatement.executeQuery(s);
			while(myResultSet.next()){
				priceTimesQuantity = Integer.parseInt(myResultSet.getObject(1).toString());
				priceTimesQuantity *= quantityNeededByUser;
			}
			
			String productID = "";
			String t = "select ID FROM PRODUCTS WHERE name = '" + name + "'";
			myResultSet = sqlStatement.executeQuery(t);
			while(myResultSet.next()){
				productID = myResultSet.getObject(1).toString();
				hasRows = true;
			}

			int productQuantityFromOrders = 0;
			int totalPriceFromOrders = 0;
			String v = "select PRODUCTQUANTITY, TOTALPRICE FROM ORDERS WHERE PRODUCTID = '" + productID + "'";
			myResultSet = sqlStatement.executeQuery(v);
			while(myResultSet.next()){
				productQuantityFromOrders =  Integer.parseInt(myResultSet.getObject(1).toString());
				productQuantityFromOrders += quantityNeededByUser;
				totalPriceFromOrders = Integer.parseInt(myResultSet.getObject(2).toString());
				totalPriceFromOrders += priceTimesQuantity;

				hasRows = true;
			}

			String u = "SELECT * FROM ORDERS WHERE ID = '" + userID + "' AND PRODUCTID = '" + productID + "'";

			myResultSet = sqlStatement.executeQuery(u);

		
			
			if(!myResultSet.next()){
				try{

					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					java.util.Date myDate = format.parse(getCurrentTimeStamp());
					PreparedStatement pstmt = sqlcon.prepareStatement(
							"INSERT INTO ORDERS ( ID, TOTALPRICE, DATEOFORDER, PAID, PRODUCTID, PRODUCTQUANTITY ) " +
							" values (?, ?, ?, ?, ?, ? )");
					pstmt.setString(1, userID);
					pstmt.setString(2, Integer.toString(priceTimesQuantity));
					java.sql.Date sqlDate = new java.sql.Date( myDate.getTime() );
					pstmt.setDate(3, sqlDate);
					pstmt.setString(4, "0");
					pstmt.setString(5, productID);
					pstmt.setString(6, Integer.toString(quantityNeededByUser));
					pstmt.executeUpdate();
					System.out.println("Order placed.");
					

				} catch(java.text.ParseException e){
					e.printStackTrace();
				}
				
				String r = "UPDATE PRODUCTS SET STOCKQUANTITY = '" + quantity + "' WHERE name = '" + name + "' AND STOCKQUANTITY > = '" + quantity + "'";
				myResultSet = sqlStatement.executeQuery(r);
				if(myResultSet.next()){
					hasRows = true;
				}
				
				// TO DO replace order if same product is chosen before checkout
				
				
				if(!hasRows){
					System.out.println("Order not placed.");
				}
				
				
			}
			else{
				String y = "UPDATE ORDERS SET PRODUCTQUANTITY = " + productQuantityFromOrders + ", TOTALPRICE = " + totalPriceFromOrders + " WHERE ID = '" + userID + "' AND PRODUCTID = '" + productID + "'";
				myResultSet = sqlStatement.executeQuery(y);
				String z = "UPDATE PRODUCTS SET STOCKQUANTITY = '" + quantity + "' WHERE name = '" + name + "' AND STOCKQUANTITY > = '" + quantity + "'";
				myResultSet = sqlStatement.executeQuery(z);
				if(myResultSet.next()){
					hasRows = true;
				}
			}
			
			

		}
		catch (SQLException ex)
		{
			System.out.println("SQLException:" + ex.getMessage() + "<BR>");
		}

	}
	
	static void checkOut(Connection sqlcon, Statement sqlStatement, ResultSet myResultSet, String userID){
		try{
			System.out.println("Current Orders");
			System.out.println("-----------------------------------");
			System.out.println("NAME\t\tSUBPRICE\tQUANTITY");
			String r = "SELECT PRODUCTID, PRODUCTQUANTITY, TOTALPRICE FROM ORDERS WHERE ID = '" + userID + "' AND PAID = 0";
			myResultSet = sqlStatement.executeQuery(r);
			
			int totalPrice = 0;
			Statement sqlStatementCheckout  = null;
			sqlStatementCheckout = sqlcon.createStatement ();

			
			while(myResultSet.next())
			{
				String productID = myResultSet.getObject(1).toString();
				String productQuantity = myResultSet.getObject(2).toString();
				String productSubPrice = myResultSet.getObject(3).toString();
				
				String s = "SELECT NAME FROM PRODUCTS WHERE ID = '" + productID + "'";
				ResultSet myOrderSet  = null;

				myOrderSet = sqlStatementCheckout.executeQuery(s);
				myOrderSet.next();
				String productName = myOrderSet.getObject(1).toString();
				
				totalPrice += Integer.parseInt(productSubPrice);
				
				System.out.println(productName + "\t" + productSubPrice + "\t\t" + productQuantity);
				
				
				
			}
			
			if(totalPrice != 0){
				System.out.println("\nTotal Price: " + totalPrice);
				System.out.println("Confirm checkout? Yes:1, No: 0");
				Scanner in = new Scanner(System.in);
				int decision = in.nextInt();
				if(decision == 1){
					String u = "UPDATE ORDERS SET PAID = 1 WHERE PAID = 0 AND ID = '" + userID + "'";
					myResultSet = sqlStatement.executeQuery(u);
					myResultSet.next();
					
					
				}
				else{
					return;
				}
			}
			
			
		}
		catch (SQLException ex)
		{
			System.out.println("SQLException:" + ex.getMessage() + "<BR>");
		}
	}
	
	public static String getCurrentTimeStamp() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    Date now = new Date();
	    String strDate = sdf.format(now);
	    return strDate;
	}
	
	static void updateOrDeleteAccount(Connection sqlcon, Statement sqlStatement, ResultSet myResultSet, String userID){
		try{
			System.out.println("Account Details");
			System.out.println("-----------------------------------");
			System.out.println("NAME\t\tADDRESS\tEMAIL\tPASSWORD");
			String t = "SELECT NAME, ADDRESS, EMAIL, PASSWORD FROM USERS WHERE ID = '" + userID + "'";
			myResultSet = sqlStatement.executeQuery(t);
			while(myResultSet.next())
			{
				String name = myResultSet.getObject(1).toString();
				String address = myResultSet.getObject(2).toString();
				String email = myResultSet.getObject(3).toString();
				String password = myResultSet.getObject(4).toString();
				
				
				System.out.println(name + "\t" + address + "\t" + email + "\t\t" + password);
				
				
				
			}
		}
		catch (SQLException ex)
		{
			System.out.println("SQLException:" + ex.getMessage() + "<BR>");
		}
		
		int decision = 0;
		Scanner in = new Scanner(System.in);
	
		do{
			System.out.println("\nUpdate Account: 1 \nDelete Account: 2 \nReturn to Customer Menu: -1");

			while (!in.hasNextInt()) {
				System.out.println("Value not an option.");
				in.nextLine();
			}
			decision = in.nextInt();
			
			if(decision == 1){
				
				try{
					
					System.out.println("Update Script");
					System.out.println("--------------------------------------------");
					System.out.println("Enter name:");
					String name = in.next();
					
					System.out.println("Enter address:");
					String address = in.next();

					
					System.out.println("Enter email:");
					String email = in.next();

					System.out.println("Enter Password:");
					String password = in.next();

					String r = "UPDATE USERS SET NAME = '" + name +  "', ADDRESS = '" + address + "', EMAIL = '" + email + "', PASSWORD = '" + password + "' WHERE ID = '" + userID + "'";
					myResultSet = sqlStatement.executeQuery(r);
				
					
					
				}
				catch (SQLException ex)
				{
					System.out.println("SQLException:" + ex.getMessage() + "<BR>");
				}
				
			}
			else if(decision == 2){
				try{
					String s = "DELETE FROM USERS WHERE ID = '" + userID + "'";
					myResultSet = sqlStatement.executeQuery(s);
				}
				catch (SQLException ex)
				{
					System.out.println("SQLException:" + ex.getMessage() + "<BR>");
				}
				System.out.println("Account deleted.\nThank you for visiting our store!");
				System.exit(0);
			}
			else
				return;
		}while(decision != -1 );
	}
	// staff initial switch statement
	static void staff(Connection sqlcon, Statement sqlStatement, ResultSet myResultSet, String userID){
		int decision = 0;
		Scanner in = new Scanner(System.in);
		
		// run query that checks products with stock < 3
		browse(sqlcon, sqlStatement, myResultSet);
	
		do{
			System.out.println("\nBrowse: 1 "
					+ "\nSearch: 2 "
					+ "\nPlace Order: 3"
					+ "\nCheckout: 4"
					+ "\nTotal Sales In All orders : 5"
					+ "\nEdit User Accounts : 6"
					+ "\nEdit Products : 7"
					+ "\nEdit Discounts : 8"
					+ "\nEdit Categories : 9"
					+ "\nEdit Orders : 10"
					+ "\nEdit Shelf : 11"
					+ "\nLeave: -1 ");
			
			while(!in.hasNextInt()){
				System.out.println("Value not an option.");
				in.nextLine();
			}
			
			decision = in.nextInt();
			switch(decision) {
			case 1:	browse(sqlcon, sqlStatement, myResultSet);
					break;
			case 2: search(sqlcon, sqlStatement, myResultSet);
					break;
			case 3: placeOrder(sqlcon, sqlStatement, myResultSet, userID);
					break;
			case 4: checkOut(sqlcon, sqlStatement, myResultSet, userID);
					break;
			case 5: // get the total sales in all orders for products provided by each supplier
					break;
			case 6: // administrative edit accounts 
					break;
			case 7: editProducts(sqlcon, sqlStatement, myResultSet, userID);
						// staff can get a list of products with their shelf locations for fast packaging.
					break;
			case 8: // edit discounts
					break;
			case 9: // edit categories
					break;
			case 10: // edit orders
					break;
			case 11: // edit shelf
					break;
			case -1: System.out.println("Thanks for visiting our store!");
					break;
			default: System.out.println("Value not an option.");
					break;
			}
		}while(decision != -1 );
	}
	static void editProducts(Connection sqlcon, Statement sqlStatement, ResultSet myResultSet, String userID){
		int decision = 0;
		Scanner in = new Scanner(System.in);
		do{
			
			System.out.println("\nAdd Product: 1 "
					+ "\nUpdate Product: 2 "
					+ "\nDelete Product: 3"
					+ "\nReturn to Staff Menu: -1 ");
			
			while(!in.hasNextInt()){
				System.out.println("Value not an option.");
				in.nextLine();
			}
			
			decision = in.nextInt();
			switch(decision) {
			case 1:	
					addProduct( sqlcon,  sqlStatement,  myResultSet);
					break;
			case 2: 
					break;
			case 3: 
					break;
			case -1: System.out.println("Returning to Staff Menu");
					break;
			default: System.out.println("Value not an option.");
					break;
			}
		}while(decision != -1 );
	}
	
	static void addProduct(Connection sqlcon, Statement sqlStatement, ResultSet myResultSet){
		Scanner in = new Scanner(System.in);
		try{
			String q = "SELECT MAX(ID) FROM PRODUCTS";

			myResultSet = sqlStatement.executeQuery(q);
			myResultSet.next();
				
				
			int maxID = Integer.parseInt(myResultSet.getObject(1).toString()); 
			int newID = maxID + 2;
			System.out.println(maxID + " " + newID);
			try{
				System.out.println("Add Product Script");
				System.out.println("--------------------------------------------");
				
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				java.util.Date myDate = format.parse(getCurrentTimeStamp());
				PreparedStatement pstmt = sqlcon.prepareStatement(
						"INSERT INTO PRODUCTS ( ID, NAME, PRICE, STOCKQUANTITY, DESCRIPTION, ACTIVE ) " +
						" values (?, ?, ?, ?, ?, ? )");
				pstmt.setString(1, Integer.toString(newID));
				System.out.println("Enter name:");
				String name = in.nextLine();
				pstmt.setString(2, name);
				System.out.println("Enter price:");
				String price = in.next();
				pstmt.setString(3, price);
				System.out.println("Enter stock quantity:");	
				String stockQuantity = in.next();
				pstmt.setString(4, stockQuantity);
				System.out.println("Enter description:");
				String description = in.nextLine();
				pstmt.setString(5, description);
				java.sql.Date sqlDate = new java.sql.Date( myDate.getTime() );
				pstmt.setDate(6, sqlDate);
				
				
				pstmt.executeUpdate();
				System.out.println("Product Added.");
			} catch(java.text.ParseException e){
				e.printStackTrace();
			}
		
			
			
		}
		catch (SQLException ex)
		{
			System.out.println("SQLException:" + ex.getMessage() + "<BR>");
		}
		


	}
	
}


