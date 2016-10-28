package net.vickymedia.hystrix;

import co.paralleluniverse.test.categories.CI;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.concurrent.TimeUnit;

/**
 * Created by vee on 10/27/16.
 */
@Category(CI.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SampleJettyApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@DirtiesContext
public class SampleJettyApplicationTests {

	@Value("${local.server.port}")
	private int port;

	@Test
	public void testHome() throws Exception {
		String url = "http://localhost:" + this.port + "/";
		System.out.println( "url: " + url );
//		TimeUnit.HOURS.sleep(1);
//		new TestRestTemplate().getForEntity(url, String.class);
		TimeUnit.MILLISECONDS.sleep(300);
		new TestRestTemplate().getForEntity(url, String.class);
//		for ( int i = 0; i < 2; ++i ) {
//			TimeUnit.MILLISECONDS.sleep(300);
//			new TestRestTemplate().getForEntity(url, String.class);
//		}
//		for ( int i = 0; i < 10; ++i ) {
//			for ( int j = 0; j < 1000; ++j ) {
//				TimeUnit.MILLISECONDS.sleep(300);
//				new TestRestTemplate().getForEntity(url, String.class);
//			}
//			TimeUnit.MINUTES.sleep(1);
//		}
	}

}
