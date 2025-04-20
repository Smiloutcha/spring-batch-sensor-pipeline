package com.smiloutcha.firstpsinrgbatch;

import org.springframework.batch.item.ItemProcessor;

import java.util.List;

public class RawToAggregateSensorDataProcessor implements ItemProcessor<DailySensorData,DailyAggregatedSensorData>  {


    @Override
    public DailyAggregatedSensorData process(DailySensorData item) throws Exception {
        List<Double> measurements = item.getMeasurements();
        double min = measurements.get(0);
        double max = min ;
        double sum = 0 ;
        for (double measurement : measurements) {
            min = Math.min(min,measurement);
            max = Math.max(max,measurement);
            sum += measurement;
        }
        double avg = sum / measurements.size() ;
        return new DailyAggregatedSensorData(
                item.getDate(),
                convertToCelsuis(min)
                ,convertToCelsuis(max)
                ,convertToCelsuis(avg));
    }

    private double convertToCelsuis(double fahT) {
        return (5*(fahT-32)) / 9 ;
    }
}
