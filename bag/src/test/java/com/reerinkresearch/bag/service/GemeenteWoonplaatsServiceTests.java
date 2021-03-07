package com.reerinkresearch.bag.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class GemeenteWoonplaatsServiceTests {

	@Test
	void testStore () {
		GemeenteWoonplaatsService s = new GemeenteWoonplaatsService();
		
		assertTrue(s.store(123, 456), "Store to an empty store should be ok");
	
		// Assert
		assertEquals(123, s.getGemeente(456), "Find gemeente for woonplaats");
		assertEquals(null, s.getGemeente(4567), "No gemeente found for woonplaats");
		List<Integer> expect = new ArrayList<Integer>();
		expect.add(456);
		assertEquals(expect, s.getWoonplaatsen(123), "Find woonplaatsen for gemeente");
		assertFalse(s.store(123, 456), "Store the same again should fail");
		
		// Do some more
		assertTrue(s.store(123, 4567), "Store to an non empty store should be ok");
		
		// Assert
		assertEquals(123, s.getGemeente(456), "Find gemeente for woonplaats");
		assertEquals(123, s.getGemeente(4567), "Find gemeente for woonplaats");
		expect.add(4567);
		assertEquals(expect, s.getWoonplaatsen(123), "Find woonplaatsen for gemeente");
		assertFalse(s.store(123, 456), "Store the same again should fail");

		// Do some more
		assertTrue(s.store(1234, 12), "Store to an non empty store should be ok");

		// Assert counts
		assertEquals(2, s.getGemeenteCount());
		assertEquals(3, s.getWoonplaatsCount());
	}
}
