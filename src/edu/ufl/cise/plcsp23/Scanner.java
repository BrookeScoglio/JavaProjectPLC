package edu.ufl.cise.plcsp23;

import java.util.Arrays;
import java.util.HashMap;


public class Scanner implements IScanner {
    final String input;
    // char arrary of the input, temrin w extra char 0 (EOF?)
    final char[] inputChars;

    //imply this is true in constructor
    // invariant ch == inputChars[pos]
    int pos; // position of the character in the input
    char ch; // next char
    int line;
    int column;
    int length;

    private enum State { // BASICALLY THE CIRCLE
        START,
        IN_IDENT,
        IN_STRING_LIT,
        IN_NUM_LIT,
        HAVE_EQUAL,
        HAVE_LT,
        HAVE_AND,
        HAVE_OR,
        HAVE_EXP,
        HAVE_GT,
        IN_COMMENT
    }


    public Scanner(String input) {
        this.input = input;
        inputChars = Arrays.copyOf(input.toCharArray(), input.length() + 1);
        pos = 0;
        ch = inputChars[pos]; //char at that pos

        System.out.println(inputChars);
        System.out.println(ch);
    }

    // Store reserved words in a hash map for easy lookup, Maps the string to the token kind
    public static HashMap<String, IToken.Kind> reservedWords;

    static {
        reservedWords = new HashMap<>();
        reservedWords.put("image", IToken.Kind.RES_image);
        reservedWords.put("pixel", IToken.Kind.RES_pixel);
        reservedWords.put("int", IToken.Kind.RES_int);
        reservedWords.put("string", IToken.Kind.RES_string);
        reservedWords.put("void", IToken.Kind.RES_void);
        reservedWords.put("nil", IToken.Kind.RES_nil);
        reservedWords.put("load", IToken.Kind.RES_load);
        reservedWords.put("display", IToken.Kind.RES_display);
        reservedWords.put("write", IToken.Kind.RES_write);
        reservedWords.put("x", IToken.Kind.RES_x);
        reservedWords.put("y", IToken.Kind.RES_y);
        reservedWords.put("a", IToken.Kind.RES_a);
        reservedWords.put("X", IToken.Kind.RES_X);
        reservedWords.put("Y", IToken.Kind.RES_Y);
        reservedWords.put("Z", IToken.Kind.RES_Z);
        reservedWords.put("x_cart", IToken.Kind.RES_x_cart);
        reservedWords.put("y_cart", IToken.Kind.RES_y_cart);
        reservedWords.put("a_polar", IToken.Kind.RES_a_polar);
        reservedWords.put("r_polar", IToken.Kind.RES_r_polar);
        reservedWords.put("rand", IToken.Kind.RES_rand);
        reservedWords.put("sin", IToken.Kind.RES_sin);
        reservedWords.put("cos", IToken.Kind.RES_cos);
        reservedWords.put("atan", IToken.Kind.RES_atan);
        reservedWords.put("if", IToken.Kind.RES_if);
        reservedWords.put("while", IToken.Kind.RES_while);
    }

    //Might not need
/*    public static HashMap<String, IToken.Kind> opsAndSeps;

    static {
        opsAndSeps = new HashMap<>();
        opsAndSeps.put(".", IToken.Kind.DOT);
        opsAndSeps.put(",", IToken.Kind.COMMA);
        opsAndSeps.put("?", IToken.Kind.QUESTION);
        opsAndSeps.put(":", IToken.Kind.COLON);
        opsAndSeps.put("(", IToken.Kind.LPAREN);
        opsAndSeps.put(")", IToken.Kind.RPAREN);
        opsAndSeps.put("<", IToken.Kind.LT);
        opsAndSeps.put(">", IToken.Kind.GT);
        opsAndSeps.put("[", IToken.Kind.LSQUARE);
        opsAndSeps.put("]", IToken.Kind.RSQUARE);
        opsAndSeps.put("{", IToken.Kind.LCURLY);
        opsAndSeps.put("}", IToken.Kind.RCURLY);
        opsAndSeps.put("=", IToken.Kind.ASSIGN);
        opsAndSeps.put("==", IToken.Kind.EQ);
        opsAndSeps.put("<->", IToken.Kind.EXCHANGE);
        opsAndSeps.put("<=", IToken.Kind.LE);
        opsAndSeps.put(">=", IToken.Kind.GE);
        opsAndSeps.put("!", IToken.Kind.BANG);
        opsAndSeps.put("&", IToken.Kind.BITAND);
        opsAndSeps.put("&&", IToken.Kind.AND);
        opsAndSeps.put("|", IToken.Kind.BITOR);
        opsAndSeps.put("||", IToken.Kind.OR);
        opsAndSeps.put("+", IToken.Kind.PLUS);
        opsAndSeps.put("-", IToken.Kind.MINUS);
        opsAndSeps.put("*", IToken.Kind.TIMES);
        opsAndSeps.put("**", IToken.Kind.EXP);
        opsAndSeps.put("/", IToken.Kind.DIV);
        opsAndSeps.put("%", IToken.Kind.MOD);
    }*/

