package tinycc.implementation.declarations;

import java.util.List;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.semantics.Scope;
import tinycc.implementation.type.Type;
import tinycc.implementation.type.FunctionType;
import tinycc.parser.Token;

public class FunctionDeclaration extends ExternalDeclaration {

    private FunctionDefinition associatedDefinition;

    public FunctionDeclaration(final Type type, final Token token) {
        this.type = type;
        this.token = token;
        this.associatedDefinition = null;
    }

    public final List<Type> getParameterTypes() {
        return ((FunctionType) type).getParameterTypes();
    }

    public final void setAssociatedDefition(final FunctionDefinition def) {
        this.associatedDefinition = def;
    }

    public final FunctionDefinition getAssociatedDefinition() {
        return this.associatedDefinition;
    }

    @Override
    public final void check(final Diagnostic d, final Scope s) {
        if (!type.isFunctionType()) {
            d.printError(token, "%s is not a valid function", token.getText());
        }
        if (((FunctionType) type).getParameterTypes().size() > 4) {
            d.printError(token, "Function %s cannot have more than 4 parameters", token.getText());
        }
    }
}
