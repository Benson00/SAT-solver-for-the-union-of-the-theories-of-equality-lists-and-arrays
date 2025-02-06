# Implementation of a SAT solver for the union of the theories of equality, lists, and arrays

Prototype solver that determines the satisfiability of a set of literals in the union of the quantifier-free fragments of three theories: 

- equality with free symbols
- non-empty possibly cyclic lists
- arrays without extensionality.

<p>
The heart of the solver is the congruence closure algorithm on DAG’s for the satisfiability of a set of equalities and disequalities in the quantifier-free fragment of the theory of equality. 
</p>

### Syntax  
Formulas in input are in DNF. The syntax accepted by this solver:  
- `-` represents negation (not)  
- `&` represents conjunction (∧)  
- `|` represents disjunction (∨)  
- `=` represents equality (=)  
- `!` represents inequality (!=)  
- `forall` represents (∀)  
- `exists` represents (∃)  
- The quantified variable is enclosed in square brackets: `forall[x]`.  
- The scope of the quantifier is represented by curly braces: `forall[x]{}`.  
- `T` represents True  

