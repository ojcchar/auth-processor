package edu.utdallas.seers.tyrion.auth_processor.authorship;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.utdallas.seers.tyrion.auth_processor.App;

public class ClassVisitor extends ASTVisitor {

	private List<String> classes = new ArrayList<String>();
	private String prefix;
	private String outterCl;
	private String file;
	private static Logger LOGGER = LoggerFactory.getLogger(App.class);

	public ClassVisitor(String prefix, String file) {
		this.prefix = prefix;
		this.file = file;
	}

	@Override
	public boolean visit(TypeDeclaration node) {

		ITypeBinding resolveBinding = node.resolveBinding();
		String qualifiedName = resolveBinding.getQualifiedName();
		// String qualifiedName = node.getName().toString();
		// if (resolveBinding != null) {
		// qualifiedName = resolveBinding.getQualifiedName();
		// }

		addQualfName(qualifiedName, node.getName().toString());
		return super.visit(node);
	}

	private void addQualfName(String qualifiedName, String className) {

		if (qualifiedName == null || qualifiedName.isEmpty()) {
			LOGGER.error("The qual. name is null or empty, class " + className
					+ ", file: " + file);
			return;
		}

		if (outterCl != null) {
			int indexOf = qualifiedName.indexOf(outterCl);
			if (indexOf != -1) {
				String substring = qualifiedName.substring(
						indexOf + outterCl.length(), qualifiedName.length());
				substring = substring.replaceAll("\\.", "\\$");
				qualifiedName = qualifiedName.substring(0,
						indexOf + outterCl.length())
						+ substring;
			}
		}
		classes.add(prefix + (prefix.isEmpty() ? "" : ".") + qualifiedName);

		if (outterCl == null) {
			outterCl = qualifiedName;
		}
	}

	@Override
	public boolean visit(EnumDeclaration node) {
		ITypeBinding resolveBinding = node.resolveBinding();
		String qualifiedName = resolveBinding.getQualifiedName();
		addQualfName(qualifiedName, node.getName().toString());
		return super.visit(node);
	}

	public List<String> getClasses() {
		return classes;
	}

}
