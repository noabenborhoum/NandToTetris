import java.io.IOException;
import java.util.HashMap;
import java.io.File;
import java.util.Scanner;

enum INSTRUCTION_Type {
    A_INSTRUCTION,
    C_INSTRUCTION,
    L_INSTRUCTION
}

public class Parser {
    private HashMap<Integer, String> finalCommands;
    public int counter;

    public Parser(File inputFile) {
        this.finalCommands = new HashMap<>();
        int lineCounter = 0;
        try {
            Scanner fileScanner = new Scanner(inputFile);
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (!line.isEmpty() && !(line.trim().indexOf("//") == 0)) {
                    String newLine = line.replaceAll("\\s+", "");
                    int index = newLine.trim().indexOf("//");
                    if (index != -1)
                        newLine = newLine.substring(0, index);
                    if (!newLine.isEmpty()) {
                        finalCommands.put(lineCounter, newLine);
                        lineCounter++;
                    }
                }
            }
            fileScanner.close();
        } catch (IOException e) {
            System.out.println("File does not exist");
        }
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


    public INSTRUCTION_Type instructionType() {
        String command = finalCommands.get(counter);
        if (command.indexOf('@') == 0)
            return INSTRUCTION_Type.A_INSTRUCTION;
        if (command.indexOf('(') == 0 && command.indexOf(')') == command.length()-1)
            return INSTRUCTION_Type.L_INSTRUCTION;
        else
            return INSTRUCTION_Type.C_INSTRUCTION;
    }

    public String symbol() {
        INSTRUCTION_Type instruction = instructionType();
        if (instruction == INSTRUCTION_Type.C_INSTRUCTION)
            return null;
        String command = finalCommands.get(counter);
        if (instruction == INSTRUCTION_Type.L_INSTRUCTION)
            return command.trim().substring(1, command.length() - 1); // Without the ()
        return command.trim().substring(1);
    }

    public String dest() {
        String command = finalCommands.get(counter);
        int equalsIndex = command.trim().indexOf('=');
        if (equalsIndex != -1)
            return command.trim().substring(0,equalsIndex);
        else
            return command.trim();
    }

    public String comp() {
        String command = finalCommands.get(counter);
        int equalsIndex = command.trim().indexOf('=');
        int semiColonIndex = command.trim().indexOf(';');
        if(semiColonIndex == -1) {
            semiColonIndex = command.trim().length();
            return command.trim().substring(equalsIndex + 1, semiColonIndex);
        }
        else
            return command.trim().substring(equalsIndex + 1, semiColonIndex);
    }

    public String jump() {
        String command = finalCommands.get(counter);
        int semiColonIndex = command.trim().indexOf(';');
        if(semiColonIndex != -1)
            return command.trim().substring(semiColonIndex + 1);
        else
            return "";
    }
}