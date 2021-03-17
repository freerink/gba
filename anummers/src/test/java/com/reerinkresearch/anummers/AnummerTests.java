package com.reerinkresearch.anummers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.reerinkresearch.pl.Anummer;

class AnummerTests {

	@Test
	void testInvalidAnummerSum() {
		long anummer = 1010101024L;
		Anummer a = new Anummer(anummer);

		boolean isValid = a.isValid();
		assertEquals("Rest na delen door 11 van sum(a0...a9)=10 moet 0 of 5 zijn, rest=10", a.getError());
		assertFalse(isValid, "A nummer should be invalid");
		assertEquals(anummer, a.getAnummer(), "Anummer");
		assertEquals(anummer + 1, a.getSkipTo(), "skipTo");
	}

	@Test
	void testCascade() {
		long anummer = 1010101024L;
		Anummer a = new Anummer(anummer);

		boolean isValid = a.isValid();
		assertEquals("Rest na delen door 11 van sum(a0...a9)=10 moet 0 of 5 zijn, rest=10", a.getError());
		assertFalse(isValid, "A nummer should be invalid");
		assertEquals(anummer, a.getAnummer(), "Anummer");
		assertEquals(anummer + 1, a.getSkipTo(), "skipTo");
		
		Anummer b = new Anummer(a.getSkipTo());

		isValid = b.isValid();
		assertNull(b.getError(), "No error");
		assertTrue(isValid, "A nummer should be invalid");
		assertEquals(anummer + 1, b.getAnummer(), "Anummer");
		assertEquals(anummer + 2, b.getSkipTo(), "skipTo");
		
	}

	@Test
	void testInvalidAnummerSequence() {
		long anummer = 1010101033L;
		Anummer a = new Anummer(anummer);

		boolean isValid = a.isValid();
		assertEquals("Opeenvolgende cijfers moeten ongelijk zijn", a.getError());
		assertFalse(isValid, "A nummer should be invalid");
		assertEquals(anummer, a.getAnummer(), "Anummer");
		assertEquals(anummer + 1, a.getSkipTo(), "skipTo");
	}


	@Test
	void testInvalidAnummerEdge() {
		long anummer = 1010101099L;
		Anummer a = new Anummer(anummer);

		boolean isValid = a.isValid();
		assertEquals("Opeenvolgende cijfers moeten ongelijk zijn", a.getError());
		assertFalse(isValid, "A nummer should be invalid");
		assertEquals(anummer, a.getAnummer(), "Anummer");
		assertEquals(anummer + 2, a.getSkipTo(), "skipTo");
	}

	@Test
	void testInvalidAnummerLoop() {
		long anummer = 1010101026L;
		Anummer a = new Anummer(anummer);;

		for(int i = 0; i < 1000; i++) {
			if ( a.isValid() ) {
				assertEquals(156, i, "Iterations");
				break;
			}
			a = new Anummer(a.getSkipTo());
		}
		boolean isValid = a.isValid();
		assertNull(a.getError(), "No error");
		assertTrue(isValid, "A nummer should be invalid");
		assertEquals(1010101291L, a.getAnummer(), "Anummer");
		assertEquals(1010101292L, a.getSkipTo(), "skipTo");
	}

	@Test
	void testInvalidAnummerMultiSum() {
		long anummer = 1010101034L;
		Anummer a = new Anummer(anummer);

		boolean isValid = a.isValid();
		assertEquals("Rest na delen door 11 van sum((1*a0)...(512*a9))=2901 moet 0 zijn, rest=8", a.getError());
		assertFalse(isValid, "A nummer should be invalid");
		assertEquals(anummer, a.getAnummer(), "Anummer");
		assertEquals(anummer + 1, a.getSkipTo(), "skipTo");
	}

	@Test
	void testInvalidAnummerEnd() {
		long anummer = 9898989899L;
		Anummer a = new Anummer(anummer);

		boolean isValid = a.isValid();
		assertEquals("Opeenvolgende cijfers moeten ongelijk zijn", a.getError());
		assertFalse(isValid, "A nummer should be invalid");
		assertEquals(9898989901L, a.getSkipTo(), "skipTo");
		assertEquals(anummer, a.getAnummer(), "Anummer");
	}

	@Test
	void testSomeValidNummers() {
		long[] anummers = { 1010101025L, 1010101291L, 7956913492L, 3601732713L, 9535931450L, 1879287373L, 1964026943L, 2495630915L, 9268409548L,
				3525303417L, 8172690967L, 7397613143L };

		for (int i = 0; i < anummers.length; i++) {
			Anummer a = new Anummer(anummers[i]);
			boolean isValid = a.isValid();
			assertTrue(isValid, "A nummer should be valid" + a.getAnummer());
			assertNull(a.getError(), "No error message expected");
			assertEquals(0, a.getErrorCode(), "Success expected");
			assertEquals(anummers[i], a.getAnummer(), "Anummer");
			assertEquals(anummers[i] + 1, a.getSkipTo(), "skipTo");
		}
	}

}
