package edu.utdallas.seers.tyrion.auth_processor;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class AppTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

	}

	@Test
	public void testMain() throws IOException {
		// Arguments: [git_repo_url] [base_folder] [project_name]
		// [comma_separated_source_folders] [project_version]
		String baseFolder = "test_folder";
		String projectName = "zookeeper";
		String projectVersion = "10x";
		String tag = "release-3.4.5";
		String[] args = {
				"https://github.com/apache/zookeeper.git",
				baseFolder,
				projectName,
				"src" + File.separator + "java" + File.separator + "main;src"
						+ File.separator + "java" + File.separator + "test",
				projectVersion, tag };
		App.main(args);

		// ----------------------------------------------------

		String projectFolder = baseFolder + File.separator + projectName;
		File fileAuthor = new File(projectFolder + File.separator + projectName
				+ "-" + projectVersion + "_Authorship.txt");
		BufferedReader br = new BufferedReader(new FileReader(fileAuthor));
		String line = br.readLine();
		if (line == null) {
			fail("The log file is empty");
		}
		br.close();

		// ----------------------------------------------------

		fileAuthor.delete();
		assertTrue(!fileAuthor.exists());

		// ----------------------------------------------------

		File file = new File(projectFolder + File.separator + projectName + "-"
				+ projectVersion + ".log");
		file.delete();
		assertTrue(!file.exists());

		// ----------------------------------------------------

		File clonedProject = new File(projectFolder);
		FileUtils.deleteDirectory(clonedProject);
		assertTrue(!clonedProject.exists());
		clonedProject.mkdir();
		assertTrue(clonedProject.exists());

	}
}