    @Override
    public IToken next() throws LexicalException {
        // call scanToken
        // return the next token
        return scanToken();
    }

    protected void nextChar() {
        pos++;
        ch = inputChars[pos];
    }

    // Helper functions to check char type
    private boolean isDigit(int ch) {
        return '0' <= ch && ch <= '9';
    }

    private boolean isLetter(int ch) {
        return ('A' <= ch && ch <= 'Z') || ('a' <= ch && ch <= 'z');
    }

    private boolean isIdentStart(int ch) {
        return isLetter(ch) || (ch == '_');
    }

    // Function to throw exception
    private void error(String message) throws LexicalException {
        throw new LexicalException("Error at pos " + pos + ": " + message);
    }

    private Token scanToken() throws LexicalException {
        State state = State.START;
        int tokenStart = -1; // position of first char in token

        while (true) { // reading if char is valid, temrinates whne token/ eof is returned
            switch (state) {
                case START -> {
                    tokenStart = pos;
                    switch (ch) {
                        //EOF
                        case 0 -> { // EOF (end of file)
                            return new Token(IToken.Kind.EOF, tokenStart, 0, line, column, inputChars);
                        }
                        //<WHITESPACE> AND <ESCAPE_SEQUENCE>
                        case ' ', '\n', '\b', '\t', '\r', '\f', '\"', '\\' -> {
                            nextChar();
                        }
                        //<OP> OR <SEPERATOR> (ALL THE SINGLE)
                        case '.' -> {
                            nextChar();
                            return new Token(IToken.Kind.DOT, tokenStart, 1, line, column, inputChars);
                        }
                        case ',' -> {
                            nextChar();
                            return new Token(IToken.Kind.COMMA, tokenStart, 1, line, column, inputChars);
                        }
                        case '?' -> {
                            nextChar();
                            return new Token(IToken.Kind.QUESTION, tokenStart, 1, line, column, inputChars);
                        }
                        case ':' -> {
                            nextChar();
                            return new Token(IToken.Kind.COLON, tokenStart, 1, line, column, inputChars);
                        }
                        case '(' -> {
                            nextChar();
                            return new Token(IToken.Kind.LPAREN, tokenStart, 1, line, column, inputChars);
                        }
                        case ')' -> {
                            nextChar();
                            return new Token(IToken.Kind.RPAREN, tokenStart, 1, line, column, inputChars);
                        }
                        case '<' -> {
                            state = State.HAVE_LT;
                            nextChar();
                        }
                        case '>' -> {
                            state = State.HAVE_GT;
                            nextChar();
                        }
                        case '[' -> {
                            nextChar();
                            return new Token(IToken.Kind.LSQUARE, tokenStart, 1, line, column, inputChars);
                        }
                        case ']' -> {
                            nextChar();
                            return new Token(IToken.Kind.RSQUARE, tokenStart, 1, line, column, inputChars);
                        }
                        case '{' -> {
                            nextChar();
                            return new Token(IToken.Kind.LCURLY, tokenStart, 1, line, column, inputChars);
                        }
                        case '}' -> {
                            nextChar();
                            return new Token(IToken.Kind.RCURLY, tokenStart, 1, line, column, inputChars);
                        }
                        case '=' -> {
                            state = State.HAVE_EQUAL;
                            nextChar();
                        }
                        case '!' -> {
                            nextChar();
                            return new Token(IToken.Kind.BANG, tokenStart, 1, line, column, inputChars);
                        }
                        case '&' -> {
                            state = edu.ufl.cise.plcsp23.Scanner.State.HAVE_AND;
                            nextChar();
                        }
                        case '|' -> {
                            state = State.HAVE_OR;
                            nextChar();
                        }
                        case '+' -> {
                            nextChar();
                            return new Token(IToken.Kind.PLUS, tokenStart, 1, line, column, inputChars);
                        }
                        case '-' -> {
                            nextChar();
                            return new Token(IToken.Kind.MINUS, tokenStart, 1, line, column, inputChars);
                        }
                        case '*' -> {
                            state = State.HAVE_EXP;
                            nextChar();
                        }
                        case '/' -> {
                            nextChar();
                            return new Token(IToken.Kind.DIV, tokenStart, 1, line, column, inputChars);
                        }
                        case '%' -> {
                            nextChar();
                            return new Token(IToken.Kind.MOD, tokenStart, 1, line, column, inputChars);
                        }
                        //comments

                        case '~' -> {
                            state = State.IN_COMMENT;
                            nextChar();
                        }

                        case '0' -> {
                            nextChar();
                            return new NumLitToken(IToken.Kind.NUM_LIT, tokenStart, 1, line, column, inputChars);
                        }
                        case '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {   //nonzero digit
                            state = State.IN_NUM_LIT;
                        }
                        //Idents and reserved words
                        default -> {
                          if (isLetter(ch)) {
                              state = State.IN_IDENT;
                            //  nextChar();
                          }
//                          if (ch == '"'){
//                              state = State.IN_STRING_LIT;
//                              nextChar();
//                          }
                          else error("illegal!!!");

                        }
                    }
                }
                //==
                case HAVE_EQUAL -> {
                    if (ch == '=') { // "==" checking the next input
                        state = state.START;
                        nextChar();
                        return new Token(IToken.Kind.EQ, tokenStart, 2, line, column, inputChars);
                    } else { // DECALARES THE = ASSIGNS KIND
                        throw new LexicalException("error: = expected");
                    }
                }
                // <= , <->,
                case HAVE_LT -> {
                    if (ch == '=') {
                        state = state.START;
                        nextChar();
                        return new Token(IToken.Kind.LE, tokenStart, 2, line, column, inputChars);
                    }
                    if (ch == '-') {
                        nextChar();
                        if (ch == '>') {
                            state = state.START;
                            nextChar();
                            return new Token(IToken.Kind.EXCHANGE, tokenStart, 3, line, column, inputChars);
                        }
                    } else {
                        state = state.START;
                        nextChar();
                        return new Token(IToken.Kind.LT, tokenStart, 1, line, column, inputChars);
                    }
                }
                //>=
                case HAVE_GT -> {
                    if (ch == '=') {
                        state = state.START;
                        nextChar();
                        return new Token(IToken.Kind.GE, tokenStart, 2, line, column, inputChars);
                    } else {
                        state = state.START;
                        nextChar();
                        return new Token(IToken.Kind.GT, tokenStart, 1, line, column, inputChars);

                    }
                }
                //&&
                case HAVE_AND -> {
                    if (ch == '&') {
                        state = state.START;
                        nextChar();
                        return new Token(IToken.Kind.AND, tokenStart, 2, line, column, inputChars);

                    } else {
                        state = state.START;
                        nextChar();
                        return new Token(IToken.Kind.BITAND, tokenStart, 1, line, column, inputChars);
                    }
                }
                //||
                case HAVE_OR -> {
                    if (ch == '|') {
                        state = state.START;
                        nextChar();
                        return new Token(IToken.Kind.OR, tokenStart, 2, line, column, inputChars);

                    } else { //add if they have regular symbols next to it
                        //throw new LexicalException("error: = expected");
                        return new Token(IToken.Kind.BITOR, tokenStart, 2, line, column, inputChars);
                    }
                }
                //**
                case HAVE_EXP -> {
                    if (ch == '*') {
                        state = state.START;
                        nextChar();
                        return new Token(IToken.Kind.EXP, tokenStart, 2, line, column, inputChars);

                    } else {
                        state = state.START;
                        nextChar();
                        return new Token(IToken.Kind.TIMES, tokenStart, 1, line, column, inputChars);
                    }
                }
                case IN_IDENT -> {
                    length = pos - tokenStart;
                    while (isIdentStart(ch)){
                        length++;
                        nextChar();
                    }
                    if (isIdentStart(ch) || isDigit(ch)){
                        length++;
                        nextChar();
                    }
                    else {
                        state = state.START;
                        return new StringLitToken(IToken.Kind.IDENT, tokenStart, length, line, column, inputChars);
                    }
                }
                case IN_STRING_LIT -> {
                    if (ch == '\\') {
                        nextChar();
                        if (ch != 'r' && ch != 't' && ch != 'f' && ch != '\\' && ch != '"' && ch != 'n') {
                            throw new LexicalException("illegal escape sequence");
                        } else {
                            nextChar();
                        }
                    } else if (ch == '\n') {
                        throw new LexicalException("illegal escape sequence");
                    } else if (ch != '"') {
                        nextChar();
                    } else {
                        int length = pos - tokenStart + 1;
                        nextChar();
                        return new StringLitToken(IToken.Kind.STRING_LIT, tokenStart, length, line, column, inputChars);
                    }
                }
                case IN_NUM_LIT -> {
                    if (isDigit(ch)) {
                        nextChar();
                    } else {
                        int length = pos-tokenStart;
                        String number = new String(inputChars, tokenStart, length);
                        try {
                            Integer.parseInt(number);
                            return new NumLitToken(IToken.Kind.NUM_LIT, tokenStart, length, line, column, inputChars);
                        } catch (Exception e) {
                            throw new LexicalException("num lit not in range");
                        }
                    }
                }
                //check commit changes
                default -> {
                    throw new UnsupportedOperationException("Bug in the Scanner");
                }
            }
        }
    }
}

