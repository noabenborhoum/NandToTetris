import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CodeWriter {
    private final BufferedWriter writer;
    private String fileName = "";
    private int labelCounter = 0;

    public CodeWriter(File file) throws IOException {
        this.writer = new BufferedWriter(new FileWriter(file));
        this.fileName = file.getName();
    }

    public void setFileName(String fileName) {
        this.fileName = fileName.substring(0, fileName.lastIndexOf('.'));
    }

    public void writeArithmetic(String command) throws IOException {
        if(command.equals("add")) {
            arithmeticFirstTemplate();
            this.writer.write( "M=M+D\n");
        }

        else if(command.equals("sub")) {
            arithmeticFirstTemplate();
            this.writer.write("M=M-D\n");
        }

        else if(command.equals("and")) {;
            arithmeticFirstTemplate();
            this.writer.write("M=M&D\n");
        }

        else if(command.equals("or")) {
            arithmeticFirstTemplate();
            this.writer.write("M=M|D\n");
        }

        else if(command.equals("gt")) {
            this.writer.write(arithmeticTemplate("JLE"));
            this.labelCounter++;
        }

        else if(command.equals("lt")) {
            this.writer.write(arithmeticTemplate("JGE"));
            this.labelCounter++;
        }

        else if(command.equals("eq")) {
            this.writer.write(arithmeticTemplate("JNE"));
            this.labelCounter++;
        }

        else if(command.equals("not")) {
            this.writer.write("@SP\nA=M-1\nM=!M\n");
        }

        else if(command.equals("neg")) {
            this.writer.write("D=0\n@SP\nA=M-1\nM=D-M\n");
        }
    }

    public void writePushPop(String command, String segment, int index) throws IOException {
        if (command.equals("C_PUSH")) {
            if (segment.equals("constant")) {
                this.writer.write("@" + index + "\nD=A\n" + pushDRegStack());
            }
            if (segment.equals("local")){
                this.writer.write("@LCL\n" + "D=M\n" + "@" + index + "\nA=D+A\n" + "D=M\n" + pushDRegStack());
            }
            if (segment.equals("argument")){
                this.writer.write("@ARG\n" + "D=M\n" + "@" + index + "\n" + "A=D+A\n" + "D=M\n" + pushDRegStack());
            }
            if (segment.equals("this")){
                this.writer.write("@THIS\n" + "D=M\n" + "@" + index + "\n" + "A=D+A\n" + "D=M\n" + pushDRegStack());
            }
            if (segment.equals("that")){
                this.writer.write("@THAT\n" + "D=M\n" + "@" + index + "\n" + "A=D+A\n" + "D=M\n" + pushDRegStack());
            }
            if(segment.equals("temp")) {
                this.writer.write("@R" + (index+5) + "\nD=M\n" + pushDRegStack());
            }
            if (segment.equals("pointer")) {
                if (index == 0)
                    this.writer.write("@THIS\n" + "D=M\n" + pushDRegStack());
                if(index == 1)
                    this.writer.write("@THAT\n" + "D=M\n" + pushDRegStack());
            }
            if (segment.equals("static")) {
                this.writer.write("@" + this.fileName + "." + index + "\nD=M\n" + pushDRegStack());
            }
        }
        if (command.equals("C_POP")) {
            if (segment.equals("local")) {
               this.writer.write("@LCL\nD=M\n@" + index + "\nD=D+A\n" + popStackDReg());
            }
            if(segment.equals("argument")) {
                this.writer.write("@ARG\nD=M\n@" + index + "\nD=D+A\n" + popStackDReg());
            }
            if (segment.equals("this")){
                this.writer.write("@THIS\nD=M\n@" + index + "\nD=D+A\n" + popStackDReg());
            }
            if (segment.equals("that")){
                this.writer.write("@THAT\nD=M\n@" + index + "\nD=D+A\n" + popStackDReg());
            }
            if(segment.equals("temp")){
                this.writer.write("@SP\nAM=M-1\nD=M\n@R" + (index + 5) +"\nM=D\n");
            }
            if (segment.equals("pointer")) {
                if(index == 0)
                    this.writer.write("@THIS\nD=A\n" + popStackDReg());
                if(index == 1)
                    this.writer.write("@THAT\nD=A\n" + popStackDReg());
            }
            else if (segment.equals("static")) {
                this.writer.write("\n@SP\nAM=M-1\nD=M\n@" + this.fileName + "." + index + "\nM=D\n");
            }
        }
    }
    public void writeInit() throws IOException {
        this.writer.write("@256\n" + "D=A\n" + "@SP\n" + "M=D\n");
        writeCall("Sys.init",0);
    }
    public void writeLabel (String label) throws IOException {
        this.writer.write("(" + label + ")\n");
    }
    public void writeGoto(String label) throws IOException {
        this.writer.write("@" + label + "\n0;JMP\n");
    }
    public void writeIf(String label) throws IOException {
        this.writer.write("@SP\nAM=M-1\nD=M\n");
        this.writer.write("@" + label + "\nD;JNE\n");
    }
    public void writeFunction(String functionName, int nVars) throws IOException {
        writeLabel(functionName);
        for (int i = 0; i < nVars; i++) {
            this.writer.write("@LCL\nD=M\n@" + i + "\nA=D+A\nM=0\n@SP\nM=M+1\n");
        }
    }
    public void writeCall(String functionName, int nArgs) throws IOException {
        this.writer.write("@" + functionName + "$ret." + this.labelCounter + "\nD=A\n" + pushDRegStack());
        this.writer.write("@LCL\nD=M\n" + pushDRegStack());
        this.writer.write("@ARG\nD=M\n" + pushDRegStack());
        this.writer.write("@THIS\nD=M\n" + pushDRegStack());
        this.writer.write("@THAT\nD=M\n" + pushDRegStack());
        this.writer.write("@SP\nD=M\n@5\nD=D-A\n@" + nArgs + "\nD=D-A\n@ARG\nM=D\n");
        this.writer.write("@SP\nD=M\n@LCL\nM=D\n");
        writeGoto(functionName);
        writeLabel(functionName + "$ret." + this.labelCounter);
        this.labelCounter++;
    }
    public void writeReturn() throws IOException {
            this.writer.write("@LCL\nD=M\n@R15\nM=D\n");
            this.writer.write("@R15\nD=M\n@5\nA=D-A\nD=M\n@R14\nM=D\n");
            this.writer.write("@SP\nAM=M-1\nD=M\n@ARG\nA=M\nM=D\n");
            this.writer.write("@ARG\nD=M+1\n@SP\nM=D\n");
            this.writer.write("@R15\nAM=M-1\nD=M\n@THAT\nM=D\n");
            this.writer.write("@R15\nAM=M-1\nD=M\n@THIS\nM=D\n");
            this.writer.write("@R15\nAM=M-1\nD=M\n@ARG\nM=D\n");
            this.writer.write("@R15\nAM=M-1\nD=M\n@LCL\nM=D\n");
            this.writer.write("@R14\nA=M\n0;JMP\n");
    }

    public void close() throws IOException {
        this.writer.close();
    }

    private String popStackDReg() {
        return "@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n";
    }

    private String pushDRegStack() {
        return "@SP\n" + "A=M\n" + "M=D\n" + "@SP\n" + "M=M+1\n";
    }

    private void arithmeticFirstTemplate() throws IOException {
        this.writer.write("@SP\nAM=M-1\nD=M\nA=A-1\n");
    }

    private String arithmeticTemplate(String type) {
        return  "@SP\nAM=M-1\nD=M\nA=A-1\nD=M-D\n@FALSE_" + this.labelCounter +
                "\nD;" + type + "\n@SP\nA=M-1\nM=-1\n@CONTINUE_" + this.labelCounter + "\n0;JMP\n(FALSE_" + this.labelCounter + ")\n@SP\nA=M-1\nM=0\n(CONTINUE_" + this.labelCounter + ")\n";
    }
}