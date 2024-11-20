import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        File inputFileOrDir = new File(args[0]);
        if (inputFileOrDir.isFile()) {
            // If arg[0] is a file, process it as usual
            if (inputFileOrDir.getName().endsWith(".vm"))
                processSingleFile(inputFileOrDir);
            else
                System.out.println("Not a valid .vm file");
        } else if (inputFileOrDir.isDirectory()) {
            // If arg[0] is a directory, process each .vm file in the directory
            File[] files = inputFileOrDir.listFiles();
            File outputFile = new File(inputFileOrDir.getAbsolutePath() + File.separator + inputFileOrDir.getName() + ".asm");
            CodeWriter writer = new CodeWriter(outputFile);
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".vm") & file.isFile()) {
                        Parser fileParser = new Parser(file);
                        while (fileParser.hasMoreLines()) {
                            if (fileParser.instructionType().equals("C_ARITHMETIC"))
                                writer.writeArithmetic(fileParser.arg1());
                            if (fileParser.instructionType().equals("C_POP") || fileParser.instructionType().equals("C_PUSH")) {
                                writer.writePushPop(fileParser.instructionType(), fileParser.arg1(), fileParser.arg2());
                            }
                            fileParser.advance();
                        }
                    }
                }
                writer.close();
            }
        } else {
            System.out.println("Error, Invalid input");
        }
    }
    public static void processSingleFile(File newFile) throws IOException {
        Parser fileParser = new Parser(newFile);
        File outputFile = new File(newFile.getAbsolutePath().substring(0, newFile.getAbsolutePath().lastIndexOf(".")) + ".asm");
        CodeWriter writer = new CodeWriter(outputFile);

        while (fileParser.hasMoreLines()) {
            if (fileParser.instructionType().equals("C_ARITHMETIC"))
                writer.writeArithmetic(fileParser.arg1());
            if (fileParser.instructionType().equals("C_POP") || fileParser.instructionType().equals("C_PUSH")) {
                writer.writePushPop(fileParser.instructionType(), fileParser.arg1(), fileParser.arg2());
            }
            fileParser.advance();
        }
        writer.close();
    }
}

