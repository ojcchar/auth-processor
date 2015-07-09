package edu.utdallas.seers.tyrion.auth_processor.authorship;

import java.util.List;
import java.util.Map;

import edu.utdallas.seers.tyrion.auth_processor.git.CommitBean;

public class AuthorInfo {

	private List<String> javaDocAuthors;
	private CommitBean firstCommit;
	private Map<String, AuthorContribution> contribution;

	public AuthorInfo(Map<String, AuthorContribution> contribution,
			CommitBean firstCommit, List<String> javaDocAuthors) {
		this.contribution = contribution;
		this.firstCommit = firstCommit;
		this.javaDocAuthors = javaDocAuthors;
	}

	public List<String> getJavaDocAuthors() {
		return javaDocAuthors;
	}

	public void setJavaDocAuthors(List<String> javaDocAuthors) {
		this.javaDocAuthors = javaDocAuthors;
	}

	public CommitBean getFirstCommit() {
		return firstCommit;
	}

	public void setFirstCommit(CommitBean firstCommit) {
		this.firstCommit = firstCommit;
	}

	public Map<String, AuthorContribution> getContribution() {
		return contribution;
	}

	public void setContribution(Map<String, AuthorContribution> contribution) {
		this.contribution = contribution;
	}

}
