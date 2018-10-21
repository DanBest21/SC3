import java.io.*;
import java.util.*;

// Bare bones interpreter class.
public class BB_Interpreter 
{
	// Declaration of the variable list (HashMap), file path and file (BufferedReader) private variables.
	private static Map<String, Integer> variableList = new HashMap<String, Integer>();
	private static String filePath = "";
	private static BufferedReader file;
	private static Boolean inComment = false;
	
	// Function which retrieves a fresh iterator of the file - doing so by re-opening the connection to the file and generating a new file.lines().iterator().
	private static Iterator<String> getLinesIterator()
	{
		// Try to re-open the BufferedReader file object to reset the iterator.
		try
		{
			file.close();
			file = new BufferedReader(new FileReader(filePath));
		}
		catch (IOException ex)
		{
			System.err.println(ex.getCause() + ": " + ex.getMessage());
		}
		
		Iterator<String> linesIterator = file.lines().iterator();
		
		return linesIterator;
	}
	
	// Function which checks for missing semi-colons at the end of each line, and then returns an Integer list of the line numbers should any missing semi-colons be found.
	private static List<Integer> checkMissingSemicolons()
	{
		List<Integer> missingSemicolons = new ArrayList<Integer>();
		Iterator<String> linesIterator = getLinesIterator();
		int i = 0;

		while (linesIterator.hasNext())
		{
			if (!linesIterator.next().endsWith(";"))
			{
				missingSemicolons.add(i);
			}
			
			i++;
		}

		return missingSemicolons;
	}
	
	// Function that checks to see if the line is a comment, and handles it accordingly.
	private static Boolean isComment(String line)
	{
		// If the global Boolean value of inComment is true, and the line ends with "*/", then specify this as the end of the comment, returning true but setting the inComment Boolean to false.
		if (line.endsWith("*/") && inComment)
		{
			inComment = false;
			return true;
		}
		// Else if the line starts with "//", or the "/*" tag has been used without a "*/" being found at the end of a line, then return true.
		else if (line.startsWith("//") || inComment)
		{
			return true;
		}
		// Otherwise, if the line starts with "/*", then set the inComment variable to true and return true.
		else if (line.startsWith("/*"))
		{
			inComment = true;
			return true;
		}
		// Else, just return false as we are not in a comment.
		else
		{
			return false;
		}
	}
	
