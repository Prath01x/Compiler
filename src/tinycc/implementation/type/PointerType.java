package tinycc.implementation.type;

import tinycc.mipsasmgen.MemoryInstruction;

public class PointerType extends Type {
    private final Type pointsTo;

    public PointerType(final Type pointsTo) {
        this.pointsTo = pointsTo;
    }

    public final Type getPointsToType() {
        return pointsTo;
    }

    @Override
    public final String toString() {
        return String.format("Pointer[%s]", pointsTo);
    }

    @Override
    public final boolean isPointerType() {
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
