package fuck.dazzlecalendar.DazzleCalendar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Calendar;

import fuck.dazzlecalendar.DazzleCalendar.MonthCalendarView;
import fuck.dazzlecalendar.DazzleCalendar.WeekCalendarView;
import fuck.dazzlecalendar.R;
import fuck.dazzlecalendar.Tool.DiviceInfo;


/**
 * Created by mac on 2017/12/25.
 */
//自己做的日历控件  一天的高度为42dp，约定好  不乱套
public class DazzleCalendar extends FrameLayout {
    //当前界面
    public View currView = null;
    //当前上下文
    private Context context = null;
    //日历类型
    public int calendarType = 1;
    //监听事件
    private DazzleCalendarListener dazzleCalendarListener = null;

    //种子时间个数（该值*2+1）
    public int sendCalendarCount = 100;
    //所有的月界面
    public ArrayList<MonthCalendarView> allMonthViews = new ArrayList<>(3);
    public ArrayList<Calendar> sendMonthCalendars = new ArrayList<>(2 * sendCalendarCount + 1);
    //月滚动视图
    public ViewPager monthViewPager = null;

    //所有的周界面
    public ArrayList<WeekCalendarView> allWeekViews = new ArrayList<>(3);
    public ArrayList<Calendar> sendWeekCalendars = new ArrayList<>(2 * sendCalendarCount + 1);
    //月滚动视图
    public ViewPager weekViewPager = null;

    //移动的y距离 用来判断是否需要拦截事件
    private float lastPointY;

    public DazzleCalendar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.currView = LayoutInflater.from(context).inflate(R.layout.dazzle_calendar_layout,this,true);
        //找到滚动视图
        this.monthViewPager = (ViewPager) this.currView.findViewById(R.id.month_view_pager);
        this.weekViewPager = (ViewPager) this.currView.findViewById(R.id.week_view_pager);
    }
    //事件分发
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean needIntercept = false;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            //将点下的点的坐标保存
            case MotionEvent.ACTION_DOWN:
                lastPointY = event.getRawY();
//                Log.d("被内部消耗了","ACTION_DOWN");
                break;
            //移动结束
            case MotionEvent.ACTION_UP:
//                Log.d("被内部消耗了","ACTION_UP");
                break;
            //计算出需要移动的距离
            case MotionEvent.ACTION_MOVE:
