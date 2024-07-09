package tinycc.logic.solver;

import tinycc.logic.Formula;

public interface SolverInterface {

	public SolverResult querySatisfiability(Formula f, Logic logic);
}
