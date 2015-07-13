package edu.utdallas.seers.tyrion.auth_processor.authorship.contrib;

public class AuthorContribution {

	private int numMod;
	private double percMod;

	public AuthorContribution(int numMod, double percMod) {
		super();
		this.numMod = numMod;
		this.percMod = percMod;
	}

	public AuthorContribution() {
	}

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
