package main.java.com.challenge.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.View;
import org.jgroups.util.Util;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import main.java.com.challenge.node.AppNode;

public class AppNodeUnitTest extends AppNode {

	@Mock
	protected AppNode appNode;
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
		a = Mockito.spy(Util.createRandomAddress("A"));
		b = Mockito.spy(Util.createRandomAddress("B"));
		c = Mockito.spy(Util.createRandomAddress("C"));
		d = Mockito.spy(Util.createRandomAddress("D"));
		e = Mockito.spy(Util.createRandomAddress("E"));
		f = Mockito.spy(Util.createRandomAddress("F"));
		g = Mockito.spy(Util.createRandomAddress("G"));
		h = Mockito.spy(Util.createRandomAddress("H"));
		i = Mockito.spy(Util.createRandomAddress("I"));
		members = Arrays.asList(a, b, c, d, e, f, g, h);
		view = Mockito.spy(View.create(a, 34, a, b, c, d, e, f, g, h));
		appNode = Mockito.spy(new AppNode(null, null, false, view));

	}

	@Test
	public final void testViewAcceptedView() throws InterruptedException {
		appNode.viewAccepted(view);
		Assert.assertNotNull(appNode.getView());
	}

	@Test(expected = NullPointerException.class)
	public final void testReceiveMessage() throws NullPointerException {
		Message msg = null;
		appNode.receive(msg);
	}

	@Test(expected = NullPointerException.class)
	public final void testGetStateOutputStream() throws Exception {
		appNode.getState(null);
	}

	@Test
	public final void testSetStateInputStream() throws Exception {
		String test = "test";
		byte[] stringByte = test.getBytes();
		ByteArrayInputStream bos = new ByteArrayInputStream(stringByte, 0, test.length());

		Mockito.doThrow(new Exception()).doNothing().when(appNode).setState(bos);

	}

	@Test
	public final void testGetNewNodes() {
		Address test = Util.createRandomAddress("new");
		View new_view = View.create(a, 34, a, b, c, d, e, f, g, h, test);
		Assert.assertNotNull(appNode.getNewNodes(new_view));

	}
	
	@Test
	public final void testIsCoordinator() {
		boolean isCoor = appNode.isCoordinator();
		Assert.assertNotNull(isCoor);
	}
	
	@Test
	public final void testGetCoordinator() {
		Address coor = appNode.getCoordinator();
		Assert.assertNotNull(coor);
	}
	
	@Test
	public final void testWaitForAllAvailableNodes() {
		int nodeCount = appNode.waitForAllAvailableNodes();
		Assert.assertNotNull(nodeCount);
		Assert.assertTrue(nodeCount>0);
	}
	
	@Test(expected = NullPointerException.class)
	public final void testSendMessageTo() {
		appNode.sendMessageTo(null, null);
		
	}
	
	@Test
	public final void testPerformTask() {
		appNode.performTask();
		Assert.assertTrue(appNode.isCompleted()==true);
	}

	
	@Test(expected = NullPointerException.class)
	public final void testBroadcastTaskStatus() {
		appNode.broadcastTaskStatus(null);
		
	}

}
