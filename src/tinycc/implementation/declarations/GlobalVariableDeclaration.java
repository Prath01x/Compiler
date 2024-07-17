package tinycc.implementation.declarations;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.codegeneration.CodeGenerator;
import tinycc.implementation.semantics.Scope;
import tinycc.implementation.semantics.Scope.IllegalRedeclarationException;
import tinycc.implementation.type.Type;
import tinycc.mipsasmgen.DataLabel;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.Token;

public class GlobalVariableDeclaration extends ExternalDeclaration {
    public GlobalVariableDeclaration(final Type type, final Token token) {
        this.type = type;
        this.token = token;
    }

    private DataLabel dataLabel;

    public final void setDataLabel(final DataLabel value) {
        this.dataLabel = value;
    }

    public final DataLabel getDataLabel() {
        return this.dataLabel;
    }

    @Override
    public final void check(final Diagnostic d, final Scope s) {
        if (type.isVoidType()) {
            d.printError(token, "Cannot assign to identifier %s of type 'void'", token.getText());
        } else {
            try {
                s.add(getName(), this);
            } catch (IllegalRedeclarationException e) {
                d.printError(token, "Cannot redeclare variable with different type " + e.reason);
            }
        }
    }

    public final void emit(final MipsAsmGen out, final CodeGenerator gen) {
        setDataLabel(out.makeDataLabel(getName()));
        if (type.isCharType()) {
            out.emitByte(dataLabel, (byte) 0);
        } else {
            out.emitWord(dataLabel, 0);
        }
    }
}
