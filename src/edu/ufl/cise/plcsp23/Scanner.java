package edu.ufl.cise.plcsp23;

import java.util.Arrays;

import static java.util.Arrays.copyOf;

public class Scanner implements IScanner{
    final String input;
    // char arrary of the input, temrin w extra char 0 (EOF?)
    final char[] inputChars;

    //imply this is true in constructor
    // invariant ch == inputChars[pos]
    int pos; // position of the character in the input
    char ch; // next char

    boolean chIs0; // the boolean for the while (true) in scantoken

    private enum State { // BASICALLY THE KIND OF TOKEN / CIRCLE
        START,
        IN_IDENT,
        IN_STRING_LIT,
        IN_NUM_LIT,
        HAVE_EQUAL,
        HAVE_LT,
        HAVE_AND,
        HAVE_OR,
        HAVE_EXP,
        HAVE_GT


    }


    public Scanner(String input){
    this.input = input;
    inputChars = Arrays.copyOf(input.toCharArray(), input.length()+1);
    pos = 0;
    ch = inputChars[pos]; //char at that pos
        chIs0 = true;

        System.out.println(inputChars);
        System.out.println(ch);


    }


    @Override
    public  IToken next() throws LexicalException {
     // call scanToken
        // return the next token
        return scanToken();




    }

    protected void nextChar(){
        pos++;
        ch = inputChars[pos];
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
                            return new Token(Token.Kind.EOF, tokenStart, 0, inputChars);
                        }
                        //<WHITESPACE> AND <ESCAPE_SEQUENCE>
                        case ' ', '\n', '\b', '\t', '\r', '\"', '\\' -> {
                            nextChar();
                        }
                        //<OP> OR <SEPERATOR> (ALL THE SINGLE)
                        case '.' -> {
                            nextChar();
                            return new Token(Token.Kind.DOT, tokenStart, 1, inputChars);
                        }
                        case ',' -> {
                            nextChar();
                            return new Token(Token.Kind.COMMA, tokenStart, 1, inputChars);
                        }
                        case '?' -> {
                            nextChar();
                            return new Token(Token.Kind.QUESTION, tokenStart, 1, inputChars);
                        }
                        case ':' -> {
                            nextChar();
                            return new Token(Token.Kind.COLON, tokenStart, 1, inputChars);
                        }
                        case '(' -> {
                            nextChar();
                            return new Token(Token.Kind.LPAREN, tokenStart, 1, inputChars);
                        }
                        case ')' -> {
                            nextChar();
                            return new Token(Token.Kind.RPAREN, tokenStart, 1, inputChars);
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
                            return new Token(Token.Kind.LSQUARE, tokenStart, 1, inputChars);
                        }
                        case ']' -> {
                            nextChar();
                            return new Token(Token.Kind.RSQUARE, tokenStart, 1, inputChars);
                        }
                        case '{' -> {
                            nextChar();
                            return new Token(Token.Kind.LCURLY, tokenStart, 1, inputChars);
                        }
                        case '}' -> {
                            nextChar();
                            return new Token(Token.Kind.RCURLY, tokenStart, 1, inputChars);
                        }
                        case '=' -> {
                            state = State.HAVE_EQUAL;
                            nextChar();
                        }
                        case '!' -> {
                            nextChar();
                            return new Token(Token.Kind.BANG, tokenStart, 1, inputChars);
                        }
                        case '&' -> {
                            state = State.HAVE_AND;
                            nextChar();
                        }
                        case '|' -> {
                            state = State.HAVE_OR;
                            nextChar();
                        }
                        case '+' -> {
                            nextChar();
                            return new Token(Token.Kind.PLUS, tokenStart, 1, inputChars);
                        }
                        case '-' -> {
                            nextChar();
                            return new Token(Token.Kind.MINUS, tokenStart, 1, inputChars);
                        }
                        case '*' -> {
                            state = State.HAVE_EXP;
                            nextChar();
                        }
                        case '/' -> {
                            nextChar();
                            return new Token(Token.Kind.DIV, tokenStart, 1, inputChars);
                        }
                        case '%' -> {
                            nextChar();
                            return new Token(Token.Kind.MOD, tokenStart, 1, inputChars);
                        }
                        default -> {
                            throw new UnsupportedOperationException(
                                    "not implemented yet"
                            );
                        }
                    }
                }
                //==
                case HAVE_EQUAL -> {
                    if (ch == '=') { // "==" checking the next input
                        state = state.START;
                        nextChar();
                        return new Token(Token.Kind.EQ, tokenStart, 2, inputChars);
                    } else { // DECALARES THE = ASSIGNS KIND

                        state = state.START;
                        nextChar();
                        return new Token(Token.Kind.ASSIGN, tokenStart, 1, inputChars);
                    }
                }
                // <= , <->,
                case HAVE_LT -> {
                    if (ch == '=') {
                        state = state.START;
                        nextChar();
                        return new Token(Token.Kind.LE, tokenStart, 2, inputChars);
                    }
                    if (ch == '-') {
                        nextChar();
                        if (ch == '>') {
                            state = state.START;
                            nextChar();
                            return new Token(Token.Kind.EXCHANGE, tokenStart, 3, inputChars);
                        }
                    } else {
                        state = state.START;
                        nextChar();
                        return new Token(Token.Kind.LT, tokenStart, 1, inputChars);
                    }
                }
                //>=
                case HAVE_GT -> {
                    if (ch == '=') {
                        state = state.START;
                        nextChar();
                        return new Token(Token.Kind.GE, tokenStart, 2, inputChars);
                    } else {
                        state = state.START;
                        nextChar();
                        return new Token(Token.Kind.GT, tokenStart, 1, inputChars);

                    }
                }
                //&&
                case HAVE_AND -> {
                    if (ch == '&'){
                        state = state.START;
                        nextChar();
                        return new Token(Token.Kind.AND, tokenStart, 2, inputChars);

                    }
                    else{
                        state = state.START;
                        nextChar();
                        return new Token(Token.Kind.BITAND, tokenStart, 1, inputChars);
                    }
                }
                //||
                case HAVE_OR -> {
                    if (ch == '|'){
                        state = state.START;
                        nextChar();
                        return new Token(Token.Kind.OR, tokenStart, 2, inputChars);

                    }
                    else{
                        state = state.START;
                        nextChar();
                        return new Token(Token.Kind.BITOR, tokenStart, 1, inputChars);
                    }
                }
                //**
                case HAVE_EXP -> {
                    if (ch == '*'){
                        state = state.START;
                        nextChar();
                        return new Token(Token.Kind.EXP, tokenStart, 2, inputChars);

                    }
                    else{
                        state = state.START;
                        nextChar();
                        return new Token(Token.Kind.TIMES, tokenStart, 1, inputChars);
                    }
                }
                    case IN_IDENT -> {
                    }
                    case IN_STRING_LIT -> {
                    }
                    case IN_NUM_LIT -> {
                    }
                    default -> {
                        throw new UnsupportedOperationException("Bug in the Scanner");
                    }

                }
            }
        }
    }

