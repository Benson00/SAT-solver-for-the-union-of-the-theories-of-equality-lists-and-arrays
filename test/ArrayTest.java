import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import Solver.ArraySolver;

import Solver.Solver;

public class ArrayTest {
    public static void main(String[] args) {
        // Example formulas for testing

        String filePath = "test\\input\\array.txt"; 
        String outputFilePath = "test\\output\\array2.txt"; 

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
                testCongruenceClosure(formula, writer);
            }
            
        }catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
        

    }
    
    public static void testCongruenceClosure(String formula, BufferedWriter writer) throws IOException {
        // Measure execution time
        long startTime = System.nanoTime();
        
        // Track memory usage before executing the congruence closure algorithm
        Runtime runtime = Runtime.getRuntime();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        
        // Call your congruence closure function
        boolean solution = solveCongruenceClosureA(formula);
        
        // Measure memory usage after execution
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = memoryAfter - memoryBefore;
        
        // Measure execution time
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        
        // Output results
        System.out.println("Formula: " + formula);
        System.out.println("Execution time: " + duration / 1_000_000.0 + " ms");
        System.out.println("Memory used: " + memoryUsed / 1024.0 / 1024.0 + " MB");

        writer.write("Formula: " + formula);
        writer.newLine();
        System.out.println("\n\n");
        if (solution) {
            writer.write("SAT");
        }else{
            writer.write("UNSAT");
        }
        writer.newLine();
        writer.write("Execution time: " + duration / 1_000_000.0 + " ms");
        writer.newLine();
        writer.write("Memory used: " + memoryUsed / 1024.0 / 1024.0 + " MB");
        writer.newLine();

    }
        
    


    public static boolean solveCongruenceClosureA(String formula) {
        Solver solver = new Solver();
        ArraySolver theory = new ArraySolver();
        theory.setForbiddenListHToTrue();
        solver.setTheory(theory);
        return solver.solve(formula);
    }
}
