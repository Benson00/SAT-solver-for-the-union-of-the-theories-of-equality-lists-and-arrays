# Implementation of a SAT solver for the union of the theories of equality, lists, and arrays

Prototype solver that determines the satisfiability of a set of literals in the union of the quantifier-free fragments of three theories: 

- equality with free symbols
- non-empty possibly cyclic lists
- arrays without extensionality.

<p>
The heart of the solver is the congruence closure algorithm on DAG’s for the satisfiability of a set of equalities and disequalities in the quantifier-free fragment of the theory of equality. 
</p>
