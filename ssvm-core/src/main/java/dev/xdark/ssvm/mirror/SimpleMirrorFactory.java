package dev.xdark.ssvm.mirror;

import dev.xdark.ssvm.VirtualMachine;
import dev.xdark.ssvm.mirror.member.JavaField;
import dev.xdark.ssvm.mirror.member.JavaMethod;
import dev.xdark.ssvm.mirror.member.SimpleJavaField;
import dev.xdark.ssvm.mirror.member.SimpleJavaMethod;
import dev.xdark.ssvm.mirror.type.ArrayClass;
import dev.xdark.ssvm.mirror.type.InstanceClass;
import dev.xdark.ssvm.mirror.type.JavaClass;
import dev.xdark.ssvm.mirror.type.PrimitiveClass;
import dev.xdark.ssvm.mirror.type.SimpleArrayClass;
import dev.xdark.ssvm.mirror.type.SimpleInstanceClass;
import dev.xdark.ssvm.mirror.type.SimplePrimitiveClass;
import dev.xdark.ssvm.value.InstanceValue;
import dev.xdark.ssvm.value.ObjectValue;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Simple mirror factory.
 *
 * @author xDark
 */
public final class SimpleMirrorFactory implements MirrorFactory {
	private final VirtualMachine vm;

	public SimpleMirrorFactory(VirtualMachine vm) {
		this.vm = vm;
	}

	@Override
	public InstanceClass newInstanceClass(ObjectValue classLoader, ClassReader classReader, ClassNode node) {
		return new SimpleInstanceClass(vm, classLoader, classReader, node);
	}

	@Override
	public PrimitiveClass newPrimitiveClass(Type type) {
		VirtualMachine vm = this.vm;
		PrimitiveClass klass = new SimplePrimitiveClass(vm, type);
		klass.setOop(vm.getMemoryManager().newClassOop(klass));
		vm.getClassStorage().register(klass);
		return klass;
	}

	@Override
	public ArrayClass newArrayClass(JavaClass componentType) {
		VirtualMachine vm = this.vm;
		ArrayClass klass = new SimpleArrayClass(vm, componentType);
		InstanceValue oop = vm.getMemoryManager().newClassOop(klass);
		klass.setOop(oop);

		// Set component type - Java 17
		if (oop.getJavaClass().getField("componentType", "Ljava/lang/Class;") != null) {
			vm.getOperations().putReference(oop, "componentType", "Ljava/lang/Class;", componentType.getOop());
		}

		vm.getClassStorage().register(klass);
		return klass;
	}

	@Override
	public JavaField newField(InstanceClass owner, FieldNode node, int slot, long offset) {
		return new SimpleJavaField(owner, node, slot, offset);
	}

	@Override
	public JavaMethod newMethod(InstanceClass owner, MethodNode node, int slot) {
		return new SimpleJavaMethod(owner, node, node.desc, slot);
	}

	@Override
	public JavaMethod newPolymorphicMethod(JavaMethod method, String desc) {
		return new SimpleJavaMethod(method.getOwner(), method.getNode(), desc, method.getSlot());
	}
}
