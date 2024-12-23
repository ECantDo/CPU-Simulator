package Assembler;

public class BuildToSchematic {

    public static void main(String[] args) {
        String file_name = "src/testing";
        Build.build(file_name + ".as");
        Build.runBuildToSchem(file_name + ".bin");
    }
}
