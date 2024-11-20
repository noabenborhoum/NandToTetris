import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

public class Parser {
    private HashMap<Integer, String> finalCommands;
    private int counter;

    public Parser(File file) throws IOException {
        this.finalCommands = new HashMap<>();
        int lineCounter = 0;
        Scanner fileScanner = new Scanner(file);
        while (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();
            if (!line.isEmpty() && !(line.trim().indexOf("//") == 0)) {
                String newLine = line;
                int index = newLine.trim().indexOf("//");
                if (index != -1)
                    newLine = newLine.substring(0, index);
                if (!newLine.isEmpty()) {
                    this.finalCommands.put(lineCounter, newLine);
                    lineCounter++;
                }
            }
        }
        fileScanner.close();
    }

    public boolean hasMoreLines() {
        return counter < finalCommands.size();
    }

    public void advance() {
        if (!hasMoreLines())
            throw new Error("End of file");
        counter++;
        while (counter + 1 < finalCommands.size() &&
                (finalCommands.get(counter + 1).isEmpty() || finalCommands.get(counter + 1).indexOf("//") == 0)) {
            counter++;
        }
    }

    public String instructionType() {
        if (!hasMoreLines())
            return "";
        String command = this.finalCommands.get(counter).trim();
        String[] parts = command.split("\\s+");
        if (parts[0].equals("pop"))
            return "C_POP";
        else if (parts[0].equals("label"))
            return "C_LABEL";
        else if (parts[0].equals("goto"))
            return "C_GOTO";
        else if (parts[0].equals("if-goto"))
            return "C_IF";
        else if (parts[0].equals("function"))
            return "C_FUNCTION";
        else if (parts[0].equals("call"))
            return "C_CALL";
        else if (parts[0].equals("push"))
            return "C_PUSH";
        else if (parts[0].equals("add") || parts[0].equals("sub") || parts[0].equals("neg") || parts[0].equals("eq")
                || parts[0].equals("gt")
                || parts[0].equals("lt") || parts[0].equals("and") || parts[0].equals("or") || parts[0].equals("not"))
            return "C_ARITHMETIC";
        else
            return "C_RETURN";
    }

    public String arg1() {
        String command = this.finalCommands.get(counter);
        if (instructionType().equals("C_ARITHMETIC"))
            return command.trim();
        else if (instructionType().equals("C_RETURN"))
            return "";
        else {
            String[] commandTemp = command.split(" ");
            return commandTemp[1].trim();
        }

    }

    public int arg2() {
        String command = this.finalCommands.get(counter);
        if (instructionType().equals("C_POP") || instructionType().equals("C_PUSH")
                || instructionType().equals("C_CALL") || instructionType().equals("C_FUNCTION")) {
            String[] commandTemp = command.split(" ");
            return Integer.parseInt(commandTemp[2].trim());
        }
        return 0;
    }
}