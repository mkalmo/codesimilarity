package com.googlecode.ounit.codesimilarity;

/**
 * @author Urmas Hoogma
 */
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class HelperMethodsTests {

	@Test
	public void removeBoilerPlateTest() {
		List<Integer> first = new ArrayList<Integer>(Arrays.asList(1, 2, 3));
		List<Integer> second = new ArrayList<Integer>(Arrays.asList(0, 1, 3, 5));
		List<Integer> difference = Similarity.removeBoilerplate(first, second);
		Assert.assertEquals(difference,
				new ArrayList<Integer>(Arrays.asList(2)));
	}

	@Test
	public void replaceMultipleWhiteSpaceTest() {
		assertEquals(" content other content", SimilarityTest
				.replaceMultipleWhiteSpace("\t\tcontent\n\n other content"));
	}
}
