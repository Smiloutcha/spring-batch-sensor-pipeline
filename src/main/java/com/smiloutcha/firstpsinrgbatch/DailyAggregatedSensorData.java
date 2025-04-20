package com.smiloutcha.firstpsinrgbatch;

import com.thoughtworks.xstream.security.ExplicitTypePermission;
import org.springframework.oxm.xstream.XStreamMarshaller;

import java.util.HashMap;
import java.util.Map;

public class DailyAggregatedSensorData {

    private final String date;
    private final double min ;
    private final double max ;
    private final double avg;


    public static final String ITEM_ROOT_ELEMENT_NAME = "daily-data";

    public static XStreamMarshaller getMarshaller() {
        XStreamMarshaller marshaller = new XStreamMarshaller();
        Map<String,Class<?>> aliases = new HashMap<>();
        aliases.put(ITEM_ROOT_ELEMENT_NAME, DailyAggregatedSensorData.class);
        aliases.put("date",String.class);
        aliases.put("min",Double.class);
        aliases.put("avg",Double.class);
        aliases.put("max",Double.class);
        ExplicitTypePermission typePermission = new ExplicitTypePermission(new Class[]{DailyAggregatedSensorData.class});
        marshaller.setAliases(aliases);
        marshaller.setTypePermissions(typePermission);
        return marshaller;
    }

    public DailyAggregatedSensorData(String date, double min, double max, double avg) {
        this.date = date;
        this.min = min;
        this.max = max;
        this.avg = avg;
    }

    public String getDate() {
        return date;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getAvg() {
        return avg;
    }
}
