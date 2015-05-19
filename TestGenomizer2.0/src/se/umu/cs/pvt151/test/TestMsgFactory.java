/**
 * 
 */
package se.umu.cs.pvt151.test;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import se.umu.cs.pvt151.com.MsgFactory;
import se.umu.cs.pvt151.model.GeneFile;
import junit.framework.TestCase;

/**
 * @author ens11pnn
 *
 */
public class TestMsgFactory extends TestCase {



	/**
	 * Test method for {@link se.umu.cs.pvt151.com.
	 * MsgFactory#createLogin(java.lang.String, java.lang.String)}.
	 */
	public void testCreateLogin() {
		try {
			JSONObject msg = MsgFactory.createLogin("user", "pass");
			assertEquals("user", msg.get("username"));
			assertEquals("pass", msg.get("password"));
		} catch (JSONException e) {
			fail("JSON exception was thrown");
		}
	}

	/**
	 * Test method for {@link se.umu.cs.pvt151.com.
	 * MsgFactory#createConversionRequest(java.util.ArrayList, 
	 * se.umu.cs.pvt151.model.GeneFile, java.lang.String, java.lang.String)}.
	 */
	public void testCreateConversionRequest() {
		try {
			ArrayList<String> parameters = new ArrayList<String>();
			parameters.add("param1");
			parameters.add("param2");
			
			GeneFile file = new GeneFile();
			
			JSONObject msg = MsgFactory.createConversionRequest(
					parameters, file, "meta", "release");
			assertEquals("meta", msg.getString("metadata"));
			assertEquals("release", msg.getString("genomeVersion"));
		} catch (JSONException e) {
			fail("JSON exception was thrown");
		}
	}

}
