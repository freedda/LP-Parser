import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class Parser {
	private static BufferedReader br;
	private int min_max; // 1 for max, -1 for min
	private boolean ErrorNotExist = true; //  Flag checkes if exists syntax error
	
	
	private ArrayList<ArrayList<String>> A = new ArrayList<ArrayList<String>>(); //size M x N, of limitaitions 
	private ArrayList<String> b = new ArrayList<String>(); //size M x 1,  right side of limitaitions 
	private ArrayList<String> c = new ArrayList<String>(); // size 1 x N,  coefficient of objective function
	private ArrayList<String> Eqin = new ArrayList<String>(); //type of limitation
	
	public Parser() {
		readFile();
		writeFile();		
	}
	
	public void readFile(){ 
		// Reads data from LP01.txt
		String currLine;
		int counterForLines =0 ;

		try {
			
			BufferedReader reader = new BufferedReader(new FileReader("LP01.txt"));
			//int temp=0;
			while (true) {
				//temp++;
				//Delete spaces
				currLine = reader.readLine().replaceAll(" \\s ", "");
				
				//All data have read If find the word 'end" 
				if (currLine.equals("end")||currLine.equals("END")) {
					break;
				}
				if(counterForLines == 0) { //First Line
					if (ErrorNotExist) {
											//check if exist min or max
						minmax_Finder(currLine);
					}}
				else if(counterForLines == 1) { //Second line
												//Search for st, s.t.  subject to 
					if (!st_Finder(currLine)) {
						System.out.println("No s.t or st or subject");
						ErrorNotExist= false;
						System.exit(1);
					}
				}
				
				if(counterForLines != 0) {
					//Search for (>= , <= , =)
					//Filling eqin with data
					if(symbolChecker(currLine) ) {
						eqinFiller(currLine); }  
					
					if(!checkforBi(currLine)){
						System.out.println("Right side is missing");
						ErrorNotExist = false;
						System.exit(1);
					}
				}	
				
				if(!checkSignError(currLine, counterForLines)) {
					System.out.println("Missing a sign in current line ");
					ErrorNotExist = false;
					System.exit(1);
				}
				
				if(ErrorNotExist) {
					ArrayFiller(currLine, counterForLines);
				}
				counterForLines++;	
	
		} 
			reader.close();
	   } catch (IOException e) {
		    e.printStackTrace();
		}  
	}
	
	//(a)the problem is min or max  
	public void minmax_Finder(String line) {
		StringTokenizer st = new StringTokenizer(line);
		String minOrmax=st.nextToken();
		if (minOrmax.equals("max") || minOrmax.equals("MAX")) {
			min_max = 1;
		} else if (minOrmax.equals("min") || minOrmax.equals("MIN")) {
			min_max = -1;
		} else {
			System.out.println("There's no exist min or max front of objective function");
			ErrorNotExist = false;
			System.exit(1);
		}
	}
	
	//(b) Serach for st , s.t. � subject to 
	public boolean st_Finder(String line) {
		StringTokenizer st = new StringTokenizer(line);
		String temp=st.nextToken();
		return temp.equals("s.t.") || temp.equals("st")
				|| (temp.equals("subject") && st.nextToken().equals("to"));
	}
	

	
	//(e) No limitation, the statement is missing
	public boolean symbolChecker(String line) {

		int symbol1 = 0;
		int symbol2 = 0;
		int symbol3 = 0;
		
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			if (c == '>') {
				symbol1++;
			} else if (c == '<') {
				symbol2++;
			} else if (c == '=') {
				symbol3++;
			}
		}
	
		if((symbol1==0)&&(symbol2==0)&&(symbol3==0)) {
			System.out.println("No <= or >= or = found. System will exit.");
			System.exit(1);
		}
		if((symbol1==1)&&(symbol2==0)&&(symbol3==1)) {
			return true;
		}
		else if ((symbol1==0)&&(symbol2==1)&&(symbol3==1)) {
			return true;
		}
		else if((symbol1==0)&&(symbol2==0)&&(symbol3==1)) {
			return true;
		}
		else
			return false;
	}
	
	// Fill eqin

	public void eqinFiller(String line) {
		
		int count1=0; // >
		int count2=0; // <
		int count3=0; // = 

		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			if (c == '>') {
				count1++;
			} else if (c == '<') {
				count2++;
			} else if (c == '=') {
				count3++;
			}
		}

		if ((count1 == 1) && (count2 == 0)&& (count3 == 1) ) { // >=
			Eqin.add("-1");
		} else if ( (count1 == 0 )&&(count2 ==1) && (count3==1)) {  // <=
			Eqin.add("1");
		} else if (count3==1 && (count2==0) && (count1==0)) { // =
			Eqin.add("0");
		}
		else {
			System.out.println("Doesn't read a symbol");
			System.exit(1);
		}

	}
	//f 
	public boolean checkforBi(String line) {
		boolean exist = false;
		for(int i=0; i < line.length(); i++) {
			char c =line.charAt(i);
			if(c == '=') {
				for(int j=i+1; j<line.length(); j++) {
					if(line.charAt(j) >= 48 || line.charAt(j) <=57) {
						exist = true;
					}
				}
			}
		}
		return exist;
	}
	
	//check Sings
	public boolean checkSignError(String line, int count) {
		String currentLine = line.replaceAll("\\s", ""); //Making the current line a string without spaces
		char car; 
		ArrayList<Character> charList = new ArrayList<Character>();
		if(count==0) {
			for(int i=3; i< currentLine.length(); i++) {
				car = currentLine.charAt(i);
				charList.add(car);
			}}
		else {
			for(int i=0; i< currentLine.length(); i++) {
				car = currentLine.charAt(i);
				charList.add(car);
				if (car == '=') {
					break;
				}
			}
		}
		//count x
		int count88=0; 
		//count + Or -
		int count43=0;
		for(int i=0; i< charList.size(); i++) {
			car = charList.get(i);
			
			if(car == '+' || car == '-') {
				count43++;
			}
			
			if(car == 'X' || car == 'x') {
				count88++;
			}
			
			if(count88==1 && count43==0) { // front of the first coefficient, i suppose that sign (+) wasn't writting.
										   // The sign (-) was writting
				count43++;
			}
		}
		
		if (count43 == count88) {
				return true;
		}
			
			return false;
	}
	
	
	//Filling the tables A, b, c
	public void ArrayFiller(String line, int count) {
		String currentLine = line.replaceAll("\\s", ""); 
		char car;
		int aNum = 0; 
		ArrayList<Character> charListAC = new ArrayList<Character>();
		ArrayList<Character> charListB = new ArrayList<Character>();
		ArrayList<String> indexList = new ArrayList<String>();
		ArrayList<String> helpList = new ArrayList<String>();
		String Element="";
		int countstar=0;
		
		for(int i=0; i< currentLine.length(); i++) {
			car = currentLine.charAt(i);
			//charListAC has extras symblos(*,%, #,x)
			if(count==0) {
				if(car == '-' || car == '+'  ) { 
					if(i!=3) {
						charListAC.add('*');}   
				}}
			else {
				if(car == '-' || car == '+'  ) {
					if(i!=0 ) {
					charListAC.add('*'); 
				}
			}
			}
				
			if (count > 1 && i==0) { //  From second line
				if(car=='x' || car=='X') {
					charListAC.add('+');
					charListAC.add('1');
				}
				if(car>=48 && car<=57) {
					charListAC.add('+');
				}
				
			}
			
			charListAC.add(car);
			if((car == '-' || car == '+') && 
					(currentLine.charAt(i+1)=='x' || currentLine.charAt(i+1)=='X')) {
				charListAC.add('1');
			}
			if(count==1) {
				if(car == 't' || car =='o' || car=='.') {
					charListAC.add('#');
					if(currentLine.charAt(i+1)=='x' || currentLine.charAt(i+1)=='X') {
						charListAC.add('+'); 
						charListAC.add('1');}
					else if(currentLine.charAt(i+1) >=48 && currentLine.charAt(i+1) <=57) {
							charListAC.add('+');
						}
					}
			}	
		
			if(car == 'X' || car == 'x' || car=='n' || car=='N') {
				if(i==2  && count==0) {
					charListAC.add('%'); 
					if(currentLine.charAt(i+1)=='x' ||currentLine.charAt(i+1)=='X') {
						charListAC.add('+'); //
						charListAC.add('1');
						continue;
					}
					else if(currentLine.charAt(i+1)>=48 && currentLine.charAt(i+1)<=57) {
						charListAC.add('+');
						continue;
					}
					else if(currentLine.charAt(i+1)=='-') {
						continue;
					}
				}
				charListAC.add('*');
			}
		}
		String temp="";
		for(int i=1; i<charListAC.size(); i++) {
			
			if(charListAC.get(i)=='*') {
				countstar++;
			}
			
		
			if(countstar==1 && (charListAC.get(i)>=48 && charListAC.get(i)<=57)) {
				temp+=charListAC.get(i).toString();
			}
			if(countstar==2 || i==charListAC.size()-1 || charListAC.get(i)=='=') {
				aNum=Integer.parseInt(temp); 
				indexList.add(temp);
				temp="";
				countstar=0;
			}
			if(charListAC.get(i)=='=') { 
				break;
			}
		}
		
		countstar=0;
	System.out.println(charListAC);
		boolean done = false;
		//Filling c
		if (count==0) {
			
			for(int i=3; i<charListAC.size(); i++) {
				if(charListAC.get(i)=='+') {     
					
					Element="+";
				} 
				else if(charListAC.get(i)== '-') {
					
					Element = "-";
				}
			
				if(charListAC.get(i) == '*') {
					countstar++;
				}
		
				 if (charListAC.get(i) >= 48 && charListAC.get(i) <= 57 && countstar==0) {
					Element += charListAC.get(i).toString();
					
				}
				 
				if(countstar==2) {
					countstar=0;
				}
				if(charListAC.get(i)=='x') {
					done=true;  
				}
				if(done) { //done=true, it means that the Element is ready(because it read a symbol :x)
					done=false;
					helpList.add(Element);
				}
			}
			int j=0;
			for(int i=0; i<indexList.size(); i++) {
				while(j<aNum) {
					if(Integer.parseInt(String.valueOf(indexList.get(i))) == j+1) {
						c.add(helpList.get(i));
						j ++;
						break;
					}
					else {
						j++;
						c.add("0");
					}
				}
			}
		} 
		
		//FIlling A and b
		else
		{	ArrayList<String> helper = new ArrayList<String>();
			//FIlling b
			for(int i=0; i< currentLine.length(); i++) {
				car=currentLine.charAt(i);
				charListB.add(car);}
			
			for(int i=0; i< charListB.size(); i++) {
				if(charListB.get(i) == '=') { //in the end exists "=" 
					for(int j=i+1; j<charListB.size(); j++) {
						Element+=charListB.get(j).toString();
						}
					b.add(Element);
			}}
			
			//Filling A
			
			for(int i=0; i< charListAC.size(); i++) {
			 
				if(charListAC.get(i)=='+') {                        
					Element="+";
				} 
				else if(charListAC.get(i)== '-') {
					Element = "-";
				}
			
				if(charListAC.get(i) == '*') {
					countstar++;
				}
		
				 if (charListAC.get(i) >= 48 && charListAC.get(i) <= 57 && countstar==0) {
					Element += charListAC.get(i).toString(); // Create an  element
				 }
			
				 if(countstar==2) {
					countstar=0;
				}
				if(charListAC.get(i)=='x') { // Element is ready
					done=true;  
				}
				if(done) { //done=true, it means that the Element is ready(because it read a symbol :x)
					done=false;
					helper.add(Element);
				}
				
				if(charListAC.get(i)=='=') {
					break;
				}
				
			}
			int j=0;
			for(int i=0; i<indexList.size(); i++) {
				while(j<aNum) {
					if(Integer.parseInt(String.valueOf(indexList.get(i))) == j+1) {
						helpList.add(helper.get(i));
						j ++;
						break;
					}
					else {
						j++;
						helpList.add("0");
					}
				}
			}
			
			A.add(helpList);
		}
	}
	
	
	//Create a new txt (LP2)
	public void writeFile() { 
		System.out.println("c = " + c);
		System.out.println("A = ");
		for (int i = 0; i < A.size(); i++) {
			String getValue = A.get(i).toString();
			System.out.println(getValue);
			}
		System.out.println("b =" );
		for(int i=0; i<A.size(); i++) {
			System.out.println( "[" +b.get(i) +"]");
		}
		System.out.println("Eqin = " + Eqin);
		System.out.println("MinMax = " + min_max);
		
		try 
			(BufferedWriter writer = new BufferedWriter(new FileWriter("LP2.txt"))) {
			
			writer.write("min Or max: " + min_max + "\n");
			writer.newLine();
			writer.newLine();
			
			writer.write("c: " + c + "\n");
				
			writer.newLine();
			writer.newLine();
			writer.write("A: ");
			for (int i = 0; i < A.size(); i++) {
				if (i == 0) {
					writer.write(A.get(i) + "\n");
		    	} else {
					writer.newLine();
					writer.write("   " + A.get(i) + "\n");
				}
		}
			
		
			writer.newLine();
			writer.newLine();
			writer.write("b: ");
			for(int i=0; i<b.size(); i++) {
				if(i==0) {
					writer.write("[" + b.get(i) +"]" + "\n");
					writer.newLine();}
				else{
					writer.write("   " + "[" + b.get(i) +"]" + "\n");
					writer.newLine();
				}
			}
				
			writer.newLine();
			writer.write("Eqin: " + Eqin);
			writer.newLine();
			
			writer.newLine();
			writer.write("x>=0");
				
		} catch (UnsupportedEncodingException e) {
			System.out.println("Unsupported encoding!");
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
		} catch (IOException e) {	
			e.printStackTrace();
		}
	}

	
}
