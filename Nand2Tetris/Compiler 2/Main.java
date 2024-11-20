import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        String fileInName = args[0];
        File input = new File(fileInName);
        String fileOutPath = "";
        File out;
        if (input.isFile()) {
            if (input.getAbsolutePath().endsWith(".jack")) {
                fileOutPath = input.getAbsolutePath().substring(0, input.getAbsolutePath().lastIndexOf(".")) + ".xml";
                out = new File(fileOutPath);
                CompilationEngine compilationEngine = new CompilationEngine(input, out);
                compilationEngine.compileClass();
            } else
                System.out.println("Not a valid Jack file.");
        } else if (input.isDirectory()) {
            File[] jackFiles = input.listFiles();
            if (jackFiles.length != 0) {
                for (File f : jackFiles) {
                    if (f.getAbsolutePath().endsWith(".jack") && f.isFile()) {
                        fileOutPath = f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf(".")) + ".xml";
                        out = new File(fileOutPath);
                        CompilationEngine compilationEngine = new CompilationEngine(f, out);
                        compilationEngine.compileClass();
                    }
                }
            }
        }
    }
}
