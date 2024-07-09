package tinycc.logic.solver.z3;

import com.microsoft.z3.Status;

import tinycc.logic.solver.SolverResult;

public class Z3Result implements SolverResult {

	private final Status status;
	private final String model;

	public Z3Result(Status status, String model) {
		this.status = status;
		this.model = model;
	}

	@Override
	public boolean isSatifiable() {
		return status.equals(Status.SATISFIABLE);
	}

	@Override
	public boolean isUnSatifiable() {
		return status.equals(Status.UNSATISFIABLE);
	}

	@Override
	public boolean isError() {
		return status.equals(Status.UNKNOWN);
	}

	@Override
	public String getModel() throws UnsupportedOperationException {
		if (model == null) {
			assert !isSatifiable();
			throw new UnsupportedOperationException("A model can only be computed for satisfiable formulas!");
		}
		return model.toString();
	}

	@Override
	public String toString() {
		if (model != null) {
			return status.toString() + "\nModel:\n" +  model + "\n";
		}

		return status.toString();
	}
}
