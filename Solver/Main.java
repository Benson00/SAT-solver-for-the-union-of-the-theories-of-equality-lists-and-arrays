package Solver;



public class Main {


    public static String prova(String formula){
        // Regex per rimuovere "forall[...]"
        formula = formula.replaceAll("forall\\[.*?\\]", "");
        // Regex per rimuovere "exists[...]"
        formula = formula.replaceAll("exists\\[.*?\\]", "");
        formula = formula.replaceAll("\\{", "").replaceAll("\\}", "");

            
        return formula;
    }

    public static void main(String[] args) {
        System.out.println(prova("forall[x]{exists[y]{f(x) = f(y)}}"));
    }
}
