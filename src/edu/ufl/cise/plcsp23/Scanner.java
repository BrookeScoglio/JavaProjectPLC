package edu.ufl.cise.plcsp23;
import java.util.Arrays;
import java.util.HashMap;

public class Scanner implements IScanner {

    // Variables
    final String input;
    final char[] inputChars;
    //NumLitToken numLitToken;
    //StringLitToken strLitToken;

    int pos; // position of the character in the input
    char ch; // next char
    int line;
    int column;
    int length;


    // Enumeration of the internal states of the token (basically the kind of token)
    private enum State {
        START,
        IN_IDENT,
        IN_STRING_LIT,
        IN_NUM_LIT,
        IN_COMMENT,
 
    }

    // Store reserved words in a hash map for easy lookup, Maps the string to the token kind
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

    // Stores operators and separators in a hash map for easy lookup, Maps the string to the token kind
    public static HashMap<String, IToken.Kind> opsAndSeps;
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
    }

    // Scanner to scan input. Goes through tokens and turns them into tokens
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

    /// TO CHANGE
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
                            return new Token(IToken.Kind.EOF, tokenStart, 0, line, column, inputChars);
                        }
                        //<WHITESPACE> AND <ESCAPE_SEQUENCE>
                        case ' ', '\n', '\b', '\t', '\r', '\"', '\\' -> {
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
                            state = edu.ufl.cise.plcsp23.Scanner.State.HAVE_LT;
                            nextChar();
                        }
                        case '>' -> {
                            state = edu.ufl.cise.plcsp23.Scanner.State.HAVE_GT;
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
                            state = edu.ufl.cise.plcsp23.Scanner.State.HAVE_EQUAL;
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
                            state = edu.ufl.cise.plcsp23.Scanner.State.HAVE_OR;
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
                            state = edu.ufl.cise.plcsp23.Scanner.State.HAVE_EXP;
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

                        //zero num literal
                        case '0' -> {
                            nextChar();
                            return new NumLitToken(IToken.Kind.NUM_LIT, tokenStart, 1, line, column, inputChars);
                        }

                        //num literals
                        case '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {   //nonzero digit
                            state = State.IN_NUM_LIT;             
                        }

                        //idents or reserved words
                        default -> {
                            if (isIdentStart(ch)) {
                                state = State.IN_IDENT;
                                nextChar();
                            } else error("illegal char with ascii value: " + (int) ch);
                        }
                    }
                }

                case IN_IDENT -> {
                }
                case IN_STRING_LIT -> {
                }
                case IN_NUM_LIT ->{
                    length = 0;
                    while (isDigit(ch) == true){
                            length++;
                            nextChar();
                        }
                    // throws the lexical exception for the numLitTooBig (maybe later use .getValue and actaully find if the value is too big..?)
                        if (length > 20 )
                        error("Error");
                    }
                    //check commit changes
                        state = state.START;
                            return new NumLitToken(IToken.Kind.NUM_LIT, tokenStart, length , line, column, inputChars);

                    }
                }
                default -> {
                    throw new UnsupportedOperationException("Bug in the Scanner");
                }

            }
        }
    }

    //EOF Scanner.Java
}



