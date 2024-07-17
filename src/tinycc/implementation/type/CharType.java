package tinycc.implementation.type;

import tinycc.mipsasmgen.MemoryInstruction;

public class CharType extends BaseType {

    public CharType() {
    }

    @Override
    public final String toString() {
        return "Type_char";
    }

    @Override
    public final boolean isCharType() {
        return true;
    }

    @Override
    public final int getSize() {
        return 1;
    }

    @Override
    public final MemoryInstruction getLoadInstruction() {
        return MemoryInstruction.LB;
    }

    @Override
    public final MemoryInstruction getStoreInstruction() {
        return MemoryInstruction.SW;
    }
}