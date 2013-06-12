package net.nooj4nlp.engine;

import java.util.ArrayList;

public class AmbiguitiesUnambiguitiesObject
{
	private Double relBegAddress;
	private ArrayList<Integer> tokenIds;
	private Double relEndAddress;

	public AmbiguitiesUnambiguitiesObject(Double relBegAddress, ArrayList<Integer> tokenIds, Double relEndAddress)
	{
		this.relBegAddress = relBegAddress;
		this.tokenIds = tokenIds;
		this.relEndAddress = relEndAddress;
	}

	public Double getRelBegAddress()
	{
		return relBegAddress;
	}

	public void setRelBegAddress(Double relBegAddress)
	{
		this.relBegAddress = relBegAddress;
	}

	public ArrayList<Integer> getTokenIds()
	{
		return tokenIds;
	}

	public void setTokenIds(ArrayList<Integer> tokenIds)
	{
		this.tokenIds = tokenIds;
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
