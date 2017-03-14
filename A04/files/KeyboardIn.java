import java.io.*;
import java.util.*;


public class KeyboardIn
{

    public static String readLine()
    {
        char nextChar;
        String result = "";
        boolean done = false;

        while (!done)
        {
            nextChar = readChar();
            if (nextChar == '\n')
               done = true;
            else if (nextChar == '\r')
            {
                //Do nothing.
                //Next loop iteration will detect '\n'
            }
            else
               result = result + nextChar;
        }

        return result;
    }


    public static String readLineWord()
    {
        String inputString = null,
               result = null;
        boolean done = false;

        while(!done)
        {
            inputString = readLine();
            StringTokenizer wordSource =
                                new StringTokenizer(inputString);
            if (wordSource.hasMoreTokens())
            {
                result = wordSource.nextToken();
                done = true;
            }
            else
            {
                System.out.println(
                       "Your input is not correct. Your input must");
                System.out.println(
                    "contain at least one nonwhitespace character.");
                System.out.println(
                                 "Please, try again. Enter input:");
           }
       }

       return result;
   }


    public static int readLineInt()
    {
        String inputString = null;
        int number = -9999;//To keep the compiler happy.
                           //Designed to look like a garbage value.
        boolean done = false;

        while (! done)
        {
            try
            {
                inputString = readLine();
                inputString = inputString.trim();
                number = Integer.parseInt(inputString);
                done = true;
            }
            catch (NumberFormatException e)
            {
                System.out.println(
                                "Your input number is not correct.");
                System.out.println("Your input number must be");
                System.out.println("a whole number written as an");
                System.out.println("ordinary numeral, such as 42");
                System.out.println("Minus signs are OK,"
                               + "but do not use a plus sign.");
                System.out.println("Please, try again.");
                System.out.println("Enter a whole number:");
            }
        }

        return number;
    }


    public static long readLineLong()
    {
        String inputString = null;
        long number = -9999;//To keep the compiler happy.
                            //Designed to look like a garbage value.
        boolean done = false;

        while (! done)
        {
            try
            {
                inputString = readLine();
                inputString = inputString.trim();
                number = Long.parseLong(inputString);
                done = true;
            }
            catch (NumberFormatException e)
            {
                System.out.println(
                                "Your input number is not correct.");
                System.out.println("Your input number must be");
                System.out.println("a whole number written as an");
                System.out.println("ordinary numeral, such as 42");
                System.out.println("Minus signs are OK,"
                               + "but do not use a plus sign.");
                System.out.println("Please, try again.");
                System.out.println("Enter a whole number:");
            }
       }

        return number;
    }


    public static double readLineDouble()
    {
        String inputString = null;
        double number = -9999;//To keep the compiler happy.
                              //Designed to look like a garbage value.
        boolean done = false;

        while (! done)
        {
            try
            {
                inputString = readLine();
                inputString = inputString.trim();
                number = Double.parseDouble(inputString);
                done = true;
            }
            catch (NumberFormatException e)
            {
                System.out.println(
                                "Your input number is not correct.");
                System.out.println("Your input number must be");
                System.out.println("an ordinary number either with");
                System.out.println("or without a decimal point,");
                System.out.println("such as 42 or 9.99");
                System.out.println("Please, try again.");
                System.out.println("Enter the number:");
            }
        }

        return number;
    }


    public static float readLineFloat()
    {
        String inputString = null;
        float number = -9999;//To keep the compiler happy.
                              //Designed to look like a garbage value.
        boolean done = false;

        while (! done)
        {
            try
            {
                inputString = readLine();
                inputString = inputString.trim();
                number = Float.parseFloat(inputString);
                done = true;
            }
            catch (NumberFormatException e)
            {
                System.out.println(
                                "Your input number is not correct.");
                System.out.println("Your input number must be");
                System.out.println("an ordinary number either with");
                System.out.println("or without a decimal point,");
                System.out.println("such as 42 or 9.99");
                System.out.println("Please, try again.");
                System.out.println("Enter the number:");
            }
        }

        return number;
    }


    public static char readLineNonwhiteChar()
    {
        boolean done = false;
        String inputString = null;
        char nonWhite = ' ';//To keep the compiler happy.

        while (! done)
        {
            inputString = readLine();
            inputString = inputString.trim();
            if (inputString.length() == 0)
            {
                System.out.println(
                                "Your input is not correct.");
                System.out.println("Your input must contain at");
                System.out.println(
                              "least one nonwhitespace character.");
                System.out.println("Please, try again.");
                System.out.println("Enter input:");
            }
            else
            {
                nonWhite = (inputString.charAt(0));
                done = true;
            }
        }

        return nonWhite;
    }


