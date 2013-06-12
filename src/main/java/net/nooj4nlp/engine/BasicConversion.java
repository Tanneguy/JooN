package net.nooj4nlp.engine;

public class BasicConversion
{

	public static String dectohex(int dec)
	{
		return Integer.toHexString(dec);
	}

	public static String bintohex(String bin)
	{
		int val = 0;

		for (int i = 0; i < bin.length(); i++)
		{
			val *= 2;
			if (bin.charAt(i) == '1')
				val++;
		}
		return Integer.toHexString(val);
	}

	public static String bintodec(String bin)
	{
		int val = 0;

		for (int i = 0; i < bin.length(); i++)
		{
			val *= 2;
			if (bin.charAt(i) == '1')
				val++;
		}
		return Integer.toString(val);
	}

	public static String dectobin(int dec)
	{
		String hex = Integer.toHexString(dec);
		if (hex.length() < 2)
			hex = "0" + hex;
		StringBuilder bin = new StringBuilder();
		for (int ichar = 0; ichar < hex.length(); ichar++)
		{
			char c = hex.charAt(ichar);
			switch (c)
			{
				case '0':
					bin.append("0000");
					break;
				case '1':
					bin.append("0001");
					break;
				case '2':
					bin.append("0010");
					break;
				case '3':
					bin.append("0011");
					break;
				case '4':
					bin.append("0100");
					break;
				case '5':
					bin.append("0101");
					break;
				case '6':
					bin.append("0110");
					break;
				case '7':
					bin.append("0111");
					break;
				case '8':
					bin.append("1000");
					break;
				case '9':
					bin.append("1001");
					break;
				case 'a':
					bin.append("1010");
					break;
				case 'b':
					bin.append("1011");
					break;
				case 'c':
					bin.append("1100");
					break;
				case 'd':
					bin.append("1101");
					break;
				case 'e':
					bin.append("1110");
					break;
				case 'f':
					bin.append("1111");
					break;
			}
		}
		return bin.toString();
	}

	public static int hextodec(String hex)
	{
		int val = 0;
	
		for (int ichar = 0; ichar < hex.length(); ichar++)
		{
			char c = hex.charAt(ichar);
			switch (c)
			{
				case '0':
					val = val * 16;
					break;
				case '1':
					val = val * 16 + 1;
					break;
				case '2':
					val = val * 16 + 2;
					break;
				case '3':
					val = val * 16 + 3;
					break;
				case '4':
					val = val * 16 + 4;
					break;
				case '5':
					val = val * 16 + 5;
					break;
				case '6':
					val = val * 16 + 6;
					break;
				case '7':
					val = val * 16 + 7;
					break;
				case '8':
					val = val * 16 + 8;
					break;
				case '9':
					val = val * 16 + 9;
					break;
				case 'A':
					val = val * 16 + 10;
					break;
				case 'B':
					val = val * 16 + 11;
					break;
				case 'C':
					val = val * 16 + 12;
					break;
				case 'D':
					val = val * 16 + 13;
					break;
				case 'E':
					val = val * 16 + 14;
					break;
				case 'F':
					val = val * 16 + 15;
					break;
			}
		}
		return val;
	}

}
