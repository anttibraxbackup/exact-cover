# Exact Cover with Color

This project provides a generic Java solver for [exact cover](https://en.wikipedia.org/wiki/Exact_cover) with color
problems that implements the solver using [dancing links](https://en.wikipedia.org/wiki/Dancing_Links).

Most XCC solvers you find in the internet are faithful recreations of the
algorithm described by Donald Knuth in [The Art Of Computer Programming,
Volume 4B](https://en.wikipedia.org/wiki/The_Art_of_Computer_Programming#Volume_4B_%E2%80%93_Combinatorial_Algorithms,_Part_2) with all the issues that come with "scientific classroom
code." This implementation tries to provide an implementation that follows
well established good programming practises, is thoroughly documented,
as easy to understand as humanly possible with descriptive and human-readable
variable names, and accepts the fact that it is not necessarily always
possible (or even feasible) to represent the universe with an integer.

A fairly faithful implementation of the algorithm that uses Knuth's [array
based matrix](https://gitlab.com/antti.brax/exact-cover/-/blob/main/solver/src/main/java/fi/iki/asb/xcc/ReferenceXCC.java) is also provided for reference.

## Initializing Options

The main issue with the common XCC implementation is initialization of the
data structure. Even a fairly simple XCC problem easily contains thousands
of items and hundreds of options and loading up the options into the solver
can be tedious. This implementation attempts to solve the problem by
extracting the item initialization into s dedicated class: [OptionItemMapper](
https://gitlab.com/antti.brax/exact-cover/-/blob/main/solver/src/main/java/fi/iki/asb/xcc/OptionItemMapper.java).
The user adds options to the DLX implementation and the DLX implementation
queries the OptionItemMapper for the relevant items.

### Options

The implementation does not place any restrictions on the objects used to
represent options. They can be fully fledged Java domain objects that represent
the action being done when the option is included in the solution or Strings
or Integers if they are sufficient. The only requirement is that an option
can be mapped to items consistently.

For example, the sample implementation of the [N queens problem](
https://gitlab.com/antti.brax/exact-cover/-/blob/main/examples/src/main/java/fi/iki/asb/xcc/examples/queen)
solver defines the option as "placing a queen into a specific location on
the chess board" (e.g. "queen to d6"). The option item mapper receives the
option and calculates the row-, column- and diagonals that the placement
covers.

### Items

For primary items, the only restriction is that they implement the equals and hashCode
methods correctly. Secondary items must also implement the [SecondaryItem](
https://gitlab.com/antti.brax/exact-cover/-/blob/main/src/main/solver/java/fi/iki/asb/xcc/SecondaryItem.java) iterface and ensure that a possible color attribute is not included in equals
and hashCode. I.e. two items that represent the same constraint with different color
must always be equal.

## Show Me the Code

 * [XCC.java](https://gitlab.com/antti.brax/exact-cover/-/blob/main/src/main/java/fi/iki/asb/xcc/XCC.java): 
   A common interface for all XCC implementations.
 * [LinkedXCC.java](https://gitlab.com/antti.brax/exact-cover/-/blob/main/src/main/java/fi/iki/asb/xcc/LinkedXCC.java):
   Implementation that pointers for the doubly linked matrix. 
   Sample sudoku solver.
 * [Pentomino](https://gitlab.com/antti.brax/exact-cover/-/blob/main/examples/src/main/java/fi/iki/asb/xcc/examples/pentomino):
   Sample pentomino solver.
 * [N-queens](https://gitlab.com/antti.brax/exact-cover/-/blob/main/examples/src/main/java/fi/iki/asb/xcc/examples/queen):
   Sample N-queens solver.
 * [Words](https://gitlab.com/antti.brax/exact-cover/-/blob/main/examples/src/main/java/fi/iki/asb/xcc/examples/words):
   Sample 4x4 word box solver (in Finnish).

## License

I publish this code into the [public domain](https://gitlab.com/antti.brax/exact-cover/-/blob/main/LICENSE).
Feel free to copy, modify, break and fix it as you see fit. You are under no
obligation to share your work to anyone (although I would of course always be
curious to know how my code can be improved).

## Contact

Any questions? E-mail: asb (a) iki.fi