	// Core function that reads and interprets each line.
	// Takes two parameters: the line start number and a Boolean to indicate if it is recursive.
	private static void readFile(int lineStart, Boolean isRecursive)
	{
		// Get the iterator which iterates through each line of the file.
		Iterator<String> linesIterator = getLinesIterator();
		
		// Set the int i variable to the lineStart parameter.
		int i = lineStart;
		
		// Loop to the start entry as defined by i.
		for (int j = 0; j < lineStart; j++)
		{
			linesIterator.next();
		}
		
		Boolean skipToWhileEnd = false;
		Boolean nestedLoop = false;
		
		int endsToIgnore = 0; 
		
		// While there are still lines to iterate through:
		while (linesIterator.hasNext())
		{	
			// Get the next line.
			String line = linesIterator.next();
			
			if (isComment(line))
			{
				continue;
			}
			
			// Separate the line into an array of words using the split and substring functions (using trim to get rid of white-space).
			String[] words = line.substring(0, line.length() - 1).trim().split("\\s+");
			
			// If the length isn't 1, 2 or 5, then it's an invalid command.
			if ((words.length != 1) && (words.length != 2) && (words.length != 5))
			{
				System.err.println("Error: Line " + i + " contains an invalid command.");
				System.exit(1);
			}
			
			// Set the command variable to the first word.
			String command = words[0];
			String variable = null;
			
			// If there is a variable associated to the command, then set it to the "variable" variable.
			if ((words.length != 1))
			{
				variable = words[1];
			}
			
			int endValue;
			
			// If the variable doesn't currently exist in the global variableList variable, then add it as 0.
			if (!variableList.containsKey(variable) && variable != null)
			{
				variableList.put(variable, 0);
			}
				
			// Switch statement that handles each command.
			switch (command)
			{
				// In the case of the clear command:
				case "clear":
					// If we need to skip to the end of the while loop, then ignore this command.
					if (!skipToWhileEnd)
					{
						// Set the variable to 0 in the variableList and print it.
						variableList.put(variable, 0);			
						System.out.println("Variable List: " + variableList);
					}
					
					break;
				
				// In the case of the incr command:
				case "incr":
					// If we need to skip to the end of the while loop, then ignore this command.
					if (!skipToWhileEnd)
					{
						// Increase the variable by 1 in the variableList and print it.
						variableList.put(variable, variableList.get(variable) + 1);
						System.out.println("Variable List: " + variableList);
					}
					
					break;
				
				// In the case of the decr command:
				case "decr":
					// If we need to skip to the end of the while loop, then ignore this command.
					if (!skipToWhileEnd)
					{
						// Decrease the variable by 1 in the variableList and print it.
						variableList.put(variable, variableList.get(variable) - 1);
						System.out.println("Variable List: " + variableList);
					}
					
					break;
				
				// In the case of the while command:
				case "while":
					// If we want to skip to the while end, and this isn't a nested loop, then count the while command so we know how many end statements to ignore.
					if (skipToWhileEnd && !nestedLoop)
					{
						endsToIgnore++;
						break;
					}
					
					// Get the end value which triggers the end of the while loop.
					endValue = Integer.parseInt(words[3]);
					
					// If the variable is currently set to the end value, then we want to skip to the end of the while loop and ignore any commands nested inside of it.
					if (variableList.get(variable) == endValue)
					{	
						skipToWhileEnd = true;
					}
					// Else if we are not at the start of the current iteration:
					else if (i != lineStart)
					{
						// Recursively call this function, setting the new starting point to the location of this statement, and the Boolean to true to show this is recursion.
						readFile(i, true);
						
						// Skip to the end of the while loop now that we've iterated through it.
						skipToWhileEnd = true;
						
						// If this is part of recursion, then set this to be a nested loop (i.e. a loop within another loop).
						if (isRecursive)
						{
							nestedLoop = true;
						}
						
						// Get a new lines iterator and set it to the location of this while statement.
						linesIterator = getLinesIterator();
						
						for (int j = 0; j < i; j++)
						{
							linesIterator.next();
						}
					}
						
					break;
				
				// In the case of the end command:
				case "end":	
					// If we are ignoring end statements that we aren't looking for, simply count this as one and continue iterating.
					if (endsToIgnore > 0)
					{
						endsToIgnore--;
					}
					// Else if we are skipping to the while end, handle it:
					else if (skipToWhileEnd)
					{
						// First turn the flag off.
						skipToWhileEnd = false;
						
						// Then if this is recursive:
						if (isRecursive)
						{
							// And if it's a nested loop, set the nested loop to false as we've dealt with it.
							if (nestedLoop)
							{
								nestedLoop = false;
							}
							// Otherwise loop to the end of the iterator to get out of this recursion.
							else
							{
								while (linesIterator.hasNext())
								{
									linesIterator.next();
								}
							}
						}
						
						// As we use i++ regardless at the end of this while loop, reduce it by 1 so that it stays at the right place in the iterator.
						i--;
					}
					// Otherwise, regenerate the iterator and set it to our current position, setting the i tracker to the start of line (take away 1 to counter-act i++).
					else
					{
						linesIterator = getLinesIterator();
						
						for (int j = 0; j < lineStart; j++)
						{
							linesIterator.next();
						}
						
						i = lineStart - 1;
					}
					
					break;
			}
			
			// Increase the i tracker by 1.
			i++;
		}
	}
	
	private static void loadFile()
	{
		// Try to open a BufferedReader to the file path.
		try
		{
			file = new BufferedReader(new FileReader(filePath));
			
			// Check for missing semicolons.
			List<Integer> liSemicolons = checkMissingSemicolons();

			// Error handling if any semicolons are found to be missing:
			if (!liSemicolons.isEmpty())
			{
				System.err.print("Error: The file is missing a semicolon on line(s):");

				Iterator<Integer> semicolonsIterator = liSemicolons.iterator();
				
				while (semicolonsIterator.hasNext())
				{
					int lineNumber = semicolonsIterator.next() + 1;
					System.err.print(" " + lineNumber);
					
					if (semicolonsIterator.hasNext())
					{
						System.err.print(",");
					}
				}
				
				System.err.println(".");
				
				System.exit(1);
			}
			
			readFile(0, false);
			
			// Close the file now we are done with it.
			file.close();
		}

		catch (IOException ex)
		{
			System.err.println(ex.getCause() + ": " + ex.getMessage());
		}

	}

	public static void main(String[] args) 
	{
		// Create a new scanner object.
		Scanner input = new Scanner(System.in);

		System.out.println("Bare Bones Interpreter");
		System.out.println("Please enter the full file path for the file that you wish to interpret.");

		// Get the user path from prompting the user by the console.
		filePath = input.nextLine();
		loadFile();
	}
}