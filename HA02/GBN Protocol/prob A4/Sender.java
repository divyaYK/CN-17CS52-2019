import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Client/Sender Program:
 * Uses UDP Sockets
 * Window size = 4
 * Timeout value = 2 seconds
 * Port number = 200023
 * Should exit after receiving ack for last packet
 * Every 4th packet should be corrupted
 * 
 * Arguments:
 * Receiver IP
 * Receiver Port
 * W - window size
 * S - Max Sequence Number
 * N - Number of messages to send
 * T - Timeout value for ack
 * I - every Ith ack from the receiver side should be dropped
 */
public class Sender {

	static InetAddress IP;
	static int port, windowSize, maxSeqNum, numOfMessages, timeout, iAck, packetCount;

	// using static initialization block for reference
	static {
		try {
			IP = InetAddress.getByName("localhost");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		port = 2023;
		windowSize = 4;
		maxSeqNum = 13;
		numOfMessages = 12;
		timeout = 2;
		iAck = 4;
		packetCount = 0; //to count every packet that has been transmitted including lost and duplicates
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		/* validating arguments */

		// Accepting exactly 7 arguments
		// arguments less or more than 7 are rejected
		if (args.length == 7) {
			if (!isValidIP(args[0])) {
				System.out.println("Invalid Receiver IP.\nTry again.");
				System.exit(0);
			}
			IP = InetAddress.getByName(args[0]);
			port = Integer.parseInt(args[1]);
			windowSize = Integer.parseInt(args[2]);
			maxSeqNum = Integer.parseInt(args[3]);
			numOfMessages = Integer.parseInt(args[4]);
			timeout = Integer.parseInt(args[5]);
			iAck = Integer.parseInt(args[6]);

			if (maxSeqNum < windowSize) {
				System.out.println("Maximum Sequence number should be greater than the WindowSize");
				System.exit(0);
			}
		}

		int seqBase = 0; // starting the seq number with 0
		packet[] pkts = new packet[numOfMessages];
		for (int i = 0, j = 0; i < numOfMessages & j < maxSeqNum; i++, j++) {
			pkts[i] = new packet(j, i + 1, 0); // setting 0 to n seq numbers and ack as 0 since the packets are not sent
												// yet
			if (j == maxSeqNum - 1)
				j = -1;

		}

		LinkedList<packet> window = new LinkedList<packet>(); // window
		DatagramSocket clientSocket = new DatagramSocket();

		transmitPackets(seqBase, pkts, window, clientSocket);

	}

	/**
	 * @param seqBase
	 * @param pkts
	 * @param window
	 * @param clientSocket
	 * @throws Exception
	 */
	private static void transmitPackets(int seqBase, packet[] pkts, LinkedList<packet> window,
			DatagramSocket clientSocket) throws Exception {

		// add packets to the window
		for (int i = 0; i < windowSize; i++) {
			if (window.size() < windowSize && (i + seqBase) < numOfMessages)
				window.addLast(pkts[i + seqBase]);
		}

		// initialization
		/*
		 * timer[] time = new timer[window.size()]; for(int i=0;i<window.size();i++)
		 * time[i] = new timer();
		 */
		LinkedList<timer> time = new LinkedList<timer>();
		for (int i = 0; i < window.size(); i++)
		{
			time.add(new timer(window.get(i),timeout));
			packet pkt = time.get(i).getPacket();
			System.out.println(pkt.getMessage());
		}

		byte[] sendData = new byte[1024];

		while (pkts[numOfMessages - 1].getAckNum() != 1) // while last packet hasn't been received yet
		{
			System.out.println("\nSender:");
			for (int i = 0; (i < windowSize && i < window.size()); i++) {
				packetCount++;
				if (packetCount % iAck == 0 && window.get(i).getAckNum() != 1) {
					window.get(i).setMessage("corrupt"); // packets to be dropped
				}
				System.out.println(
						"Sending Packet " + window.get(i).getPktNum() + " with content: " + window.get(i).getMessage());
				sendData = window.get(i).getMessage().getBytes();
				DatagramPacket sendPkt = new DatagramPacket(sendData, sendData.length, IP, port);
				clientSocket.send(sendPkt); // send the ith packet
				//time.set(i, new timer(window.get(i), timeout)); // start timer
				time.get(i).start();
				TimeUnit.SECONDS.sleep(1);
			}

			for (int i = 0; (i < windowSize && i < window.size()); i++) {
				if (isReceived(window.get(i), clientSocket)) // if ack is received
				{
					time.get(i).stop(); // stop timer
					System.out.println(time.get(i).getPacket().getMessage());
					if (window.get(i).getAckNum() != 1) {
						window.get(i).setAckNum(1); // setting 0 for unsent, 1 for ack received and -1 for not received
						// System.out.println("Ack received for packet "+ window.get(i).getPktNum());
					} else {
						System.out.println("Packet " + window.get(i).getPktNum() + " already acked.");
					}
					if (window.get(0).getAckNum() != -1) {
						window.removeFirst();
						System.out.println(time.getFirst().getPacket().getMessage());
						time.removeFirst();
						i--;
					}

					if (windowSize + seqBase < numOfMessages) {
						seqBase++;
						window.addLast(pkts[windowSize + seqBase - 1]);
						time.addLast(new timer(window.getLast(),timeout));
						System.out.println(time.getLast().getPacket().getMessage());
					}

				} else {
					window.get(i).setAckNum(-1);
					// System.out.println("Ack not received for packet "+
					// window.get(i).getPktNum());
					if (checkIfTimedOut(time, window)) // if timer timed out then retransmit the packets in window
					{
						System.out.println("retransmitting all packets in window...");
						transmitPackets(seqBase, pkts, window, clientSocket);
					}
				}
				// sender sleeps for a second; added to slow down the output
				// TimeUnit.SECONDS.sleep(1);
			}

		}
		clientSocket.close();
	}

	private static boolean checkIfTimedOut(LinkedList<timer> time, LinkedList<packet> window) {
		for (int i = 0; i < window.size(); i++) {

			if (time.get(i).isTimedOut()/* &&time[i].isRunning() */) {
				packet pkt = time.get(i).getPacket();
				pkt.setMessage(pkt.getSeqNum() + " Message Number " + pkt.getPktNum() + " " + pkt.getChecksum());
				System.out.println(pkt.getMessage());
				return true;
			}

		}
		return false;
	}

	private static boolean isReceived(packet packet, DatagramSocket clientSocket) {
		byte[] recvData = new byte[1024];
		DatagramPacket recvPkt = new DatagramPacket(recvData, recvData.length);
		try {
			clientSocket.receive(recvPkt);
			String s = new String(recvData);
			String seq = "seq number " + Integer.toString(packet.getSeqNum());
			System.out.println(s);
			if (s.trim().endsWith(seq))
				return true;

		} catch (IOException e) {
			return false;
		}
		return false;
	}

	private static boolean isValidIP(String iPAddress) {
		String IPAddress_Pattern = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
		Pattern pattern = Pattern.compile(IPAddress_Pattern);
		Matcher matcher = pattern.matcher(iPAddress);
		if (matcher.matches())
			return true;
		return false;
	}

}
