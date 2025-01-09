import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import Solver.ListSolver;

public class ListTest {
    public static void main(String[] args) {

        ListSolver ESolver = new ListSolver();
        //ESolver.setForbiddenListHToFalse();

        String filePath = "test\\input\\list.txt"; 
        String outputFilePath = "test\\output\\list.txt"; 
        List<String> equalityTest = new ArrayList<String>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            System.out.println("Lines from the file:");
            for (String line : lines) {

                equalityTest.add(line.trim());
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            for (String formula : equalityTest) {
                writer.write("FORMULA: " + formula);
                writer.newLine();

                boolean solution = ESolver.solve(formula); 
                System.out.println("\n\n");
                if (solution) {
                    writer.write("SAT");
                }else{
                    writer.write("UNSAT");
                }
                writer.newLine();
                writer.newLine();
            }
            System.out.println("Results successfully written to file: " + outputFilePath);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}
