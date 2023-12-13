# FIZK
Digital Signature scheme from the idea behind Zero Knowledge proofs for MPC-in-the-head and the Fish digital signature scheme.

The zero knowledge proof is and implementation for the ZKBoo protocol.

FIZK is an implementation of the Fish signature scheme and is made to showcase our understanding of the three papers:

- [Fish Signature Scheme](https://eprint.iacr.org/2017/279)
- [MPC-in-the-head](https://web.cs.ucla.edu/~rafail/PUBLIC/77.pdf)
- [ZKBoo](https://eprint.iacr.org/2016/163)

## Code overview
The code is split into four parts:

- `BooleanCircuit` contains the implementation of the Boolean circuit.
  - In the `BooleanCircuit\input` folder you will find the boolean circuits for SHA-256 and some smaller circuits for testing purposes. The circuits are encoded in the Bristol Fashion format.
- `Fizk` contains the implementation of the Fish signature scheme.
- `ZKBoo` contains the implementation of the ZKBoo protocol.
- `Util` contains some utility functions for conversions used across the project.
- `Test` contains the tests for the different parts of the implementation.


## Test
To run the code you can run the different tests in the test folder. The tests are made to showcase the different parts of the implementation.

