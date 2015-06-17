package edu.utdallas.seers.tyrion.auth_processor.authorship;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class AuthorshipWritter {

	private static final String SEMI = ";";

	public File writeAuthorship(
			Map<String, Map<String, AuthorContribution>> clAuthorInfo,
			String filePath) throws IOException {

		FileWriter writer = null;

		File stFile = new File(filePath);
		if (stFile.isDirectory()) {
			throw new RuntimeException("Output file invalid: " + filePath);
		}

		if (clAuthorInfo == null) {
			throw new RuntimeException("No info to write");
		}

		writer = new FileWriter(filePath);

		Set<Entry<String, Map<String, AuthorContribution>>> authorSet = clAuthorInfo
				.entrySet();
		for (Entry<String, Map<String, AuthorContribution>> classAuthor : authorSet) {

			Set<Entry<String, AuthorContribution>> authoInfo = classAuthor
					.getValue().entrySet();

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

		return stFile;
	}
}
