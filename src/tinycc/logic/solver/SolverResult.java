package tinycc.logic.solver;

public interface SolverResult {

	public boolean isSatifiable();

	public boolean isUnSatifiable();

	public boolean isError();

	public String getModel() throws UnsupportedOperationException;
}
