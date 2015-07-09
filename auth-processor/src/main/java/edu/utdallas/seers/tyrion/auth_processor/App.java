package edu.utdallas.seers.tyrion.auth_processor;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.utdallas.seers.tyrion.auth_processor.authorship.AuthorInfo;
import edu.utdallas.seers.tyrion.auth_processor.authorship.AuthorshipExtractor;
import edu.utdallas.seers.tyrion.auth_processor.authorship.AuthorshipWritter;
import edu.utdallas.seers.tyrion.auth_processor.git.CommitBean;
import edu.utdallas.seers.tyrion.auth_processor.git.GitUtilities;

public class App {

	public static final String AUTHOR_HISTORY_TXT = "_Authorship_History.txt";
	public static final String AUTHOR_FIRST_TXT = "_Authorship_First.txt";
	public static final String AUTHOR_JAVADOC_TXT = "_Authorship_Javadoc.txt";

	private static Logger LOGGER = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {

		String repositoryAddress;
		String destinationFolder;
		String[] sourceSubFolders;
		String projectName;
		String projectVersion;
		String tag;
		try {
			repositoryAddress = args[0];
			destinationFolder = args[1];
			projectName = args[2];
			sourceSubFolders = args[3].split(";");
			projectVersion = args[4];
			tag = args[5];
		} catch (Exception e1) {
			LOGGER.error("Arguments error");
			LOGGER.info("Arguments: [git_repo_url] [base_folder] [project_name] [comma_separated_source_folders] [project_version] [git_tag]");
			return;
		}

		System.out.println("args: " + Arrays.toString(args));

		if ("null".equalsIgnoreCase(tag)) {
			tag = null;
		}

		try {
			String projectFolder = destinationFolder + File.separator
					+ projectName;
			String logFilePath = projectFolder + File.separator + projectName
					+ "-" + projectVersion + ".log";
			String[] outfilePaths = {
					projectFolder + File.separator + projectName + "-"
							+ projectVersion + AUTHOR_HISTORY_TXT,
					projectFolder + File.separator + projectName + "-"
							+ projectVersion + AUTHOR_FIRST_TXT,
					projectFolder + File.separator + projectName + "-"
							+ projectVersion + AUTHOR_JAVADOC_TXT };

			LOGGER.info("Cloning repository");
			GitUtilities.cloneGitRepository(repositoryAddress, projectFolder);

			LOGGER.info("Getting log from repository");
			GitUtilities.saveLogFromGitRepository(logFilePath, projectFolder,
					tag);

			LOGGER.info("Checking out tag");
			GitUtilities.checkoutToTag(projectFolder, tag);

			LOGGER.info("Reading log");
			Vector<CommitBean> commits = GitUtilities.readCommits(logFilePath);

			LOGGER.info("Extracting the author contributions");
			AuthorshipExtractor extractor = new AuthorshipExtractor(
					projectFolder, sourceSubFolders,
					new String[] { projectFolder });
			Map<String, AuthorInfo> authorInfo = extractor
					.getClassAuthorContributions(commits);

			LOGGER.info("Writing contributions");
			AuthorshipWritter writer = new AuthorshipWritter();
			File[] files = writer.writeAuthorInfo(authorInfo, outfilePaths);

			for (File file : files) {
				LOGGER.info("Info written in " + file.getAbsolutePath());
			}
			LOGGER.info("Done!");

		} catch (Exception e) {
			LOGGER.error("Unexpected error", e);
		}

	}
}
