package edu.utdallas.seers.tyrion.auth_processor.authorship;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import edu.utdallas.seers.tyrion.auth_processor.authorship.contrib.AuthorContribution;
import edu.utdallas.seers.tyrion.auth_processor.authorship.contrib.AuthorInfo;
import edu.utdallas.seers.tyrion.auth_processor.authorship.contrib.FirstCommitContrib;
import edu.utdallas.seers.tyrion.auth_processor.git.CommitBean;

public class AuthorshipExtractor {

	private String projectFolder;

	private ASTParser parser;
	private String[] encodings;
	private String[] sourceFolders;
	private String[] classPaths;

	private HashMap<String, String> subFoldPrefixes = new HashMap<String, String>();
	// file --> [class1, class2, ...]
	private Map<String, List<String>> filesClasses = new HashMap<String, List<String>>();

	// class --> [author1, author2, ...]
	private Map<String, List<String>> javadocClassAuthors = new HashMap<String, List<String>>();

	public AuthorshipExtractor(String projectFolder, String[] sourceSubFolders,
			String[] classPaths) {
		parser = ASTParser.newParser(AST.JLS8);
		this.projectFolder = projectFolder;
		this.sourceFolders = sourceSubFolders;
		this.classPaths = classPaths;

		encodings = new String[sourceSubFolders.length];
		for (int i = 0; i < sourceSubFolders.length; i++) {

			String subF = sourceSubFolders[i].replaceAll("/", "\\"
					+ File.separator);
			subFoldPrefixes.put(subF,
					subF.replaceAll("\\" + File.separator, "."));

			encodings[i] = "UTF-8";
			this.sourceFolders[i] = projectFolder + File.separator
					+ sourceFolders[i];
		}
		// setParserConf();

		this.projectFolder = projectFolder;
	}

	public Map<String, AuthorInfo> getClassAuthorContributions(
			Vector<CommitBean> history) throws IOException {

		// class --> { author --> contribution }
		Map<String, Map<String, AuthorContribution>> clAuthContr = new HashMap<String, Map<String, AuthorContribution>>();
		// class --> [first-commit]
		Map<String, CommitBean> classFirstCommit = new HashMap<String, CommitBean>();

		for (CommitBean commitBean : history) {

			List<String> classes = getClasses(commitBean);

			if (classes.isEmpty()) {
				continue;
			}

			for (String cl : classes) {

				if (cl.isEmpty()) {
					continue;
				}

				// author contributions
				Map<String, AuthorContribution> authors = clAuthContr.get(cl);
				if (authors == null) {
					authors = new HashMap<String, AuthorContribution>();
					clAuthContr.put(cl, authors);
				}
				AuthorContribution contr = authors.get(commitBean.getAuthor());
				if (contr == null) {
					contr = new AuthorContribution();
					authors.put(commitBean.getAuthor(), contr);
				}
				contr.addNumMod();

				clAuthContr.put(cl, authors);

				// first commit
				setFirstCommit(classFirstCommit, cl, commitBean);

			}

		}

		// compute the contributions
		Set<Entry<String, Map<String, AuthorContribution>>> clAuthContrSet = clAuthContr
				.entrySet();
		for (Entry<String, Map<String, AuthorContribution>> entry : clAuthContrSet) {
			Set<Entry<String, AuthorContribution>> authContr = entry.getValue()
					.entrySet();
			int totalCl = 0;
			for (Entry<String, AuthorContribution> entry2 : authContr) {
				totalCl += entry2.getValue().getNumMod();
			}

			for (Entry<String, AuthorContribution> entry2 : authContr) {
				double percMod = ((double) entry2.getValue().getNumMod())
						/ totalCl;
				entry2.getValue().setPercMod(percMod);
			}
		}

		// /----------------------
		Map<String, AuthorInfo> authorInfo = new HashMap<String, AuthorInfo>();

		clAuthContrSet = clAuthContr.entrySet();
		for (Entry<String, Map<String, AuthorContribution>> entry : clAuthContrSet) {
			String cl = entry.getKey();
			Map<String, AuthorContribution> authContrbs = entry.getValue();

			// -----------------------------------
			CommitBean commitBean = classFirstCommit.get(cl);
			AuthorContribution contrib = authContrbs
					.get(commitBean.getAuthor());

			FirstCommitContrib fistComContrib = new FirstCommitContrib(
					commitBean,
					new AuthorContribution(contrib.getNumMod(), 1.0));

			// -------------------------------------

			List<String> jAuthors = javadocClassAuthors.get(cl);

			Map<String, AuthorContribution> jDocAuthors = new HashMap<String, AuthorContribution>();
			for (String jAuth : jAuthors) {
				jDocAuthors.put(jAuth,
						new AuthorContribution(1, 1.0 / jAuthors.size()));
			}

			// -------------------------------------

			AuthorInfo info = new AuthorInfo(entry.getValue(), fistComContrib,
					jDocAuthors);
			authorInfo.put(cl, info);
		}

		return authorInfo;
	}

