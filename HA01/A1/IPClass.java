import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPClass {

	public static void main(String[] args) throws Exception{
		
		if(args.length==0)
		{
			System.out.println("No Arguments specified.\nTry again.");
			System.exit(0);
		}
		
		String IPAddress = args[0];
		validateIP(IPAddress);

	}

	private static void validateIP(String iPAddress) {
		
		String IPAddress_Pattern = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
				"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
		Pattern pattern = Pattern.compile(IPAddress_Pattern);
		Matcher matcher = pattern.matcher(iPAddress);
		if(matcher.matches())
			checkAddressRange(iPAddress);
		else
		{
			System.out.println("Given argument is an invalid IP Addresss.");
			System.exit(0);
		}
		
	}

	private static void checkAddressRange(String iPAddress) {
		
		String[] ipNums = iPAddress.split("[.]");

		int a, b, c, d;
		a = Integer.parseInt(ipNums[0]);
		b = Integer.parseInt(ipNums[1]);
		c = Integer.parseInt(ipNums[2]);
		d = Integer.parseInt(ipNums[3]);
		
		if((a>=1&&a<=9)&&(b>=0&&b<=255)&&(c>=0&&c<=255)&&(d>=1&&d<=255))
			System.out.println("IP Address is in the range 1.0.0.1 to 9.255.255.255");
		else if((a>=11&&a<=126)&&(b>=0&&b<=255)&&(c>=0&&c<=255)&&(d>=1&&d<=255))
			System.out.println("IP Address is in the range 11.0.0.1 to 126.255.255.255");
		else if(a==127&&b==0&&c==0&&(d>=1&&d<=255))
			System.out.println("IP Address is in the range 127.0.0.1 to 127.0.0.255");
		else if((a>=128&&a<=239)&&(b>=0&&b<=255)&&(c>=0&&c<=255)&&(d>=1&&d<=255))
			System.out.println("IP Address is in the range 128.0.0.1 to 239.255.255.255");
		else
			System.out.println("IP Address is not in any given range and is reserved for experimental purposes.");
	}

}
