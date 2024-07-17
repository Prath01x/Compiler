package tinycc.implementation.type;

import tinycc.mipsasmgen.MemoryInstruction;

/**
 * The main type class (see project description)
 *
 * You can change this class but the given name of the class must not be
 * modified.
 */
public abstract class Type {

	/**
	 * Creates a string representation of this type.
	 *
	 * @remarks See project documentation.
	 * @see StringBuilder
	 */
	@Override
	public abstract String toString();

	public boolean isCharType() {
		return false;
	}

	public boolean isIntType() {
		return false;
	}

	public boolean isIntegerType() {
		return isIntType() || isCharType();
	}

	public boolean isPointerType() {
		return false;
	}

	public boolean isScalarType() {
		return isPointerType() || isIntegerType();
	}

	public boolean isVoidType() {
		return false;
	}

	public boolean isObjectType() {
		return isVoidType() || isScalarType();
	}

	public boolean isCompleteType() {
		return isScalarType();
	}

	public boolean isFunctionType() {
		return false;
	}

	public boolean isVoidPointer() {
		return isPointerType() && ((PointerType) this).getPointsToType().isVoidType();
	}

	@Override
	public final boolean equals(final Object other) {
		if (other instanceof Type) {
			return toString().equals(other.toString());
		}
		return false;
	}

	public abstract MemoryInstruction getStoreInstruction();

	public abstract MemoryInstruction getLoadInstruction();

	public abstract int getSize();

	public final boolean isAssignable(final Type other) {
		boolean isVoidType = this.isVoidType();
		boolean areIdentical = this.equals(other);
		boolean areBothInts = this.isIntegerType() && other.isIntegerType();
		boolean atleastOneVoidP = (isVoidPointer() && other.isPointerType())
				|| (other.isVoidPointer() && isPointerType());
		return ((!isVoidType) && areIdentical || areBothInts || atleastOneVoidP);
	}
}
