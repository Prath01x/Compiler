package tinycc.implementation.semantics;

import java.util.List;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.AST.ASTBuilder;
import tinycc.implementation.declarations.ExternalDeclaration;
import tinycc.implementation.declarations.FunctionDeclaration;
import tinycc.implementation.declarations.FunctionDefinition;
import tinycc.implementation.semantics.Scope.IllegalRedeclarationException;

public class TypeChecker {

    private final Diagnostic diagnostic;
    private final ASTBuilder AST;
    private final Scope global;

    public TypeChecker(Diagnostic d, ASTBuilder AST) {
        this.diagnostic = d;
        this.AST = AST;
        this.global = new Scope();
    }

    public final Scope checkTypes() {
        List<ExternalDeclaration> decls = List.copyOf(AST.getExternalDeclarations());
        for (ExternalDeclaration ext : decls) {
            if (ext instanceof FunctionDeclaration) {
                FunctionDefinition def = getCorrespondingDefinition((FunctionDeclaration) ext);
                ((FunctionDeclaration) ext).setAssociatedDefition(def);
            }
            try {
                global.add(ext.getName(), ext);
            } catch (IllegalRedeclarationException e) {
                diagnostic.printError(ext.getToken(), "Trying to redeclare identifier " + e.reason);
            }
            ext.check(diagnostic, global);
        }
        return global;
    }

    private FunctionDefinition getCorrespondingDefinition(final FunctionDeclaration ext) {
        FunctionDefinition res = null;
        for (final FunctionDefinition f : AST.getFunctionDefinitions()) {
            if (!((f.getName().equals(ext.getName())) && (f.getType().equals(ext.getType())))) {
                continue;
            }
            if (res != null) {
                diagnostic.printError(f.getToken(), "Redefinion of function %s", ext.getName());
                return null;
            } else {
                res = f;
            }

        }
        return res;
    }
}
