package edu.ufl.cise.plcsp23;


public class NumLitToken extends Token implements INumLitToken {

    public NumLitToken(IToken.Kind kind, int pos, int length, int line, int col, char[] input){
        super(kind, pos, length, line, col, input);
    }

    //returns integer from num literal
    @Override
    public int getValue() {
        String number = new String(source, pos, length);
        return Integer.parseInt(number);
    }
    //EOF NumLitToken.Java
}