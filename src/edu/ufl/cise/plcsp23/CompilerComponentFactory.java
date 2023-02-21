package edu.ufl.cise.plcsp23;
public class CompilerComponentFactory {
    public static IScanner makeScanner (String input) {
        return new Scanner(input);
    }

    public static IParser makeAssignment2Parser(String input)
     throws LexicalException {
          //add code to create a scanner and parser and return the parser.
      return new Parser(input);
    }

}
