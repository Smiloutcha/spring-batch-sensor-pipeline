package com.smiloutcha.firstpsinrgbatch;

import org.springframework.batch.item.ItemProcessor;

public class SensorDataAnomalyProcessor implements ItemProcessor<DailyAggregatedSensorData,DataAnomaly> {

    private static final double THRESH_HOLD = .9 ;

    @Override
    public DataAnomaly process(DailyAggregatedSensorData item) throws Exception {
        if (item.getMin() / item.getAvg() < THRESH_HOLD) {
            return new DataAnomaly(item.getDate(),AnomalyType.MINIMUM,item.getMin());
        } else if(item.getMax() / item.getAvg() < THRESH_HOLD) {
            return new DataAnomaly(item.getDate(),AnomalyType.MAXIMUM,item.getMax());
        } else {
            return null;
        }
    }
}
