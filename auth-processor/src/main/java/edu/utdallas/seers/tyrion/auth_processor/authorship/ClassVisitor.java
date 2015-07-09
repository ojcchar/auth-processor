package edu.utdallas.seers.tyrion.auth_processor.authorship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.utdallas.seers.tyrion.auth_processor.App;

public class ClassVisitor extends ASTVisitor {

	private Map<String, List<String>> classesAuthors = new HashMap<>();
	private String prefix;
	private String outterCl;
	private List<String> outterClAuthors = new ArrayList<>();
	private String file;
	private static Logger LOGGER = LoggerFactory.getLogger(App.class);

	public ClassVisitor(String prefix, String file) {
		this.prefix = prefix;
		this.file = file;
	}

	@Override
	public boolean visit(TypeDeclaration node) {

		addQualfName(node);
		// Javadoc javadoc = node.getJavadoc();
		return super.visit(node);
	}

	private void addQualfName(AbstractTypeDeclaration node) {

		ITypeBinding resolveBinding = node.resolveBinding();
		String qualifiedName = resolveBinding.getQualifiedName();
		String className = node.getName().toString();

		// check for the names
		if (qualifiedName == null || qualifiedName.isEmpty()) {
			LOGGER.error("The qual. name is null or empty, class " + className
					+ ", file: " + file);
			return;
		}

		// inner class
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

		List<String> listAuthors = getListAuthors(node);
		classesAuthors.put(prefix + (prefix.isEmpty() ? "" : ".")
				+ qualifiedName, listAuthors);

		if (outterCl == null) {
			outterCl = qualifiedName;
			outterClAuthors = listAuthors;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<String> getListAuthors(AbstractTypeDeclaration node) {
		Javadoc javadoc = node.getJavadoc();
		List<String> authors = new ArrayList<>();

		if (javadoc == null) {
			return authors;
		}

		List<TagElement> tagEls = javadoc.tags();

		for (TagElement tagElement : tagEls) {
			if (tagElement.getTagName() == null
					|| !"@author".equalsIgnoreCase(tagElement.getTagName())) {
				continue;
			}

			List fragments = tagElement.fragments();
			StringBuffer author = new StringBuffer();
			for (Object object : fragments) {
				author.append(object.toString());
				author.append(" ");
			}

			author.replace(author.length() - 1, author.length(), "");
			authors.add(author.toString().trim());
		}

		return authors;
	}

	@Override
	public boolean visit(EnumDeclaration node) {
		addQualfName(node);
		return super.visit(node);
	}

	public List<String> getClasses() {
		return new ArrayList<>(classesAuthors.keySet());
	}

	public Map<String, List<String>> getClassesAuthors() {

		if (outterClAuthors == null || outterClAuthors.isEmpty()) {
			return classesAuthors;
		}

		Set<Entry<String, List<String>>> entrySet = classesAuthors.entrySet();

		for (Entry<String, List<String>> entry : entrySet) {
			if (entry.getValue().isEmpty()) {
				classesAuthors.put(entry.getKey(), outterClAuthors);
			}
		}

		return classesAuthors;
	}

}
