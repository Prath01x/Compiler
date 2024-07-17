package tinycc.implementation.expression;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.semantics.IdUndeclared;
import tinycc.implementation.semantics.Scope;
import tinycc.implementation.type.Type;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.JumpInstruction;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.mipsasmgen.RegisterInstruction;
import tinycc.mipsasmgen.TextLabel;
import tinycc.implementation.type.FunctionType;
import tinycc.implementation.codegeneration.CodeGenerator;
import tinycc.implementation.codegeneration.StackFrame;
import tinycc.implementation.declarations.Declaration;
import tinycc.implementation.declarations.FunctionDeclaration;
import tinycc.implementation.declarations.FunctionDefinition;
import tinycc.implementation.expression.primaryexpressions.IdentifierExpression;
import tinycc.parser.Token;

public class CallExpression extends Expression {

    private final Expression callee;
    private final List<Expression> arguments;

    public CallExpression(final Token token, final Expression callee, final List<Expression> arguments) {
        super(token);
        this.callee = callee;
        this.arguments = arguments;
    }

    @Override
    public final String toString() {
        String res = "Call[" + callee;
        for (final Expression e : arguments) {
            res += ", " + e;
        }
        return res + "]";
    }

    private final FunctionDefinition checkCalleeScope(Diagnostic d, Scope s) {
        if (!callee.isIdentifier()) {
            d.printError(this, "Callee %s is not an identifier", callee);
            return null;
        }
        final IdentifierExpression calleeIdExp = (IdentifierExpression) callee;
        Declaration res;
        try {
            res = s.lookupId(calleeIdExp.getId());
        } catch (IdUndeclared e) {
            d.printError(this, "Function %s has not been declared yet", calleeIdExp.getId());
            return null;
        }
        if (res instanceof FunctionDeclaration) {
            if (((FunctionDeclaration) res).getAssociatedDefinition() == null) {
                FunctionDeclaration decl = (FunctionDeclaration) res;
                return new FunctionDefinition(decl.getType(), decl.getToken(), new ArrayList<>(), null);
            } else
                return ((FunctionDeclaration) res).getAssociatedDefinition();
        } else if (res instanceof FunctionDefinition)
            return (FunctionDefinition) res;
        else {
            d.printError(calleeIdExp, "Calling a non-function");
            return null;
        }
    }

    @Override
    protected final void computeType(Diagnostic d, Scope s) {
        final FunctionDefinition functionDefinition = this.checkCalleeScope(d, s);
        if (functionDefinition == null) {
            d.printNote(this, "Calling declared but undefined function", token.getText());
            this.type = ((FunctionType) callee.getType(d, s)).getReturnType();
            return;
        }
        final List<Type> params = ((FunctionType) functionDefinition.getType()).getParameterTypes();
        if (arguments.size() != params.size()) {
            d.printError(this, "Function %s expects %s arguments, passed %s", functionDefinition.getName(),
                    params.size(), arguments.size());
            return;
        }
        for (int i = 0; i < params.size(); i++) {
            if (!arguments.get(i).getType(d, s).isAssignable(params.get(i))) {
                d.printError(this, "Argument type %s does not match parameter type %s",
                        arguments.get(i).getType(d, s),
                        params.get(i));
                return;
            }
        }
        this.type = ((FunctionType) callee.getType(d, s)).getReturnType();
    }

    @Override
    public final GPRegister evalToRegister(final StackFrame stackFrame, final MipsAsmGen out, final CodeGenerator gen) {
        final List<GPRegister> argRegs = Arrays.asList(GPRegister.A0, GPRegister.A1, GPRegister.A2, GPRegister.A3);
        for (int i = 0; i < arguments.size(); i++) {
            out.emitInstruction(RegisterInstruction.XOR, argRegs.get(i), argRegs.get(i));
            final GPRegister arg = arguments.get(i).evalToRegister(stackFrame, out, gen);
            out.emitInstruction(RegisterInstruction.ADD, argRegs.get(i), arg);
            gen.free(arg);
        }
        final TextLabel functionLabel = gen.getFunctionLabel(callee.token.getText());
        List<GPRegister> used = gen.getAllUsed();
        if (used.size() > 0) {
            stackFrame.pushCurrentlyUsed(out, gen, used);
            out.emitInstruction(JumpInstruction.JAL, functionLabel);
            stackFrame.loadCurrentlyUsed(out, gen, used);
        } else {
            out.emitInstruction(JumpInstruction.JAL, functionLabel);
        }
        final GPRegister target = gen.getNextUnused();
        out.emitInstruction(RegisterInstruction.ADD, target, GPRegister.V0);
        return target;
    }
}
