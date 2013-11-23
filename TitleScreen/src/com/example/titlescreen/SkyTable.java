package com.example.titlescreen;

import java.util.ArrayList;

public class SkyTable 
{
	//Size of the table is 9x9
	public ArrayList<TableValues> Table = new ArrayList<TableValues>(81);
	
	SkyTable()
	{
		//Table = new ArrayList<TableValues>(81);
		for(int i = 0; i < 9; i++)
		{
			//ArrayList<TableValues> values = new ArrayList<TableValues>(9);
			for(int j = 0; j < 9; j++)
			{
				
				TableValues temp = new TableValues(i*40,j*40, false);
				//Table.set(i*9+j, temp);
				Table.add(temp);
			}
			//Table.set(i,values);
		}
	}
	
	public void SetTableSlot(int i, int j, boolean isUsed)
	{
		TableValues temp = new TableValues(Table.get(i*9+j).xrot,Table.get(i*9+j).yrot, isUsed);
		Table.set(i*9+j, temp);
	}
	
	public IntPair FindFirstEmptySlot()
	{
		boolean found = false;
		int loci = 0;
		int locj = 0;
		Random generator = new Random();
		
		//We know there is room in the table for up to 80 constellations, and that limit is not reached, so this loop will end.
		while(!found)
		{
			loci = generator.nextInt(8);
			locj = generator.nextInt(8);
			if(!Table.get(loci*9+locj).isUsed)
			{
				SetTableSlot(loci,locj, true);
				IntPair rotationCoordinates = new IntPair(loci,locj);
				found = true;
				return rotationCoordinates;
			}
		}
		/*
		for(int i = 0; i < 9; i++)
		{
			for(int j = 0; j < 9; j++)
			{
				if(!Table.get(i*9+j).isUsed)
				{
					SetTableSlot(i,j, true);
					IntPair rotationCoordinates = new IntPair(i,j);
					return rotationCoordinates;
				}
			}
		}
		*/
		IntPair rotationCoordinates = new IntPair(0,0);
		return rotationCoordinates;
	}
	
	public int GetXrot(int i, int j)
	{
		return Table.get(i*9+j).xrot;
	}
	
	public int GetYrot(int i, int j)
	{
		return Table.get(i*9+j).yrot;
	}
}
