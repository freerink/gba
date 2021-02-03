package com.reerinkresearch.anummers;

public class Anummer {
	private final long index;
	private final long anummer;
	private int gemeenteCode;
	private String error = null;
	private int errorCode = 0;
	private long skipTo;

	public Anummer(long index, long anummer) {
		this.index = index;
		this.anummer = anummer;
	}

	public long getIndex() {
		return this.index;
	}

	public long getAnummer() {
		return this.anummer;
	}

	public String getError() {
		return this.error;
	}

	public int getErrorCode() {
		return this.errorCode;
	}
	
	private void calculateSkipTo(int consecutiveIssueIndex) {
		char[] anummer = ("" + this.anummer).toCharArray();

		if( anummer[consecutiveIssueIndex] < '9') {
			// Increment digit that causes the issue
			anummer[consecutiveIssueIndex]++;
			// Reset all consecutive digits
			boolean nextDigitIsZero = true;
			for (int i = consecutiveIssueIndex + 1; i < anummer.length; i++) {
				anummer[i] = nextDigitIsZero ? '0' : '1';
				nextDigitIsZero = !nextDigitIsZero;
			}
			this.skipTo = Long.parseLong(new String(anummer));
			// Issue is solved
			this.errorCode = 0;
		} else {
			// example v
			//  1010109900 ->
			//  1010110101 ->
			//  1010120101
			if(consecutiveIssueIndex > 2) {
				anummer[consecutiveIssueIndex - 2]++;
				// Reset all consecutive digits
				boolean nextDigitIsZero = true;
				for (int i = consecutiveIssueIndex - 1; i < anummer.length; i++) {
					anummer[i] = nextDigitIsZero ? '0' : '1';
					nextDigitIsZero = !nextDigitIsZero;
				}
				this.skipTo = Long.parseLong(new String(anummer));
			} else {
				this.skipTo = this.anummer + 1;
				this.error = "Einde van de reeks";
				this.errorCode = 99;
			}
		}
	}

	public boolean isValid() {
		char[] anummer = ("" + this.anummer).toCharArray();

		// length exactly 10 digits
		if (anummer.length != 10) {
			this.error = "Lengte moet 10 zijn";
			this.errorCode = 10;
			return false;
		}

		// can't start with 0
		if (anummer[0] == '0') {
			this.error = "a0 mag niet 0 zijn";
			this.errorCode = 11;
			return false;
		}

		// consecutive digits may not be equal
		for (int i = 1; i < anummer.length; i++) {
			if (anummer[i - 1] == anummer[i]) {
				this.error = "Opeenvolgende cijfers moeten ongelijk zijn";
				this.errorCode = i;
				this.calculateSkipTo(i);
				return false;
			}
		}

		// the sum is divisible by 11 with rest 0 or 5
		int sum = 0;
		for (int i = 0; i < anummer.length; i++) {
			sum += anummer[i] - '0';
		}
		int rest = sum % 11;
		if ( !(rest == 0 || rest == 5) ) {
			this.error = "Rest na delen door 11 van sum(a0...a9)=" + sum + " moet 0 of 5 zijn, rest=" + rest;
			this.errorCode = 12;
			this.skipTo = this.anummer + 1;
			return false;
		}
		
		// (1*a0)+(2*a1)+(4*a2)+...+(512*a9) is divisible by 11 with rest 0
		sum = 0;
		int multiplier = 1;
		for (int i = 0; i < anummer.length; i++) {
			sum += multiplier * (anummer[i] - '0');
			multiplier *= 2;
		}
		rest = sum % 11;
		if ( !(rest == 0) ) {
			this.error = "Rest na delen door 11 van sum((1*a0)...(512*a9))=" + sum + " moet 0 zijn, rest=" + rest;
			this.errorCode = 13;
			this.skipTo = this.anummer + 1;
			return false;
		}

		this.skipTo = this.anummer + 1;
		return true;
	}

	public long getSkipTo() {
		return this.skipTo;
	}

	public int getGemeenteCode() {
		return gemeenteCode;
	}

	public void setGemeenteCode(int gemeenteCode) {
		this.gemeenteCode = gemeenteCode;
	}
}