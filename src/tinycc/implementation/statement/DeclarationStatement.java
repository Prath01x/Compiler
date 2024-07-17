package tinycc.implementation.statement;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.codegeneration.CodeGenerator;
import tinycc.implementation.codegeneration.StackFrame;
import tinycc.implementation.declarations.LocalDeclaration;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.semantics.Scope;
import tinycc.implementation.semantics.Scope.IllegalRedeclarationException;
import tinycc.implementation.type.Type;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MemoryInstruction;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.Token;

public class DeclarationStatement extends Statement {

    private final Type type;
    private final Token name;
    private final Expression init;
    private LocalDeclaration decl;

    public DeclarationStatement(Type type, Token name, Expression init) {
        this.type = type;
        this.name = name;
        this.init = init;
    }

    @Override
    public boolean isDeclaration() {
        return true;
    }

    @Override
    public final String toString() {
        return String.format("Declaration_%s[%s", name, type) +
                (init == null || init.toString().isEmpty() ? "]" : "," + init + "]");
    }

    @Override
    public final void computeType(Diagnostic d, Scope s, boolean isBreakable) {
        if (!type.isCompleteType()) {
            d.printError(name, "Cannot declare variable %s as type 'void'", name);
            return;
        }
        boolean isNullPointerAssignment = ((init != null) && (init.isZero() && type.isPointerType()));
        boolean isAssignable = (init != null) && init.getType(d, s).isAssignable(type);
        if (init == null || isNullPointerAssignment || isAssignable) {
            decl = new LocalDeclaration(name, type);
            try {
                s.add(name.getText(), decl);
            } catch (IllegalRedeclarationException e) {
                d.printError(name, "Trying to redeclare an already declared identifier " + e.reason);
            }
        } else {
            d.printError(name, "Types do not match (%s, %s)", init.getType(d, s), type);
        }
    }

    @Override
    public final void eval(StackFrame stackFrame, MipsAsmGen out, CodeGenerator gen) {
        int offset = decl.stackalloc(stackFrame);
        if (init != null) {
            final GPRegister initReg = init.evalToRegister(stackFrame, out, gen);
            out.emitInstruction(MemoryInstruction.SW, initReg, null, offset, GPRegister.SP);
            gen.free(initReg);
        }
    }
}
