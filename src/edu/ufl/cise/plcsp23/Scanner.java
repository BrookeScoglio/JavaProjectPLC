package edu.ufl.cise.plcsp23;
import edu.ufl.cise.plcsp23.IToken.Kind;
import java.util.Arrays;
import java.util.HashMap;

//expected IToken.Kind<EOF> but got Token.Kind<EOF>
//scanning ==

    // Store reserved words in a hash map for easy lookup
    // Maps the string to the kind
    public class Scanner implements IScanner {
        final String input;
        // char array of the input, temrin w extra char 0 (EOF?)
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

        public static HashMap<String,IToken.Kind> reservedWords;
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
        public IToken next() throws LexicalException {
            // call scanToken
            // return the next token
            return scanToken();
        }

        protected void nextChar(){
            pos++;
            ch = inputChars[pos];
        }

        private Token scanToken() throws LexicalException {
            edu.ufl.cise.plcsp23.Scanner.State state = edu.ufl.cise.plcsp23.Scanner.State.START;
            int tokenStart = -1; // position of first char in token

            while (true) { // reading if char is valid, temrinates whne token/ eof is returned
                switch (state) {
                    case START -> {
                        tokenStart = pos;
                        switch (ch) {
                            //EOF
                            case 0 -> { // EOF (end of file)
                                return new Token(IToken.Kind.EOF, tokenStart, 0, inputChars);
                            }
                            //<WHITESPACE> AND <ESCAPE_SEQUENCE>
                            case ' ', '\n', '\b', '\t', '\r', '\"', '\\' -> {
                                nextChar();
                            }
                            //<OP> OR <SEPERATOR> (ALL THE SINGLE)
                            case '.' -> {
                                nextChar();
                                return new Token(IToken.Kind.DOT, tokenStart, 1, inputChars);
                            }
                            case ',' -> {
                                nextChar();
                                return new Token(IToken.Kind.COMMA, tokenStart, 1, inputChars);
                            }
                            case '?' -> {
                                nextChar();
                                return new Token(IToken.Kind.QUESTION, tokenStart, 1, inputChars);
                            }
                            case ':' -> {
                                nextChar();
                                return new Token(IToken.Kind.COLON, tokenStart, 1, inputChars);
                            }
                            case '(' -> {
                                nextChar();
                                return new Token(IToken.Kind.LPAREN, tokenStart, 1, inputChars);
                            }
                            case ')' -> {
                                nextChar();
                                return new Token(IToken.Kind.RPAREN, tokenStart, 1, inputChars);
                            }
                            case '<' -> {
                                state = edu.ufl.cise.plcsp23.Scanner.State.HAVE_LT;
                                nextChar();
                            }
                            case '>' -> {
                                state = edu.ufl.cise.plcsp23.Scanner.State.HAVE_GT;
                                nextChar();
                            }
                            case '[' -> {
                                nextChar();
                                return new Token(IToken.Kind.LSQUARE, tokenStart, 1, inputChars);
                            }
                            case ']' -> {
                                nextChar();
                                return new Token(IToken.Kind.RSQUARE, tokenStart, 1, inputChars);
                            }
                            case '{' -> {
                                nextChar();
                                return new Token(IToken.Kind.LCURLY, tokenStart, 1, inputChars);
                            }
                            case '}' -> {
                                nextChar();
                                return new Token(IToken.Kind.RCURLY, tokenStart, 1, inputChars);
                            }
                            case '=' -> {
                                state = edu.ufl.cise.plcsp23.Scanner.State.HAVE_EQUAL;
                                nextChar();
                            }
                            case '!' -> {
                                nextChar();
                                return new Token(IToken.Kind.BANG, tokenStart, 1, inputChars);
                            }
                            case '&' -> {
                                state = edu.ufl.cise.plcsp23.Scanner.State.HAVE_AND;
                                nextChar();
                            }
                            case '|' -> {
                                state = edu.ufl.cise.plcsp23.Scanner.State.HAVE_OR;
                                nextChar();
                            }
                            case '+' -> {
                                nextChar();
                                return new Token(IToken.Kind.PLUS, tokenStart, 1, inputChars);
                            }
                            case '-' -> {
                                nextChar();
                                return new Token(IToken.Kind.MINUS, tokenStart, 1, inputChars);
                            }
                            case '*' -> {
                                state = edu.ufl.cise.plcsp23.Scanner.State.HAVE_EXP;
                                nextChar();
                            }
                            case '/' -> {
                                nextChar();
                                return new Token(IToken.Kind.DIV, tokenStart, 1, inputChars);
                            }
                            case '%' -> {
                                nextChar();
                                return new Token(IToken.Kind.MOD, tokenStart, 1, inputChars);
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
                            return new Token(IToken.Kind.EQ, tokenStart, 2, inputChars);
                        } else { // DECALARES THE = ASSIGNS KIND

                            state = state.START;
                            nextChar();
                            return new Token(IToken.Kind.ASSIGN, tokenStart, 1, inputChars);
                        }
                    }
                    // <= , <->,
                    case HAVE_LT -> {
                        if (ch == '=') {
                            state = state.START;
                            nextChar();
                            return new Token(IToken.Kind.LE, tokenStart, 2, inputChars);
                        }
                        if (ch == '-') {
                            nextChar();
                            if (ch == '>') {
                                state = state.START;
                                nextChar();
                                return new Token(IToken.Kind.EXCHANGE, tokenStart, 3, inputChars);
                            }
                        } else {
                            state = state.START;
                            nextChar();
                            return new Token(IToken.Kind.LT, tokenStart, 1, inputChars);
                        }
                    }
                    //>=
                    case HAVE_GT -> {
                        if (ch == '=') {
                            state = state.START;
                            nextChar();
                            return new Token(IToken.Kind.GE, tokenStart, 2, inputChars);
                        } else {
                            state = state.START;
                            nextChar();
                            return new Token(IToken.Kind.GT, tokenStart, 1, inputChars);

                        }
                    }
                    //&&
                    case HAVE_AND -> {
                        if (ch == '&'){
                            state = state.START;
                            nextChar();
                            return new Token(IToken.Kind.AND, tokenStart, 2, inputChars);

                        }
                        else{
                            state = state.START;
                            nextChar();
                            return new Token(IToken.Kind.BITAND, tokenStart, 1, inputChars);
                        }
                    }
                    //||
                    case HAVE_OR -> {
                        if (ch == '|'){
                            state = state.START;
                            nextChar();
                            return new Token(IToken.Kind.OR, tokenStart, 2, inputChars);

                        }
                        else{
                            state = state.START;
                            nextChar();
                            return new Token(IToken.Kind.BITOR, tokenStart, 1, inputChars);
                        }
                    }
                    //**
                    case HAVE_EXP -> {
                        if (ch == '*'){
                            state = state.START;
                            nextChar();
                            return new Token(IToken.Kind.EXP, tokenStart, 2, inputChars);

                        }
                        else{
                            state = state.START;
                            nextChar();
                            return new Token(IToken.Kind.TIMES, tokenStart, 1, inputChars);
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



