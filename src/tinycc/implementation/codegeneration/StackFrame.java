package tinycc.implementation.codegeneration;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tinycc.implementation.type.Type;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.ImmediateInstruction;
import tinycc.mipsasmgen.JumpInstruction;
import tinycc.mipsasmgen.JumpRegisterInstruction;
import tinycc.mipsasmgen.MemoryInstruction;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.mipsasmgen.TextLabel;

/**
 * Takes care of creating and destroying the stack frame for functions,
 * as well as tracking the offsets for all local identifiers.
 */
public final class StackFrame {
    private Map<String, Integer> offset;
    private StackFrame superFrame;
    private TextLabel fEnd;
    private int currentOffset;
    private int size;

    public StackFrame() {
        this.offset = new HashMap<>();
        this.superFrame = null;
        this.size = 0;
        this.currentOffset = 0;
    }

    public StackFrame(final StackFrame superFrame) {
        this.superFrame = superFrame;
        this.offset = new HashMap<>();
        this.size = superFrame.size;
        this.currentOffset = superFrame.currentOffset;
        this.fEnd = superFrame.fEnd;
    }

    public final class NoOffsetForIdException extends Exception {
        public NoOffsetForIdException() {
            super();
        }
    }

    public final int getCurrentOffset() {
        return this.currentOffset;
    }

    public final Integer getOffset(String uniqueId) throws NoOffsetForIdException {
        Integer localOffset = offset.get(uniqueId);
        if (localOffset != null)
            return localOffset;
        else if (superFrame != null && superFrame.getOffset(uniqueId) != null) {
            return superFrame.getOffset(uniqueId);
        }
        throw new NoOffsetForIdException();
    }

    public final void setNVars(final int n) {
        this.size = n;
    }

    public final int getNVars() {
        return this.size;
    }

    public final void setFEnd(final TextLabel end) {
        this.fEnd = end;
    }

    public final void pushCurrentlyUsed(final MipsAsmGen out, final CodeGenerator gen, List<GPRegister> regs) {
        out.emitInstruction(ImmediateInstruction.ADDIU, GPRegister.SP, -(regs.size() * 4));
        size += (4 * regs.size());
        for (final GPRegister reg : regs) {
            out.emitInstruction(MemoryInstruction.SW, reg, null, currentOffset, GPRegister.SP);
            gen.free(reg);
            currentOffset += 4;
        }
    }

    public final void loadCurrentlyUsed(final MipsAsmGen out, final CodeGenerator gen, List<GPRegister> regs) {
        Collections.reverse(regs);
        for (final GPRegister reg : regs) {
            currentOffset -= 4;
            out.emitInstruction(MemoryInstruction.LW, reg, null, currentOffset, GPRegister.SP);
            gen.setRegister(reg, true);
        }
        size -= (4 * regs.size());
        out.emitInstruction(ImmediateInstruction.ADDIU, GPRegister.SP, regs.size() * 4);
    }

    public final int addIdOffset(String Id) {
        offset.put(Id, currentOffset);
        int oldOffset = currentOffset;
        currentOffset += 4;
        return oldOffset;
    }

    public final void jumpToEnd(final MipsAsmGen out) {
        out.emitInstruction(JumpInstruction.J, this.fEnd);
    }

    public final void emitConstruction(final List<Type> parameters, final MipsAsmGen out) {
        out.emitInstruction(ImmediateInstruction.ADDIU, GPRegister.SP, GPRegister.SP, -(size + 4));
        out.emitInstruction(MemoryInstruction.SW, GPRegister.RA, null, size, GPRegister.SP);

        final GPRegister[] argRegs = { GPRegister.A0, GPRegister.A1, GPRegister.A2, GPRegister.A3 };

        for (int i = parameters.size() - 1; i >= 0; i--) {
            out.emitInstruction(parameters.get(i).getStoreInstruction(), argRegs[i], null, i * 4, GPRegister.SP);
        }
    }

    public final void emitDestruction(final MipsAsmGen out) {
        out.emitInstruction(MemoryInstruction.LW, GPRegister.RA, null, size, GPRegister.SP);
        out.emitInstruction(ImmediateInstruction.ADDIU, GPRegister.SP, GPRegister.SP, (size + 4));
        out.emitInstruction(JumpRegisterInstruction.JR, GPRegister.RA);
    }
}
