/*
 * Abdul Wahab
 * A small Java Interpreter
 * To see how reflection in Java works.
 */

import java.util.HashMap;
import java.util.Scanner;

public class Interpreter {
	   // The symbol table is a dictionary of variable identifiers to bindings.
	   private HashMap<String, Object> mySymbolTable = new HashMap<String, Object>();
	   private Parser myParser;
	  
	   public Interpreter(){
	       mySymbolTable = new HashMap<String,Object>();
	       myParser = new Parser();
	   }
	  
	   /*
	   * Continually ask the user to enter a line of code.
	   */
	   public void promptLoop() throws Exception{
	       System.out.println("This is a simple interpreter. I'm not a good compiler, so be careful and follow my special rules:\n" +
	               "Each class name should be fully qualified, \n"+
	               "I only create objects and call methods. \n" +
	               "I only use literals of integers and Strings. \n"+
	               "Enter 'Q' to quit.");
	       Scanner scan = new Scanner(System.in);
	       String line = scan.nextLine();
	       while(!line.equalsIgnoreCase("q")){
	           // Find the important words out of the line.
	           ParseResults parse = myParser.parse(line);
	           // Do the command, and give a String response telling what happened.
	           String answer = process(parse);
	           System.out.println(answer);
	           line = scan.nextLine();
	       }
	   }
	  
	   /*
	   * This method does the work of carrying out the intended command.
	   * The input is a ParseResults object with all the important information
	   * from the typed line identified. The output is the String to print telling
	   * the result of carrying out the command.
	   */
	   public String process(ParseResults parse) throws Exception{  // SA
	       //System.out.println(parse);
	       if (parse.isMethodCall){
	           return callMethod(parse);
	       }
	       else return makeObject(parse);
	      
	   }
	  
	   /*
	   * TODO: convertNameToInstance
	   * Given a name (identifier or literal) return the object.
	   * If the name is in the symbol table, you can just return the object it is
	   * associated with. If it's not in the symbol table, then it is either
	   * a String literal or it is an integer literal. Check to see if the
	   * first character is quotation marks. If so, create a new String object to
	   * return that has the same characters as the name, just without the quotes.
	   * If there aren't quotation marks, then it is an integer literal. Use Integer.parseInt
	   * to turn the String into an int.
	   */
	   public Object convertNameToInstance(String name){ // SA
	       if (mySymbolTable.containsKey(name))
	           return mySymbolTable.get(name);
	       else if (name.charAt(0) == '"') {
	           return name.substring(1, name.lastIndexOf('"'));
	       }
	       else
	           return Integer.parseInt(name);
	   }
	  
	  
	   /*TODO: convertNameToInstance.
	   * Takes an array of Strings and converts all of them to their associated objects.
	   * Simply call the other helper method of the same name on each item in the array.
	   */
	   public Object[] convertNameToInstance(String[] names){ // SA
	       Object instances[] = new Object[names.length];
	       for(int i = 0; i < names.length; i++)
	           instances[i] = convertNameToInstance(names[i]);
	       return instances;
	   }
	  
	  
	   /* TODO: makeObject
	   * This method does the "process" job for making objects.
	   * Don't forget to add the new object to the symbol table.
	   * The String that is returned should be a basic message telling what happened.
	   */
	   public String makeObject(ParseResults parse) throws Exception{ // SA
	       Object[] args = convertNameToInstance(parse.arguments);
	       Object newObj = ReflectionUtilities.createInstance(parse.className, args);
	       if (newObj != null) {
	           mySymbolTable.put(parse.objectName, newObj);
	           return "You made a new " + parse.className + " called " + parse.objectName;
	       }
	       else
	           return "We had a problem with that class. It was not added to the symbol table!";
	   }
	  
	   /*
	   * TODO: callMethod
	   * This method does the "process" job for calling methods.
	   * You MUST use the ReflectionUtilities to call the method for
	   * this to work, because ints can't be directly transformed into Objects.
	   * When you call a method, if it has an output, be sure to
	   * either create a new object for that output or change the
	   * existing object. This will require either adding a new
	   * thing to the symbol table or removing what is currently there
	   * and replacing it with something else.
	   */
	   public String callMethod(ParseResults parse) throws Exception{ // SA
	       Object[] args = convertNameToInstance(parse.arguments);
	       Object objName = convertNameToInstance(parse.objectName);
	       Object created = ReflectionUtilities.callMethod(objName, parse.methodName, args);

	       if(created != null) {
	           if (!mySymbolTable.containsKey(parse.answerName)) {
	               mySymbolTable.put(parse.answerName, created);
	               return "You have made a new object. The result was " + created;
	           } else if (mySymbolTable.containsKey(parse.answerName)) {
	               mySymbolTable.put(parse.answerName, created);
	               return "You have changed the value of " + parse.answerName + " to " + created;
	           }
	       }
	       return "Operation performed. My reply is " + created;
	   }
	}