//                Log.d("被内部消耗了","ACTION_MOVE");
                float dy = (int) event.getRawY() - lastPointY;
                //如果垂直方向移动了10dp，就需要拦截了
                if(dy > DiviceInfo.dpToPx(context,10) || dy < -DiviceInfo.dpToPx(context,10))
                    needIntercept = true;
                break;
        }
        return needIntercept;
    }
    //###########################  月视图配置 begin  ###########################
    public void configMonthView() {
        //填充3个月的视图  ###第1步###
        for (int index = 0;index < 3 ;index ++) {
            MonthCalendarView tempView = new MonthCalendarView(context);
            //添加42天context
            for (int childIndex = 0;childIndex < 42;childIndex ++) {
                CalendarDayView calendarDayView = new CalendarDayView(context);
                tempView.addView(calendarDayView);
            }
            tempView.setOnItemClickListener(new MonthCalendarView.CalendarListener() {
                @Override
                public void onDayViewClick(CalendarDayView calendarDayView) {
                    if(dazzleCalendarListener != null)
                        dazzleCalendarListener.onDayViewClick(calendarDayView);
                }
                @Override
                public void dayViewDidShow(CalendarDayView calendarDayView) {
                    if(dazzleCalendarListener != null)
                        dazzleCalendarListener.dayViewDidShow(calendarDayView);
                }
            });
            allMonthViews.add(tempView);
        }
        //设置201个种子时间 ###第2步###
        for (int index = 0;index < sendCalendarCount * 2 + 1;index ++) {
            Calendar tempCalendar = Calendar.getInstance();
            tempCalendar.add(Calendar.MONTH,index - sendCalendarCount);
            sendMonthCalendars.add(tempCalendar);
        }
        monthViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {return sendCalendarCount * 2 + 1;}
            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {return view == object;}
            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                //刷新当月的数据 ###第3步###
                MonthCalendarView monthCalendarView = allMonthViews.get(position % 3);
                monthCalendarView.monthCalendar = sendMonthCalendars.get(position);
                monthCalendarView.reloadCurrMonth();

                if (container.getChildCount() == allMonthViews.size()) {
                    container.removeView(allMonthViews.get(position % 3));
                }
                if (container.getChildCount() < allMonthViews.size()) {
                    container.addView(monthCalendarView, 0);
                } else {
                    container.addView(monthCalendarView, position % 3);
                }
                return monthCalendarView;
            }
            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView(container);
            }
        });
        monthViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //滚动停止的时间，才开始计算，这里有一个问题，就是用户可能会一直滚动，这个positionOffset一直都不会为0
                if(positionOffset == 0 && positionOffsetPixels == 0) {
                    if(position == 0) {
                        setMonthSendCalendar(allMonthViews.get(0).monthCalendar);
                        return;
                    }
                    if(position == 2 * sendCalendarCount) {
                        setMonthSendCalendar(allMonthViews.get(2 * sendCalendarCount).monthCalendar);
                        return;
                    }
                    if(dazzleCalendarListener != null)
                        dazzleCalendarListener.calendarDidShow(sendMonthCalendars.get(position));
                }
            }
            @Override
            public void onPageSelected(int position) {
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        monthViewPager.setCurrentItem(sendCalendarCount,false);
    }
    //设置月日历各自的种子时间，重新配置一下界面，让日历滚动当当前时间
    public void setMonthSendCalendar(Calendar calendar) {
        sendMonthCalendars.clear();
        for (int index = 0;index < sendCalendarCount * 2 + 1;index ++) {
            Calendar tempCalendar = Calendar.getInstance();
            tempCalendar.setTime(calendar.getTime());
            tempCalendar.add(Calendar.MONTH,index - sendCalendarCount);
            sendMonthCalendars.add(tempCalendar);
        }
        //刷新一下界面
        if(monthViewPager.getCurrentItem() < (sendCalendarCount - 1) || monthViewPager.getCurrentItem() > (sendCalendarCount + 1)) {
            monthViewPager.setCurrentItem(sendCalendarCount,false);
        } else {
            allMonthViews.get((sendCalendarCount - 1) % 3).monthCalendar = sendMonthCalendars.get(sendCalendarCount - 1);
            allMonthViews.get(sendCalendarCount % 3).monthCalendar = sendMonthCalendars.get(sendCalendarCount);
            allMonthViews.get((sendCalendarCount + 1) % 3).monthCalendar = sendMonthCalendars.get(sendCalendarCount + 1);
            allMonthViews.get(0).reloadCurrMonth();
            allMonthViews.get(1).reloadCurrMonth();
            allMonthViews.get(2).reloadCurrMonth();
            monthViewPager.setCurrentItem(sendCalendarCount,false);
        }
    }
    //###########################  月视图配置 end  ###########################
    //###########################  周视图配置 begin  ###########################
    public void configWeekView() {
        //填充3个月的视图  ###第1步###
        for (int index = 0;index < 3 ;index ++) {
            WeekCalendarView tempView = new WeekCalendarView(context);
            //添加7天context
            for (int childIndex = 0;childIndex < 7;childIndex ++) {
                CalendarDayView calendarDayView = new CalendarDayView(context);
                tempView.addView(calendarDayView);
            }
            tempView.setOnItemClickListener(new WeekCalendarView.CalendarListener() {
                @Override
                public void onDayViewClick(CalendarDayView calendarDayView) {
                    if(dazzleCalendarListener != null)
                        dazzleCalendarListener.onDayViewClick(calendarDayView);
                }
                @Override
                public void dayViewDidShow(CalendarDayView calendarDayView) {
                    if(dazzleCalendarListener != null)
                        dazzleCalendarListener.dayViewDidShow(calendarDayView);
                }
            });
            allWeekViews.add(tempView);
        }
        //设置201个种子时间 ###第2步###
        for (int index = 0;index < sendCalendarCount * 2 + 1;index ++) {
            Calendar tempCalendar = Calendar.getInstance();
            tempCalendar.add(Calendar.WEEK_OF_YEAR,index - sendCalendarCount);
            sendWeekCalendars.add(tempCalendar);
        }
        weekViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {return sendCalendarCount * 2 + 1;}
            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {return view == object;}
            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                //刷新当月的数据 ###第3步###
                WeekCalendarView weekCalendarView = allWeekViews.get(position % 3);
                weekCalendarView.weekCalendar = sendWeekCalendars.get(position);
                weekCalendarView.reloadCurrMonth();

                if (container.getChildCount() == allWeekViews.size()) {
                    container.removeView(allWeekViews.get(position % 3));
                }
                if (container.getChildCount() < allWeekViews.size()) {
                    container.addView(weekCalendarView, 0);
                } else {
                    container.addView(weekCalendarView, position % 3);
                }
                return weekCalendarView;
            }
            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView(container);
            }
        });
        weekViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //滚动停止的时间，才开始计算，这里有一个问题，就是用户可能会一直滚动，这个positionOffset一直都不会为0
                if(positionOffset == 0 && positionOffsetPixels == 0) {
                    if(position == 0) {
                        setWeekSendCalendar(allWeekViews.get(0).weekCalendar);
                        return;
                    }
                    if(position == 2 * sendCalendarCount) {
                        setWeekSendCalendar(allWeekViews.get(2 * sendCalendarCount).weekCalendar);
                        return;
                    }
                    if(dazzleCalendarListener != null)
                        dazzleCalendarListener.calendarDidShow(sendWeekCalendars.get(position));
                }
            }
            @Override
            public void onPageSelected(int position) {
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        weekViewPager.setCurrentItem(sendCalendarCount,false);
    }
    //设置月日历各自的种子时间，重新配置一下界面，让日历滚动当当前时间
    public void setWeekSendCalendar(Calendar calendar) {
        sendWeekCalendars.clear();
        for (int index = 0;index < sendCalendarCount * 2 + 1;index ++) {
            Calendar tempCalendar = Calendar.getInstance();
            tempCalendar.setTime(calendar.getTime());
            tempCalendar.add(Calendar.WEEK_OF_YEAR,index - sendCalendarCount);
            sendWeekCalendars.add(tempCalendar);
        }
        //刷新一下界面
        if(weekViewPager.getCurrentItem() < (sendCalendarCount - 1) || weekViewPager.getCurrentItem() > (sendCalendarCount + 1)) {
            weekViewPager.setCurrentItem(sendCalendarCount,false);
        } else {
            allWeekViews.get((sendCalendarCount - 1) % 3).weekCalendar = sendWeekCalendars.get(sendCalendarCount - 1);
            allWeekViews.get(sendCalendarCount % 3).weekCalendar = sendWeekCalendars.get(sendCalendarCount);
            allWeekViews.get((sendCalendarCount + 1) % 3).weekCalendar = sendWeekCalendars.get(sendCalendarCount + 1);
            allWeekViews.get(0).reloadCurrMonth();
            allWeekViews.get(1).reloadCurrMonth();
            allWeekViews.get(2).reloadCurrMonth();
            weekViewPager.setCurrentItem(sendCalendarCount,false);
        }
    }
    //###########################  周视图配置 end  ###########################
    //设置监听事件
    public void setDazzleCalendarListener(DazzleCalendarListener listener) {
        this.dazzleCalendarListener = listener;
    }
    //define interface
    public static interface DazzleCalendarListener {
        //某一天被点击
        void onDayViewClick(CalendarDayView calendarDayView);
        //某一天已经显示
        void dayViewDidShow(CalendarDayView calendarDayView);
        //日历已经加载到当前时间
        void calendarDidShow(Calendar calendar);
    }

}
