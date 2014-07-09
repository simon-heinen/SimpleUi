package meta;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import module.meta.Meta;
import module.meta.MetaAttr;
import module.meta.RegexUtils;

import org.junit.Test;

public class MetaTests {

	public class ExampleClass {

		@MetaAttr(type = MetaAttr.TYPE_ID + MetaAttr.FIELDS_READ_ONLY
				+ MetaAttr.FIELDS_UNIQ_IN_SCOPE)
		private long id;

		@MetaAttr(regex = "YES|MAYBE|NO")
		private String myField1;
		@MetaAttr(regex = RegexUtils.EMAIL_ADDRESS)
		private String email;
		@MetaAttr(type = MetaAttr.TYPE_DATE_LONG)
		private Long createDate;

	}

	@Test
	public void testMeta() {

		Meta meta = new Meta();

		ExampleClass a = new ExampleClass();
		a.id = 2l;
		a.myField1 = "YES";
		a.email = "firstmail@b.com";
		a.createDate = null;

		// test if all fields in object A are valid:
		assertTrue(meta.validate(a));

		ExampleClass newA = meta.cloneViaGson(a);
		String validMail = "secondMail@d.com";
		newA.email = validMail;

		// simulate a change in the object:
		assertTrue(meta.updateObject(a, newA));

		// test if the valid changes were applied to obj A:
		assertTrue(a.email.equals(validMail));

		ExampleClass invalidA = meta.cloneViaGson(a);
		invalidA.id = 3l; // changing the id later on is not allowed

		// test if the validation notices the invalid change:
		assertFalse(meta.validateUpdateObject(a, invalidA));

		// test if the update method does not update a:
		assertFalse(meta.updateObject(a, invalidA));
		assertTrue(a.id == 2l);

		// print out a and invalidA as json:
		meta.toString("a", a);
		meta.toString("invalidA", invalidA);

	}

}
