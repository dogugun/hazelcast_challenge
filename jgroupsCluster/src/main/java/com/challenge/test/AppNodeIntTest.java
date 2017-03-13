package main.java.com.challenge.test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.View;
import org.jgroups.util.Util;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;




import main.java.com.challenge.node.AppNode;

public class AppNodeIntTest extends AppNode {

	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

	private final String testLine ="We are started!";
	
	@Mock
	protected AppNode appNode1;

	@Mock
	protected AppNode appNode2;
	@Mock
	protected Address a, b, c, d, e, f, g, h, i;
	@Mock
	protected View view;
	@Mock
	protected List<Address> members;
	@Mock
	protected JChannel channel;

	@Before
	public void setUp() throws Exception {
		appNode1 = Mockito.spy(AppNode.class);
	}

	@Before
	public void setUpStreams() {
		System.setOut(new PrintStream(outContent));
		System.setErr(new PrintStream(errContent));
	}

	@After
	public void cleanUpStreams() {
		System.setOut(null);
		System.setErr(null);
	}

	@Test
	public void testSingleNode() throws Exception {
		Runnable r1 = new Runnable() {
			@Override
			public void run() {
				try {
					appNode1.main(null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		Thread firstInstance = new Thread(r1);
		firstInstance.start();
		Thread.sleep(6000);
		firstInstance.stop();

		int lastIndex = 0;
		int count = 0;
		while (lastIndex != -1) {
			lastIndex = outContent.toString().indexOf(testLine, lastIndex);
			if (lastIndex != -1) {
				count++;
				lastIndex += testLine.length();
			}
		}
		Assert.assertEquals(1, count);
	}

	@Test
	public void testTenNodes() throws Exception {

		for (int i = 0; i < 10; i++) {
			Runnable r1 = new Runnable() {
				@Override
				public void run() {
					try {
						appNode1.main(null);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			Thread firstInstance = new Thread(r1);
			firstInstance.start();
			if(i==0) {
				Thread.sleep(6000);	
			}
			firstInstance.stop();
		}

		int lastIndex = 0;
		int count = 0;
		while (lastIndex != -1) {
			lastIndex = outContent.toString().indexOf(testLine, lastIndex);
			if (lastIndex != -1) {
				count++;
				lastIndex += testLine.length();
			}
		}
		Assert.assertEquals(1, count);
	}
}
