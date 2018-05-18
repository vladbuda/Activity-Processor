package edu.ro.utcn.calc.activities;
 
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
 
public class Start 
{
    public static String split;
    public static PrintWriter writer;
    private static List<MonitoredData> data;
     
    private static List<MonitoredData> getData(String filename)
    {
        data = new ArrayList<MonitoredData>();
        try(Stream<String> stream = Files.lines(Paths.get(filename)))
        {
            data = stream.map(s -> new MonitoredData(s.split("\t\t")[0], s.split("\t\t")[1], s.split("\t\t")[2])).collect(Collectors.toList());
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return data;
    }
     
    private static long countDays()
    {
        split = data.get(0).getStart().split("-| ")[2];
        long daysCount = data.stream().filter(s -> 
        {
            if (s.getEnd().split("-| ")[2].equals(split) == false)
            {
                split = s.getEnd().split("-| ")[2];
                return true;
            }
            return false;
        }).count();
         
        daysCount++;
         
        try
        {
            writer = new PrintWriter("Number of days.txt", "UTF-8");
            writer.println(""+daysCount+" days");
            writer.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return daysCount;
    }
     
    private static Map<String, Long> countActivities()
    {
        Map<String, Long> activities = data.stream().collect(Collectors.groupingBy(MonitoredData::getActivity, Collectors.counting()));
        try
        {
            writer = new PrintWriter("Count each activity.txt", "UTF-8");
            activities.entrySet().stream().forEach(p -> writer.println(""+p.getKey()+" "+p.getValue()));
            writer.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return activities;
    }
     
    private static void countDailyActivities()
    {
        Map<String, Map<String, Long>> activities = data.stream().collect(Collectors.groupingBy(s -> (String) s.getStart().split(" ")[0], Collectors.groupingBy(MonitoredData::getActivity, Collectors.counting())));
        try
        {
            writer = new PrintWriter("Count daily activities.txt", "UTF-8");
            activities.entrySet().stream().forEach(p->
            {
                writer.println(p.getKey());
                activities.get(p.getKey()).entrySet().stream().forEach(s -> writer.println(" "+s.getKey()+" "+s.getValue()));
            });
            writer.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
     
    private static void getDuration()
    {
        List<Long> duration = data.stream().map(MonitoredData::getDuration).collect(Collectors.toList());
        try
        {
            writer = new PrintWriter("Duration per activity (minutes).txt", "UTF-8");
            duration.stream().forEach(p -> writer.println(String.format("%.1f",(float)p/60)));
            writer.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
     
    private static void getTotalDuration()
    {
        Map<String, Long> duration = data.stream().collect(Collectors.groupingBy(s -> s.getActivity(), Collectors.summingLong(s -> s.getDuration())));
        try
        {
            writer = new PrintWriter("Total duration (minutes).txt", "UTF-8");
            duration.entrySet().stream().forEach(p ->
            {
                writer.println(""+p.getKey()+" "+String.format("%.1f",(float) p.getValue()/60));
            });
            writer.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
     
    private static void getFilteredActivities()
    {
        Map<String, Long> activitiesCount = countActivities(); //total number of activities
        Map<String, Long> map = data.stream().filter(s -> (s.getDuration() < 300)).collect(Collectors.groupingBy(MonitoredData::getActivity, Collectors.counting()));
        List<String> filteredActivities = map.entrySet().stream().filter(p -> 
        {
            if(p.getValue() >= 0.9 * activitiesCount.get(p.getKey())) return true;
            return false;
        }).map(p -> p.getKey()).collect(Collectors.toList());
         
        PrintWriter writer = null;
        try
        {
            writer = new PrintWriter("Filtered activities.txt", "UTF-8");
            filteredActivities.stream().forEach(writer::println);
            writer.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) 
    {
        getData("Activities.txt");
        countDays();
        countActivities();
        countDailyActivities();
        getDuration();
        getTotalDuration();
        getFilteredActivities();
    }
}