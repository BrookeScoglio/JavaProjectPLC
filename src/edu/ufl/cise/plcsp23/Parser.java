package edu.ufl.cise.plcsp23;

import edu.ufl.cise.plcsp23.ast.AST;
import java.util.List;
import edu.ufl.cise.plcsp23.IToken.Kind;
import edu.ufl.cise.plcsp23.ast.Expr;
import edu.ufl.cise.plcsp23.Scanner;
import edu.ufl.cise.plcsp23.Token;

//NOTE IN Tb: TokenType type == our IToken kind
//HELP ME PLSSSS what is lox :(

//Parser class to implement the IParser.java interface
public class Parser implements IParser {

    private int current;                //Holds current position
    IToken t;                           //Always holds the current token
    private final List<Token> tokens;   //List of tokens

                                        //Parser Constructor
    public Parser(String input){
        this.current = 0;

        Parser(List<Token> tokens) {
            this.tokens = tokens;
        }
    }

    private static class ParseError extends SyntaxException {
        public ParseError(String message) {
            super(message);
        }
    }

    //Function to help with errors in consume
    private ParseError error(Token token, String message) {
        Lox.error(token, message);
        return new ParseError();
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

    //It checks to see if the next token is of the expected type.
    //If so, it consumes the token.
    //If some other token is there, then we’ve hit an error
    private Token consume(Token kind, String message) {
        if (check(kind)) return advance();

        throw error(peek(), message);
    }

    //Checks to see if the current token has any of the given types.
    //If so, it consumes the token and returns true.
    //Otherwise, it returns false and leaves the current token alone.
    private boolean match(Token types) {
        for (Token type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    //returns true if the current token is of the given type.
    //Unlike match(), it never consumes the token, it only looks at it.
    private boolean check(Token kind) {
        if (isAtEnd()) return false;
        return peek().kind == kind.getKind();
    }


    //Expression Expr: <expr> ::= <conditional_expr> | <or_expr>
    private Expr expression() {

    }

    //Conditional Expr: <conditional_expr> ::= if <expr> ? <expr> ? <expr>
    private Expr conditional() {

    }

    //Or Expr: <or_expr> ::= <and_expr> ( ( | | || ) <and_expr>)*
    private Expr or() {

    }

    //And Expr: <and_expr> ::= <comparison_expr> ( ( & | && ) <comparison_expr>)*
    private Expr and(){

    }

    //Comparison Expr: <comparison_expr> ::= <power_expr> ( (< | > | == | <= | >=) <power_expr>)*
    private Expr comparison() {
        Expr expr = term();

        while (match(Kind.GT, Kind.GE, Kind.LT,Kind.LE)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    //Power Expr: <power_expr> ::= <additive_expr> ** <power_expr> | <additive_expr>
    private Expr power() {

    }

    //In tb they called this function term()
    //Additive expr: <additive_expr> ::= <multiplicative_expr> ( ( + | - ) <multiplicative_expr> )*
    private Expr additive() {
        Expr expr = factor();

        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    //In tb: called factor()
    //Multiplicative Expr: <multiplicative_expr> ::= <unary_expr> (( * | / | % ) <unary_expr>)*
    private Expr multiplicative {
        Expr expr = unary();

        while (match(STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    //Unary Expr: <unary_expr> ::= ( ! | - | sin | cos | atan) <unary_expr> | <primary_expr>
    private Expr unary() {
        if (match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }
        return primary();
    }

    //Primary Expr: <primary_expr> ::= STRING_LIT | NUM_LIT | IDENT | ( <expr> ) | Z | rand
    private Expr primary() {
        if (match(FALSE)) return new Expr.Literal(false);
        if (match(TRUE)) return new Expr.Literal(true);
        if (match(NIL)) return new Expr.Literal(null);

        if (match(NUMBER, STRING)) {
            return new Expr.Literal(previous().literal);
        }

        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }
    }

    //Not sure yet
    @Override
    public AST parse() throws PLCException {
        return null;
    }
}
