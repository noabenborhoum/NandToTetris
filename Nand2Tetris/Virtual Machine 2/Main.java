import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        File inputFileOrDir = new File(args[0]);
        if (inputFileOrDir.isFile()) {
            // If arg[0] is a file, process it as usual
            if (inputFileOrDir.getName().endsWith(".vm")) {
                Parser fileParser = new Parser(inputFileOrDir);
                File outputFile = new File(inputFileOrDir.getAbsolutePath().substring(0, inputFileOrDir.getAbsolutePath().lastIndexOf(".")) + ".asm");
                CodeWriter writer = new CodeWriter(outputFile);
                if(inputFileOrDir.getName().startsWith("Sys"))
                    writer.writeInit();
                processSingleFile(fileParser,writer);
                writer.close();
            }
            else
                System.out.println("Not a valid .vm file");
        } else if (inputFileOrDir.isDirectory()) {
            // If arg[0] is a directory, process each .vm file in the directory
            File[] files = inputFileOrDir.listFiles();
            File outputFile = new File(inputFileOrDir.getAbsolutePath() + File.separator + inputFileOrDir.getName() + ".asm");
            CodeWriter writer = new CodeWriter(outputFile);
            if (files != null) {
                for (File file : files) {
                    if (file.getName().startsWith("Sys") & file.getName().endsWith(".vm") & file.isFile()) {
                        writer.writeInit();
                        writer.setFileName(file.getName());
                        Parser fileParser = new Parser(file);
                        processSingleFile(fileParser,writer);
                    }
                }
                for (File file : files) {
                    if (file.getName().endsWith(".vm") & file.isFile() & !file.getName().startsWith("Sys")) {
                        writer.setFileName(file.getName());
                        Parser fileParser = new Parser(file);
                        processSingleFile(fileParser,writer);
                    }
                }
                writer.close();
            }
        } else {
            System.out.println("Error, Invalid input");
        }
    }
    public static void processSingleFile(Parser fileParser , CodeWriter writer) throws IOException {
        while (fileParser.hasMoreLines()) {
            if (fileParser.instructionType().equals("C_ARITHMETIC"))
                writer.writeArithmetic(fileParser.arg1());
            else if (fileParser.instructionType().equals("C_POP") || fileParser.instructionType().equals("C_PUSH"))
                writer.writePushPop(fileParser.instructionType(), fileParser.arg1(), fileParser.arg2());
            else if (fileParser.instructionType().equals("C_LABEL"))
                writer.writeLabel(fileParser.arg1());
            else if (fileParser.instructionType().equals("C_GOTO"))
                writer.writeGoto(fileParser.arg1());
            else if (fileParser.instructionType().equals("C_IF"))
                writer.writeIf(fileParser.arg1());
            else if (fileParser.instructionType().equals("C_RETURN"))
                writer.writeReturn();
            else if (fileParser.instructionType().equals("C_FUNCTION"))
                writer.writeFunction(fileParser.arg1(),fileParser.arg2());
            else if (fileParser.instructionType().equals("C_CALL"))
                writer.writeCall(fileParser.arg1(),fileParser.arg2());
            fileParser.advance();
        }
    }
}

