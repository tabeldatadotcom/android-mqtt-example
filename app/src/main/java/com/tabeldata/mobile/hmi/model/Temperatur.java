package com.tabeldata.mobile.hmi.model;

import java.math.RoundingMode;
import java.text.NumberFormat;

/**
 * Created by dimmaryanto93 on 01/15/2018.
 */

public class Temperatur {

    private NumberFormat decimalFormat = NumberFormat.getNumberInstance();

    public Temperatur() {
        this.temp = 0d;
        this.humidity = 0d;
    }

    public Temperatur(Double temp, Double humidity) {
        this.temp = temp;
        this.humidity = humidity;
    }

    private Double temp;
    private Double humidity;

    public Double getTemp() {
        return temp;
    }

    public String getTempDecimal() {
        decimalFormat.setGroupingUsed(false);
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat.setMinimumFractionDigits(2);
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        return decimalFormat.format(this.temp);
    }

    public void setTemp(Double temp) {
        this.temp = temp;
    }

    public Double getHumidity() {
        return humidity;
    }

    public String getHumidityDecimal() {
        decimalFormat.setGroupingUsed(false);
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat.setMinimumFractionDigits(2);
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        return decimalFormat.format(this.humidity);
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }
}
