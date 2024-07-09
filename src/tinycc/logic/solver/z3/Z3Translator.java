package tinycc.logic.solver.z3;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.BoolSort;
import com.microsoft.z3.Context;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.IntSort;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

import tinycc.logic.BinaryOpFormula;
import tinycc.logic.BinaryOperator;
import tinycc.logic.BoolConst;
import tinycc.logic.Formula;
import tinycc.logic.IntConst;
import tinycc.logic.Type;
import tinycc.logic.UnaryOpFormula;
import tinycc.logic.Variable;
import tinycc.logic.solver.Logic;
import tinycc.logic.solver.SolverInterface;

public class Z3Translator implements SolverInterface {

	private BoolExpr translateConstBoolFormula(BoolConst b, Context ctx) {
		if (b.equals(BoolConst.TRUE)) {
			return ctx.mkTrue();
		}

		assert b.equals(BoolConst.FALSE);
		return ctx.mkFalse();
	}

	private IntExpr translateConstIntFormula(IntConst i, Context ctx) {
		return ctx.mkInt(i.getValue());
	}

	private IntExpr translateVariableFormula(Variable f, Context ctx) {
		IntSort sort = ctx.mkIntSort();
		return (IntExpr) ctx.mkConst(f.getName(), sort);
	}

	private BoolExpr translateBoolVariableFormula(Variable f, Context ctx) {
		BoolSort sort = ctx.mkBoolSort();
		return (BoolExpr) ctx.mkConst(f.getName(), sort);
	}

	private BoolExpr translateUnaryFormula(UnaryOpFormula unary, Context ctx) {
		return ctx.mkNot(translateFormula(unary.getOperand(), ctx));
	}

	private BoolExpr translateBinaryFormula(BinaryOpFormula binary, Context ctx) {

		assert binary.getType().equals(Type.BOOL);

		BinaryOperator binOp = binary.getOperator();

		Formula left = binary.getLeft();
		Formula right = binary.getRight();

		if (left.getType().equals(Type.BOOL)) {
			assert right.getType().equals(Type.BOOL);
			return translateBoolBinaryFormula(binary, ctx);
		}

		ArithExpr leftPart = translateIntSubformula(left, ctx);
		ArithExpr rightPart = translateIntSubformula(right, ctx);

		switch (binOp) {
		case LT:
			return ctx.mkLt(leftPart, rightPart);
		case LE:
			return ctx.mkLe(leftPart, rightPart);
		case GT:
			return ctx.mkGt(leftPart, rightPart);
		case GE:
			return ctx.mkGe(leftPart, rightPart);
		case EQ:
			return ctx.mkEq(leftPart, rightPart);
		case NE:
			return ctx.mkNot(ctx.mkEq(leftPart, rightPart));
		default:
			throw new IllegalArgumentException("Only (int, int) -> bool operators are handled.");
		}

	}

	private BoolExpr translateBoolBinaryFormula(BinaryOpFormula binary, Context ctx) {

		assert binary.getType().equals(Type.BOOL);

		BinaryOperator binOp = binary.getOperator();

		BoolExpr leftPart = translateFormula(binary.getLeft(), ctx);
		BoolExpr rightPart = translateFormula(binary.getRight(), ctx);

		switch (binOp) {
		case EQ:
			return ctx.mkEq(leftPart, rightPart);
		case NE:
			return ctx.mkNot(ctx.mkEq(leftPart, rightPart));
		case AND:
			return ctx.mkAnd(leftPart, rightPart);
		case OR:
			return ctx.mkOr(leftPart, rightPart);
		case IMPLIES:
			return ctx.mkImplies(leftPart, rightPart);
		default:
			throw new IllegalArgumentException("Only (bool, bool) -> bool operators are handled.");
		}
	}

	private ArithExpr translateIntBinaryFormula(BinaryOpFormula binary, Context ctx) {

		assert binary.getType().equals(Type.INT);

		BinaryOperator binOp = binary.getOperator();

		ArithExpr leftPart = translateIntSubformula(binary.getLeft(), ctx);
		ArithExpr rightPart = translateIntSubformula(binary.getRight(), ctx);

		switch (binOp) {
		case ADD:
			return ctx.mkAdd(leftPart, rightPart);
		case SUB:
			return ctx.mkSub(leftPart, rightPart);
		case MUL:
			return ctx.mkMul(leftPart, rightPart);
		default:
			throw new IllegalArgumentException("Only (int, int) -> int operators are handled.");
		}
	}

	private ArithExpr translateIntSubformula(Formula f, Context ctx) {

		if (f instanceof Variable) {
			Variable v = (Variable) f;
			if (!v.getType().equals(Type.INT)) {
				throw new IllegalArgumentException("Expected variable " + v.getName() + " to be of integer type.");
			}
			return translateVariableFormula(v, ctx);
		}

		if (f instanceof IntConst) {
			return translateConstIntFormula((IntConst) f, ctx);
		}

		if (f instanceof BinaryOpFormula && f.getType().equals(Type.INT)) {
			return translateIntBinaryFormula((BinaryOpFormula) f, ctx);
		}

		assert !f.getType().equals(Type.INT);
		throw new IllegalArgumentException("Expected the subformula " + f + " to be of integer type.");
	}

	private BoolExpr translateFormula(Formula f, Context ctx) throws IllegalArgumentException {

		if (f instanceof BinaryOpFormula) {
			return translateBinaryFormula((BinaryOpFormula) f, ctx);
		}

		if (f instanceof UnaryOpFormula) {
			return translateUnaryFormula((UnaryOpFormula) f, ctx);
		}

		if (f instanceof BoolConst) {
			return translateConstBoolFormula((BoolConst) f, ctx);
		}

		if (f instanceof Variable && f.getType().equals(Type.BOOL)) {
			return translateBoolVariableFormula((Variable) f, ctx);
		}

		throw new IllegalArgumentException("Only formulas that evaluate to bool are allowed.");
	}

	@Override
	public Z3Result querySatisfiability(Formula f, Logic logic) {
		Context ctx = new Context();

		// Translate our own formula to a z3 formula
		BoolExpr e = translateFormula(f, ctx);

		// Compute the satisfiability of the formula
		Solver solver = ctx.mkSolver(logic.name());
		solver.add(e);
		Status result = solver.check();

		// Include the model in case the formula is satisfiable
		String modelString = null;
		if (result.equals(Status.SATISFIABLE)) {
			Model model = solver.getModel();
			modelString = model.toString();
		}

		Z3Result solverResult = new Z3Result(result, modelString);

		ctx.close();

		return solverResult;
	}
	
	public static Z3Result query(Formula f, Logic logic) {
		return new Z3Translator().querySatisfiability(f, logic);
	}

}
