package passwordCheck;

import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.time.LocalDate;
import java.util.Calendar;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class Display extends JFrame implements ActionListener{
	
	//create variables to determine scores and feedback
    boolean hasUpper = false;
    boolean hasLower = false;
    boolean hasDigit = false;
    boolean hasSpecial = false;
    boolean hasPass = false;
    boolean hasRepeat = false;
    int iScore = 0;
	
	Container c = this.getContentPane();
    JLabel userlabel = new JLabel("Username");
    JLabel pwdlabel = new JLabel("Password");
    JLabel pwdverifylabel = new JLabel("Verify Password");
    JTextField usertf = new JTextField();
    JPasswordField passtf = new JPasswordField();
    JPasswordField passverifytf = new JPasswordField();
    JButton btnCheck = new JButton("Check Password");
    JButton btnSave = new JButton("Save Password");
    JTextArea txtScore = new JTextArea();

	public static void main(String[] args) {
		
		Display d = new Display();
		d.setTitle("Password Checker");
        d.setVisible(true);
        d.setBounds(100, 100, 600, 450);
        d.setDefaultCloseOperation(3);
	}
	
	Display() {
        this.c.setLayout((LayoutManager)null);
        this.userlabel.setBounds(120, 50, 100, 20);
        this.pwdlabel.setBounds(120, 75, 100, 20);
        this.pwdverifylabel.setBounds(120, 100, 100, 20);
        this.usertf.setBounds(220, 50, 200, 20);
        this.passtf.setBounds(220, 75, 200, 20);
        this.passverifytf.setBounds(220, 100, 200, 20);
        this.btnCheck.setBounds(220, 125, 150, 50);
        this.btnSave.setBounds(220, 175, 150, 50);
        this.txtScore.setBounds(100, 235, 400, 150);
        
        this.c.add(this.usertf);
        this.c.add(this.passtf);
        this.c.add(this.pwdverifylabel);
        this.c.add(this.pwdlabel);
        this.c.add(this.userlabel);
        this.c.add(this.passverifytf);
        this.c.add(this.btnCheck);
        this.c.add(this.btnSave);
        this.c.add(this.txtScore);
        
        this.btnSave.setVisible(false);
        
        this.btnCheck.addActionListener((ActionListener) this);
        this.btnSave.addActionListener((ActionListener )this);
    }

    public void actionPerformed(ActionEvent e) {

        String rating = "No score given";
        String feedback = " Your feedback is as follows:";
        String txtDisplay = "";
        String database = "";
        String fileName = "database.txt";
        
        //carry out password checks
       	if (e.getSource() == this.btnCheck) {
       		
        	try {
        		database = readFile(fileName);
        		String[] stringArray = database.split(" ");
        		String[] outputArray = new String[stringArray.length - 1];
        		for (int i = 0; i < stringArray.length - 1; i++) {
        		    outputArray[i] = stringArray[i] + " " + stringArray[i+1];
        		}
        		
        		Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/CyberPasswords" , stringArray[0] , stringArray[1]);
        		
        		Statement myStmt = myConn.createStatement();
				
    			  ResultSet myRs = myStmt.executeQuery("select * from Users where Username = '" + usertf.getText() + "'");
    			  
    			    Integer UserID = 0;
  				if (myRs.next()){
  					System.out.println(myRs.getString("UserID"));
  					UserID = myRs.getInt("UserID");
  					
  				}
  				
  				Statement previousStatement = myConn.createStatement();
  				
  				ResultSet previous = previousStatement.executeQuery("select * from Previous where User_ID = '" + UserID + "'");
  				
  				while(previous.next()) {
  					
  					String password = previous.getString("Password").toString();
  					System.out.println(password);
  					
  					//check if new password has been previously used
  					
  					if (BCrypt.checkpw(passtf.getText().toString(), password)) {
  						
  						hasRepeat = true;
  					}
  				}
  				if(this.passtf.getText().equals(this.passverifytf.getText()) ) {
            
  				//check password length and add score
            if (this.passtf.getText().length() < 8) {
                iScore = 0;
            } else if (this.passtf.getText().length() >= 10) {
                iScore = iScore + 10;
            } else {
                iScore = iScore + 5;
            }

            //check if a didgit is included and add to score
            if (this.passtf.getText().matches("(?=.*[0-9]).*")) {
                iScore += 10;
                hasDigit = true;
            }

            //check if a lower case letter is included and add to score
            if (this.passtf.getText().matches("(?=.*[a-z]).*")) {
                iScore += 10;
                hasLower = true;
            }

            //check if upper case letter is included and add to score
            if (this.passtf.getText().matches("(?=.*[A-Z]).*")) {
                iScore += 10;
                hasUpper = true;
            }

            //check if special character is included and add to score
            if (this.passtf.getText().matches("(?=.*[~!@#$%^&*()_-]).*")) {
                iScore += 10;
                hasSpecial = true;
            }

            //check if the word 'password' is in the new password and add to score
            if (this.passtf.getText().contains("password") || this.passtf.getText().contains("Password")) {
                iScore -= 20;
                hasPass = true;
            }
            
            //Determine what rating to give
            if(iScore <=20) {
            	rating = "Poor.";
            }else if ( iScore > 20 && iScore <=30) {
            	rating = "Average.";
            }else if (iScore > 30 && iScore <=40) {
            	rating = "Good.";
            }else if (iScore > 40) {
            	rating = "Excelent.";
            }
            
            //Determine the feedback
            if (hasUpper == false) {
            	feedback = feedback + "\n Your password did not contain an uppercase letter";
            }else {
            	feedback = feedback + " \n Your password contained an uppercase letter";
            }
            
            if (hasLower == false) {
            	feedback = feedback + "\n Your password did not contain a lowercase letter";
            }else {
            	feedback = feedback + " \n Your password contained a lowercase letter";
            }
           
            
            if (hasDigit == false) {
            	feedback = feedback + " \n Your password did not contain a Digit";
            }else {
            	feedback = feedback + " \n Your password contained a Digit";
            }
            
            if (hasSpecial == false) {
            feedback = feedback + " \n Your password did not contain a special character";
            }else {
            	feedback = feedback + " \n Your password contained a special character";
            }
            
            if (hasPass == true) {
            	feedback = feedback + "\n Your password contained the word password";
            }
            
          //set score and info
            
            if (hasRepeat == true) {
            	txtDisplay = "This password has been previously used. \n Please enter another password";
            	
            }else {
            	txtDisplay = "The score of password: " + passtf.getText() + " is: " + iScore + 
            			"\n Giving it a rating of: " + rating + 
                		feedback;
            	//make button visible to save password
            	btnSave.setVisible(true);
            }
            
            //display info in text box
            txtScore.setText(txtDisplay);
            
  				}else {
  					txtScore.setText("Passwords do not match");
  				}
			//close connection
			myConn.close();
			
        	}catch(Exception e1)
        	{
        		System.err.println("Got an exception!");
        		System.err.println(e1.getMessage());
        		}
        	
        }

        if (e.getSource() == this.btnSave) {
        	
        	// gensalt's log_rounds parameter determines the complexity
        	// the work factor is 2**log_rounds, and the default is 10
        	
        	String hashed = BCrypt.hashpw(passtf.getText().toString(), BCrypt.gensalt(12));
        	
        	try {
        		
        		database = readFile(fileName);
        		String[] stringArray = database.split(" ");
        		String[] outputArray = new String[stringArray.length - 1];
        		for (int i = 0; i < stringArray.length - 1; i++) {
        		    outputArray[i] = stringArray[i] + " " + stringArray[i+1];
        		}
        		Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/CyberPasswords" , stringArray[0] , stringArray[1]);
        		
            	// create a sql date object so can use it in INSERT statement
                Calendar calendar = Calendar.getInstance();
                java.sql.Date startDate = new java.sql.Date(calendar.getTime().getTime());

                //Check if user exists
        			Statement Userstmnt = myConn.createStatement();
			
        			ResultSet userResult = Userstmnt.executeQuery("select * from Users where Username = '" + usertf.getText() + "'");
			
        			if (userResult.next() == true) {
        				System.out.println("user exists");
				
        				//mysql insert statement
        				String query =" update Users set Password = ?, DateCreated = ? where Username = ?";
        				
        				//create the insert preparedstatement
        				try(PreparedStatement preparedStmt = myConn.prepareStatement(query)){
        			      preparedStmt.setString   (1, hashed);
        			      preparedStmt.setDate (2, startDate);
        			      preparedStmt.setString (3, usertf.getText());
        			      
        			   // execute the java preparedstatement
        			      preparedStmt.executeUpdate();
        				}
        			      LocalDate futureDate = LocalDate.now().plusMonths(3);
        			      txtScore.setText("Your password has been updated. \n The score of your current password is: " + iScore +
        			    		  "\n Your password will need updating in three months on: \n" + futureDate);
        			      Statement myStmt = myConn.createStatement();
        					
          			  ResultSet myRs = myStmt.executeQuery("select UserID from Users where Username = '" + usertf.getText() + "'");
  			
          				
        			      //update stored previous passwords
        			    String queryPrevious =  "insert into Previous (Password, ID, User_ID, Score)" + " values (?, ?, ?, ?)";
      				
          			    Integer UserID = 0;
        				if (myRs.next()){
        					System.out.println(myRs.getString("UserID"));
        					UserID = myRs.getInt("UserID");
        					
        				}
        			    //create mysql insert preparedstatement
        			    try(PreparedStatement preparedPrevious = myConn.prepareStatement(queryPrevious)){
        			    preparedPrevious.setString (1, hashed);
        			    preparedPrevious.setString (2, null);
        			    preparedPrevious.setInt (3, UserID);
        			    preparedPrevious.setInt (4,  iScore);
        			    
        			    preparedPrevious.execute();
        			    }
        			      
        			}else {
        				
        				// the mysql insert statement
        				String query = " insert into Users (Username, UserID, Password, DateCreated)"
        						+ " values (?, ?, ?, ?)";

        				// create the mysql insert preparedstatement
        				try(PreparedStatement preparedStmt = myConn.prepareStatement(query)){
        				preparedStmt.setString (1, usertf.getText());
        				preparedStmt.setString (2, null);
        				preparedStmt.setString (3, hashed);
        				preparedStmt.setDate   (4, startDate);
        				
        				// execute the preparedstatement
        				preparedStmt.execute();
        				}
        				LocalDate futureDate = LocalDate.now().plusMonths(3);
        			      txtScore.setText("Your password has been created. \n The score of your current password is: " + iScore +
        			    		  "\n Your password will need updating in three months on: \n" + futureDate);
        			      
        				Statement myStmt = myConn.createStatement();
			
        				ResultSet myRs = myStmt.executeQuery("select UserID from Users where Username = '" + usertf.getText() + "'");
			       				
      			      //update stored previous passwords
      			    String queryPrevious =  "insert into Previous (Password, ID, User_ID, Score)" 
      			      + " values (?, ?, ?, ?)";
    				
      			    Integer UserID = 0;
    				if (myRs.next()){
    					System.out.println(myRs.getString("UserID"));
    					UserID = myRs.getInt("UserID");
    					
    				}
      			    //create mysql insert preparedstatement
      			    try(PreparedStatement preparedPrevious = myConn.prepareStatement(queryPrevious)){
      			    preparedPrevious.setString (1, hashed);
      			    preparedPrevious.setString (2, null);
      			    preparedPrevious.setInt (3, UserID);
      			    preparedPrevious.setInt (4,  iScore);
    				
      			    preparedPrevious.execute();
      			    }

        			}
            
        			//close connection
        			myConn.close();
        	}catch(Exception e1)
        	{
        		System.err.println("Got an exception!");
        		System.err.println(e1.getMessage());
        	}

        }
    	}
    
    private String readFile(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader (file));
        String         line = null;
        StringBuilder  stringBuilder = new StringBuilder();
        String         ls = System.getProperty("line.separator");

        try {
            while((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                //stringBuilder.append(ls);
            }

            return stringBuilder.toString();
        } finally {
            reader.close();
        }
    }
}
