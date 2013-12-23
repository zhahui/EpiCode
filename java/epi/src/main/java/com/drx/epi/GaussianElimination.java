package com.drx.epi;


import static com.drx.epi.utils.Utils.copy;
import static com.drx.epi.utils.Utils.simplePrint;
import static com.drx.epi.utils.Utils.swap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class GaussianElimination {
	// @include
	static boolean[] Gaussian_elimination(List<List<Boolean>> A, boolean[] y) {

		List<List<Boolean>> B = copy(A);
		for (int i = 0; i < B.size(); ++i) {
				B.get(i).add(y[i]);
		}

		for (int i = 0; i < B.size(); ++i) {
			// Find the coefficient starting with 1.
			int idx = i;
			for (int j = i + 1; j < B.size(); ++j) {
				if (B.get(j).get(i)) {
					idx = j;
					break;
				}
			}
			swap(B, i, idx);

			// Perform elimination except i-th row.
			if (B.get(i).get(i)) {
				Eliminate_rows(B, i, i);
			}
		}

		for (int i = B.size() - 1; i >= 0; --i) {
			if (B.get(i).get(i) == false) {
				boolean have_coefficient = false;
				for (int j = i + 1; j < A.size(); ++j) {
					if (B.get(i).get(j)) {
						Eliminate_rows(B, i, j);
						have_coefficient = true;
						swap(B, i, j); // row permutation.
						break;
					}
				}

				if (!have_coefficient && B.get(i).get(B.get(i).size() - 1)) {
					System.out.println("No solution");
					return new boolean[0];
				}
			}
		}

		boolean[] x = new boolean[B.size()];
		for (int i = 0; i < B.size(); ++i) {
			x[i] = B.get(i).get(B.get(i).size() - 1);
		}
		return x;
	}

	static void Eliminate_rows(List<List<Boolean>> B, int i, int j) {
		// Use B[i] to eliminate other rows' entry j.
		for (int a = 0; a < B.size(); ++a) {
			if (i != a && B.get(a).get(j)) {
				for (int b = 0; b < B.get(i).size(); ++b) {
					boolean set = B.get(a).get(b) ^ B.get(i).get(b);
					B.get(a).set(b, set);
				}
			}
		}
	}
	// @exclude

	static boolean check_answer_with_solution(List<List<Boolean>> A,
			boolean[] b, boolean[] x) {
		for (int i = 0; i < A.size(); ++i) {
			boolean res = A.get(i).get(0) && x[0];
			for (int j = 1; j < A.get(i).size(); ++j) {
				res = res ^ (A.get(i).get(j) && x[j]);
			}

			if (res != b[i]) {
				return false;
			}
		}
		return true;
	}

	static boolean check_answer_no_solution(List<List<Boolean>> A, boolean[] b) {
		// Generate all possible combinations of x to test
		// there is no solution actually.
		for (int val = 0; val < (1 << b.length); ++val) {
			boolean[] x = new boolean[b.length];
			int temp = val;
			for (int i = 0; i < b.length; ++i) {
				x[i] = (temp & 1) > 0;
				temp >>= 1;
			}

			/*
			 * simplePrint(x); System.out.println();
			 */

			assert check_answer_with_solution(A, b, x) == false;
		}
		return true;
	}

	static List<List<Boolean>> rand_matrix(int m, int n) {
		Random gen = new Random();
		
		List<List<Boolean>> A = new ArrayList<List<Boolean>>(m);
		List<Boolean> row;
		for (int i = 1; i <= m; i++) {
			row = new ArrayList<Boolean>(n);
			
			for (int j = 1; j <= n; j++) {
				row.add(gen.nextBoolean());
			}
			
			A.add(row);
		}
		
		return A;
	}

	static boolean[] rand_vec(int n) {
		boolean[] b = new boolean[n];
		Random gen = new Random();
		
		for (int i = 0; i < n; ++i) {
			b[i] = gen.nextBoolean();
		}
		
		return b;
	}

	public static void main(String[] args) {
		Random gen = new Random();

		// Predefined tests.
		List<List<Boolean>> A = new ArrayList<List<Boolean>>(4);
		// java.util.Arrays.ArrayList<T> used in java.util.Arrays.asList(T...)
		// doesn't support adding a new element, so we'll wrap it with
		// java.util.ArrayList<E> 
		A.add(new ArrayList<Boolean>(Arrays.asList(false, false, false, true)));
		A.add(new ArrayList<Boolean>(Arrays.asList(false, false, false, true)));
		A.add(new ArrayList<Boolean>(Arrays.asList(false, true, true, true)));
		A.add(new ArrayList<Boolean>(Arrays.asList(true, false, false, false)));
		
		boolean[] b = { true, true, false, true };

		boolean[] x = Gaussian_elimination(A, b);

		simplePrint(x);
		System.out.println();

		if (x.length == 0) { // no solution
			assert check_answer_no_solution(A, b);
		} else { // solution
			assert check_answer_with_solution(A, b, x);
		}

		// Perform random tests below
		for (int times = 0; times < 10000; ++times) {
			int n;
			if (args.length == 1) {
				n = Integer.valueOf(args[0]);
				System.out.println("n = " + n);
			} else {
				n = gen.nextInt(16) + 1;
			}

			A = rand_matrix(n, n);

			b = rand_vec(n);

			x = Gaussian_elimination(A, b);
			System.out.println("n = " + n);
			System.out.println("A = ");

			for (List<Boolean> row : A) {
				simplePrint(row);
				System.out.println();
			}
			
			System.out.println("b = ");
			simplePrint(b);
			System.out.println();
			System.out.println();

			if (x.length == 0) { // no solution
				assert check_answer_no_solution(A, b);
			} else { // solution
				System.out.println("x = ");
				simplePrint(x);
				System.out.println();
				System.out.println();
				assert check_answer_with_solution(A, b, x);
			}
		}
	}
}