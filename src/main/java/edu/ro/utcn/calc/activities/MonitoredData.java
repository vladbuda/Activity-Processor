package edu.ro.utcn.calc.activities;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class MonitoredData 
{
	private String start, end, activity;
	
	public MonitoredData(String start, String end, String activity)
	{
		this.start = start;
		this.end = end;
		this.activity = activity;
	}
	
	public String getStart()
	{
		return start;
	}
	
	public String getEnd()
	{
		return end;
	}
	
	public String getActivity()
	{
		return activity;
	}
	
	public long getDuration()
	{
		LocalDateTime startTime = LocalDateTime.of(LocalDate.parse(start.split(" ")[0]), LocalTime.parse(start.split(" ")[1]));
		LocalDateTime endTime = LocalDateTime.of(LocalDate.parse(end.split(" ")[0]), LocalTime.parse(end.split(" ")[1]));
		return Duration.between(startTime, endTime).getSeconds();
	}
	@Override
	public String toString()
	{
		return ""+start+" "+end+" "+activity;
	}
}
