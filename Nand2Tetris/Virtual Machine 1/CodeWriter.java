import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CodeWriter {
    private BufferedWriter writer;
    private int jump;
    private String fileName;

    public CodeWriter(File file) throws IOException {
        this.writer = new BufferedWriter(new FileWriter(file));
        this.jump = 0;
        this.fileName = file.getName();
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

        else if(command.equals("and")) {
            arithmeticFirstTemplate();
            this.writer.write("M=M&D\n");
        }

        else if(command.equals("or")) {
            arithmeticFirstTemplate();
            this.writer.write("M=M|D\n");
        }

        else if(command.equals("gt")) {
            this.writer.write(arithmeticTemplate("JLE"));
        }

        else if(command.equals("lt")) {
            this.writer.write(arithmeticTemplate("JGE"));
        }

        else if(command.equals("eq")) {
            this.writer.write(arithmeticTemplate("JNE"));
        }

        else if(command.equals("not")) {
            this.writer.write("@SP\nA=M-1\nM=!M\n");
        }

        else if(command.equals("neg")) {
            this.writer.write("D=0\n@SP\nA=M-1\nM=D-M\n");
        }
    }

    public void writePushPop(String command, String segment, int index) throws IOException {
        String segmentPointer = "";
        int newIndex = index;
        if (segment.equals("static")) {
            segmentPointer = "R16";
            newIndex += 16;
        }
        if (segment.equals("temp")) {
            segmentPointer = "R5";
            newIndex += 5;
        }
        if (segment.equals("pointer") && index == 0) {
            segmentPointer = "THIS";
            newIndex += 3;
        }
        if (segment.equals("pointer") && index == 1){
            segmentPointer = "THAT";
        newIndex += 3;
        }
        if (segment.equals("local"))
            segmentPointer = "LCL";
        if (segment.equals("argument"))
            segmentPointer = "ARG";
        if (segment.equals("this"))
            segmentPointer = "THIS";
        if (segment.equals("that"))
            segmentPointer = "THAT";

        if (command.equals("C_POP")) {
            if (segment.equals("constant")) {
                this.writer.write("@" + newIndex + "\n");
            } else if (segment.equals("pointer") || segment.equals("temp")) {
                this.writer.write("@" + segmentPointer + "\n" + "@" + newIndex + "\n");
            } else if (segment.equals("local") || segment.equals("argument") || segment.equals("this") || segment.equals("that")) {
                this.writer.write("@" + segmentPointer + "\n" + "D=M\n" + "@" + newIndex + "\n" + "A=D+A\n");
            } else if (segment.equals("static")) {
                String newName = this.fileName.split("\\.asm")[0];
                this.writer.write("@" + newName + "." + newIndex + "\n");
            }
            this.writer.write("D=A\n" + "@R13\n" + "M=D\n");
            this.writer.write(popStackDReg());
            this.writer.write("@R13\n" + "A=M\n" + "M=D\n");
        }
        if (command.equals("C_PUSH")) {
            if (segment.equals("constant")) {
                this.writer.write("@" + newIndex + "\n");
                this.writer.write("D=A\n");
            }
            if (segment.equals("pointer") || segment.equals("temp")) {
                this.writer.write("@" + segmentPointer + "\n" + "@R" + newIndex + "\n");
                this.writer.write("D=M\n");
            }
            if (segment.equals("local") || segment.equals("argument") || segment.equals("this") || segment.equals("that")) {
                this.writer.write("@" + segmentPointer + "\n" + "D=M\n" + "@" + newIndex + "\n" + "A=D+A\n");
                this.writer.write("D=M\n");
            }
            if (segment.equals("static")) {
                String newName = this.fileName.split("\\.asm")[0];
                this.writer.write("@" + newName + "." + newIndex + "\n");
                this.writer.write("D=M\n");
            }
            this.writer.write(pushDRegStack());
        }

    }

    public void close() throws IOException {
        this.writer.close();
    }

    private String popStackDReg() {
        return "@SP\n" + "M=M-1\n" + "A=M\n" + "D=M\n";
    }

    private String pushDRegStack() {
        return "@SP\n" + "A=M\n" + "M=D\n" + "@SP\n" + "M=M+1\n";
    }

    private void arithmeticFirstTemplate() throws IOException {
        this.writer.write("@SP\n" +"AM=M-1\n" +"D=M\n" +"A=A-1\n");
        }

    private String arithmeticTemplate(String type) {
        String out = "@SP\n" + "AM=M-1\n" + "D=M\n" + "A=A-1\n" + "D=M-D\n" + "@FALSE" + this.jump + "\n" + "D;" + type + "\n" + "@SP\n" +
                "A=M-1\n" + "M=-1\n" + "@CONTINUE" + this.jump + "\n" + "0;JMP\n" + "(FALSE" + this.jump + ")\n" + "@SP\n" + "A=M-1\n" +
                "M=0\n" + "(CONTINUE" + this.jump + ")\n";
        this.jump++;
        return out;
    }
}
