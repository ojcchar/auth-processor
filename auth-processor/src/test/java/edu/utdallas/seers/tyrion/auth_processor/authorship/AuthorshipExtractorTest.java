package edu.utdallas.seers.tyrion.auth_processor.authorship;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.utdallas.seers.tyrion.auth_processor.authorship.contrib.AuthorContribution;
import edu.utdallas.seers.tyrion.auth_processor.authorship.contrib.AuthorInfo;
import seers.cvsanalyzer.git.CommitBean;
import seers.cvsanalyzer.git.GitUtilities;

public class AuthorshipExtractorTest {
	private static String baseFolder = "test_folder" + File.separator;
	private static String projectName2 = "zookeeper2";
	private static String logFilePath2 = baseFolder + "logs" + File.separator
			+ "log-" + projectName2 + ".txt";
	private static String destinationFolder = baseFolder + "cloning"
			+ File.separator + projectName2;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testGetClassAuthorContributions() throws IOException,
			ParseException {
		Vector<CommitBean> commits = GitUtilities.readCommits(logFilePath2);

		String[] sourceSubFolders = new String[] {
				"src" + File.separator + "java" + File.separator + "main",
				"src" + File.separator + "java" + File.separator + "systest",
				"src" + File.separator + "java" + File.separator + "test",
				"src" + File.separator + "contrib" + File.separator
						+ "zooinspector" + File.separator + "src"
						+ File.separator + "java" };

		String[] classPaths = new String[] { destinationFolder };

		Map<String, AuthorInfo> authorInfo = new AuthorshipExtractor(
				destinationFolder, sourceSubFolders, classPaths)
				.getClassAuthorContributions(commits);

		String clName = "src.java.main.org.apache.zookeeper.ClientCnxn$AuthData";
		String authors[] = { "camille@apache.org", "henry@apache.org",
				"phunt@apache.org", "michim@apache.org", "mahadev@apache.org",
				"breed@apache.org", "fpj@apache.org", "rakeshr@apache.org" };
		String firstCommitId = "0c68cec";
		String javadocAuthors[] = {};
		int contrib[] = { 3, 3, 21, 6, 22, 13, 6, 2 };
		int total = 76;
		int contribFirstCommit = contrib[2];

		assertClass(authorInfo, clName, authors, contrib, total, firstCommitId,
				javadocAuthors, contribFirstCommit);

		clName = "src.java.main.org.apache.zookeeper.server.NettyServerCnxn";
		authors = new String[] { "camille@apache.org", "henry@apache.org",
				"phunt@apache.org", "michim@apache.org", "mahadev@apache.org",
				"fpj@apache.org", "rakeshr@apache.org" };
		firstCommitId = "ed69746";
		contrib = new int[] { 4, 1, 3, 3, 3, 1, 1 };
		total = 16;
		contribFirstCommit = contrib[2];

		assertClass(authorInfo, clName, authors, contrib, total, firstCommitId,
				javadocAuthors, contribFirstCommit);

		clName = "src.java.systest.org.apache.zookeeper.test.system.GenerateLoad$GeneratorInstance$ZooKeeperThread";
		authors = new String[] { "phunt@apache.org", "michim@apache.org",
				"mahadev@apache.org", "fpj@apache.org", "shralex@apache.org" };
		firstCommitId = "f58e18b";
		contrib = new int[] { 1, 2, 2, 1, 1 };
		total = 7;
		contribFirstCommit = contrib[2];

		assertClass(authorInfo, clName, authors, contrib, total, firstCommitId,
				javadocAuthors, contribFirstCommit);

		clName = "src.contrib.zooinspector.src.java.org.apache.zookeeper.inspector.gui.ZooInspectorTreeViewer$ZooInspectorTreeCellRenderer";
		authors = new String[] { "phunt@apache.org", "michim@apache.org" };
		firstCommitId = "4426f7f";
		javadocAuthors = new String[] { "Colin" };
		contrib = new int[] { 2, 1 };
		total = 3;
		contribFirstCommit = contrib[0];

		assertClass(authorInfo, clName, authors, contrib, total, firstCommitId,
				javadocAuthors, contribFirstCommit);

	}

	private void assertClass(Map<String, AuthorInfo> authorInfo, String clName,
			String[] authors, int[] contrib, int total, String firstCommitId,
			String[] javadocAuthors, int contribFirstCommit) {
		System.out.println("Checking class [" + clName + "]");

		Map<String, AuthorContribution> clContr = authorInfo.get(clName)
				.getHistoryContrib();
		Set<Entry<String, AuthorContribution>> entrySet = clContr.entrySet();

		assertEquals(authors.length, entrySet.size());
		for (int i = 0; i < authors.length; i++) {

			String auth = authors[i];
			AuthorContribution contr = clContr.get(auth);

			double expecPerc = ((double) contrib[i]) / total;

			System.out.println("checking auth: [" + auth
					+ "] --> expected vals: [" + contrib[i] + ", " + expecPerc
					+ "], actual: [" + contr.getNumMod() + ", "
					+ contr.getPercMod() + "]");

			assertEquals(contrib[i], contr.getNumMod());
			assertEquals(expecPerc, contr.getPercMod(), 0.00000001);

		}

		// ---------------------------------------------------------

		String commitId = authorInfo.get(clName).getFirstCommit().getCommit()
				.getCommitId();
		assertEquals(firstCommitId, commitId);

		AuthorContribution contrib2 = authorInfo.get(clName).getFirstCommit()
				.getContrib();
		assertEquals(contribFirstCommit, contrib2.getNumMod());
		assertEquals(1.0, contrib2.getPercMod(), 0.0);

		// ---------------------------------------------------------

		Map<String, AuthorContribution> javaDocAuthors2 = authorInfo
				.get(clName)
				.getJavaDocAuthors();

		assertEquals(javadocAuthors.length, javaDocAuthors2.size());
		for (int i = 0; i < javadocAuthors.length; i++) {
			String auth = javadocAuthors[i];

			System.out.println("checking javadoc auth: [" + auth + "]");
			AuthorContribution jDocContrib = javaDocAuthors2.get(auth);
			assertNotNull(jDocContrib);

			assertEquals(1, jDocContrib.getNumMod());
			assertEquals(1.0 / javaDocAuthors2.size(),
					jDocContrib.getPercMod(), 0.0);
		}

	}
}
