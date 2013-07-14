package net.nooj4nlp.engine;

@SuppressWarnings("serial")
public class UnsignedByte implements java.io.Serializable
{
	private byte b;
	

	public UnsignedByte()
	{
				
	super();
		
	}

	public static final int MAX_VALUE = 255;

	public int getInt()
	{
		if (b > 0)
			return b;
		else
			return b & 0xFF;
	}

	public void setB(byte b)
	{
		this.b = b;
	}
}
