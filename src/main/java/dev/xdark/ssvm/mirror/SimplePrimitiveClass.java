package dev.xdark.ssvm.mirror;

import dev.xdark.ssvm.VirtualMachine;
import dev.xdark.ssvm.value.InstanceValue;
import dev.xdark.ssvm.value.ObjectValue;
import org.objectweb.asm.Opcodes;

/**
 * Java class implementation for long, int, double, float, etc.
 *
 * @author xDark
 */
public final class SimplePrimitiveClass implements PrimitiveClass {

	private final VirtualMachine vm;
	private final String name;
	private final String descriptor;
	private final InstanceValue oop;
	private final InstanceJavaClass objectClass;
	private final int sort;
	private ArrayJavaClass arrayClass;

	/**
	 * @param vm         VM instance.
	 * @param name       Name of the class.
	 * @param descriptor Descriptor of the class.
	 * @param sort       Type sort.
	 */
	public SimplePrimitiveClass(VirtualMachine vm, String name, String descriptor, int sort) {
		this.vm = vm;
		this.name = name;
		this.descriptor = descriptor;
		oop = vm.getMemoryManager().newClassOop(this);
		objectClass = vm.getSymbols().java_lang_Object();
		this.sort = sort;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getInternalName() {
		return name;
	}

	@Override
	public String getDescriptor() {
		return descriptor;
	}

	@Override
	public int getModifiers() {
		return Opcodes.ACC_PUBLIC;
	}

	@Override
	public ObjectValue getClassLoader() {
		return vm.getMemoryManager().nullValue();
	}

	@Override
	public InstanceValue getOop() {
		return oop;
	}

	@Override
	public FieldLayout getVirtualFieldLayout() {
		return objectClass.getVirtualFieldLayout();
	}

	@Override
	public FieldLayout getStaticFieldLayout() {
		return FieldLayout.EMPTY;
	}

	@Override
	public InstanceJavaClass getSuperClass() {
		return objectClass;
	}

	@Override
	public InstanceJavaClass[] getInterfaces() {
		return new InstanceJavaClass[0];
	}

	@Override
	public ArrayJavaClass newArrayClass() {
		ArrayJavaClass arrayClass = this.arrayClass;
		if (arrayClass == null) {
			VirtualMachine vm = this.vm;
			arrayClass = this.arrayClass = new ArrayJavaClass(vm, '[' + descriptor, 1, this);
			vm.getHelper().setComponentType(arrayClass, this);
		}
		return arrayClass;
	}

	@Override
	public ArrayJavaClass getArrayClass() {
		return arrayClass;
	}

	@Override
	public void initialize() {
	}

	@Override
	public boolean isAssignableFrom(JavaClass other) {
		return this == other;
	}

	@Override
	public boolean isPrimitive() {
		return true;
	}

	@Override
	public boolean isArray() {
		return false;
	}

	@Override
	public boolean isInterface() {
		return false;
	}

	@Override
	public JavaClass getComponentType() {
		return null;
	}

	@Override
	public int getSort() {
		return sort;
	}
}
