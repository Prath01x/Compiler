package tinycc.implementation.type;

import tinycc.mipsasmgen.MemoryInstruction;

public class VoidType extends BaseType {

    public VoidType() {
    }

    @Override
    public final String toString() {
        return "Type_void";
    }

    @Override
    public final boolean isVoidType() {
        return true;
    }

    @Override
    public final int getSize() {
        return 4;
    }

    @Override
    public final MemoryInstruction getLoadInstruction() {
        return MemoryInstruction.LW;
    }

    @Override
    public final MemoryInstruction getStoreInstruction() {
        return MemoryInstruction.SW;
    }
}