import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CompilationEngine {

    private final FileWriter writer;
    private final JackTokenizer tokens;
    private boolean first; // check if we in the same routine, or we moved to new routine

    public CompilationEngine(File in, File outFile) throws IOException {
        writer = new FileWriter(outFile);
        tokens = new JackTokenizer(in);
        first = true;
    }

    public void compileClass() throws IOException {
        tokens.advance();
        writer.write("<class>\n");
        writer.write("<keyword> class </keyword>\n");
        tokens.advance();
        writer.write("<identifier> " + tokens.identifier() + " </identifier>\n");
        tokens.advance();
        writer.write("<symbol> { </symbol>\n");
        compileClassVarDec();
        compileSubRoutine();
        writer.write("<symbol> } </symbol>\n");
        writer.write("</class>\n");
        writer.close();
    }

    public void compileClassVarDec() throws IOException {
        tokens.advance();
        while (tokens.keyWord().equals("static") || tokens.keyWord().equals("field")) {
            writer.write("<classVarDec>\n");
            writer.write("<keyword> " + tokens.keyWord() + " </keyword>\n");
            tokens.advance();
            if (tokens.tokenType().equals("IDENTIFIER")) {
                writer.write("<identifier> " + tokens.identifier() + " </identifier>\n");
            } else {
                writer.write("<keyword> " + tokens.keyWord() + " </keyword>\n");
            }
            tokens.advance();
            writer.write("<identifier> " + tokens.identifier() + " </identifier>\n");
            tokens.advance();
            if (tokens.symbol() == ',') {
                writer.write("<symbol> , </symbol>\n");
                tokens.advance();
                writer.write(("<identifier> " + tokens.identifier() + " </identifier>\n"));
                tokens.advance();
            }
            writer.write("<symbol> ; </symbol>\n");
            tokens.advance();
            writer.write("</classVarDec>\n");
        }
        if (tokens.keyWord().equals("function") || tokens.keyWord().equals("method")
                || tokens.keyWord().equals("constructor")) {
            tokens.decrementPointer();
            return;
        }
    }

    public void compileSubRoutine() throws IOException {
        boolean thereSubRoutines = false;
        tokens.advance();
        if (tokens.symbol() == '}' && tokens.tokenType().equals("SYMBOL")) {
            return;
        }
        if ((first) && (tokens.keyWord().equals("function") || tokens.keyWord().equals("method")
                || tokens.keyWord().equals("constructor"))) {
            first = false;
            writer.write("<subroutineDec>\n");
            thereSubRoutines = true;
        }
        if (tokens.keyWord().equals("function") || tokens.keyWord().equals("method")
                || tokens.keyWord().equals("constructor")) {
            thereSubRoutines = true;
            writer.write("<keyword> " + tokens.keyWord() + " </keyword>\n");
            tokens.advance();
        }
        if (tokens.tokenType().equals("IDENTIFIER")) {
            writer.write("<identifier> " + tokens.identifier() + " </identifier>\n");
            tokens.advance();
        }
        else if (tokens.tokenType().equals("KEYWORD")) {
            writer.write("<keyword> " + tokens.keyWord() + " </keyword>\n");
            tokens.advance();
        }
        if (tokens.tokenType().equals("IDENTIFIER")) {
            writer.write("<identifier> " + tokens.identifier() + " </identifier>\n");
            tokens.advance();
        }
        if (tokens.symbol() == '(') {
            writer.write("<symbol> ( </symbol>\n");
            writer.write("<parameterList>\n");
            compileParameterList();
            writer.write("</parameterList>\n");
            writer.write("<symbol> ) </symbol>\n");
        }
        compileSubroutineBody();
        if (thereSubRoutines) {
            writer.write("</subroutineBody>\n");
            writer.write("</subroutineDec>\n");
            first = true;
        }
        compileSubRoutine();
    }

    public void compileParameterList() throws IOException {
        tokens.advance();
            while (!(tokens.tokenType().equals("SYMBOL") && tokens.symbol() == ')')) {
                if (tokens.tokenType().equals("IDENTIFIER")) {
                    writer.write("<identifier> " + tokens.identifier() + " </identifier>\n");
                    tokens.advance();
                } else if (tokens.tokenType().equals("KEYWORD")) {
                    writer.write("<keyword> " + tokens.keyWord() + " </keyword>\n");
                    tokens.advance();
                } else if ((tokens.tokenType().equals("SYMBOL")) && (tokens.symbol() == ',')) {
                    writer.write("<symbol> , </symbol>\n");
                    tokens.advance();

                }
            }
    }

    public void compileSubroutineBody() throws IOException {
        tokens.advance();
        if (tokens.symbol() == '{') {
            writer.write("<subroutineBody>\n");
            writer.write("<symbol> { </symbol>\n");
            tokens.advance();
        }
        while (tokens.keyWord().equals("var") && (tokens.tokenType().equals("KEYWORD"))) {
            writer.write("<varDec>\n");
            tokens.decrementPointer();
            compileVarDec();
            writer.write("</varDec>\n");
        }
        writer.write("<statements>\n");
        compileStatements();
        writer.write("</statements>\n");
        writer.write("<symbol> " + tokens.symbol() + " </symbol>\n");
    }

    public void compileVarDec() throws IOException {
        tokens.advance();
        if (tokens.keyWord().equals("var") && (tokens.tokenType().equals("KEYWORD"))) {
            writer.write("<keyword> var </keyword>\n");
            tokens.advance();
        }
        if (tokens.tokenType().equals("IDENTIFIER")) {
            writer.write("<identifier> " + tokens.identifier() + " </identifier>\n");
            tokens.advance();
        } else if (tokens.tokenType().equals("KEYWORD")) {
            writer.write("<keyword> " + tokens.keyWord() + " </keyword>\n");
            tokens.advance();
        }
        if (tokens.tokenType().equals("IDENTIFIER")) {
            writer.write("<identifier> " + tokens.identifier() + " </identifier>\n");
            tokens.advance();
        }
        if ((tokens.tokenType().equals("SYMBOL")) && (tokens.symbol() == ',')) {
            writer.write("<symbol> , </symbol>\n");
            tokens.advance();
            writer.write(("<identifier> " + tokens.identifier() + " </identifier>\n"));
            tokens.advance();
        }
        if ((tokens.tokenType().equals("SYMBOL")) && (tokens.symbol() == ';')) {
            writer.write("<symbol> ; </symbol>\n");
            tokens.advance();

        }
    }

    public void compileStatements() throws IOException {
        if (tokens.symbol() == '}' && (tokens.tokenType().equals("SYMBOL"))) {
            return;
        } else if (tokens.keyWord().equals("do") && (tokens.tokenType().equals("KEYWORD"))) {
            writer.write("<doStatement>\n");
            compileDo();
            writer.write(("</doStatement>\n"));
        } else if (tokens.keyWord().equals("let") && (tokens.tokenType().equals("KEYWORD"))) {
            writer.write("<letStatement>\n");
            compileLet();
            writer.write(("</letStatement>\n"));
        } else if (tokens.keyWord().equals("if") && (tokens.tokenType().equals("KEYWORD"))) {
            writer.write("<ifStatement>\n");
            compileIf();
            writer.write(("</ifStatement>\n"));
        } else if (tokens.keyWord().equals("while") && (tokens.tokenType().equals("KEYWORD"))) {
            writer.write("<whileStatement>\n");
            compileWhile();
            writer.write(("</whileStatement>\n"));
        } else if (tokens.keyWord().equals("return") && (tokens.tokenType().equals("KEYWORD"))) {
            writer.write("<returnStatement>\n");
            compileReturn();
            writer.write(("</returnStatement>\n"));
        }
        tokens.advance();
        compileStatements();
    }

    public void compileDo() throws IOException {
        if (tokens.keyWord().equals("do")) {
            writer.write("<keyword> do </keyword>\n");
        }
        tokens.advance();
        writer.write("<identifier> " + tokens.identifier() + " </identifier>\n");
        tokens.advance();
        if ((tokens.tokenType().equals("SYMBOL")) && (tokens.symbol() == '.')) {
            writer.write("<symbol> " + tokens.symbol() + " </symbol>\n");
            tokens.advance();
            writer.write("<identifier> " + tokens.identifier() + " </identifier>\n");
            tokens.advance();
            writer.write("<symbol> " + tokens.symbol() + " </symbol>\n");
            writer.write("<expressionList>\n");
            compileExpressionList();
            writer.write("</expressionList>\n");
            tokens.advance();
            writer.write("<symbol> " + tokens.symbol() + " </symbol>\n");
        } else if ((tokens.tokenType().equals("SYMBOL")) && (tokens.symbol() == '(')) {
            writer.write("<symbol> " + tokens.symbol() + " </symbol>\n");
            writer.write("<expressionList>\n");
            compileExpressionList();
            writer.write("</expressionList>\n");
            tokens.advance();
            writer.write("<symbol> " + tokens.symbol() + " </symbol>\n");
        }
        tokens.advance();
        writer.write("<symbol> " + tokens.symbol() + " </symbol>\n");
    }

   /* private void compileCall() throws IOException {
        tokens.advance();
        output.write("<identifier> " + tokens.identifier() + " </identifier>\n");
        tokens.advance();
        if ((tokens.tokenType().equals("SYMBOL")) && (tokens.symbol() == '.')) {
            output.write("<symbol> " + tokens.symbol() + " </symbol>\n");
            tokens.advance();
            output.write("<identifier> " + tokens.identifier() + " </identifier>\n");
            tokens.advance();
            output.write("<symbol> " + tokens.symbol() + " </symbol>\n");
            output.write("<expressionList>\n");
            compileExpressionList();
            output.write("</expressionList>\n");
            tokens.advance();
            output.write("<symbol> " + tokens.symbol() + " </symbol>\n");
        } else if ((tokens.tokenType().equals("SYMBOL")) && (tokens.symbol() == '(')) {
            output.write("<symbol> " + tokens.symbol() + " </symbol>\n");
            output.write("<expressionList>\n");
            compileExpressionList();
            output.write("</expressionList>\n");
            tokens.advance();
            output.write("<symbol> " + tokens.symbol() + " </symbol>\n");
        }
    } */

    public void compileLet() throws IOException {
        writer.write("<keyword> " + tokens.keyWord() + " </keyword>\n");
        tokens.advance();
        writer.write("<identifier> " + tokens.identifier() + " </identifier>\n");
        tokens.advance();
        if ((tokens.tokenType().equals("SYMBOL")) && (tokens.symbol() == '[')) {
            writer.write("<symbol> " + tokens.symbol() + " </symbol>\n");
            compileExpression();
            tokens.advance();
            if ((tokens.tokenType().equals("SYMBOL")) && ((tokens.symbol() == ']'))) {
                writer.write("<symbol> " + tokens.symbol() + " </symbol>\n");
            }
            tokens.advance();
        }
        writer.write("<symbol> " + tokens.symbol() + " </symbol>\n");
        compileExpression();
        writer.write("<symbol> " + tokens.symbol() + " </symbol>\n");
        tokens.advance();
    }

    public void compileWhile() throws IOException {
        writer.write("<keyword> " + tokens.keyWord() + " </keyword>\n");
        tokens.advance();
        writer.write("<symbol> " + tokens.symbol() + " </symbol>\n");
        compileExpression();
        tokens.advance();
        writer.write("<symbol> " + tokens.symbol() + " </symbol>\n");
        tokens.advance();
        writer.write("<symbol> " + tokens.symbol() + " </symbol>\n");
        writer.write("<statements>\n");
        compileStatements();
        writer.write("</statements>\n");
        writer.write("<symbol> " + tokens.symbol() + " </symbol>\n");
    }

    public void compileReturn() throws IOException {
        writer.write("<keyword> return </keyword>\n");
        tokens.advance();
        if (!((tokens.tokenType().equals("SYMBOL") && tokens.symbol() == ';'))) {
            tokens.decrementPointer();
            compileExpression();
        }
        if (tokens.tokenType().equals("SYMBOL") && tokens.symbol() == ';') {
            writer.write("<symbol> ; </symbol>\n");
        }
    }

    public void compileIf() throws IOException {
        writer.write("<keyword> if </keyword>\n");
        tokens.advance();
        writer.write("<symbol> ( </symbol>\n");
        compileExpression();
        writer.write("<symbol> ) </symbol>\n");
        tokens.advance();
        writer.write("<symbol> { </symbol>\n");
        tokens.advance();
        writer.write("<statements>\n");
        compileStatements();
        writer.write("</statements>\n");
        writer.write("<symbol> } </symbol>\n");
        tokens.advance();
        if (tokens.tokenType().equals("KEYWORD") && tokens.keyWord().equals("else")) {
            writer.write("<keyword> else </keyword>\n");
            tokens.advance();
            writer.write("<symbol> { </symbol>\n");
            tokens.advance();
            writer.write("<statements>\n");
            compileStatements();
            writer.write("</statements>\n");
            writer.write("<symbol> } </symbol>\n");
        } else {
            tokens.decrementPointer();
        }
    }

    public void compileExpression() throws IOException {
        writer.write("<expression>\n");
        compileTerm();
        while (true) {
            tokens.advance();
            if (tokens.tokenType().equals("SYMBOL") && tokens.isOperation()) {
                if (tokens.symbol() == '<') {
                    writer.write("<symbol> &lt; </symbol>\n");
                } else if (tokens.symbol() == '>') {
                    writer.write("<symbol> &gt; </symbol>\n");
                } else if (tokens.symbol() == '&') {
                    writer.write("<symbol> &amp; </symbol>\n");
                } else {
                    writer.write("<symbol> " + tokens.symbol() + " </symbol>\n");
                }
                compileTerm();
            } else {
                tokens.decrementPointer();
                break;
            }
        }
        writer.write("</expression>\n");
    }

    public void compileTerm() throws IOException {
        writer.write("<term>\n");
        tokens.advance();
        if (tokens.tokenType().equals("IDENTIFIER")) {
            String prev = tokens.identifier();
            tokens.advance();
            if (tokens.tokenType().equals("SYMBOL") && tokens.symbol() == '[') {
                writer.write("<identifier> " + prev + " </identifier>\n");
                writer.write("<symbol> [ </symbol>\n");
                compileExpression();
                tokens.advance();
                writer.write("<symbol> ] </symbol>\n");
            } else if (tokens.tokenType().equals("SYMBOL") && (tokens.symbol() == '(' || tokens.symbol() == '.')) {
                tokens.decrementPointer();
                tokens.decrementPointer();
                tokens.advance();
                writer.write("<identifier> " + tokens.identifier() + " </identifier>\n");
                tokens.advance();
                if ((tokens.tokenType().equals("SYMBOL")) && (tokens.symbol() == '.')) {
                    writer.write("<symbol> " + tokens.symbol() + " </symbol>\n");
                    tokens.advance();
                    writer.write("<identifier> " + tokens.identifier() + " </identifier>\n");
                    tokens.advance();
                    writer.write("<symbol> " + tokens.symbol() + " </symbol>\n");
                    writer.write("<expressionList>\n");
                    compileExpressionList();
                    writer.write("</expressionList>\n");
                    tokens.advance();
                    writer.write("<symbol> " + tokens.symbol() + " </symbol>\n");
                } else if ((tokens.tokenType().equals("SYMBOL")) && (tokens.symbol() == '(')) {
                    writer.write("<symbol> " + tokens.symbol() + " </symbol>\n");
                    writer.write("<expressionList>\n");
                    compileExpressionList();
                    writer.write("</expressionList>\n");
                    tokens.advance();
                    writer.write("<symbol> " + tokens.symbol() + " </symbol>\n");
                }
            } else {
                writer.write("<identifier> " + prev + " </identifier>\n");
                tokens.decrementPointer();
            }
        } else {
            if (tokens.tokenType().equals("INT_CONST")) {
                writer.write("<integerConstant> " + tokens.intVal() + " </integerConstant>\n");
            } else if (tokens.tokenType().equals("STRING_CONST")) {
                writer.write("<stringConstant> " + tokens.stringVal() + " </stringConstant>\n");
            } else if (tokens.tokenType().equals("KEYWORD")
                    && (tokens.keyWord().equals("this") || tokens.keyWord().equals("null")
                    || tokens.keyWord().equals("false") || tokens.keyWord().equals("true"))) {
                writer.write("<keyword> " + tokens.keyWord() + " </keyword>\n");
            } else if (tokens.tokenType().equals("SYMBOL") && tokens.symbol() == '(') {
                writer.write("<symbol> " + tokens.symbol() + " </symbol>\n");
                compileExpression();
                tokens.advance();
                writer.write("<symbol> " + tokens.symbol() + " </symbol>\n");
            } else if (tokens.tokenType().equals("SYMBOL") && (tokens.symbol() == '-' || tokens.symbol() == '~')) {
                writer.write("<symbol> " + tokens.symbol() + " </symbol>\n");
                compileTerm();
            }
        }
        writer.write("</term>\n");
    }

    public void compileExpressionList() throws IOException {
        tokens.advance();
        if (tokens.symbol() == ')' && tokens.tokenType().equals("SYMBOL")) {
            tokens.decrementPointer();
        } else {
            tokens.decrementPointer();
            compileExpression();
        }
        while (true) {
            tokens.advance();
            if (tokens.tokenType().equals("SYMBOL") && tokens.symbol() == ',') {
                try {
                    writer.write("<symbol> , </symbol>\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                compileExpression();
            } else {
                tokens.decrementPointer();
                break;
            }
        }
    }
}