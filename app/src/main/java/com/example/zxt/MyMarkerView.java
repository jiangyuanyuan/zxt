
package com.example.zxt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
@SuppressLint("ViewConstructor")
public class MyMarkerView extends MarkerView {

    private final TextView tvContent;
    private final TextView num;
    private int mType = 1;

    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        tvContent = findViewById(R.id.tvContent);
        num = findViewById(R.id.num);
    }

    // runs every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;

            tvContent.setText(Utils.formatNumber(ce.getHigh(), 0, true));
        } else {
            String mFormat = null;
            switch (mType) {
                case 1:
                    mFormat = "日";
                    tvContent.setText(((int) e.getX())+ mFormat);
                    break;
                case 2:
                    mFormat = "月";
                    tvContent.setText(((int) e.getX())+ mFormat);
                    break;
                case 3:
                    mFormat = "年";
                    if((int) e.getX() < 10){
                        tvContent.setText("0"+((int) e.getX())+ mFormat);
                    }else{
                        tvContent.setText(((int) e.getX())+ mFormat);
                    }
                    break;
                case 4:
                    mFormat = "时";
                    tvContent.setText(((int) e.getX())+ mFormat);
                    break;
                default:
                    mFormat = "日";
                    tvContent.setText(((int) e.getX())+ mFormat);
                    break;
            }

            num.setText(Utils.formatNumber(e.getY(), 0, true));
        }

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }


    public void setType(int type) {
        mType = type;
    }

}
