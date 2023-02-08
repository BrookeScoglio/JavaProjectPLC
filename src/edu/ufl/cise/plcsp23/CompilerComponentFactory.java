package edu.ufl.cise.plcsp23;
public class CompilerComponentFactory {
    public static IScanner makeScanner (String input) {
        return new Scanner(input);
    }
    //new methods will be added for additional components
}
