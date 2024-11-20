import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class JackTokenizer {

    private static ArrayList<String> tokens; // list of the tokenized file
    private static final ArrayList<String> keyWords; // the possible keywords as seen in the lecture
    private static final String symbols; // the possible symbols as seen in the lecture
    static final String operations; // the possible operations as seen in the lecture
    private static int currentTokenPointer; // points to the current token we are on
    private static boolean First; // flag that tells if we advanced at least once
    private static String TokenType;
    private static String keyWordType;
    static char SymbolType;
    private static String IdentifierValue;
    private static String StringValue;
    private static int IntValue;

    static {
        keyWords = new ArrayList<>();
        keyWords.add("class");
        keyWords.add("constructor");
        keyWords.add("function");
        keyWords.add("method");
        keyWords.add("field");
        keyWords.add("static");
        keyWords.add("var");
        keyWords.add("int");
        keyWords.add("char");
        keyWords.add("boolean");
        keyWords.add("void");
        keyWords.add("true");
        keyWords.add("false");
        keyWords.add("null");
        keyWords.add("this");
        keyWords.add("do");
        keyWords.add("if");
        keyWords.add("else");
        keyWords.add("while");
        keyWords.add("return");
        keyWords.add("let");
        operations = "+-*/&|<>=";
        symbols = "{}()[].,;+-*/&|<>=-~";
    }

    public JackTokenizer(File file) throws FileNotFoundException {
        Scanner reader = new Scanner(new FileReader(file));
        String JackLine = "";
        while (reader.hasNextLine()) {
            String Line = reader.nextLine();
            while (Line.equals("") || hasComments(Line)) {
                if (hasComments(Line)) {
                    Line = removeComments(Line);
                }
                if (Line.trim().equals("")) {
                    if (reader.hasNextLine()) {
                        Line = reader.nextLine();
                    } else {
                        break;
                    }
                }
            }
            JackLine += Line.trim();
        }
            tokens = new ArrayList<>();

            while (JackLine.length() > 0) {
                JackLine = JackLine.trim();

            for (int i = 0; i < keyWords.size(); i++) {
                if (JackLine.startsWith(keyWords.get(i))) {
                    String keyword = keyWords.get(i);
                    tokens.add(keyword);
                    JackLine = JackLine.substring(keyword.length());
                }
            }

            if (symbols.contains(JackLine.substring(0, 1))) {
                char symbol = JackLine.charAt(0);
                tokens.add(Character.toString(symbol));
                JackLine = JackLine.substring(1);
            }

            else if (Character.isDigit(JackLine.charAt(0))) {
                String val = JackLine.substring(0, 1);
                JackLine = JackLine.substring(1);
                while (Character.isDigit(JackLine.charAt(0))) {
                    val += JackLine.substring(0, 1);
                    JackLine = JackLine.substring(1);
                }
                tokens.add(val);
            }

            else if (JackLine.charAt(0) == '\"') {
                JackLine = JackLine.substring(1);
                String str = "\"";
                while ((JackLine.charAt(0) != '\"')) {
                    str += JackLine.charAt(0);
                    JackLine = JackLine.substring(1);
                }
                str = str + "\"";
                tokens.add(str);
                JackLine = JackLine.substring(1);
            }

            else if (Character.isLetter(JackLine.charAt(0)) || (JackLine.charAt(0) == '_')) {
                String identifier = JackLine.substring(0, 1);
                JackLine = JackLine.substring(1);
                while ((Character.isLetter(JackLine.charAt(0))) || (JackLine.charAt(0) == '_')) {
                    identifier += JackLine.substring(0, 1);
                    JackLine = JackLine.substring(1);
                }
                tokens.add(identifier);
            }
        }
        First = true;
        currentTokenPointer = 0;
    }
    public static boolean hasMoreTokens(){ return currentTokenPointer < tokens.size(); }

    public static void advance(){
        if(!hasMoreTokens()) {
            return;
        }
            if(!First)
                currentTokenPointer++;
            else
                First = false;
            String current = tokens.get(currentTokenPointer);
            if(keyWords.contains(current)){
                TokenType = "KEYWORD";
                keyWordType = current;
            }
            else if (symbols.contains(current)){
                SymbolType = current.charAt(0);
                TokenType = "SYMBOL";
            }
            else if (Character.isDigit(current.charAt(0))){
                IntValue = Integer.parseInt(current);
                TokenType = "INT_CONST";
            }
            else if (current.charAt(0) == '\"'){
                TokenType = "STRING_CONST";
                StringValue = current.substring(1, current.length() - 1);
            }
            else if ((Character.isLetter(current.charAt(0))) || (current.charAt(0) == '_')) {
                TokenType = "IDENTIFIER";
                IdentifierValue = current;
            }
        }
    public static String tokenType(){ return TokenType; }
    public static String keyWord(){ return keyWordType; }
    public static char symbol(){ return SymbolType; }
    public static String identifier(){ return IdentifierValue; }
    public static int intVal(){ return IntValue; }
    public static String stringVal(){ return StringValue; }
    private String removeComments(String str) {
        String NoComments = str;
        if (hasComments(str)) {
            int index;
            if (str.startsWith(" *")) {
                index = str.indexOf("*");
            } else if (str.contains("/*")) {
                index = str.indexOf("/*");
            } else {
                index = str.indexOf("//");
            }
            NoComments = str.substring(0, index).trim();
        }
        return NoComments;
    }
    private boolean hasComments(String str) { return str.contains("//") || str.contains("/*") || str.startsWith(" *"); }
    public boolean isOperation() {
        for (int i = 0; i < operations.length(); i++) {
            if (operations.charAt(i) == SymbolType) {
                return true;
            }
        }
        return false;
    }
    public void decrementPointer() {
        if (currentTokenPointer > 0) {
            currentTokenPointer--;
        }
    }
}
