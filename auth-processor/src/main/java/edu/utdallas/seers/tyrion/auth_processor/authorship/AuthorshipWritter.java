package edu.utdallas.seers.tyrion.auth_processor.authorship;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.utdallas.seers.tyrion.auth_processor.git.CommitBean;

public class AuthorshipWritter {

	private static final String SEMI = ";";

	public File[] writeAuthorInfo(Map<String, AuthorInfo> authorInfo,
			String[] outfilePaths) throws IOException {

		File[] files = new File[outfilePaths.length];
		for (int i = 0; i < outfilePaths.length; i++) {

			File stFile = new File(outfilePaths[i]);

			if (stFile.isDirectory()) {
				throw new RuntimeException("Output file invalid: "
						+ outfilePaths[i]);
			}

			files[i] = stFile;
		}

		// ---------------------------------------------------------------------

		FileWriter writer = new FileWriter(outfilePaths[0]);

		Set<Entry<String, AuthorInfo>> authorSet = authorInfo.entrySet();
		for (Entry<String, AuthorInfo> classAuthor : authorSet) {

			Set<Entry<String, AuthorContribution>> authoInfo = classAuthor
					.getValue().getContribution().entrySet();

			StringBuffer str = new StringBuffer(classAuthor.getKey() + SEMI);
			for (Entry<String, AuthorContribution> entry2 : authoInfo) {

				str.append(entry2.getKey() + SEMI
						+ entry2.getValue().getNumMod() + SEMI
						+ entry2.getValue().getPercMod() + SEMI);

			}
			str.delete(str.length() - 1, str.length());
			str.append("\n");
			writer.write(str.toString());
		}

		writer.close();

		// ---------------------------------------------------------------------

		writer = new FileWriter(outfilePaths[1]);

		for (Entry<String, AuthorInfo> classAuthor : authorSet) {

			CommitBean firstCommit = classAuthor.getValue().getFirstCommit();

			StringBuffer str = new StringBuffer(classAuthor.getKey() + SEMI);
			str.append(firstCommit.getAuthor() + SEMI + "0" + SEMI + "0");

			str.append("\n");
			writer.write(str.toString());
		}

		writer.close();

		// ---------------------------------------------------------------------

		writer = new FileWriter(outfilePaths[2]);

		for (Entry<String, AuthorInfo> classAuthor : authorSet) {

			List<String> jDocAuthors = classAuthor.getValue()
					.getJavaDocAuthors();

			StringBuffer str = new StringBuffer(classAuthor.getKey() + SEMI);
			for (String auth : jDocAuthors) {
				str.append(auth + SEMI + "0" + SEMI + "0" + SEMI);
			}
			if (jDocAuthors.isEmpty()) {
				str.append("NO_AUTHOR" + SEMI + "0" + SEMI + "0" + SEMI);
			}
			str.delete(str.length() - 1, str.length());
			str.append("\n");
			writer.write(str.toString());
		}

		writer.close();

		// ---------------------------------------------------------------------

		return files;
	}
}
