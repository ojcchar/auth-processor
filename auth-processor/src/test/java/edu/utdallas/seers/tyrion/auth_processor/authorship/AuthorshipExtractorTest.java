package edu.utdallas.seers.tyrion.auth_processor.authorship;

import static org.junit.Assert.assertEquals;

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

import edu.utdallas.seers.tyrion.auth_processor.git.CommitBean;
import edu.utdallas.seers.tyrion.auth_processor.git.GitUtilities;

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
				"src" + File.separator + "java" + File.separator + "test" };

		String[] classPaths = new String[] { destinationFolder };

		Map<String, Map<String, AuthorContribution>> contributions = new AuthorshipExtractor(
				destinationFolder, sourceSubFolders, classPaths)
				.getClassAuthorContributions(commits);

		String clName = "src.java.main.org.apache.zookeeper.ClientCnxn$AuthData";
		String authors[] = { "camille@apache.org", "henry@apache.org",
				"phunt@apache.org", "michim@apache.org", "mahadev@apache.org",
				"breed@apache.org", "fpj@apache.org", "rakeshr@apache.org" };
		int contrib[] = { 3, 3, 21, 6, 22, 13, 6, 2 };
		int total = 76;

		assertClass(contributions, clName, authors, contrib, total);

		clName = "src.java.main.org.apache.zookeeper.server.NettyServerCnxn";
		authors = new String[] { "camille@apache.org", "henry@apache.org",
				"phunt@apache.org", "michim@apache.org", "mahadev@apache.org",
				"fpj@apache.org", "rakeshr@apache.org" };
		contrib = new int[] { 4, 1, 3, 3, 3, 1, 1 };
		total = 16;

		assertClass(contributions, clName, authors, contrib, total);

		clName = "src.java.systest.org.apache.zookeeper.test.system.GenerateLoad$GeneratorInstance$ZooKeeperThread";
		authors = new String[] { "phunt@apache.org", "michim@apache.org",
				"mahadev@apache.org", "fpj@apache.org", "shralex@apache.org" };
		contrib = new int[] { 1, 2, 2, 1, 1 };
		total = 7;

		assertClass(contributions, clName, authors, contrib, total);

	}

	private void assertClass(
			Map<String, Map<String, AuthorContribution>> contributions,
			String clName, String[] authors, int[] contrib, int total) {
		System.out.println("Checking class [" + clName + "]");

		Map<String, AuthorContribution> clContr = contributions.get(clName);
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
	}
}
