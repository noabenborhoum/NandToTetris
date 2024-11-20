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
        if (parts[0].equals("push"))
            return "C_PUSH";
        if (parts[0].equals("add") || parts[0].equals("sub") || parts[0].equals("neg") || parts[0].equals("eq") || parts[0].equals("gt")
                || parts[0].equals("lt") || parts[0].equals("and") || parts[0].equals("or") || parts[0].equals("not"))
            return "C_ARITHMETIC";
        else
            return "C_RETURN";
    }
    public String arg1(){
        String command = this.finalCommands.get(counter);
        String finalS = "";
        if (instructionType().equals("C_ARITHMETIC"))
            return command.trim();
        if(instructionType().equals("C_RETURN"))
            return "";
        if(instructionType().equals("C_POP")) {
            finalS = command.substring(4);
            if (finalS.indexOf(' ') != -1)
            return finalS.substring(0,finalS.indexOf(' ')).trim();
            else
                return finalS.trim();
        }
        if(instructionType().equals("C_PUSH")) {
            finalS = command.substring(5);
            if (finalS.indexOf(' ') != -1)
            return finalS.substring(0,finalS.indexOf(' ')).trim();
            else
                return finalS.trim();
        }
    return "";
    }
    public int arg2() {
        String command = this.finalCommands.get(counter);
        if (instructionType().equals("C_ARITHMETIC"))
            return 0;
        if (instructionType().equals("C_RETURN"))
            return 0;
        if (instructionType().equals("C_POP") || instructionType().equals("C_PUSH") || instructionType().equals("C_CALL") || instructionType().equals("C_FUNCTION")) {
            String [] commandTemp = command.split(" ");
            return Integer.parseInt(commandTemp[2]);
        }
        return 0;
    }
}
