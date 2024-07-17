package tinycc.implementation.declarations;

import java.util.ArrayList;
import java.util.List;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.codegeneration.CodeGenerator;
import tinycc.implementation.codegeneration.StackFrame;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.semantics.Scope;
import tinycc.implementation.semantics.Scope.IllegalRedeclarationException;
import tinycc.implementation.statement.BlockStatement;
import tinycc.implementation.statement.ReturnStatement;
import tinycc.implementation.statement.Statement;
import tinycc.implementation.type.FunctionType;
import tinycc.implementation.type.Type;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.mipsasmgen.TextLabel;
import tinycc.parser.Token;

public class FunctionDefinition extends ExternalDeclaration {

    private final List<Token> parameterNames;
    private final List<LocalDeclaration> parameteDeclarations;
    private final Statement body;
    private final StackFrame stackFrame;
    private TextLabel label;
    private byte nLocals;

    public FunctionDefinition(final Type type, final Token token,
            final List<Token> parameterNames, final Statement body) {
        this.type = type;
        this.token = token;
        this.parameterNames = parameterNames;
        this.parameteDeclarations = new ArrayList<>();
        this.stackFrame = new StackFrame();
        this.body = body;
        this.nLocals = (byte) parameterNames.size();
    }

    public FunctionDefinition() {
        this.parameterNames = null;
        this.parameteDeclarations = null;
        this.body = null;
        this.stackFrame = null;
    }

    public final List<Token> getParameterNames() {
        return this.parameterNames;
    }

    public final Statement getBody() {
        return this.body;
    }

    @Override
    public final boolean equals(final Object other) {
        if (other instanceof FunctionDefinition) {
            FunctionDefinition _other = (FunctionDefinition) other;
            return super.equals((ExternalDeclaration) other) &&
                    _other.getBody() == body && _other.getParameterNames() == parameterNames;
        }
        return false;
    }

    public final TextLabel getTextLabel() {
        return this.label;
    }

    public final void setTextLabel(final TextLabel label) {
        this.label = label;
    }

    private void checkReturnStatement(final Diagnostic d, final Scope s, final ReturnStatement r) {
        final FunctionType functionType = (FunctionType) getType();
        final Type returnType = functionType.getReturnType();
        final Expression returnExpression = r.getExpression();
        if (returnExpression != null && returnType.isVoidType()) {
            d.printError(r, "Cannot return an expression as function because return type is 'void'");
            return;
        }
        if (returnExpression == null && !returnType.isVoidType()) {
            d.printError(r, "Must return something to function of non-void type %s", returnType);
            return;
        }
        if (returnExpression == null && returnType.isVoidType()) {
            return;
        }
        if (!returnType.isAssignable(returnExpression.getType(d, s)) && !returnExpression.isZero()) {
            d.printError(r, "Invalid return type, expected %s got %s", returnType,
                    returnExpression.getType(d, s));
        }
    }

    @Override
    public final Type getType() {
        return this.type;
    }

    @Override
    public void check(final Diagnostic d, final Scope global) {
        Scope functionScope = global.newNestedScope();
        final List<Type> parameterTypes = ((FunctionType) getType()).getParameterTypes();
        final List<Token> tokenNames = getParameterNames();

        if (parameterTypes.size() > 4) {
            d.printError(token, "Function %s cannot have more than 4 parameters", token.getText());
        }

        for (int i = 0; i < parameterTypes.size(); i++) {
            final LocalDeclaration decl = new LocalDeclaration(tokenNames.get(i), parameterTypes.get(i));
            try {
                parameteDeclarations.add(decl);
                functionScope.add(tokenNames.get(i).getText(), decl);
            } catch (IllegalRedeclarationException e) {
                d.printError(token,
                        "Should never see this error as parameters are always the first local declarations :) " +
                                e.reason);
            }
        }

        for (final Statement s : ((BlockStatement) getBody()).getStatements()) {
            if (s.isReturn()) {
                checkReturnStatement(d, functionScope, (ReturnStatement) s);
                return;
            }
            if (s.isDeclaration()) {
                nLocals++;
            }
            s.computeType(d, functionScope, false);
            if (s.isBlock()) {
                nLocals += ((BlockStatement) s).getNLocals();
            }
        }

    }

    public final void emit(final MipsAsmGen out, final CodeGenerator gen) {
        this.stackFrame.setNVars(nLocals * 4);
        label = out.makeTextLabel(getName());
        TextLabel endLabel = out.makeTextLabel(getName() + "_end");
        this.stackFrame.setFEnd(endLabel);
        out.emitLabel(label);
        stackFrame.emitConstruction(((FunctionType) type).getParameterTypes(), out);
        for (int i = 0; i < getParameterNames().size(); i++) {
            parameteDeclarations.get(i).stackalloc(stackFrame);
        }
        for (final Statement s : ((BlockStatement) getBody()).getStatements()) {
            s.eval(stackFrame, out, gen);
        }
        out.emitLabel(endLabel);
        stackFrame.emitDestruction(out);
    }
}