	private void setFirstCommit(Map<String, CommitBean> classFirstCommit,
			String cl, CommitBean commitBean) {
		CommitBean commitBean2 = classFirstCommit.get(cl);
		if (commitBean2 == null) {
			commitBean2 = commitBean;
		}
		if (commitBean.getDate().before(commitBean2.getDate())) {
			commitBean2 = commitBean;
		}
		classFirstCommit.put(cl, commitBean2);
	}

	private List<String> getClasses(CommitBean commitBean) throws IOException {
		List<String> cls = getClassesFromFiles(commitBean.getModifiedFiles());
		List<String> cls2 = getClassesFromFiles(commitBean.getAddedFiles());
		List<String> cls3 = getClassesFromFiles(commitBean.getDeletedFiles());

		cls.addAll(cls2);
		cls.addAll(cls3);

		return cls;
	}

	private List<String> getClassesFromFiles(Vector<String> files)
			throws IOException {
		List<String> cls = new ArrayList<String>();

		for (String fileStr : files) {

			if (!fileStr.endsWith(".java")) {
				continue;
			}

			File file = new File(projectFolder + File.separator + fileStr);

			if (!file.exists()) {
				continue;
			}

			List<String> classes = filesClasses.get(file.getAbsolutePath());
			if (classes == null) {
				// System.out.println(file.getAbsolutePath());

				char[] fileContent = readFile(file);
				parser.setUnitName(file.getName());
				parser.setSource(fileContent);
				parser.setKind(ASTParser.K_COMPILATION_UNIT);

				setParserConf();

				CompilationUnit cu = (CompilationUnit) parser.createAST(null);

				// IProblem[] problems = cu.getProblems();
				//
				// for (IProblem problem : problems) {
				// if (problem.isError()) {
				// LOGGER.error(problem.toString() + " - "
				// + problem.getSourceLineNumber());
				// }
				// }
				// ---------------------

				String pref = getSubFoldPref(fileStr);
				ClassVisitor vis = new ClassVisitor(pref, fileStr);
				cu.accept(vis);
				classes = vis.getClasses();

				// java doc authors
				Map<String, List<String>> classesAuthors = vis
						.getClassesAuthors();
				javadocClassAuthors.putAll(classesAuthors);

				// files and classes per file
				filesClasses.put(file.getAbsolutePath(), classes);

			}
			cls.addAll(classes);

		}
		return cls;
	}

	private String getSubFoldPref(String fileStr) {
		Set<Entry<String, String>> entrySet = subFoldPrefixes.entrySet();

		fileStr = fileStr.replaceAll("/", "\\" + File.separator);

		for (Entry<String, String> entry : entrySet) {
			if (fileStr.startsWith(entry.getKey())) {
				return entry.getValue();
			}
		}
		return "";
	}

	private char[] readFile(File path) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path.getAbsolutePath()));
		return new String(encoded, Charset.defaultCharset()).toCharArray();
	}

	public void setParserConf() {
		@SuppressWarnings("unchecked")
		Map<String, String> options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
		options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM,
				JavaCore.VERSION_1_8);
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_8, options);

		// parser.setBindingsRecovery(true);
		// parser.setStatementsRecovery(true);
		parser.setCompilerOptions(options);
		parser.setResolveBindings(true);

		parser.setEnvironment(classPaths, sourceFolders, encodings, true);
	}
}