    public static boolean readLineBoolean()
    {
        boolean done = false;
        String inputString = null;
        boolean result = false;//To keep the compiler happy.

        while (! done)
        {
            inputString = readLine();
            inputString = inputString.trim();
            if (inputString.equalsIgnoreCase("true")
                   || inputString.equalsIgnoreCase("t"))
            {
                result = true;
                done = true;
            }
            else if (inputString.equalsIgnoreCase("false")
                        || inputString.equalsIgnoreCase("f"))
            {
                result = false;
                done = true;
            }
            else
            {
                System.out.println(
                                "Your input number is not correct.");
                System.out.println("Your input must be");
                System.out.println("one of the following:");
                System.out.println("the word true,");
                System.out.println("the word false,");
                System.out.println("the letter T,");
                System.out.println("or the letter F.");
                System.out.println("You may use either upper-");
                System.out.println("or lowercase letters.");
                System.out.println("Please, try again.");
                System.out.println("Enter input:");
            }
         }

        return result;
    }


    public static char readChar()
    {
        int charAsInt = -1; //To keep the compiler happy
        try
        {
            charAsInt = System.in.read();
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
            System.out.println("Fatal error. Ending Program.");
            System.exit(0);
        }

        return (char)charAsInt;
    }


    public static char readNonwhiteChar()
    {
      char next;

      next =  readChar();
      while (Character.isWhitespace(next))
          next =  readChar();

      return next;
    }


    public static int readInt() throws NumberFormatException
    {
        String inputString = null;
        inputString = readWord();
        return Integer.parseInt(inputString);
    }



    public static long readLong()
                      throws NumberFormatException
    {
        String inputString = null;
        inputString = readWord();
        return Long.parseLong(inputString);
    }


    public static double readDouble()
                       throws NumberFormatException
    {
        String inputString = null;
        inputString = readWord();
        return Double.parseDouble(inputString);
    }




    public static float readFloat()
                         throws NumberFormatException
    {
        String inputString = null;
        inputString = readWord();
        return Float.parseFloat(inputString);
    }


    public static String readWord()
    {
        String result = "";
        char next;

        next =  readChar();
//        while (Character.isWhitespace(next))
        while (Character.isWhitespace(next) && next != '\n')
             next =  readChar();

        while (!(Character.isWhitespace(next)))
        {
            result = result + next;
            next =  readChar();
        }

        if (next == '\r')
        {
            next = readChar();
            if (next != '\n')
            {
                System.out.println(
                    "Fatal Error in method readWord of class SavitchIn.");
                System.exit(1);
            }
        }

        return result;
    }


    public static byte readLineByte()
    {
        String inputString = null;
        byte number = -123;//To keep the compiler happy.
                           //Designed to look like a garbage value.
        boolean done = false;

        while (! done)
        {
            try
            {
                inputString = readLine();
                inputString = inputString.trim();
                number = Byte.parseByte(inputString);
                done = true;
            }
            catch (NumberFormatException e)
            {
                System.out.println(
                                "Your input number is not correct.");
                System.out.println("Your input number must be a");
                System.out.println("whole number in the range");
                System.out.println("-128 to 127, written as");
                System.out.println("an ordinary numeral, such as 42.");
                System.out.println("Minus signs are OK,"
                               + "but do not use a plus sign.");
                System.out.println("Please, try again.");
                System.out.println("Enter a whole number:");
            }
        }

        return number;
    }


    public static short readLineShort()
    {
        String inputString = null;
        short number = -9999;//To keep the compiler happy.
                           //Designed to look like a garbage value.
        boolean done = false;

        while (! done)
        {
            try
            {
                inputString = readLine();
                inputString = inputString.trim();
                number = Short.parseShort(inputString);
                done = true;
            }
            catch (NumberFormatException e)
            {
                System.out.println(
                                "Your input number is not correct.");
                System.out.println("Your input number must be a");
                System.out.println("whole number in the range");
                System.out.println("-32768 to 32767, written as");
                System.out.println("an ordinary numeral, such as 42.");
                System.out.println("Minus signs are OK,"
                               + "but do not use a plus sign.");
                System.out.println("Please, try again.");
                System.out.println("Enter a whole number:");
            }
        }

        return number;
    }


    public static byte readByte() throws NumberFormatException
    {
        String inputString = null;
        inputString = readWord();
        return Byte.parseByte(inputString);
    }


    public static short readShort() throws NumberFormatException
    {
        String inputString = null;
        inputString = readWord();
        return Short.parseShort(inputString);
    }


    public static int read()
    {
        int result = -1; //To keep the compiler happy
        try
        {
            result = System.in.read();
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
            System.out.println("Fatal error. Ending Program.");
            System.exit(0);
        }
        return result;
    }
}
