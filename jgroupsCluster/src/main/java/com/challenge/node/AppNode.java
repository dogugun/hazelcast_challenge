package main.java.com.challenge.node;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


	public class AppNode extends ReceiverAdapter {
	JChannel channel;
	String user_name = System.getProperty("user.name", "n/a");
	final List<String> state = new LinkedList<String>();
	private final String testLine ="We are started!";

	private final String OK = "OK";
	private final String NOK = "NOK";
	boolean isCompleted = false;
	View view = new View();

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public AppNode(){};
	
	public AppNode(JChannel channel, List<String> state, boolean isCompleted, View view) throws Exception {
		this.channel = new JChannel();
		this.channel.setReceiver(this);
		this.channel.connect("Cluster");
		this.channel.getState(null, 10000);
		
		this.isCompleted=isCompleted;
		this.view = view;
	}
	
	public void viewAccepted(View new_view) {

		List<Address> newNodes = getNewNodes(new_view);
		this.view = new_view;
		logger.debug("** view: " + new_view);
		if (isCoordinator() == false) {
			if (newNodes != null && newNodes.size() > 0) {
				broadcastTaskStatus(newNodes);
			}
		}
	}

	public void receive(Message msg) {
		if (msg.getObject().toString().equals("OK")) {
			String line = this.channel.name() + "received OK message, no need to perform task";
			logger.debug(line);
			this.isCompleted = true;
			synchronized (state) {
				state.add(line);
			}
		}
		if (msg.getSrc().toString().equals(this.channel.name()) == false) {
			String line = "Receiver: " + this.channel.name() + " | " + msg.getSrc() + "->" + msg.getDest() + ": "
					+ msg.getObject();
			logger.debug(line);
			synchronized (state) {
				state.add(line);
			}
		}

	}

	public void getState(OutputStream output) throws Exception {
		synchronized (state) {
			Util.objectToStream(state, new DataOutputStream(output));

		}
	}

	public void setState(InputStream input) throws Exception {
		List<String> list = (List<String>) Util.objectFromStream(new DataInputStream(input));
		synchronized (state) {
			state.clear();
			state.addAll(list);
		}
		logger.debug("received state (" + list.size() + " messages in history):");
		for (String str : list) {
			logger.debug(str);
		}
	}

	private void start() throws Exception {

		channel = new JChannel();
		channel.setReceiver(this);
		channel.connect("Cluster");
		channel.getState(null, 10000);

		int totalNodesInCluster = waitForAllAvailableNodes();
		logger.debug("Number Of Nodes:" + totalNodesInCluster);
		performTask();

		waitForExitCommand();

		channel.close();
	}

	public void performTask() {
		if (isCoordinator() && isCompleted == false) {
			System.out.println(testLine);
			this.isCompleted = true;
			sendMessageTo(null, OK);
		}
	}

	public void sendMessageTo(Address receiver, String msgBody) {
		if(msgBody == null) {
			throw new NullPointerException();
		}
		Message msg = new Message(receiver, msgBody);
		try {
			this.channel.send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public int waitForAllAvailableNodes() {
		long currentTime = System.currentTimeMillis();
		long endTime = currentTime + 3000;
		while (System.currentTimeMillis() < endTime) {
			if (this.channel.view().getMembers().size() > 1) {
				return this.channel.view().getMembers().size();

			} else {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
		return 1;
	}

	private void waitForExitCommand() {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			try {
				String line =in.readLine();
				if (line.startsWith("quit") || line.startsWith("exit")) {
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		new AppNode().start();

	}

	public Address getCoordinator() {
		return this.channel.view().getCoord();
	}

	public boolean isCoordinator() {
		if (this.channel.name().equals(this.channel.view().getCoord().toString())) {
			return true;
		} else
			return false;
	}

	public void broadcastTaskStatus(List<Address> newNodes) {
		if (isCoordinator()) {
			for (Address receiver : newNodes) {
				if (this.isCompleted) {
					sendMessageTo(receiver, OK);
				} else {
					sendMessageTo(receiver, NOK);
				}
			}
		}
	}

	public List<Address> getNewNodes(View new_view) {
		List<Address> newNodes = new ArrayList<Address>();
		if (this.view != null && this.view.getMembersRaw() != null) {
			newNodes = this.view.newMembers(this.view, new_view);
		}
		return newNodes;
	}

	public JChannel getChannel() {
		return channel;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public View getView() {
		return view;
	}

}
