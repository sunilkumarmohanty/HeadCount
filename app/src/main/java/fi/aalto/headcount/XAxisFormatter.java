package fi.aalto.headcount;

/**
 * Created by sunil on 26-05-2017.
 */

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class XAxisFormatter implements IAxisValueFormatter
{

    protected String[] mMonths = new String[]{
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    };
    private Long fromDate;

    public XAxisFormatter(Long fromDate) {
        // maybe do something here or provide parameters in constructor
        this.fromDate = fromDate * 1000;

    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date date=new Date(fromDate + (long)(value * 1000));
        return sdf.format(date);
    }
}