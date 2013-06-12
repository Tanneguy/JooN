package net.nooj4nlp.engine;

/**
 * Helper class which represents objects for lists in TransitionObject objects.
 * 
 * 
 */
public class TransitionPair
{
	private Integer tokenId;
	private Double relEndAddress;

	public TransitionPair(Integer tokenId, Double relEndAddress)
	{
		this.tokenId = tokenId;
		this.relEndAddress = relEndAddress;
	}

	public Integer getTokenId()
	{
		return tokenId;
	}

	public void setTokenId(Integer tokenId)
	{
		this.tokenId = tokenId;
	}

	public Double getRelEndAddress()
	{
		return relEndAddress;
	}

	public void setRelEndAddress(Double relEndAddress)
	{
		this.relEndAddress = relEndAddress;
	}
}
