import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class usuableIP {

	public static void main(String[] args) throws Exception {

		if (args.length == 0 || args.length < 2) {
			System.out.println("No or less Arguments specified.\nTry again.");
			System.exit(0);
		}

		String ip = args[0];

		if (validate(ip) == false) {
			System.out.println("Invalid IP address specified.\nProvide valid IP address.");
			System.exit(0);
		}

		// Assuming netmask is just an integer
		int mask = Integer.parseInt(args[1]);
		String networkAddr = "";

		String[] ipAddrParts = ip.split("[.]");

		String a, b, c, d;
		a = Integer.toBinaryString(Integer.parseInt(ipAddrParts[0]));
		b = Integer.toBinaryString(Integer.parseInt(ipAddrParts[1]));
		c = Integer.toBinaryString(Integer.parseInt(ipAddrParts[2]));
		d = Integer.toBinaryString(Integer.parseInt(ipAddrParts[3]));

		a = String.format("%8s", a).replace(" ", "0");
		b = String.format("%8s", b).replace(" ", "0");
		c = String.format("%8s", c).replace(" ", "0");
		d = String.format("%8s", d).replace(" ", "0");

		String binaryIP = a + b + c + d;
		String netmask = "";
		for (int i = 0; i < 32; i++) {
			if (i < mask)
				netmask += "1";
			else
				netmask += "0";
		}

		String andResult = "";

		for (int i = 0; i < 32; i++) {
			if (binaryIP.charAt(i) == '1' && netmask.charAt(i) == '1')
				andResult += "1";
			else
				andResult += "0";
		}

		a = andResult.substring(0, 8);
		b = andResult.substring(8, 16);
		c = andResult.substring(16, 24);
		d = andResult.substring(24, 32);

		int w, x, y, z;
		w = Integer.parseInt(a, 2);
		x = Integer.parseInt(b, 2);
		y = Integer.parseInt(c, 2);
		z = Integer.parseInt(d, 2);

		String netNum = Integer.toString(w) + "." + Integer.toString(x) + "." + Integer.toString(y) + "."
				+ Integer.toString(z);
		System.out.println("Network Address is:" + netNum);
		System.out.println("Every 10th usuable IP addresses are:");
		z = z + 10;
		while (z <= 255) {

			netNum = Integer.toString(w) + "." + Integer.toString(x) + "." + Integer.toString(y) + "."
					+ Integer.toString(z);
			z = z + 10;
			System.out.println(netNum);
		}

	}

	private static boolean validate(String ip) {
		String pattern = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

		Pattern ptrn = Pattern.compile(pattern);
		Matcher match = ptrn.matcher(ip);
		if (match.matches())
			return true;
		return false;
	}

}
