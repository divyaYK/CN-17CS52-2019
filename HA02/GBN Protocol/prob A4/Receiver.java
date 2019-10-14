import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

/*
 * Server/Receiver Program:
 * Uses UDP Sockets
 * Window size = 1
 * Port number = 20023
 * Should run forever
 * 
 * Arguments:
 * Receiver port
 * S - Max sequence number
 */
public class Receiver{

	static int port, maxSeqNum;
	static {
		port = 2023;
		maxSeqNum = 13;
	}
	static DatagramSocket serverSocket;
	
	public static void main(String[] args) throws SocketException, IOException, InterruptedException {
		
		//Accepting exactly two arguments
		if(args.length==2)
		{
			port = Integer.parseInt(args[0]);
			maxSeqNum = Integer.parseInt(args[1]);
		}
		
		int seqBase = 0;
		int packetCount = 1;
		serverSocket = new DatagramSocket(port);
		while(true)
		{
			
			byte[] receiveBuffer = new byte[1024];
			byte[] sendBuffer = new byte[1024];
			DatagramPacket receivedPacket = new DatagramPacket(receiveBuffer,receiveBuffer.length);
			serverSocket.receive(receivedPacket);
			InetAddress IP = receivedPacket.getAddress();
			int senderPort = receivedPacket.getPort();
			String clientData = new String(receivedPacket.getData());
			if(seqBase>=maxSeqNum)
				seqBase=0;
			String sendData = "";
			System.out.print("Receiver:");
			if(clientData.startsWith(Integer.toString(seqBase))&&!clientData.contains("corrupt")) //if seq number matches and packet isn't corrupt
			{
				System.out.println("Received packet "+ packetCount +".\nSending ack for "+clientData);
				sendData = "Ack received for packet "+ packetCount + " with seq number "+ Integer.toString(seqBase);
				packetCount++;
				seqBase++;
			}
			else
			{
				System.out.println("Received packet is discarded.");
				System.out.println("Waiting for packet "+packetCount);
				sendData = "Last acked packet "+Integer.toString(packetCount-1); 		//do nothing
			}			
			sendBuffer = sendData.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length,IP,senderPort);
			serverSocket.send(sendPacket);
			//sleep for a second
			TimeUnit.SECONDS.sleep(1);
		}
		

	}

}
