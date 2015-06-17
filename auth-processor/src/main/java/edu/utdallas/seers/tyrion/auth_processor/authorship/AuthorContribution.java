package edu.utdallas.seers.tyrion.auth_processor.authorship;

public class AuthorContribution {

	private int numMod;
	private double percMod;

	public int getNumMod() {
		return numMod;
	}

	public double getPercMod() {
		return percMod;
	}

	public void setPercMod(double percMod) {
		this.percMod = percMod;
	}

	public void addNumMod() {
		numMod++;
	}

	@Override
	public String toString() {
		return "AuthorContribution [numMod=" + numMod + ", percMod=" + percMod
				+ "]";
	}

}
