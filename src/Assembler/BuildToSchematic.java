package Assembler;

public class BuildToSchematic {

    public static void main(String[] args) {
        String file_name = "src/Programs_V1_1/MandelbrotSet";
        Build.build(file_name + ".as");
        Build.runBuildToSchem(file_name + ".bin");
    }
}
