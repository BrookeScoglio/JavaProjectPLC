package edu.ufl.cise.plcsp23;

//  EXTEND ITOKEN FOR THINGS THAT HAVE VALUES IN TOKEN: NUM_LIT method, STRING_LIT method
public class Token implements IToken {

    final IToken.Kind kind; //type
    final int pos;
    final int length;
    final char[] source; //text of the token

    public record SourceLocation(int line, int column){}

    public Token(IToken.Kind kind, int pos, int length, char[] source){
        super();
        this.kind = kind;
        this.pos = pos;
        this.length = length;
        this.source = source;
    }

    @Override
    public IToken.SourceLocation getSourceLocation() {
        return null;
    }

    @Override
    public IToken.Kind getKind() {
        return kind;
    }

    @Override
    public String getTokenString() {
        return null;
    }
    @Override
    public String toString(){
        return null;
    }

}
