package com.cflint;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.cflint.config.CFLintConfig;

import cfml.parsing.reporting.ParseException;

//TODO add literal checked to cflintdefinition
@Ignore
public class TestLiteralLocalChecker {

	private CFLint cfBugs;

	@Before
	public void setUp() throws Exception{
		final com.cflint.config.CFLintConfiguration conf = CFLintConfig.createDefaultLimited("LiteralChecker");
		cfBugs = new CFLint(conf);
	}

	@Test
	public void testOK() throws ParseException, IOException {
		final String scriptSrc = "<cfscript>\r\n"
			+ "var pi = 3.14;\r\n"
			+ "var code = \"CODE\";\r\n"
			+ "</cfscript>";
			
		cfBugs.process(scriptSrc, "test");
		final Map<String, List<BugInfo>> result = cfBugs.getBugs().getBugList();
		assertEquals(0, result.size());
	}

	@Test
	public void testTooManyHardCodevedValues() throws ParseException, IOException {
		final String scriptSrc = "<cfscript>\r\n"
			+ "pi = 3.14;\r\n"
			+ "calc = \"AREA\";\r\n"
			+ "if (code == \"AREA\") {\r\n"
			+ "area = 3.14 * radius * radius;\r\n"
			+ "circumference = 2 * 3.14 * radius;\r\n"
			+ "volume = 4/3 * 3.14 * radius * radius * radius;\r\n"
			+"}\r\n"
			+ "</cfscript>";
			
		cfBugs.process(scriptSrc, "test");
		final List<BugInfo> result = cfBugs.getBugs().getBugList().values().iterator().next();
		assertEquals(1, result.size());
		assertEquals("LOCAL_LITERAL_VALUE_USED_TOO_OFTEN", result.get(0).getMessageCode());
		assertEquals(7, result.get(0).getLine());
	}

}
