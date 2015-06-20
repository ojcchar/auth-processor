package edu.utdallas.seers.tyrion.auth_processor.git;

import java.util.Date;
import java.util.Vector;

public class CommitBean {

	private String commitId;
	private String authorEmail;
	private Date authorDate;
	private String committerEmail;
	private Date committerDate;
	private String commitMessage;
	private Vector<String> modifiedFiles;
	private Vector<String> addedFiles;
	private Vector<String> deletedFiles;

	public String getAuthor() {
		return authorEmail;
	}

	public void setAuthor(String author) {
		this.authorEmail = author;
	}

	public Vector<String> getModifiedFiles() {
		return modifiedFiles;
	}

	public void setModifiedFiles(Vector<String> modifiedFiles) {
		this.modifiedFiles = modifiedFiles;
	}

	public Vector<String> getAddedFiles() {
		return addedFiles;
	}

	public void setAddedFiles(Vector<String> addedFiles) {
		this.addedFiles = addedFiles;
	}

	public Vector<String> getDeletedFiles() {
		return deletedFiles;
	}

	public void setDeletedFiles(Vector<String> deletedFiles) {
		this.deletedFiles = deletedFiles;
	}

	public String getCommitId() {
		return commitId;
	}

	public void setCommitId(String commitId) {
		this.commitId = commitId;
	}

	public String getCommitMessage() {
		return commitMessage;
	}

	public void setCommitMessage(String commitMessage) {
		this.commitMessage = commitMessage;
	}

	public Date getDate() {
		return authorDate;
	}

	public void setDate(Date date) {
		this.authorDate = date;
	}

	public String getCommitterEmail() {
		return committerEmail;
	}

	public void setCommitterEmail(String committerEmail) {
		this.committerEmail = committerEmail;
	}

	public Date getCommitterDate() {
		return committerDate;
	}

	public void setCommitterDate(Date committerDate) {
		this.committerDate = committerDate;
	}

	@Override
	public String toString() {
		return "CommitBean [commitId=" + commitId + ", authorEmail="
				+ authorEmail + ", authorDate=" + authorDate
				+ ", committerEmail=" + committerEmail + ", committerDate="
				+ committerDate + "]";
	}

}