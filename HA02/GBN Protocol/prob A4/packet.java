import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class packet {

	int seqNum;
	int pktNum;
	String checksum;
	int ackNum;
	String message;

	public String generateChecksum(int seqnum, int pktnum) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			String cs = seqnum + " Message Number " + pktnum + " ";
			byte[] digest = md.digest(cs.getBytes());
			// Convert byte array into signum representation
			BigInteger no = new BigInteger(1, digest);

			// Convert message digest into hex value
			String hashtext = no.toString(16);
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
			return hashtext;
		} catch (NoSuchAlgorithmException e) {
			return null;
		}

	}

	public packet(packet p) {
		seqNum = p.getSeqNum();
		pktNum = p.getPktNum();
		ackNum = p.getAckNum();
		checksum = p.getChecksum();
		message = seqNum + " Message Number " + pktNum + " " + checksum;
	}

	public packet(int seq, int pkt, int ack, String check) {
		seqNum = seq;
		ackNum = ack;
		pktNum = pkt;
		// checksum = check;
		if (check == null) {
			checksum = "";
		} else
			checksum = check;
		message = seqNum + " Message Number " + pktNum + " " + checksum;
	}

	public packet(int seq, int pkt, int ack) {
		seqNum = seq;
		ackNum = ack;
		pktNum = pkt;
		checksum = generateChecksum(seq,pkt);
		message = seqNum + " Message Number " + pktNum + " " + checksum;
	}

	public boolean setSeqNum(int n) {
		seqNum = n;
		return true;
	}

	public boolean setAckNum(int n) {
		ackNum = n;
		return true;
	}

	public boolean setChecksum(String n) {
		checksum = n;
		message = seqNum + " Message Number " + pktNum + " " + checksum;
		return true;
	}

	public boolean setMessage(String msg) {
		if (msg == null) {
			message = "";
			return false;
		} else {
			message = new String(msg);
			return true;
		}
	}

	public int getSeqNum() {
		return seqNum;
	}

	public int getPktNum() {
		return pktNum;
	}

	public int getAckNum() {
		return ackNum;
	}

	public String getChecksum() {
		return checksum;
	}

	public String getMessage() {
		return message;
	}

	public String getInfo() {
		return "seqnum: " + seqNum + "\npktnum: " + pktNum + "\nacknum: " + ackNum + "\nchecksum: " + checksum;
	}
}
