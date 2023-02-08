package edu.ufl.cise.plcsp23;

public class Token implements IToken{


public static enum Kind{
    IDENT,
    NUM_LIT,
    STRING_LIT,
    RES_image,
    RES_pixel,
    RES_int,
    RES_string,
    RES_void,
    RES_nil,
    RES_load,
    RES_display,
    RES_write,
    RES_x,
    RES_y,
    RES_a,
    RES_r,
    RES_X,
    RES_Y,
    RES_Z,
    RES_x_cart,
    RES_y_cart,
    RES_a_polar,
    RES_r_polar,
    RES_rand,
    RES_sin,
    RES_cos,
    RES_atan,
    RES_if,
    RES_while,
    DOT, //  .
    COMMA, // ,
    QUESTION, // ?
    COLON, // :
    LPAREN, // (
    RPAREN, // )
    LT, // <
    GT, // >
    LSQUARE, // [
    RSQUARE, // ]
    LCURLY, // {
    RCURLY, // }
    ASSIGN, // =
    EQ, // ==
    EXCHANGE, // <->
    LE, // <=
    GE, // >=
    BANG, // !
    BITAND, // &
    AND, // &&
    BITOR, // |
    OR, // ||
    PLUS, // +
    MINUS, // -
    TIMES, // *
    EXP, // **
    DIV, // /
    MOD, // %
    EOF,
    ERROR // maybe useful

}

final Kind kind; //type
final int pos; //
final int length;
final char[] source; //text of the toek

// value


    public record SourceLocation(int line, int column){}

    public Token(Kind kind, int pos, int length, char[] source){
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
    public Kind getKind() {
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
