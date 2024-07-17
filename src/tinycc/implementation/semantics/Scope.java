package tinycc.implementation.semantics;

import java.util.HashMap;
import java.util.Map;

import tinycc.implementation.declarations.Declaration;

public class Scope {

    private final Map<String, Declaration> typeEnvironment;
    private final Scope parent;
    private byte nLocals;

    public Scope() {
        this.typeEnvironment = new HashMap<String, Declaration>();
        this.parent = null;
        this.nLocals = 0;
    }

    public Scope(Scope parent) {
        this.parent = parent;
        this.nLocals = 0;
        this.typeEnvironment = new HashMap<String, Declaration>();
    }

    public Scope newNestedScope() {
        return new Scope(this);
    }

    public final byte getNLocals() {
        return this.nLocals;
    }

    public class IllegalRedeclarationException extends Exception {
        public OverrideType reason;

        public IllegalRedeclarationException(OverrideType o) {
            this.reason = o;
        }
    }

    public final void add(String id, Declaration decl) throws IllegalRedeclarationException {
        final OverrideType overrideType = checkOverride(id, decl);
        if (overrideType == OverrideType.NONE || overrideType == OverrideType.GLOBAL_REDECL_SAME_TYPE
                || overrideType == OverrideType.PARENT_REDECL) {
            typeEnvironment.put(id, decl);
            this.nLocals++;
        } else {
            throw new IllegalRedeclarationException(overrideType);
        }
    }

    public enum OverrideType {
        GLOBAL_REDECL_SAME_TYPE,
        GLOBAL_REDECL_DIFF_TYPE,
        LOCAL_REDECL,
        PARENT_REDECL,
        NONE;
    }

    public final OverrideType checkOverride(String id, Declaration decl) {
        if (parent == null) {
            final Declaration globalDecl = typeEnvironment.get(id);
            if (globalDecl == null) {
                return OverrideType.NONE;
            }
            if (globalDecl.getType().equals(decl.getType())) {
                return OverrideType.GLOBAL_REDECL_SAME_TYPE;
            } else {
                return OverrideType.GLOBAL_REDECL_DIFF_TYPE;
            }
        } else {
            final Declaration localDecl;
            try {
                parent.lookupId(id);
                return OverrideType.PARENT_REDECL;
            } catch (IdUndeclared e) {
                localDecl = typeEnvironment.get(id);
                if (localDecl == null)
                    return OverrideType.NONE;
                else
                    return OverrideType.LOCAL_REDECL;
            }
        }
    }

    public final Declaration lookupId(String id) throws IdUndeclared {
        Declaration local = typeEnvironment.get(id);
        if (local != null)
            return local;
        if (parent != null) {
            Declaration parentDecl = parent.lookupId(id);
            if (parentDecl != null)
                return parentDecl;
        }
        throw new IdUndeclared("Variable is undefined " + id);
    }
}
