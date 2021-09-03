package id.kopipintar.pos.model;

import java.util.List;

public class Measure {

   private List<MeasureData> measure;

    public List<MeasureData> getMeasureDataList() {
        return measure;
    }

    public void setMeasureDataList(List<MeasureData> measureDataList) {
        this.measure = measureDataList;
    }
}

