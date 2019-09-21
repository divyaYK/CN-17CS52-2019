package subnets;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Subnet {

	public static void main(String[] args) throws Exception {
		
		if(args.length==0||args.length<3)
		{
			System.out.println("No or less Arguments specified.\nTry again.");
			System.exit(0);
		}
		
		String ip1 = args[0];
		String ip2 = args[1];
		//Assuming netmask is an integer
		int netmask = Integer.parseInt(args[2]);
		
		if(!validate(ip1)||!validate(ip2))
		{
			System.out.println("One or more invalid IP Addresses specified.\nTry again.");
			System.exit(0);
		}
		
		String mask="";
		for(int i=0;i<32;i++)
		{
			if(i<netmask)
				mask+='1';
			else
				mask+='0';
		}
		
		String result1 = andMask(ip1,mask);
		String result2 = andMask(ip2,mask);
		
		if(result1.equals(result2))
			System.out.println(ip1+" and "+ip2+" are in the same subnet");
		else
			System.out.println(ip1+" and "+ip2+" are in different subnet");

	}

	private static String andMask(String ipAddress, String mask) {
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
