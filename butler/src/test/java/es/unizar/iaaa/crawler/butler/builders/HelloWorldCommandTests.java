
package es.unizar.iaaa.crawler.butler.builders;

import es.unizar.iaaa.crawler.butler.Application;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandResult;
import org.springframework.shell.core.JLineShellComponent;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={Application.class})
public class HelloWorldCommandTests {

	@Autowired
	private JLineShellComponent shell;
	 
	@Test
	@Ignore
	public void testSimple() {
		CommandResult cr = shell.executeCommand("date");
		assertEquals(true, cr.isSuccess());
		assertEquals("Message = [hello] Location = [null]", cr.getResult());
	}
}