package edu.utdallas.seers.tyrion.auth_processor.authorship;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import java.util.Vector;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.utdallas.seers.tyrion.auth_processor.git.CommitBean;
import edu.utdallas.seers.tyrion.auth_processor.git.GitUtilities;

public class AuthorshipWritterTest {

	private static String baseFolder = "test_folder" + File.separator;
	private static String projectName2 = "zookeeper2";
	private static String projectName3 = "zookeeper3";
	private static String logFilePath2 = baseFolder + "logs" + File.separator
			+ "log-" + projectName2 + ".txt";
	private static String projectFolder = baseFolder + "cloning"
			+ File.separator + projectName3;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testWriteAuthorship() throws IOException, ParseException {

		Vector<CommitBean> commits = GitUtilities.readCommits(logFilePath2);

		String[] sourceSubFolders = new String[] { "src" + File.separator
				+ "java" + File.separator + "main" };

		String[] classPaths = null;

		Map<String, Map<String, AuthorContribution>> contributions = new AuthorshipExtractor(
				projectFolder, sourceSubFolders, classPaths)
				.getClassAuthorContributions(commits);

		AuthorshipWritter writter = new AuthorshipWritter();
		String filePath = baseFolder + File.separator + "authorship.csv";
		File file = writter.writeAuthorship(contributions, filePath);

		assertTrue(file.exists());

		BufferedReader br = new BufferedReader(new FileReader(file));

		String lineExpected = "src.java.main.org.apache.jute.XmlInputArchive$XmlIndex;phunt@apache.org;3;0.75;breed@apache.org;1;0.25";
		String readLine;
		boolean actual = false;
		while ((readLine = br.readLine()) != null) {
			if (lineExpected.equals(readLine)) {
				actual = true;
			}
		}
		assertTrue(actual);
		br.close();

		file.delete();
		assertTrue(!file.exists());
	}
}
