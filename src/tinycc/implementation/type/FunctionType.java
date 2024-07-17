package tinycc.implementation.type;

import java.util.List;

import tinycc.mipsasmgen.MemoryInstruction;

public class FunctionType extends Type {

    private final Type returnType;
    private final List<Type> parameters;

    public FunctionType(final Type returnType, final List<Type> parameters) {
        this.returnType = returnType;
        this.parameters = parameters;
    }

    public final boolean hasParameters() {
        return this.parameters.size() > 0;
    }

    public final Type getReturnType() {
        return this.returnType;
    }

    public final List<Type> getParameterTypes() {
        return this.parameters;
    }

    @Override
    public final String toString() {
        String res = String.format("FunctionType[%s", returnType);
        for (final Type t : parameters) {
            res += ", " + t;
        }
        return res + "]";
    }

    @Override
    public final int getSize() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Functions do not have a size");
    }

    @Override
    public final boolean isFunctionType() {
        return true;
    }

    @Override
    public final MemoryInstruction getLoadInstruction() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Cannot load a function");
    }

    @Override
    public final MemoryInstruction getStoreInstruction() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Cannot store a function");
    }
}
