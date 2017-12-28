package fuck.dazzlecalendar.DazzleCalendar;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;

import fuck.dazzlecalendar.CustemRoundView.RoundTextView;
import fuck.dazzlecalendar.CustemRoundView.RoundView;
import fuck.dazzlecalendar.R;
import fuck.dazzlecalendar.Tool.DiviceInfo;


/**
 * Created by mac on 2017/12/25.
 */
//每一天的view
public class CalendarDayView extends RelativeLayout {
    //中间的文本
    public TextView centerTextView = null;
    //背后的背景图
    public RoundView roundView = null;
    //偏下面的小红点
    public RoundView dotView = null;
    //这个是显示带有数字的红点
    public RoundTextView dotNumberText = null;

    //今天对应的时间
    public Calendar dayCalendar = Calendar.getInstance();
    //今天所在的周、月时间
    public Calendar weekMonthCalendar = Calendar.getInstance();
    //今天是在哪个类型的日历里面存在 0周,1月
    public int calendarType = 0;

    public CalendarDayView(Context context) {
        super(context);
        //设置背景图片的位置
        roundView = new RoundView(context);
        addView(roundView);
        RelativeLayout.LayoutParams roundViewLayoutParams = new RelativeLayout.LayoutParams(DiviceInfo.dpToPx(getContext(),42),DiviceInfo.dpToPx(getContext(),42));
        roundViewLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
        roundViewLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);
        roundView.setLayoutParams(roundViewLayoutParams);
        //在这里设置文本控件的位置
        centerTextView = new TextView(context);
        centerTextView.setTextSize(12);
        centerTextView.setGravity(Gravity.CENTER);
        addView(centerTextView);
        RelativeLayout.LayoutParams textViewLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        textViewLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
        textViewLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);
        centerTextView.setLayoutParams(textViewLayoutParams);
        //设置小红点
        dotView = new RoundView(context);
        addView(dotView);
        RelativeLayout.LayoutParams dotViewLayoutParams = new RelativeLayout.LayoutParams(DiviceInfo.dpToPx(getContext(),4),DiviceInfo.dpToPx(getContext(),4));
        dotViewLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
        dotViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
        dotViewLayoutParams.setMargins(0,0,0, DiviceInfo.dpToPx(context,7));
        dotView.setBackground(context.getResources().getColor(R.color.red),2,2,2,2);
        dotView.setLayoutParams(dotViewLayoutParams);
        //设置带数字的红点
        dotNumberText = new RoundTextView(context);
        addView(dotNumberText);
        RelativeLayout.LayoutParams dotNumberLayoutParams = new RelativeLayout.LayoutParams(DiviceInfo.dpToPx(getContext(),12),DiviceInfo.dpToPx(getContext(),12));
        dotNumberLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
        dotNumberLayoutParams.setMargins(0,DiviceInfo.dpToPx(context,1),0,0);
        dotNumberText.setTextColor(context.getResources().getColor(R.color.white));
        dotNumberText.setTextSize(10);
        dotNumberText.setGravity(Gravity.CENTER);
        dotNumberText.setBackground(context.getResources().getColor(R.color.red),6,6,6,6);
        dotNumberText.setLayoutParams(dotNumberLayoutParams);
    }
}
