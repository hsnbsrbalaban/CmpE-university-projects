import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class COMP_Main {
	static PrintStream printer;
	static int additionCarry = 0;
	static int printCounter = 0;
	static int powCounter = 0;

	public static void main(String[] args) throws FileNotFoundException {
//		Scanner scan = new Scanner(new File(args[0]));
//		printer = new PrintStream(new File(args[0].substring(0,args[0].length()-3)+".asm"));
		Scanner scan = new Scanner(new File("C:\\Users\\Basri\\Desktop\\testcases\\testcases\\error4.co"));
		printer = new PrintStream(new File("C:\\Users\\Basri\\Desktop\\testcases\\testcases\\error4.asm"));
		int lineCounter = 0;

		ArrayList<Vars> vars = new ArrayList<Vars>();

		//This loop takes every line, controls if it is in correct form or not
		//It adds the line to array list if it is correct
		//It gives error message if the line is not in correct form
		while(scan.hasNextLine()) {
			String newLine = scan.nextLine();
			lineCounter++;

			if(!isLineEmpty(newLine)) { //checks the line if it is empty 
				if(!isOutput(newLine)) { //checks the line if it is not an output line 
					if(checkParant(newLine)) { //checks if the parenthesis are correct
						String varName = "";
						String varValue = "";

						for(int i=0; i<newLine.length(); i++) { //split the line into LHS and RHS
							if(newLine.charAt(i) != '=') {
								varName += newLine.charAt(i);
							}
							else {
								varValue = newLine.substring(i+1);
								break;
							}
						}
						varName += "="; //adding an "=" to the end of the varName
						if(!varNameCheck(varName).equals("-1")) //checks the LHS of the line
							varName = varNameCheck(varName);
						else {
							printer.println("Error : Line "+lineCounter);
							scan.close();
							return;
						}
						varValue = removePow(varValue); //replace "pow(x,y)" with "x^y"

						if(!infixToPostfix(varValue).equals("-1")) //checks the RHS of the line
							varValue = infixToPostfix(varValue);
						else {
							printer.println("Error : Line "+lineCounter);
							scan.close();
							return;
						}
						vars.add(new Vars(varName, varValue)); //adds the assignment to stack arraylist
					}else { 
						printer.println("Error : Line "+lineCounter);
						scan.close();
						return;
					}
				}else { //if it is an output line
					if(!infixToPostfix(newLine).equals("-1")) {
						vars.add(new Vars(infixToPostfix(newLine)));
					}
					else {
						printer.println("Error : Line "+lineCounter);
						scan.close();
						return;
					}
				}
			}
		}

		ArrayList<String> varList = new ArrayList<String>(); //This list will hold the names of variables
		for(int i=0; i<vars.size(); i++)
			if(!vars.get(i).varName.contains(" "))
				if(!varList.contains(vars.get(i).varName))
					varList.add(vars.get(i).varName);
		declareVariables(varList); //makes the declaration of variables in the top of the .asm file

		//Loop that executes every line in the vars array list
		for(int i=0; i<vars.size(); i++) {
			if(!vars.get(i).isOutput()) { //if it is an assignment expression
				String line = vars.get(i).varName + " " + vars.get(i).varValue + " =";

				Scanner temp = new Scanner(line);
				temp.next(); //skip the variable name to avoid extra pushes to stack
				while(temp.hasNext()) {
					String s = temp.next();
					if(!s.equals("+") && !s.equals("*") && !s.equals("^") && !s.equals("=")) { //if it is an operand
						if(isNumber(s)) { //if it is a number
							if(s.length()>4) { //if it is more than 16-bit
								printer.println("mov ax,"+s.substring(s.length()-4)+"h");
								printer.println("push ax");
								printer.println("mov ax,"+s.substring(0,s.length()-4)+"h");
								printer.println("push ax");
							}
							else { //if it is less than 16-bit
								printer.println("mov ax,"+s+"h");
								printer.println("push ax");
								printer.println("mov ax,0h");
								printer.println("push ax");
							}
						}
						else { //if it is a varible
							printer.println("mov ax,w[" + s + "]");
							printer.println("push ax");
							printer.println("mov ax,w[" + s + "+2]");
							printer.println("push ax");
						}
					}
					else { //if it is an operator
						if(s.equals("+")) {
							addition();
						}
						else if(s.equals("*")) {
							multiplication();
						}
						else if(s.equals("^")) {
							power();
						}
						else {
							assignment(vars.get(i).varName);
						}
					}
				}
				temp.close();
			}
			else { //if it is an output expression
				String line = vars.get(i).varName; //Get the line

				Scanner temp = new Scanner(line);
				while(temp.hasNext()) { //Read every element in the line
					String s = temp.next();
					if(!temp.hasNext()) { //If it is an only variable or number to print
						if(isNumber(s)) { //If it is a number
							if(s.length()>4) { //if it is more than 16-bit
								printer.println("mov ax,"+s.substring(s.length()-4)+"h");
								printer.println("push ax");
								printer.println("mov ax,"+s.substring(0,s.length()-4)+"h");
								printer.println("push ax");
							}
							else { //if it is less than 16-bit
								printer.println("mov ax,"+s+"h");
								printer.println("push ax");
								printer.println("mov ax,0h");
								printer.println("push ax");
							}
						}
						else {//if it is a varible
							printer.println("mov ax,w[" + s + "]");
							printer.println("push ax");
							printer.println("mov ax,w[" + s + "+2]");
							printer.println("push ax");
						}
						output();
					}
					else { //If it is a mathematical expression
						if(!s.equals("+") && !s.equals("*") && !s.equals("^") && !s.equals("=")) { //if it is an operand
							if(isNumber(s)) { //if it is a number
								if(s.length()>4) { //if it is more than 16-bit
									printer.println("mov ax,"+s.substring(s.length()-4)+"h");
									printer.println("push ax");
									printer.println("mov ax,"+s.substring(0,s.length()-4)+"h");
									printer.println("push ax");
								}
								else { //if it is less than 16-bit
									printer.println("mov ax,"+s+"h");
									printer.println("push ax");
									printer.println("mov ax,0h");
									printer.println("push ax");
								}
							}
							else { //if it is a varible
								printer.println("mov ax,w[" + s + "]");
								printer.println("push ax");
								printer.println("mov ax,w[" + s + "+2]");
								printer.println("push ax");
							}
						}
						else { //if it is an operator
							if(s.equals("+"))
								addition();
							else if(s.equals("*"))
								multiplication();
							else
								power();
						}
						if(!temp.hasNext()) {
							output();
							break;
						}
					}
				}
				temp.close();
			}
		}
		printer.println("int 20h");
		printer.println("code ends");
		scan.close();
	}

	//Determine if the given string is a number or not
	private static boolean isNumber(String s) {
		if(Character.isDigit(s.charAt(0))) {
			try {
				Integer.parseInt(s,16);
				return true;
			}catch(NumberFormatException ne) {
				return false;
			}
		}
		return false;
	}

	//This method generates necessary assembly code to print the value of given variable
	private static void output() {
		printer.println("pop bx");
		printer.println("mov cx,4h");
		printer.println("mov ah,2h");
		printer.println("loop"+printCounter+":");
		printer.println("mov dx,0fh");
		printer.println("rol bx,4h");
		printer.println("and dx,bx");
		printer.println("cmp dl,0ah");
		printer.println("jae hexdigit"+printCounter);
		printer.println("add dl,'0'");
		printer.println("jmp output"+printCounter);
		printer.println("hexdigit"+printCounter+":");
		printer.println("add dl,'A'");
		printer.println("sub dl,0ah");
		printer.println("output"+printCounter+":");
		printer.println("int 21h");
		printer.println("dec cx");
		printer.println("jnz loop"+printCounter);
		printCounter++;
		printer.println("pop bx");
		printer.println("mov cx,4h");
		printer.println("mov ah,2h");
		printer.println("loop"+printCounter+":");
		printer.println("mov dx,0fh");
		printer.println("rol bx,4h");
		printer.println("and dx,bx");
		printer.println("cmp dl,0ah");
		printer.println("jae hexdigit"+printCounter);
		printer.println("add dl,'0'");
		printer.println("jmp output"+printCounter);
		printer.println("hexdigit"+printCounter+":");
		printer.println("add dl,'A'");
		printer.println("sub dl,0ah");
		printer.println("output"+printCounter+":");
		printer.println("int 21h");
		printer.println("dec cx");
		printer.println("jnz loop"+printCounter);
		printer.println("mov ah,02h");
		printer.println("mov dl,0ah");
		printer.println("int 21h");
		printCounter++;
	}

	//This method generates necessary assembly code to assign the value of given variable
	private static void assignment(String s) {
		printer.println("pop w["+s+"+2]");
		printer.println("pop w["+s+"]");
	}

	//This method generates necessary assembly code to perform power operation
	private static void power() {
		printer.println("pop w[varN]");
		printer.println("pop w[varN]");
		printer.println("pop w[varA+2]");
		printer.println("pop w[varA]");
		printer.println("mov w[varACons],w[varA]");
		printer.println("mov w[varACons+2],w[varA+2]");
		printer.println("mov bx,w[varN]");
		printer.println("cmp bx,0h");
		printer.println("je zeroPower"+powCounter);
		printer.println("cmp bx,1h");
		printer.println("je onePower"+powCounter);
		printer.println("jne multPower"+powCounter);
		printer.println("multPower"+powCounter+":");
		printer.println("mov ax,w[varACons]");
		printer.println("push ax");
		printer.println("mov ax,w[varACons+2]");
		printer.println("push ax");
		printer.println("mov ax,w[varA]");
		printer.println("push ax");
		printer.println("mov ax,w[varA+2]");
		printer.println("push ax");
		multiplication();
		printer.println("pop w[varA+2]");
		printer.println("pop w[varA]");
		printer.println("dec w[varN]");
		printer.println("mov bx,w[varN]");
		printer.println("cmp bx,2h");
		printer.println("jae multPower"+powCounter);
		printer.println("push w[varA]");
		printer.println("push w[varA+2]");
		printer.println("jmp endLabel"+powCounter);
		printer.println("onePower"+powCounter+":");
		printer.println("mov ax,w[varA]");
		printer.println("push ax");
		printer.println("mov ax,w[varA+2]");
		printer.println("push ax");
		printer.println("jmp endLabel"+powCounter);
		printer.println("zeroPower"+powCounter+":");
		printer.println("mov ax,0h");
		printer.println("push ax");
		printer.println("mov ax,1h");
		printer.println("push ax");
		printer.println("jmp endLabel"+powCounter);
		printer.println("endLabel"+powCounter+":");
		powCounter++;
	}

	//This method generates necessary assembly code to perform multiplication operation
	private static void multiplication() {
		printer.println("pop bx");
		printer.println("pop cx");
		printer.println("pop dx");
		printer.println("pop w[temp]");
		printer.println("mov ax,dx");
		printer.println("mul cx");
		printer.println("push ax");
		printer.println("mov ax,bx");
		printer.println("mul w[temp]");
		printer.println("push ax");
		printer.println("mov ax,w[temp]");
		printer.println("mov dx,0");
		printer.println("mul cx");
		printer.println("mov cx,ax");
		printer.println("pop ax");
		printer.println("pop bx");
		printer.println("add ax,bx");
		printer.println("add ax,dx");
		printer.println("push cx");
		printer.println("push ax");
	}

	//This method generates necessary assembly code to perform addition operation
	private static void addition() {
		printer.println("pop w[temp]");
		printer.println("pop ax");
		printer.println("pop dx");
		printer.println("pop bx");
		printer.println("push dx");
		printer.println("push w[temp]");
		printer.println("add ax,bx");
		printer.println("mov w[temp],ax");
		printer.println("pop ax");
		printer.println("pop bx");
		printer.println("adc ax,bx");
		printer.println("jnc noCarry"+additionCarry);
		printer.println("inc al");
		printer.println("noCarry"+additionCarry+":");
		printer.println("push w[temp]");
		printer.println("push ax");
		additionCarry++;
	}

	//This method declares all the variables 
	private static void declareVariables(ArrayList<String>varList) {
		printer.println("code segment");
		printer.println("jmp start");
		printer.println();
		for(int i=0; i<varList.size(); i++) {
			printer.println(varList.get(i) + ": dd 0");
		}
		printer.println("temp: dd 0");
		printer.println("varN: dd 0");
		printer.println("varA: dd 0");
		printer.println("varACons: dd 0");
		printer.println();
		printer.println("start:");
	}

	//=====================================================================================
	//THE PART WHICH CHECKS THE ERRORS IN THE .CO AND CONVERTS THE .CO INTO POSTFIX FORM ||
	//=====================================================================================
	//Checks if the line is empty or not
	private static boolean isLineEmpty(String s) {
		for(int i=0; i<s.length(); i++)
			if(s.charAt(i) != ' ')
				return false;
		return true;
	}

	//Checks if the line is an output line or not
	private static boolean isOutput(String s) {
		for(int i=0; i<s.length(); i++)
			if(s.charAt(i) == '=')
				return false;
		return true;
	}

	//Checks the paranthesis of the string
	private static boolean checkParant(String s) {
		int counter = 0;
		for(int i=0; i<s.length(); i++) {
			if(s.charAt(i) == '(')
				counter++;
			else if(s.charAt(i) == ')')
				counter--;
		}
		return counter == 0;
	}

	//Checks the left-hand side of the expression
	//If there is an error returns -1
	//Else returns the proper variable name
	private static String varNameCheck(String s) {
		String result = "";
		Character[] chars = new Character[s.length()];

		for(int i=0; i<s.length(); i++) //put every character to the character array
			chars[i] = s.charAt(i);

		int i=0;
		while(i<s.length()) {
			while(chars[i] == ' ') //clears the whitespace
				i++;
			while(chars[i] != ' ' && chars[i] != '=') { //takes the variable's name
				result += chars[i];
				i++;
			}
			while(chars[i] == ' ') //clears the whitespace
				i++;
			if(chars[i] != '=') //return if there is an error like "x y = ..."
				return "-1";
			else
				break;
		}
		return result;
	}

	//Replace "pow(x,y)" with "x^y"
	private static String removePow(String s) {
		int counter = 0;
		String result = s;
		for(int i=0; i<s.length()-2; i++) //counts how many pow operation used in the expression
			if(s.charAt(i) == 'p' && s.charAt(i+1) == 'o' && s.charAt(i+2) == 'w')
				counter++;
		String temp = "";
		for(int i=0; i<counter; i++) { //replace pow with '^' by eliminating all pows one by one
			temp = "";
			for(int j=0; j<result.length()-2; j++)
				if(result.charAt(j) == 'p' && result.charAt(j+1) == 'o' && result.charAt(j+2) == 'w') {
					temp = result.substring(0,j);
					j += 3;
					while(true) {
						if(result.charAt(j) != ',') {
							temp += result.charAt(j);
							j++;
						}
						else {
							temp += " ^";
							temp += result.substring(j+1);
							break;
						}
					}
				}
			result = temp;
		}
		return result;
	}

	//A utility function to return precedence of a given operator
	//Higher returned value means higher precedence
	private static int prec(char ch) {
		if(ch == '+')
			return 1;
		else if(ch == '*')
			return 2;
		else if(ch == '^')
			return 3;
		else 
			return -1;
	}

	// The main method that converts given infix expression to postfix expression. 
	private static String infixToPostfix(String exp) {
		// initializing empty String for result
		String result = new String("");
		
		boolean isOperator = false;

		// initializing empty stack
		Stack<Character> stack = new Stack<Character>();

		int i = 0;
		while(exp.charAt(i) == ' ') //removes the whitespaces
			i++;

		for (; i<exp.length(); ++i) {
			char c = exp.charAt(i);

			// If the scanned character is an operand, add it to output.
			if (Character.isLetterOrDigit(c)) {
				result += c;
				if(i+1 < exp.length()) {
					while(Character.isLetterOrDigit(exp.charAt(i+1))) {
						result += exp.charAt(i+1);
						i++;
						if(!(i+1 < exp.length()))
							break;
					}
				}
				isOperator = false;
				result += ' ';
			}
			// If the scanned character is a whitespace, checks the next character
			// if the next character is not an operator return -1
			else if(c == ' ') {
				if(i+1 < exp.length()) {
					if(exp.charAt(i+1) == ' ') {/*increments i*/}
					else if(!isOperator && Character.isLetterOrDigit(exp.charAt(i+1)))
						return "-1";
				}
			}
			// If the scanned character is an '(', push it to the stack.
			else if (c == '(')
				stack.push(c);

			//  If the scanned character is an ')', pop and output from the stack 
			// until an '(' is encountered.
			else if (c == ')') {
				while (!stack.isEmpty()) {
					char a = stack.pop();
					if(a != '(')
						result += a + " ";
					else
						break;
				}
			}
			else { // an operator is encountered
				while (!stack.isEmpty() && prec(c) <= prec(stack.peek()))
					result += stack.pop() + " ";
				stack.push(c);
				isOperator = true;
			}
		}
		// pop all the operators from the stack
		while (!stack.isEmpty())
			result += stack.pop() + " ";

		return result;
	}
}