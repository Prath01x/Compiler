package tinycc.implementation.declarations;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.codegeneration.StackFrame;
import tinycc.implementation.semantics.Scope;
import tinycc.implementation.semantics.Scope.IllegalRedeclarationException;
import tinycc.implementation.type.Type;
import tinycc.mipsasmgen.DataLabel;
import tinycc.parser.Token;

public class LocalDeclaration implements Declaration {

    private Token token;
    private Type type;

    private DataLabel dataLabel;

    public LocalDeclaration(final Token token, final Type type) {
        this.token = token;
        this.type = type;
    }

    public final void setDataLabel(final DataLabel value) {
        this.dataLabel = value;
    }

    public final DataLabel getDataLabel() {
        return this.dataLabel;
    }

    public final String getName() {
        return this.token.getText();
    }

    public final Type getType() {
        return this.type;
    }

    @Override
    public final void check(final Diagnostic d, final Scope s) {
        if (type.isVoidType()) {
            d.printError(token, "Cannot assign to identifier %s of type 'void'", token.getText());
        } else {
            try {
                s.add(getName(), this);
            } catch (IllegalRedeclarationException e) {
                d.printError(token, "Cannot redeclare local variable " + e.reason);
            }
        }
    }

    public final int stackalloc(final StackFrame stackFrame) {
        return stackFrame.addIdOffset(getName());
    }
}
