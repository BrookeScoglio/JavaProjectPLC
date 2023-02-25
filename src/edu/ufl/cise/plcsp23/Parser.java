package edu.ufl.cise.plcsp23;

import edu.ufl.cise.plcsp23.ast.*;
import java.util.List;
import edu.ufl.cise.plcsp23.IToken.Kind;
import static edu.ufl.cise.plcsp23.IToken.Kind.BANG;
import static edu.ufl.cise.plcsp23.IToken.Kind.TIMES;
import static edu.ufl.cise.plcsp23.IToken.Kind.PLUS;
import static edu.ufl.cise.plcsp23.IToken.Kind.MINUS;
import static edu.ufl.cise.plcsp23.IToken.Kind.EXP;
import static edu.ufl.cise.plcsp23.IToken.Kind.GT;
import static edu.ufl.cise.plcsp23.IToken.Kind.GE;
import static edu.ufl.cise.plcsp23.IToken.Kind.LT;
import static edu.ufl.cise.plcsp23.IToken.Kind.LE;
import static edu.ufl.cise.plcsp23.IToken.Kind.OR;
import static edu.ufl.cise.plcsp23.IToken.Kind.BITOR;

//NOTE IN Tb: TokenType type == our IToken kind
//HELP ME PLSSSS what is lox :(

//Parser class to implement the IParser.java interface
public class Parser implements IParser {

    private int current;                //Holds current position
    IToken t;                           //Always holds the current token
    private final List<Token> tokens;   //List of tokens
    Parser(List<Token> tokens){this.tokens = tokens;}

private IScanner scanner;

    //Parser Constructor -- FIX TOKEN LIST
    public Parser(String input) {
        this.current = 0;
        IScanner scanner = CompilerComponentFactory.makeScanner(input);

    }

    //Function to parse AST and throws PLCException if not valid AST -- NEEDS WORK

    public AST parse() throws PLCException {
        t = scanner.next(); // token
        return null;
    }

    private static class ParseError extends SyntaxException {
        public ParseError(String message) {
            super(message);
        }
    }


    private static void report(int line, String where,
                               String message) {
        System.err.println(
                "[line " + line + "] Error" + where + ": " + message);

    }
    //< lox-error
//> Parsing Expressions token-error



    //Function to help with errors in consume -- From TB needs work (IDK WHAT LOX IS)
    private ParseError error(Token token) {
        error(token);
        return new ParseError();
    }

    //Function to print errors -- From TB needs work (IDK WHAT REPORT IS)
    static void error(Token token, String message) {
        if (token.kind == IToken.Kind.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.getTokenString() + "'", message);
        }
    }

    //Function to parse single expression and return it
    //returns an error if not a valid expression
    Expr parse() {
        try {
            return expression();
        } catch (ParseError error) {
            return null;
        }
    }

    //Returns true if the input kind is the same as the current token, false otherwise
    protected boolean isKind(Kind kind) {
        return t.getKind() == kind;
    }
    //Returns true if the input kinds of the token match the current token kind, false otherwise
    protected boolean isKind(Kind... kinds) {
        for (Kind k : kinds) {
            if (k == t.getKind())
                return true;
        }
        return false;
    }

    //Checks if we’ve run out of tokens to parse
    private boolean isAtEnd() {
        return peek().kind == IToken.Kind.EOF;
    }

    private Token consume(Token kind, String message) {
        if (check(kind)) return advance();
        throw error(peek(), message);

    }
    //Get current token we have yet to consume
    private Token peek() {
        return tokens.get(current);
    }

    //Returns the most recently consumed token
    private Token previous() {
        return tokens.get(current - 1);
    }

    //Consumes the current token and returns it,
    //similar to how our scanner’s corresponding method crawled through characters.
    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    //It checks to see if the next token is of the expected kind.
    //If so, it consumes the token.
    //If some other token is there, then we’ve hit an error


    //returns true if the current token is of the given kind.
    //Unlike match(), it never consumes the token, it only looks at it.
    private boolean check(Token kind) {
        if (isAtEnd()) return false;
        return peek().kind == kind.getKind();
    }

    //Checks to see if the current token has any of the given kinds.
    //If so, it consumes the token and returns true.
    //Otherwise, it returns false and leaves the current token alone.
    private boolean match(Token... tokenKind) {
        for (Token kind : tokenKind) {
            if (check(kind)) {
                advance();
                return true;
            }
        }
        return false;
    }

    //NEED TO FIX ERROR FUNCTION TO FIX CONSUME FUNCTION TO FIX EXPR
    //Expression Expr: <expr> ::= <conditional_expr> | <or_expr>
    private Expr expression() {
        IToken firstToken = t;  //Save current token
        Expr e = null;
        if(isKind(IToken.Kind.RES_if)){
            e = conditional();
            consume();
        } else if (isKind(IToken.Kind.OR)||isKind(Kind.BITOR)) {
            consume();
            e = or();
        }
        else error();
        return e;
    }

//REVISE FUNCTIONS BELOW FROM TB------------------------------------------------------------------------------------

    //Conditional Expr: <conditional_expr> ::= if <expr> ? <expr> ? <expr>
    private Expr conditional() {
        IToken firstToken = t;
        return null;
    }

    //Or Expr: <or_expr> ::= <and_expr> ( ( | | || ) <and_expr>)*
    private Expr or() {
        Expr expr = and();

        while (match(OR,BITOR)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new BinaryExpr(expr, left, operator, right);
        }
        return expr;
    }

    //And Expr: <and_expr> ::= <comparison_expr> ( ( & | && ) <comparison_expr>)*
    private Expr and(){
        Expr expr = comparison();

        while(match()){}

        return expr;
    }

    //Comparison Expr: <comparison_expr> ::= <power_expr> ( (< | > | == | <= | >=) <power_expr>)*
    private Expr comparison() {
        Expr expr = additive();

        while (match(GT, GE, LT, LE)) {
            Token operator = previous();
            Expr right = additive();
            expr = new BinaryExpr(expr, operator, right);
        }
        return expr;
    }

    //Power Expr: <power_expr> ::= <additive_expr> ** <power_expr> | <additive_expr>
    private Expr power() {
        Expr expr = additive();

        while(match()){}

        return expr;
    }

    //In tb they called this function term()
    //Additive expr: <additive_expr> ::= <multiplicative_expr> ( ( + | - ) <multiplicative_expr> )*
    private Expr additive() {
        Expr expr = multiplicative();

        while (match(PLUS, MINUS)) {
            Token operator = previous();
            Expr right = multiplicative();
            expr = new BinaryExpr(expr, operator, right);
        }
        return expr;
    }

    //In tb: called factor()
    //Multiplicative Expr: <multiplicative_expr> ::= <unary_expr> (( * | / | % ) <unary_expr>)*
    private Expr multiplicative() {
        Expr expr = unary();

        while (match(EXP)) {
            Token operator = previous();
            Expr right = unary();
            expr = new BinaryExpr(expr, operator, right);
        }
        return expr;
    }

    //Unary Expr: <unary_expr> ::= ( ! | - | sin | cos | atan) <unary_expr> | <primary_expr>
    private Expr unary() {
        if (match(TIMES, BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new UnaryExpr(operator, right);
        }
        return primary();
    }

    //Primary Expr: <primary_expr> ::= STRING_LIT | NUM_LIT | IDENT | ( <expr> ) | Z | rand
    private Expr primary() {
        return null;
    }
}
