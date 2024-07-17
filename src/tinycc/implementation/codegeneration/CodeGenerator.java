package tinycc.implementation.codegeneration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tinycc.implementation.AST.ASTBuilder;
import tinycc.implementation.declarations.ExternalDeclaration;
import tinycc.implementation.declarations.FunctionDefinition;
import tinycc.implementation.declarations.GlobalVariableDeclaration;
import tinycc.implementation.semantics.Scope;
import tinycc.mipsasmgen.*;
import tinycc.parser.ASTFactory;
import tinycc.mipsasmgen.GPRegister;

public class CodeGenerator {

    private final MipsAsmGen out;
    private final ASTBuilder AST;
    private boolean isLEval = false;
    private List<GlobalVariableDeclaration> globals;

    private TextLabel currentLoopEnd = null;
    private TextLabel currentLoopHead = null;

    private Map<GPRegister, Boolean> registers;

    public CodeGenerator(final Scope scope, final MipsAsmGen out, final ASTFactory AST) {
        this.out = out;
        this.AST = (ASTBuilder) AST;
        this.globals = new ArrayList<>();
        initRegisters();
    }

    public final TextLabel getFunctionLabel(final String id) {
        List<FunctionDefinition> defs = AST.getFunctionDefinitions();
        for (FunctionDefinition def : defs) {
            if (def.getName().equals(id)) {
                if (def.getTextLabel() != null)
                    return def.getTextLabel();
            }
        }
        final TextLabel label = out.makeTextLabel(id);
        return label;
    }

    public final void setLEval(final boolean val) {
        this.isLEval = val;
    }

    public final boolean isLEval() {
        return this.isLEval;
    }

    public final void initRegisters() {
        this.registers = new HashMap<>() {
            {
                put(GPRegister.T0, false);
                put(GPRegister.T1, false);
                put(GPRegister.T2, false);
                put(GPRegister.T3, false);
                put(GPRegister.T4, false);
                put(GPRegister.T5, false);
                put(GPRegister.T6, false);
                put(GPRegister.T7, false);
                put(GPRegister.T8, false);
                put(GPRegister.T9, false);
            }
        };
    }

    public final void setRegister(final GPRegister reg, final boolean val) {
        this.registers.put(reg, val);
    }

    public final void free(GPRegister... regs) {
        for (GPRegister gp : regs) {
            setRegister(gp, false);
        }
    }

    public final List<GPRegister> getAllUsed() {
        ArrayList<GPRegister> res = new ArrayList<>();
        for (final Map.Entry<GPRegister, Boolean> kv : registers.entrySet()) {
            if (kv.getValue()) {
                res.add(kv.getKey());
            }
        }
        return res;
    }

    public final GPRegister getNextUnused() {
        for (final Map.Entry<GPRegister, Boolean> kv : registers.entrySet()) {
            if (!kv.getValue()) {
                GPRegister key = kv.getKey();
                boolean value = true;
                registers.put(key, value);
                out.emitInstruction(RegisterInstruction.XOR, key, key);
                return kv.getKey();
            }
        }
        throw new UnsupportedOperationException("There are no unused registers left.");
    }

    public final DataLabel getGlobalLabel(final String id) {
        for (final GlobalVariableDeclaration g : globals) {
            if (g.getName().equals(id))
                return g.getDataLabel();
        }
        return null;
    }

    public final TextLabel getLoopEndLabel() {
        return this.currentLoopEnd;
    }

    public final TextLabel getLoopHeadLabel() {
        return this.currentLoopHead;
    }

    public final void setCurrentLoopEnd(final TextLabel label) {
        this.currentLoopEnd = label;
    }

    public final void setCurrentLoopHead(final TextLabel label) {
        this.currentLoopHead = label;
    }

    public final Map<GPRegister, Boolean> getRegisters() {
        return registers;
    }

    public final void generate() {
        for (final ExternalDeclaration ext : AST.getExternalDeclarations()) {
            if (ext instanceof GlobalVariableDeclaration) {
                globals.add((GlobalVariableDeclaration) ext);
                ((GlobalVariableDeclaration) ext).emit(out, this);
            } else if (ext instanceof FunctionDefinition) {
                if (((FunctionDefinition) ext).getBody() == null) {
                    continue;
                }
                ((FunctionDefinition) ext).emit(out, this);
            }
        }
    }
}
