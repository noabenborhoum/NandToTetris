import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Main {

    public static void main(String[] args) {
        File inputFileOrDir = new File(args[0]);
        if (inputFileOrDir.isFile()) {
            // If arg[0] is a file, process it as usual
            processFile(inputFileOrDir);
        } else if (inputFileOrDir.isDirectory()) {
            // If arg[0] is a directory, process each file in the directory
            File[] files = inputFileOrDir.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        processFile(file);
                    }
                }
            }
        } else {
            System.out.println("Error, Invalid input");
        }
    }
    public static void processFile(File inputFile) {
        SymbolTable symbolTable = new SymbolTable();
        int variableAddress = 16;
        int count = 0;
        Parser parser = new Parser(inputFile);
        StringBuilder output = new StringBuilder();
        while (parser.hasMoreLines()) {
            if (parser.instructionType() == INSTRUCTION_Type.L_INSTRUCTION) {
                if (!symbolTable.contains(parser.symbol())) {
                    symbolTable.addEntry(parser.symbol(), parser.counter - count);
                    count++;
                }
            }
            parser.advance();
        }

        parser.counter = 0;
        while (parser.hasMoreLines()) {
            INSTRUCTION_Type type = parser.instructionType();
            String command = "";

            if (type == INSTRUCTION_Type.L_INSTRUCTION) {
                if (!symbolTable.contains(parser.symbol())) {
                    symbolTable.addEntry(parser.symbol(), parser.counter + 1);
                }
            } else if (type == INSTRUCTION_Type.A_INSTRUCTION) {
                String symbol = parser.symbol();
                String value = "";

                if (!isNum(symbol)) {
                    if (!symbolTable.contains(symbol)) {
                        symbolTable.addEntry(symbol, variableAddress++);
                    }
                    value = intToBinary(symbolTable.getAddress(symbol), 15);
                } else {
                    value = intToBinary(Integer.parseInt(symbol), 15);
                }
                command = "0" + value;
            } else {
                command = "111" + Code.comp(parser.comp()) + Code.dest(parser.dest()) + Code.jump(parser.jump());
            }

            if (!command.isEmpty()) {
                output.append(command).append("\n");
            }

            parser.advance();
        }

        if (!inputFile.exists()) {
            System.out.println("Error");
        }

        String inputPath = inputFile.getAbsolutePath();
        String inputFileName = inputFile.getName();
        int extensionIndex = inputFileName.indexOf(".");
        String baseName = inputFileName.substring(0, extensionIndex);
        int nameIndex = inputPath.indexOf(inputFileName);
        String outputPath = inputPath.substring(0, nameIndex) + baseName + ".hack";

        File outputFile = new File(outputPath);
        try (PrintWriter pw = new PrintWriter(outputFile)) {
            pw.print(output.toString());
        } catch (FileNotFoundException e) {
            System.out.println("Error");
        }
    }
    public static boolean isNum(String str) {
        return str.matches("\\d+");
    }
    public static String intToBinary(int number, int padding) {
        return String.format("%" + padding + "s", Integer.toBinaryString(number)).replace(' ', '0');
    }
}
