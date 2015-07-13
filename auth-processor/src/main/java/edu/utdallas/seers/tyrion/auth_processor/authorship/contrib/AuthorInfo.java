package edu.utdallas.seers.tyrion.auth_processor.authorship.contrib;

import java.util.Map;

public class AuthorInfo {

	private Map<String, AuthorContribution> javaDocAuthors;
	private FirstCommitContrib firstCommit;
	private Map<String, AuthorContribution> historyContrib;

	public AuthorInfo(Map<String, AuthorContribution> contribution,
			FirstCommitContrib firstCommit,
			Map<String, AuthorContribution> javaDocAuthors) {
		this.historyContrib = contribution;
		this.firstCommit = firstCommit;
		this.javaDocAuthors = javaDocAuthors;
	}

	public Map<String, AuthorContribution> getHistoryContrib() {
		return historyContrib;
	}

	public FirstCommitContrib getFirstCommit() {
		return firstCommit;
	}

	public Map<String, AuthorContribution> getJavaDocAuthors() {
		return javaDocAuthors;
	}

}
