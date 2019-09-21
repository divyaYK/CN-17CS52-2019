import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class subsume {

	public static void main(String[] args) throws Exception {
		
		if(args.length==0||args.length<2)
		{
			System.out.println("No or less arguments specified.\nTry again.");
			System.exit(0);
		}
		
		String[] ipAndMask =  args[0].split("[/]");
		String ip1 = ipAndMask[0];
		int netmask1 = Integer.parseInt(ipAndMask[1]);
		
		ipAndMask = args[1].split("[/]");
		String ip2 = ipAndMask[0];
		int netmask2 = Integer.parseInt(ipAndMask[1]);
		
		if(!(validate(ip1))||!(validate(ip2)))
		{
			System.out.println("One or more invalid IP addresses specified.\nTry again.");
			System.exit(0);
		}
		
		String mask1="";
		for(int i=0;i<32;i++)
		{
			if(i<netmask1)
				mask1+='1';
			else
				mask1+='0';
		}
		
		String mask2="";
		for(int i=0;i<32;i++)
		{
			if(i<netmask2)
				mask2+='1';
			else
				mask2+='0';
		}
		
		String nwAddr1 = getNetworkAddress(ip1,mask1);
		String nwAddr2 = getNetworkAddress(ip2,mask2);
		
		if(nwAddr1.equals(nwAddr2))
			checkIfSubsumes(ip1, ip2);
		else
			System.out.println("Given IP addresses do not subsume each other.");
			
		
	}

	private static void checkIfSubsumes(String ip1, String ip2) {
		
		String[] AddrParts = ip1.split("[.]");
		int a1, a2, a3, a4;
		a1 = Integer.parseInt(AddrParts[0]);
		a2 = Integer.parseInt(AddrParts[1]);
		a3 = Integer.parseInt(AddrParts[2]);
		a4 = Integer.parseInt(AddrParts[3]);
		
		AddrParts = ip2.split("[.]");
		int b1, b2,b3, b4;
		b1 = Integer.parseInt(AddrParts[0]);
		b2 = Integer.parseInt(AddrParts[1]);
		b3 = Integer.parseInt(AddrParts[2]);
		b4 = Integer.parseInt(AddrParts[3]);
		
		if(a1==b1&&a2==b2&&a3==b3)
		{
			if(a4<b4)
				System.out.println("First subnet subsumes second subnet.");
			else if(a4>b4)
				System.out.println("Second subnet subsumes first subnet.");
			else 
				System.out.println("Both IP Addresses are the same.");
		}
		
		
	}

	private static String getNetworkAddress(String ipAddress, String mask) {
		String[] ipNums = ipAddress.split("[.]");
		int a, b, c, d;
		a = Integer.parseInt(ipNums[0]);
		b = Integer.parseInt(ipNums[1]);
		c = Integer.parseInt(ipNums[2]);
		d = Integer.parseInt(ipNums[3]);
		String A, B, C, D;
		A = Integer.toBinaryString(a);
		A = String.format("%8s", A).replace(" ", "0");
		B = Integer.toBinaryString(b);
		B = String.format("%8s", B).replace(" ", "0");
		C = Integer.toBinaryString(c);
		C = String.format("%8s", C).replace(" ", "0");
		D = Integer.toBinaryString(d);
		D = String.format("%8s", D).replace(" ", "0");
		
		String octet = A+B+C+D;
		String result = "";
		for(int i =0;i<32;i++)
		{
			if(octet.charAt(i)=='1'&&mask.charAt(i)=='1')
				result+="1";
			else 
				result+="0";
		}
		
		String aSubString, bSubString, cSubString, dSubString;
		aSubString = result.substring(0,8);
		bSubString = result.substring(8, 16);
		cSubString = result.substring(16, 24);
		dSubString = result.substring(24, 32);
		
		a = Integer.parseInt(aSubString, 2);
		b = Integer.parseInt(bSubString, 2);
		c = Integer.parseInt(cSubString, 2);
		d = Integer.parseInt(dSubString, 2);
		result = Integer.toString(a)+"."+Integer.toString(b)+"."+Integer.toString(c)+"."+Integer.toString(d);
		return result;
	}

	private static boolean validate(String iPAddress) {
		String IPAddress_Pattern = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
				"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
		Pattern pattern = Pattern.compile(IPAddress_Pattern);
		Matcher matcher = pattern.matcher(iPAddress);
		if(matcher.matches())
			return true;
		
		return false;
	}

}
