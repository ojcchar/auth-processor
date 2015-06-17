package edu.utdallas.seers.tyrion.auth_processor.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class GitUtilitiesTest {

	private static String baseFolder = "test_folder" + File.separator;
	private static String projectName = "zookeeper";
	private static String projectName0 = "zookeeper0";
	private static String repositoryAddress = "https://github.com/apache/zookeeper.git";
	private static String destinationFolder = baseFolder + "cloning"
			+ File.separator + projectName;
	private static String destinationFolder0 = baseFolder + "cloning"
			+ File.separator + projectName0;
	private static String projectName2 = "zookeeper2";
	private static String logFilePath = baseFolder + "logs" + File.separator
			+ projectName2 + "_log.txt";
	private static String logFilePath2 = baseFolder + "logs" + File.separator
			+ "log-" + projectName2 + ".txt";
	private static final String tag = "release-3.0.0";
	private static File clonedProject;
	private static File clonedProject0;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		if (clonedProject != null && clonedProject.exists()) {
			FileUtils.deleteDirectory(clonedProject);
		}
		if (clonedProject0 != null && clonedProject0.exists()) {
			FileUtils.deleteDirectory(clonedProject0);
		}
	}


	@Test
	public void testCloneGitRepository() throws IOException,
			InterruptedException {

		int resp = GitUtilities.cloneGitRepository(repositoryAddress,
				destinationFolder);

		clonedProject = new File(destinationFolder);
		assertTrue(resp == 0);
		assertTrue(clonedProject.exists());
	}

	@Test
	public void testCheckoutToTag() throws IOException,
			InterruptedException {

		int resp = GitUtilities.cloneGitRepository(repositoryAddress,
				destinationFolder0);

		clonedProject0 = new File(destinationFolder0);
		assertTrue(resp == 0);
		assertTrue(clonedProject0.exists());

		resp = GitUtilities.checkoutToTag(destinationFolder0, tag);

		assertTrue(resp == 0);

	}

	@Test
	public void testGetLogFromGitRepository() throws IOException,
			InterruptedException {

		String repositoryPath = baseFolder + "cloning" + File.separator
				+ projectName2;
		File file = new File(logFilePath);
		int resp = GitUtilities.saveLogFromGitRepository(
				file.getAbsolutePath(),
				new File(
				repositoryPath).getAbsolutePath());
		assertTrue(resp == 0);

		assertTrue(file.exists());

		BufferedReader br = new BufferedReader(new FileReader(file));
		if (br.readLine() == null) {
			fail("The log file is empty");
		}
		br.close();

		file.delete();
		assertTrue(!file.exists());

	}

	@Test
	public void testReadCommits() throws IOException, InterruptedException,
			ParseException {

		Vector<CommitBean> commits = GitUtilities.readCommits(logFilePath2);

		assertEquals(1349, commits.size());

		CommitBean commitBean = commits.get(0);

		assertEquals("7288576", commitBean.getCommitId());
		assertEquals(2, commitBean.getModifiedFiles().size());
	}
}
