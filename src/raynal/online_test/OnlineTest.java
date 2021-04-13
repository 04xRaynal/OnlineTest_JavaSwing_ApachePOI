/*
 * An Online Test using Java Swing with an Excel sheet to retrieve and store data (Apache POI Library)
 * 
 * The user is required to login either as a student or as a teacher.
 * When a student logs in, different subjects are displayed and can choose a subject to start the quiz.
 * When a subject is clicked a question is displayed along with 4 options, the student has to click on any 1 option,
 * there are 3 buttons, previous, to go to the previous question, next, to go to the next question, and result which ends the test and displays the final result.
 * The student has to attempt 10 questions and is required to finish the test in 15 minutes.
 * Once 15 minutes are exceeded or the student clicks on the result button, the quiz is ended and the result is displayed along with the score obtained.
 * Each student can appear for each subject test thrice, after 3 attempts the student is'nt allowed to reappear that particular subject test.
 * 
 * When a teacher logs in, different subjects are displayed and can choose a subject to input values for that particular subject.
 * Input fields are displayed, a question field, 4 option fields and an answer field.
 * The teacher can input values in these fields and click on the submit button to submit his input.
 * If a subject already has 25 questions present, an error dialog is displayed.
 * 
 * @author - 04xRaynal
 */
package raynal.online_test;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class OnlineTest extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	
	Container c;
	JComboBox<Integer> rollNoComboBox;
	JPasswordField passwordField;
	JButton loginButton, loginSwitch;
	private Integer rollnos[];
	JTextField teacherName;
	boolean teacherFlag;
	FileInputStream xlFile;
	XSSFWorkbook xlworkBook;
	XSSFSheet xlSheet;
	XSSFRow xlRow;
	XSSFCell xlCell;
	DataFormatter formatter = new DataFormatter();
	
	JLabel chooseSubjLabel, scoreLabel;
	JButton gkSubject, mathSubject, sciSubject, progSubject;
	
	JTextArea questionTextArea, inputQuestion;
	JRadioButton[] radioButton = new JRadioButton[5];
	ButtonGroup radioButtonGroup;
	JButton previous, next, result, mainMenu, submit, logout;
	JTextField inputOption1, inputOption2, inputOption3, inputOption4, inputAnswer;
	JLabel questionLabel, option1Label, option2Label, option3Label, option4Label, answerLabel;
	int count = 0, current = 1, score = 0, rollNo, noOfRows, noOfColumns;
	String gkData[][], mathData[][], sciData[][], progData[][], tempData[][];
	JTextArea resultText;
	String subjectFlag;
	JLabel welcomeLabel = new JLabel("Welcome, ");
	String teacherUsername;
	JLabel rollNoLabel, passwordLabel, teacherNameLabel, subjectLabel;
	JLabel studentLogin, teacherLogin, timeLabel;
	Integer[] array25;
	DecimalFormat decimalFormatter = new DecimalFormat("00");
	long startTime, currentTime, runningTime, remainingTime;
	Duration duration15 = Duration.ofMinutes(15);
	long duration15Millis = duration15.toMillis() + 999;				//999 milliseconds are added as a buffer time so that initially 1 second is not lost
	boolean timeFlag;
	
	
	public OnlineTest() {
		c = getContentPane();
		
		try {
			xlFile = new FileInputStream(new File("src\\resources\\online_test_sheet.xlsx"));				//Excel File to store and retrieve data
			xlworkBook = new XSSFWorkbook(xlFile);
		}
		catch(FileNotFoundException ex) {
			ex.printStackTrace();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		createUI();
		
		Image bookIcon = Toolkit.getDefaultToolkit().getImage("src\\resources\\book-open-icon.png").getScaledInstance(60, 60, Image.SCALE_SMOOTH);
		setIconImage(bookIcon);
		setTitle("Online Test");
		setSize(530, 390);
		setLayout(null);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	public void createUI() {								//Student Login 
		studentLogin = new JLabel("Student Login");
		studentLogin.setFont(new Font("Arial", Font.ITALIC, 14));
		studentLogin.setBounds(90, 60, 150, 30);
		
		rollNoLabel = new JLabel("Roll No: ");
		rollNoLabel.setBounds(80, 100, 80, 30);
		
		rollnos = new Integer[25];							//data contains roll nos from 1-25
		for(int i = 0; i < 25; i++ ) {
			rollnos[i] = i+1;
		}
		rollNoComboBox = new JComboBox<>(rollnos);
		((JLabel)rollNoComboBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
		rollNoComboBox.setBounds(160, 100, 50, 30);
		
		passwordLabel = new JLabel("Password: ");
		passwordLabel.setBounds(80, 140, 80, 30);
		
		passwordField= new JPasswordField(15);
		passwordField.setBounds(160, 140, 120, 30);
		
		loginButton = new JButton("Login");
		loginButton.setBounds(120, 190, 70, 30);
		loginButton.addActionListener(this);
		
		loginSwitch = new JButton("Login as Teacher");				//Switch for Teacher Login
		loginSwitch.setBounds(220, 190, 140, 30);
		loginSwitch.addActionListener(this);
		
		c.add(studentLogin);
		c.add(rollNoLabel);  c.add(rollNoComboBox);
		c.add(passwordLabel);  c.add(passwordField);
		c.add(loginButton);  c.add(loginSwitch);
	}
	
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				new OnlineTest();
			}
		});
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getActionCommand().equals("Login as Teacher")) {			//When the login switch is clicked
			c.removeAll();
			c.setVisible(false);
			teacherFlag = true;
			
			teacherLogin = new JLabel("Teacher Login");
			teacherLogin.setFont(new Font("Arial", Font.ITALIC, 14));
			teacherLogin.setBounds(90, 60, 150, 30);
			
			teacherNameLabel = new JLabel("Username: ");
			teacherNameLabel.setBounds(80, 100, 80, 30);
			teacherName = new JTextField();
			teacherName.setBounds(160, 100, 120, 30);
			
			passwordLabel = new JLabel("Password: ");
			passwordLabel.setBounds(80, 140, 80, 30);
			passwordField = new JPasswordField();
			passwordField.setBounds(160, 140, 120, 30);
			
			loginButton = new JButton("Login");
			loginButton.setBounds(120, 190, 70, 30);
			loginButton.addActionListener(this);
			
			loginSwitch = new JButton("Login as Student");
			loginSwitch.setBounds(220, 190, 140, 30);
			loginSwitch.addActionListener(this);
			
			c.add(teacherLogin);
			c.add(teacherNameLabel);  c.add(teacherName);
			c.add(passwordLabel);  c.add(passwordField);
			c.add(loginButton);  c.add(loginSwitch);
			c.setVisible(true);
		}
		
		if(e.getActionCommand().equals("Login as Student")) {			//When Login switch is clicked again
			teacherFlag = false;
			c.removeAll();
			c.setVisible(false);
			
			createUI();
			c.setVisible(true);
		}
		
		if(e.getSource() == loginButton && teacherFlag == false) {				//When login button is clicked as a student
			xlSheet = xlworkBook.getSheet("student_info"); 				//student_info sheet contains students details
			int noOfRows = xlSheet.getPhysicalNumberOfRows();
			xlRow = xlSheet.getRow(0);
			int noOfColumns = xlRow.getLastCellNum();
			
			String[][] loginData = new String[noOfRows][noOfColumns];
			Integer[][] username = new Integer[noOfRows][1];
			String[][] password = new String[noOfRows][1];
			
			for(int r = 1; r < noOfRows; r++) {
				for(int c = 0; c < noOfColumns; c++) {
					loginData[r][c] = formatter.formatCellValue(xlSheet.getRow(r).getCell(c));
				}
			}
			
			for(int r = 1; r < noOfRows; r++) {
				username[r][0] = Integer.parseInt(loginData[r][0]);
				password[r][0] = loginData[r][1];
				if(rollNoComboBox.getSelectedItem() == username[r][0]) {
					rollNo = r;
					password[r][0] = loginData[r][1];
					if(String.valueOf(passwordField.getPassword()).equals(password[r][0])) {
						c.removeAll();
						c.setVisible(false);
						addSubjects();
						
						c.setVisible(true);
					}
					else {
						JOptionPane.showMessageDialog(this, "Plese check your Credentials.\nThe entered Password is wrong.", "Wrong Password", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
		
		
		if(e.getSource() == loginButton && teacherFlag == true) {			//when login button is clicked as a teacher
			xlSheet = xlworkBook.getSheet("teacher_info"); 				//teacher_info sheet contains teachers details
			int noOfRows = xlSheet.getPhysicalNumberOfRows();
			xlRow = xlSheet.getRow(0);
			int noOfColumns = xlRow.getLastCellNum();
			
			String[][] loginData = new String[noOfRows][noOfColumns];
			String[][] username = new String[noOfRows][1];
			String[][] password = new String[noOfRows][1];
			
			for(int r = 1; r < noOfRows; r++) {
				for(int c = 0; c < noOfColumns; c++) {
					loginData[r][c] = formatter.formatCellValue(xlSheet.getRow(r).getCell(c));
				}
			}
			
			for(int r = 1; r < noOfRows; r++) {
				username[r][0] = loginData[r][0];
				password[r][0] = loginData[r][1];
 				if(teacherName.getText().equals(username[r][0])) {
 					teacherUsername = teacherName.getText();
					password[r][0] = loginData[r][1];
					if(String.valueOf(passwordField.getPassword()).equals(password[r][0])) {
						c.removeAll();
						c.setVisible(false);
						addSubjects();
						
						c.setVisible(true);
					}
					else {
						JOptionPane.showMessageDialog(this, "Plese check your Credentials.\nThe entered Credentials are wrong.", "Wrong Credentials", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
		
		
		if(e.getSource() == gkSubject && teacherFlag == false) {			//when student clicks General Knowledge button
			xlCell = xlSheet.getRow(rollNo).getCell(3);					//Cell index 3 contains attempts remaining for this subject
			if(Double.parseDouble(xlCell.toString()) > 0){				//If attempts remaining is less than zero, a error message is thrown
				subjectFlag = "gk";
				c.removeAll();
				c.setVisible(false);
				
				timeLabel = new JLabel("15 : 00");
				timeLabel.setFont(new Font("Sans Serif", Font.BOLD, 18));
				timeLabel.setBounds(20, 20, 60, 40);
				add(timeLabel);
				
				addQA();												//adds the question, 4 options and buttons to the ui
				xlSheet = xlworkBook.getSheet("gk_qa");					//contains questions along with options for this subject 
				int noOfRows = xlSheet.getPhysicalNumberOfRows();
				xlRow = xlSheet.getRow(0);
				int noOfColumns = xlRow.getLastCellNum();
				//copy questions and their respective options from the excel sheet into out string matrix
				if(noOfRows < 10)					//if the sheet contains less than 10 questions, the matrix will still have 10 rows, otherwise it leads to an error in future calculations
					gkData = new String[10][noOfColumns];
				else
					gkData = new String[noOfRows][noOfColumns];
				
				for(int r = 1; r < noOfRows; r++) {				//here the string matrix is filled
					for(int c = 0; c < noOfColumns; c++) {
						gkData[r-1][c] = formatter.formatCellValue(xlSheet.getRow(r).getCell(c));
					}
				}
				
				welcomeLabel.setText("Welcome, Roll No: " + rollNo);
				welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
				welcomeLabel.setBounds(380, 10, 160, 20);
				add(welcomeLabel);
				
				xlSheet = xlworkBook.getSheet("temp_qa");			//temp_qa holds 10 questions randomly selected
				noOfRows = gkData.length;
				//even if the length is lower than 10 or greater than 25, array length is between 10-25
				if(noOfRows < 10)
					array25 = new Integer[10];
				else if(noOfRows > 25)
					array25 = new Integer[25];
				else
					array25 = new Integer[noOfRows - 1];
				
				//array is filled with numbers upto its length and then shuffled
				for(int i = 0; i < array25.length; i++) {
					array25[i] = i;
				}
				Collections.shuffle(Arrays.asList(array25));
				
				//the shuffled array helps to randomly pick 10 questions from the string matrix and fill into the temp_qa sheet 
				for(int r = 1; r <= 10; r++) {
					xlRow = xlSheet.createRow(r);
					for(int c = 0; c < 7; c++) {
						xlCell = xlRow.createCell(c);
						xlCell.setCellValue(gkData[array25[r-1]][c]);
					}
				}
				
				updateSheet();				//updates the sheet with the new values
				populateTempQA();			//10 questions from the temp_qa sheet are displayed on the ui
				countDown();				//timer is started
				c.setVisible(true);
			}
			else {
				JOptionPane.showMessageDialog(this, "Maximum attempts for this subject has been exceeded.\nPlease try a different subject.", "Eligibility Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		
		if(e.getSource() == gkSubject && teacherFlag == true) {
			subjectFlag = "gk";
			c.removeAll();
			c.setVisible(false);
			addQuestionField();							//adds fields to input question, options and answer for the particular selected subject
			xlSheet = xlworkBook.getSheet("gk_qa");
			
			c.setVisible(true);
		}
		
		
		if(e.getSource() == mathSubject && teacherFlag == false) {				//when student clicks Mathematics button
			xlCell = xlSheet.getRow(rollNo).getCell(5);						//Cell index 5 contains attempts remaining for this subject
			if(Double.parseDouble(xlCell.toString()) > 0){					//If attempts remaining is less than zero, a error message is thrown
				subjectFlag = "math";
				c.removeAll();
				c.setVisible(false);
				
				timeLabel = new JLabel("15 : 00");
				timeLabel.setFont(new Font("Sans Serif", Font.BOLD, 18));
				timeLabel.setBounds(20, 20, 60, 40);
				add(timeLabel);
				
				addQA();												//adds the question, 4 options and buttons to the ui
				xlSheet = xlworkBook.getSheet("math_qa");				//contains questions along with options for this subject 
				int noOfRows = xlSheet.getPhysicalNumberOfRows();
				xlRow = xlSheet.getRow(0);
				int noOfColumns = xlRow.getLastCellNum();
				//copy questions and their respective options from the excel sheet into out string matrix
				if(noOfRows < 10)								//if the sheet contains less than 10 questions, the matrix will still have 10 rows, otherwise it leads to an error in future calculations
					mathData = new String[10][noOfColumns];
				else
					mathData = new String[noOfRows][noOfColumns];
				
				for(int r = 1; r < noOfRows; r++) {						//here the string matrix is filled
					for(int c = 0; c < noOfColumns; c++) {
							mathData[r-1][c] = formatter.formatCellValue(xlSheet.getRow(r).getCell(c));
					}
				}
				
				welcomeLabel.setText("Welcome, Roll No: " + rollNo);
				welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
				welcomeLabel.setBounds(380, 10, 160, 20);
				add(welcomeLabel);
				
				xlSheet = xlworkBook.getSheet("temp_qa");				//temp_qa holds 10 questions randomly selected
				noOfRows = mathData.length;
				//even if the length is lower than 10 or greater than 25, array length is between 10-25
				if(noOfRows < 10)
					array25 = new Integer[10];
				else if(noOfRows > 25)
					array25 = new Integer[25];
				else
					array25 = new Integer[noOfRows - 1];
				
				//array is filled with numbers upto its length and then shuffled
				for(int i = 0; i < array25.length; i++) {
					array25[i] = i;
				}
				Collections.shuffle(Arrays.asList(array25));
				
				//the shuffled array helps to randomly pick 10 questions from the string matrix and fill into the temp_qa sheet 
				for(int r = 1; r <= 10; r++) {
					xlRow = xlSheet.createRow(r);
					for(int c = 0; c < 7; c++) {
						xlCell = xlRow.createCell(c);
						xlCell.setCellValue(mathData[array25[r-1]][c]);
					}
				}
				
				updateSheet();					//updates the sheet with the new values
				populateTempQA();				//10 questions from the temp_qa sheet are displayed on the ui
				countDown();					//timer is started
				c.setVisible(true);
			}
			else {
				JOptionPane.showMessageDialog(this, "Maximum attempts for this subject has been exceeded.\nPlease try a different subject.", "Eligibility Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		
		if(e.getSource() == mathSubject && teacherFlag == true) {
			subjectFlag = "math";
			c.removeAll();
			c.setVisible(false);
			addQuestionField();								//adds fields to input question, options and answer for the particular selected subject
			xlSheet = xlworkBook.getSheet("math_qa");
			
			c.setVisible(true);
		}
		
		
		if(e.getSource() == sciSubject && teacherFlag == false) {					//when student clicks Science button
			xlCell = xlSheet.getRow(rollNo).getCell(7);							//Cell index 7 contains attempts remaining for this subject
			if(Double.parseDouble(xlCell.toString()) > 0){						//If attempts remaining is less than zero, a error message is thrown
				subjectFlag = "sci";
				c.removeAll();
				c.setVisible(false);
				
				timeLabel = new JLabel("15 : 00");
				timeLabel.setFont(new Font("Sans Serif", Font.BOLD, 18));
				timeLabel.setBounds(20, 20, 60, 40);
				add(timeLabel);
				
				addQA();													//adds the question, 4 options and buttons to the ui
				xlSheet = xlworkBook.getSheet("sci_qa");					//contains questions along with options for this subject 
				int noOfRows = xlSheet.getPhysicalNumberOfRows();
				xlRow = xlSheet.getRow(0);
				int noOfColumns = xlRow.getLastCellNum();
				//copy questions and their respective options from the excel sheet into out string matrix
				if(noOfRows < 10)									//if the sheet contains less than 10 questions, the matrix will still have 10 rows, otherwise it leads to an error in future calculations
					sciData = new String[10][noOfColumns];
				else
					sciData = new String[noOfRows][noOfColumns];
				
				for(int r = 1; r < noOfRows; r++) {					//here the string matrix is filled
					for(int c = 0; c < noOfColumns; c++) {
						sciData[r-1][c] = formatter.formatCellValue(xlSheet.getRow(r).getCell(c));
					}
				}
				
				welcomeLabel.setText("Welcome, Roll No: " + rollNo);
				welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
				welcomeLabel.setBounds(380, 10, 160, 20);
				add(welcomeLabel);
				
				xlSheet = xlworkBook.getSheet("temp_qa");					//temp_qa holds 10 questions randomly selected
				noOfRows = sciData.length;
				//even if the length is lower than 10 or greater than 25, array length is between 10-25
				if(noOfRows < 10)
					array25 = new Integer[10];
				else if(noOfRows > 25)
					array25 = new Integer[25];
				else
					array25 = new Integer[noOfRows - 1];
				
				//array is filled with numbers upto its length and then shuffled
				for(int i = 0; i < array25.length; i++) {
					array25[i] = i;
				}
				Collections.shuffle(Arrays.asList(array25));
				
				//the shuffled array helps to randomly pick 10 questions from the string matrix and fill into the temp_qa sheet 
				for(int r = 1; r <= 10; r++) {
					xlRow = xlSheet.createRow(r);
					for(int c = 0; c < 7; c++) {
						xlCell = xlRow.createCell(c);
						xlCell.setCellValue(sciData[array25[r-1]][c]);
					}
				}
				
				updateSheet();						//updates the sheet with the new values
				populateTempQA();					//10 questions from the temp_qa sheet are displayed on the ui
				countDown();						//timer is started
				c.setVisible(true);
			}
			else {
				JOptionPane.showMessageDialog(this, "Maximum attempts for this subject has been exceeded.\nPlease try a different subject.", "Eligibility Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		
		if(e.getSource() == sciSubject && teacherFlag == true) {
			subjectFlag = "sci";
			c.removeAll();
			c.setVisible(false);
			addQuestionField();								//adds fields to input question, options and answer for the particular selected subject
			xlSheet = xlworkBook.getSheet("sci_qa");
			
			c.setVisible(true);
		}
		
		
		if(e.getSource() == progSubject && teacherFlag == false) {						//when student clicks Programming button
			xlCell = xlSheet.getRow(rollNo).getCell(9);								//Cell index 9 contains attempts remaining for this subject
			if(Double.parseDouble(xlCell.toString()) > 0){							//If attempts remaining is less than zero, a error message is thrown
				subjectFlag = "prog";
				c.removeAll();
				c.setVisible(false);
				
				timeLabel = new JLabel("15 : 00");
				timeLabel.setFont(new Font("Sans Serif", Font.BOLD, 18));
				timeLabel.setBounds(20, 20, 60, 40);
				add(timeLabel);
				
				addQA();												//adds the question, 4 options and buttons to the ui
				xlSheet = xlworkBook.getSheet("prog_qa");				//contains questions along with options for this subject
				int noOfRows = xlSheet.getPhysicalNumberOfRows();
				xlRow = xlSheet.getRow(0);
				int noOfColumns = xlRow.getLastCellNum();
				//copy questions and their respective options from the excel sheet into out string matrix
				if(noOfRows < 10)
					progData = new String[10][noOfColumns];
				else
					progData = new String[noOfRows][noOfColumns];
				
				for(int r = 1; r < noOfRows; r++) {						//here the string matrix is filled
					for(int c = 0; c < noOfColumns; c++) {
						progData[r-1][c] = formatter.formatCellValue(xlSheet.getRow(r).getCell(c));
					}
				}
				
				welcomeLabel.setText("Welcome, Roll No: " + rollNo);
				welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
				welcomeLabel.setBounds(380, 10, 160, 20);
				add(welcomeLabel);
				
				xlSheet = xlworkBook.getSheet("temp_qa");				//temp_qa holds 10 questions randomly selected
				noOfRows = progData.length;
				//even if the length is lower than 10 or greater than 25, array length is between 10-25
				if(noOfRows < 10)
					array25 = new Integer[10];
				else if(noOfRows > 25)
					array25 = new Integer[25];
				else
					array25 = new Integer[noOfRows - 1];
				
				//array is filled with numbers upto its length and then shuffled
				for(int i = 0; i < array25.length; i++) {
					array25[i] = i;
				}
				Collections.shuffle(Arrays.asList(array25));
				
				//the shuffled array helps to randomly pick 10 questions from the string matrix and fill into the temp_qa sheet 
				for(int r = 1; r <= 10; r++) {
					xlRow = xlSheet.createRow(r);
					for(int c = 0; c < 7; c++) {
						xlCell = xlRow.createCell(c);
						xlCell.setCellValue(progData[array25[r-1]][c]);
					}
				}
				
				updateSheet();							//updates the sheet with the new values
				populateTempQA();						//10 questions from the temp_qa sheet are displayed on the ui
				countDown();							//timer is started
				c.setVisible(true);
			}
			else {
				JOptionPane.showMessageDialog(this, "Maximum attempts for this subject has been exceeded.\nPlease try a different subject.", "Eligibility Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		
		if(e.getSource() == progSubject && teacherFlag == true) {
			subjectFlag = "prog";
			c.removeAll();
			c.setVisible(false);
			addQuestionField();									//adds fields to input question, options and answer for the particular selected subject
			xlSheet = xlworkBook.getSheet("prog_qa");
			
			c.setVisible(true);
		}
		
		
		if(e.getSource() == submit) {						//when teacher clicks submit after filling the input fields
			noOfRows = xlSheet.getPhysicalNumberOfRows();

			if(noOfRows > 25) {						//if 25 questions already present, it displays an error dialog
				JOptionPane.showMessageDialog(this, "25 questions have already been added to this Subject.\nCannot add more questions.", "Input Error", JOptionPane.ERROR_MESSAGE);
			}
			else {
				int r = noOfRows;
				
				xlRow = xlSheet.createRow(r);
				xlCell = xlRow.createCell(0);
				xlCell.setCellValue(inputQuestion.getText());
				
				xlCell = xlRow.createCell(1);
				xlCell.setCellValue(inputOption1.getText());
				
				xlCell = xlRow.createCell(2);
				xlCell.setCellValue(inputOption2.getText());
				
				xlCell = xlRow.createCell(3);
				xlCell.setCellValue(inputOption3.getText());
				
				xlCell = xlRow.createCell(4);
				xlCell.setCellValue(inputOption4.getText());
				
				xlCell = xlRow.createCell(5);
				xlCell.setCellValue(inputAnswer.getText());
						
				updateSheet();
			}
			
			inputQuestion.setText("");
			inputOption1.setText("");
			inputOption2.setText("");
			inputOption3.setText("");
			inputOption4.setText("");
			inputAnswer.setText("");
		}
		
		
		if(e.getSource() == previous) {						//when student clicks the previous button
			if(current == 1)
				previous.setEnabled(false);
			
			next.setEnabled(true);
			for(int i = 0; i < 4; i++) {
				if(radioButton[i].isSelected()) {
					xlRow = xlSheet.getRow(current);
					int noOfColumns = xlRow.getLastCellNum();
					xlCell = xlRow.createCell(noOfColumns - 1);
					xlCell.setCellValue(radioButton[i].getText());
				}
			}
				
			if(current > 1) {
				current--;
				
				//previous question is displayed
				questionTextArea.setText("Question " + current + ": " + tempData[current][0]);
				for(int i = 0; i <= 3; i++) {
					radioButton[i].setText(tempData[current][i+1]);
				}
				radioButtonGroup.clearSelection();
			}
		}
		
		
		if(e.getSource() == next) {							//when student clicks next button
			if(current == 10)
				next.setEnabled(false);
			
			previous.setEnabled(true);
			for(int i = 0; i < 4; i++) {
				if(radioButton[i].isSelected()) {
					xlRow = xlSheet.getRow(current);
					int noOfColumns = xlRow.getLastCellNum();
					xlCell = xlRow.createCell(noOfColumns - 1);
					xlCell.setCellValue(radioButton[i].getText());
				}
			}
				
			if(current < 10) {
				current++;
				
				//next question is displayed
				questionTextArea.setText("Question " + current + ": " + tempData[current][0]);
				for(int i = 0; i <= 3; i++) {
					radioButton[i].setText(tempData[current][i+1]);
				}
				radioButtonGroup.clearSelection();
			}
			
		}
		
		
		if(e.getSource() == result) {						//displays the result of the quiz
			timeFlag = false;
			
			for(int i = 0; i < 4; i++) {
				if(radioButton[i].isSelected()) {
					xlRow = xlSheet.getRow(current);
					int noOfColumns = xlRow.getLastCellNum();
					xlCell = xlRow.createCell(noOfColumns - 1);
					xlCell.setCellValue(radioButton[i].getText());
				}
			}
			
			updateSheet();
			c.removeAll();
			c.setVisible(false);
			resultText = new JTextArea();
			JScrollPane scrollPane = new JScrollPane(resultText);
			scrollPane.setBounds(65, 65, 380, 250);
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			
			welcomeLabel.setText("Welcome, Roll No: " + rollNo);
			welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
			welcomeLabel.setBounds(380, 10, 160, 20);
			
			logout = new JButton("Logout");
			logout.setBounds(400, 30, 90, 25);
			logout.addActionListener(this);
			
			int noOfRows = xlSheet.getPhysicalNumberOfRows();
			xlRow = xlSheet.getRow(0);
			int noOfColumns = xlRow.getLastCellNum();
			StringBuilder sb = new StringBuilder();
			
			//all the quiz questions are displayed with their selected options
			for(int r = 1; r < noOfRows; r++) {
				sb.append("Question " + r + ": " + xlSheet.getRow(r).getCell(0) + "\n");
				sb.append("Your answer: " + xlSheet.getRow(r).getCell(noOfColumns - 1));
				
				if(xlSheet.getRow(r).getCell(noOfColumns - 1).toString().equals(xlSheet.getRow(r).getCell(noOfColumns - 2).toString())) {
					sb.append("  (Correct Answer)\n\n");
					score++;								//if the selected option is correct, score is incremented
				}
				else {
					sb.append("  (Wrong Answer)\n" + "Correct Answer : " + xlSheet.getRow(r).getCell(noOfColumns - 2) + "\n\n");
				}
			}
			
			resultText.setText(sb.toString());
			
			scoreLabel = new JLabel("Your Score: " + score + " / 10");
			scoreLabel.setBounds(200, 40, 160, 20);
			
			mainMenu = new JButton("Main Menu");
			mainMenu.setBounds(200, 320, 100, 25);
			mainMenu.addActionListener(this);
			
			//Attempts remaining from the particular subject is decremented
			xlSheet = xlworkBook.getSheet("student_info");
			if(subjectFlag.equals("gk")) {
				xlCell = xlSheet.getRow(rollNo).getCell(3);
				if(Double.parseDouble(xlCell.toString()) > 0){
					if(score > Double.parseDouble(xlSheet.getRow(rollNo).getCell(2).toString())) {
						xlCell = xlSheet.getRow(rollNo).getCell(2);
						xlCell.setCellValue(score);
					}
				xlCell = xlSheet.getRow(rollNo).getCell(3);
				xlCell.setCellValue(Double.parseDouble(xlCell.toString()) - 1);
				}
			}
			else if(subjectFlag.equals("math")) {
				xlCell = xlSheet.getRow(rollNo).getCell(5);
				if(Double.parseDouble(xlCell.toString()) > 0){
					if(score > Double.parseDouble(xlSheet.getRow(rollNo).getCell(4).toString())) {
						xlCell = xlSheet.getRow(rollNo).getCell(4);
						xlCell.setCellValue(score);
					}
				xlCell = xlSheet.getRow(rollNo).getCell(5);
				xlCell.setCellValue(Double.parseDouble(xlCell.toString()) - 1);
				}
			}
			else if(subjectFlag.equals("sci")) {
				xlCell = xlSheet.getRow(rollNo).getCell(7);
				if(Double.parseDouble(xlCell.toString()) > 0){
					if(score > Double.parseDouble(xlSheet.getRow(rollNo).getCell(6).toString())) {
						xlCell = xlSheet.getRow(rollNo).getCell(6);
						xlCell.setCellValue(score);
					}
				xlCell = xlSheet.getRow(rollNo).getCell(7);
				xlCell.setCellValue(Double.parseDouble(xlCell.toString()) - 1);
				}
			}
			else if(subjectFlag.equals("prog")) {
				xlCell = xlSheet.getRow(rollNo).getCell(9);
				if(Double.parseDouble(xlCell.toString()) > 0){
					if(score > Double.parseDouble(xlSheet.getRow(rollNo).getCell(8).toString())) {
						xlCell = xlSheet.getRow(rollNo).getCell(8);
						xlCell.setCellValue(score);
					}
				xlCell = xlSheet.getRow(rollNo).getCell(9);
				xlCell.setCellValue(Double.parseDouble(xlCell.toString()) - 1);
				}
			}
			
			updateSheet();
			
			add(scoreLabel);  add(scrollPane);  add(mainMenu);
			add(welcomeLabel);  add(logout);
			
			c.setVisible(true);
		}
		
		
		if(e.getSource() == mainMenu) {
			c.removeAll();
			c.setVisible(false);
			addSubjects();
			
			//questions present in the temp_qa sheet are reseted
			xlSheet = xlworkBook.getSheet("temp_qa");
			for(int r = 1; r <= 10; r++) {
				for(int c = 0; c < 7; c++) {
					xlRow = xlSheet.getRow(r);
					xlCell = xlRow.getCell(c);
					xlCell.setCellValue("");
				}
			}
			updateSheet();
			c.setVisible(true);
		}
		
		
		if(e.getSource() == logout) {
			c.removeAll();
			c.setVisible(false);
			teacherFlag = false;
			//questions present in the temp_qa sheet are reseted
			xlSheet = xlworkBook.getSheet("temp_qa");
			for(int r = 1; r <= 10; r++) {
				for(int c = 0; c < 7; c++) {
					xlRow = xlSheet.getRow(r);
					xlCell = xlRow.getCell(c);
					xlCell.setCellValue("");
				}
			}
			updateSheet();
			createUI();								//back to login
			c.setVisible(true);
		}
	}
	
	
	public void addSubjects() {						//buttons with subjects are displayed
		if(! teacherFlag) {
			welcomeLabel.setText("Welcome, Roll No: " + rollNo);
			welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
			welcomeLabel.setBounds(380, 10, 160, 20);
		}
		else {
			welcomeLabel.setText("Welcome, " + teacherUsername);
			welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
			welcomeLabel.setBounds(340, 10, 160, 20);
		}
		
		logout = new JButton("Logout");
		logout.setBounds(400, 30, 90, 25);
		logout.addActionListener(this);
		
		chooseSubjLabel = new JLabel("Choose a Subject");
		chooseSubjLabel.setBounds(200, 80, 200, 30);
		chooseSubjLabel.setFont(new Font("Arial", Font.ITALIC, 13));
		
		gkSubject = new JButton("General Knowledge");
		gkSubject.setBounds(60, 120, 180, 30);
		gkSubject.addActionListener(this);
		
		mathSubject = new JButton("Mathematics");
		mathSubject.setBounds(270, 120, 180, 30);
		mathSubject.addActionListener(this);
		
		sciSubject = new JButton("Science");
		sciSubject.setBounds(60, 170, 180, 30);
		sciSubject.addActionListener(this);
		
		progSubject = new JButton("Programming");
		progSubject.setBounds(270, 170, 180, 30);
		progSubject.addActionListener(this);
		
		add(welcomeLabel);  add(logout);
		add(chooseSubjLabel);
		add(gkSubject);  add(mathSubject);
		add(sciSubject);  add(progSubject);
	}
	
	
	public  void addQA() {						//blueprint for the question and its respective options are created
		questionTextArea = new JTextArea();
		questionTextArea.setEditable(false);
		questionTextArea.setLineWrap(true);
		questionTextArea.setOpaque(false);
		questionTextArea.setBorder(BorderFactory.createEmptyBorder());
		questionTextArea.setBounds(80, 70, 400, 30);
		add(questionTextArea);
		
		radioButtonGroup = new ButtonGroup();
		for(int i = 0; i < 4; i++) {
			radioButton[i] = new JRadioButton();
			radioButtonGroup.add(radioButton[i]);
			add(radioButton[i]);
			radioButton[i].setBounds(80, 100+i*30, 150, 30);
		}
		
		previous = new JButton("Previous");
		previous.setBounds(80, 230, 100, 30);
		previous.addActionListener(this);
		
		next = new JButton("Next");
		next.setBounds(200, 230, 100, 30);
		next.addActionListener(this);
		
		result = new JButton("Result");
		result.setBounds(320, 230, 100, 30);
		result.addActionListener(this); 
		
		add(questionTextArea);
		add(previous);  add(next);  add(result);
	}
	
	
	public void addQuestionField() {						//input fields for question, options and answer is created 
		welcomeLabel.setText("Welcome, " + teacherUsername);
		welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
		welcomeLabel.setBounds(340, 10, 160, 20);
		
		logout = new JButton("Logout");
		logout.setBounds(400, 30, 90, 25);
		logout.addActionListener(this);
		
		subjectLabel = new JLabel();
		if(subjectFlag.equals("gk"))
			subjectLabel.setText("Subject: General Knowledge");
		else if(subjectFlag.equals("math"))
			subjectLabel.setText("Subject: Mathematics");
		else if(subjectFlag.equals("sci"))
			subjectLabel.setText("Subject: Science");
		else if(subjectFlag.equals("prog"))
			subjectLabel.setText("Subject: Programming");
		subjectLabel.setBounds(60, 50, 180, 30);
		
		questionLabel = new JLabel("Question: ");
		questionLabel.setBounds(60, 70, 80, 50);
		inputQuestion = new JTextArea();
		inputQuestion.setLineWrap(true);
		inputQuestion.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		inputQuestion.setBounds(150, 80, 300, 50);
		
		option1Label = new JLabel("Option 1: ");
		option1Label.setBounds(60, 140, 80, 30);
		inputOption1 = new JTextField();
		inputOption1.setBounds(150, 140, 160, 30);
		
		option2Label = new JLabel("Option 2: ");
		option2Label.setBounds(60, 180, 80, 30);
		inputOption2 = new JTextField();
		inputOption2.setBounds(150, 180, 160, 30);
		
		option3Label = new JLabel("Option 3: ");
		option3Label.setBounds(60, 220, 80, 30);
		inputOption3 = new JTextField();
		inputOption3.setBounds(150, 220, 160, 30);
		
		option4Label = new JLabel("Option 4: ");
		option4Label.setBounds(60, 260, 80, 30);
		inputOption4 = new JTextField();
		inputOption4.setBounds(150, 260, 160, 30);
		
		answerLabel = new JLabel("Answer: ");
		answerLabel.setBounds(60, 300, 80, 30);
		inputAnswer = new JTextField();
		inputAnswer.setBounds(150, 300, 160, 30);
		
		submit = new JButton("Submit");
		submit.setBounds(350, 270, 80, 30);
		submit.addActionListener(this);
		
		mainMenu = new JButton("Main Menu");
		mainMenu.setBounds(350, 310, 100, 30);
		mainMenu.addActionListener(this);
		
		add(subjectLabel);
		add(welcomeLabel);  add(logout);
		add(questionLabel);  add(inputQuestion);
		add(option1Label);  add(inputOption1);  
		add(option2Label);  add(inputOption2);
		add(option3Label);  add(inputOption3);  
		add(option4Label);  add(inputOption4);
		add(answerLabel);  add(inputAnswer);
		add(submit);  add(mainMenu);
	}
	
	
	public void populateTempQA() {							//temp_qa sheet is filled with 10 random questions
		noOfRows = xlSheet.getPhysicalNumberOfRows();
		xlRow = xlSheet.getRow(0);
		noOfColumns = xlRow.getLastCellNum();
		
		tempData = new String[noOfRows][noOfColumns];
		for(int r = 0; r < noOfRows; r++) {
			for(int c = 0; c < noOfColumns; c++) {
				tempData[r][c] = formatter.formatCellValue(xlSheet.getRow(r).getCell(c));
			}
		}
		
		questionTextArea.setText("Question " + current + ": " + tempData[current][0]);
		for(int i = 0; i <= 3; i++) {
			radioButton[i].setText(tempData[current][i+1]);
		}
	}
	
	
	public void countDown() {							//countDown Timer of 15 minutes
		timeFlag = true;
		startTime = System.currentTimeMillis();
		Thread t = new Thread(new Runnable() {	
			@Override
			public void run() {
				while(timeFlag) {
					currentTime = System.currentTimeMillis();
					runningTime = currentTime - startTime;
					
					remainingTime = duration15Millis - runningTime;
					Duration duration = Duration.ofMillis(remainingTime);
					long minutes = duration.toMinutes();
					duration = duration.minusMinutes(minutes);
					long seconds = duration.toMillis() / 1000;
					
					timeLabel.setText(decimalFormatter.format(minutes) + " : " + decimalFormatter.format(seconds));
					
					//when the timer hits zero, the result button is automatically clicked and the result is displayed
					if(remainingTime <= 0) {
						ActionEvent event = new ActionEvent(result, ActionEvent.ACTION_PERFORMED, "Time's Up", runningTime, 0);

						for (ActionListener listener : result.getActionListeners()) {
						    listener.actionPerformed(event);
						}
						timeFlag = false;
					}
					
					try {
						Thread.sleep(1000);
					}
					catch(InterruptedException ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		t.start();
	}
	
	
	public void updateSheet() {						//the excel sheet is updated with the changed values
		try {
			FileOutputStream fout = new FileOutputStream(new File("src\\resources\\online_test_sheet.xlsx"));
			xlworkBook.write(fout);
			fout.close();
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
	}

}
