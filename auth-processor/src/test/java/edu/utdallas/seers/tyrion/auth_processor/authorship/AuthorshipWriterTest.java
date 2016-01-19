package edu.utdallas.seers.tyrion.auth_processor.authorship;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import java.util.Vector;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.utdallas.seers.tyrion.auth_processor.App;
import edu.utdallas.seers.tyrion.auth_processor.authorship.contrib.AuthorInfo;
import seers.cvsanalyzer.git.CommitBean;
import seers.cvsanalyzer.git.GitUtilities;

public class AuthorshipWriterTest {

	private static String baseFolder = "test_folder" + File.separator;
	private static String projectName2 = "zookeeper2";
	private static String projectName3 = "zookeeper3";
	private static String logFilePath2 = baseFolder + "logs" + File.separator + "log-" + projectName2 + ".txt";
	private static String projectFolder = baseFolder + "cloning" + File.separator + projectName3;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testWriteAuthorship() throws IOException, ParseException {

		Vector<CommitBean> commits = GitUtilities.readCommits(logFilePath2);

		String[] sourceSubFolders = new String[] { "src" + File.separator + "java" + File.separator + "main" };

		String[] classPaths = null;

		Map<String, AuthorInfo> authorInfo = new AuthorshipExtractor(projectFolder, sourceSubFolders, classPaths)
				.getClassAuthorContributions(commits);

		AuthorshipWriter writter = new AuthorshipWriter();
		String[] outfilePaths = { baseFolder + File.separator + projectName3 + "-" + App.AUTHOR_HISTORY_TXT,
				baseFolder + File.separator + projectName3 + "-" + App.AUTHOR_FIRST_TXT,
				baseFolder + File.separator + projectName3 + "-" + App.AUTHOR_JAVADOC_TXT };
		File[] files = writter.writeAuthorInfo(authorInfo, outfilePaths);

		File file = files[0];
		String lineExpected = "src.java.main.org.apache.jute.XmlInputArchive.XmlIndex;phunt@apache.org;3;0.75;breed@apache.org;1;0.25";
		Integer expNumLines = 16;
		assertFile(file, lineExpected, expNumLines);

		file = files[1];
		lineExpected = "src.java.main.org.apache.zookeeper.Watcher.Event.KeeperState;phunt@apache.org;8;1.0";
		assertFile(file, lineExpected, expNumLines);

		file = files[2];
		lineExpected = "src.java.main.org.apache.zookeeper.OpResult;author1;1;1.0";
		assertFile(file, lineExpected, expNumLines);
	}

	private void assertFile(File file, String lineExpected, Integer expectNumLines)
			throws FileNotFoundException, IOException {
		assertTrue(file.exists());

		BufferedReader br = new BufferedReader(new FileReader(file));

		String readLine;
		boolean actual = false;
		Integer numLines = 0;
		while ((readLine = br.readLine()) != null) {
			numLines++;
			if (lineExpected.equals(readLine)) {
				actual = true;
			}
		}
		assertTrue(actual);
		assertEquals(expectNumLines, numLines);
		br.close();

		file.delete();
		assertTrue(!file.exists());
	}
}
