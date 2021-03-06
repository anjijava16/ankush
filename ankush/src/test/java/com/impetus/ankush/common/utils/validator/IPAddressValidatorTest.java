/*******************************************************************************
 * ===========================================================
 * Ankush : Big Data Cluster Management Solution
 * ===========================================================
 * 
 * (C) Copyright 2014, by Impetus Technologies
 * 
 * This is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License (LGPL v3) as
 * published by the Free Software Foundation;
 * 
 * This software is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this software; if not, write to the Free Software Foundation, 
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 ******************************************************************************/
package com.impetus.ankush.common.utils.validator;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * The Class IPAddressValidatorTest.
 */
public class IPAddressValidatorTest {
	
	/**
	 * Test ip address validator_1.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testIPAddressValidator_1()
		throws Exception {
		IPAddressValidator result = new IPAddressValidator();
		assertNotNull(result);
	}

	/**
	 * Test validate_1.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testValidate_1()
		throws Exception {
		String ip = "192.168.0.0";

		boolean result = IPAddressValidator.validate(ip);

		assertEquals(true, result);
	}

	/**
	 * Test validate_2.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testValidate_2()
		throws Exception {
		String ip = "";

		boolean result = IPAddressValidator.validate(ip);

		assertEquals(false, result);
	}

}
