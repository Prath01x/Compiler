package tinycc.implementation.type;

import tinycc.mipsasmgen.MemoryInstruction;

public class IntType extends BaseType {

    public IntType() {
    }

    @Override
    public final String toString() {
        return "Type_int";
    }

    @Override
    public final boolean isIntType() {
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
