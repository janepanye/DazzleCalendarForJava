package fuck.dazzlecalendar.DazzleCalendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;

import fuck.dazzlecalendar.MyApplication.MyApplication;
import fuck.dazzlecalendar.Tool.CalendarTool;
import fuck.dazzlecalendar.Tool.DiviceInfo;


/**
 * Created by mac on 2017/12/25.
 */

public class WeekCalendarView extends ViewGroup implements View.OnClickListener {
    //当前周对应的种子时间
    public Calendar weekCalendar = Calendar.getInstance();
    //监听事件
    public CalendarListener calendarListener = null;

    //传入上下文、宽、高 开始添加子视图
    public WeekCalendarView(Context context) {
        super(context);
    }
    public WeekCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 计算控件的大小
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth = measureWidth(widthMeasureSpec);
        int measureHeight = measureHeight(heightMeasureSpec);
        // 计算自定义的ViewGroup中所有子控件的大小
        int childCount = this.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = this.getChildAt(i);
            this.measureChild(child, measureWidth / 7, DiviceInfo.dpToPx(MyApplication.getInstance(),
                    42));
        }
        // 设置自定义的控件MyViewGroup的大小
        setMeasuredDimension(measureWidth, measureHeight);
    }

    private int measureWidth(int pWidthMeasureSpec) {
        int result = 0;
        int widthMode = MeasureSpec.getMode(pWidthMeasureSpec);// 得到模式
        int widthSize = MeasureSpec.getSize(pWidthMeasureSpec);// 得到尺寸
        switch (widthMode) {
            /**
             * mode共有三种情况，取值分别为MeasureSpec.UNSPECIFIED, MeasureSpec.EXACTLY,
             * MeasureSpec.AT_MOST。
             *
             *
             * MeasureSpec.EXACTLY是精确尺寸，
             * 当我们将控件的layout_width或layout_height指定为具体数值时如andorid
             * :layout_width="50dip"，或者为FILL_PARENT是，都是控件大小已经确定的情况，都是精确尺寸。
             *
             *
             * MeasureSpec.AT_MOST是最大尺寸，
             * 当控件的layout_width或layout_height指定为WRAP_CONTENT时
             * ，控件大小一般随着控件的子空间或内容进行变化，此时控件尺寸只要不超过父控件允许的最大尺寸即可
             * 。因此，此时的mode是AT_MOST，size给出了父控件允许的最大尺寸。
             *
             *
             * MeasureSpec.UNSPECIFIED是未指定尺寸，这种情况不多，一般都是父控件是AdapterView，
             * 通过measure方法传入的模式。
             */
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                result = widthSize;
                break;
        }
        return result;
    }

    private int measureHeight(int pHeightMeasureSpec) {
        int result = 0;
        int heightMode = MeasureSpec.getMode(pHeightMeasureSpec);
        int heightSize = MeasureSpec.getSize(pHeightMeasureSpec);
        switch (heightMode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                result = heightSize;
                break;
        }
        return result;
    }

    //由这里来控制位置
    @Override
    protected void onLayout(boolean b, int left, int top, int right, int bottom) {
        ViewGroup.LayoutParams layoutParams;
        //对内部7个子view进行布局
        //每个子视图的宽高
        int itemWidth = (right - left) / 7;
        int itemHeight = DiviceInfo.dpToPx(MyApplication.getInstance(), 42);
        for (int col = 0; col < 7; col++) {//列
            //得到第几个view
            View childView = getChildAt(col);
            //除了位置之外
            childView.layout(col * itemWidth,0, col * itemWidth + itemWidth, itemHeight);
            childView.setTag( col);
            //还要设置宽高
            layoutParams = childView.getLayoutParams();
            layoutParams.width = itemWidth;
            layoutParams.height = itemHeight;
            childView.setLayoutParams(layoutParams);
            childView.setOnClickListener(this);
        }
    }
    //根据当前月的种子时间，刷新一下界面
    public void reloadCurrMonth() {
        ArrayList<Calendar> currWeekCalendars = CalendarTool.getWeekCalendars(weekCalendar);
        for (int index = 0; index < 7; index++) {
            CalendarDayView calendarDayView = (CalendarDayView) getChildAt(index);
            calendarDayView.calendarType = 0;
            calendarDayView.dayCalendar = currWeekCalendars.get(index);
            calendarDayView.weekMonthCalendar = weekCalendar;
            if (calendarListener != null)
                calendarListener.dayViewDidShow(calendarDayView);
        }
    }
    @Override
    public void onClick(View view) {
        if (calendarListener != null)
            calendarListener.onDayViewClick((CalendarDayView) view);
    }
    public void setOnItemClickListener(CalendarListener listener) {
        this.calendarListener = listener;
    }
    //define interface
    public static interface CalendarListener {
        //某一天被点击
        void onDayViewClick(CalendarDayView calendarDayView);
        //某一天已经显示
        void dayViewDidShow(CalendarDayView calendarDayView);
    }
}