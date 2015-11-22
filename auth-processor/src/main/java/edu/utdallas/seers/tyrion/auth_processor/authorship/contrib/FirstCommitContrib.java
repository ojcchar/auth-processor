package edu.utdallas.seers.tyrion.auth_processor.authorship.contrib;

import seers.cvsanalyzer.git.CommitBean;

public class FirstCommitContrib {

	private CommitBean commit;
	private AuthorContribution contrib;

	public FirstCommitContrib(CommitBean commit, AuthorContribution contrib) {
		super();
		this.commit = commit;
		this.contrib = contrib;
	}

	public CommitBean getCommit() {
		return commit;
	}

	public void setCommit(CommitBean commit) {
		this.commit = commit;
	}

	public AuthorContribution getContrib() {
		return contrib;
	}

	public void setContrib(AuthorContribution contrib) {
		this.contrib = contrib;
	}

}
