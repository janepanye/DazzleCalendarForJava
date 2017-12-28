package fuck.dazzlecalendar;

import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import fuck.dazzlecalendar.DazzleCalendar.CalendarDayView;
import fuck.dazzlecalendar.DazzleCalendar.DazzleCalendar;
import fuck.dazzlecalendar.Tool.CalendarTool;
import fuck.dazzlecalendar.Tool.DiviceInfo;
import fuck.dazzlecalendar.Tool.StringUtil;


public class DazzleCalendarActivity extends AppCompatActivity implements DazzleCalendar.DazzleCalendarListener, View.OnTouchListener {
    //用户当前选择的时间
    private Calendar selectCalendar = Calendar.getInstance();
    //日历控件
    private DazzleCalendar dazzleCalendar = null;
    //下面这些属性都是拿来做周月切换的
    //移动的起始点，保存着，用来计算移动了多少距离
    private float lastPointY;
    //月视图上下移动了多少距离
    private float monthCalendarMovedPointY;
    //月视图开始移动的点
    private float monthCalendarStartMovePonitY;
    //当前选中的时间在第几行
    private int selectWeekOfMonth;
    //日历视图的布局
    ViewGroup.LayoutParams calendarLayoutParams = null;
    //月视图的布局
    RelativeLayout.LayoutParams monthLayoutParams = null;
    //周视图的布局
    RelativeLayout.LayoutParams weekLayoutParams = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dazzle_calendar_layout_two);
        dazzleCalendar = (DazzleCalendar)findViewById(R.id.dazzle_calendar);
        dazzleCalendar.setDazzleCalendarListener(this);
        //设置界面 月视图
        dazzleCalendar.calendarType = 1;
        //注意 不能隐藏和显示来控制周月视图，而是改变LayoutParams；因为隐藏下，viewPager并不会执行任何方法
        dazzleCalendar.configMonthView();
        dazzleCalendar.configWeekView();
        //让周视图隐藏（如果是周视图，你就需要让月视图隐藏）
        final RelativeLayout.LayoutParams tempWeek = new RelativeLayout.LayoutParams(dazzleCalendar.weekViewPager.getLayoutParams());
        tempWeek.setMargins(0,-DiviceInfo.dpToPx(this,42),0,0);
        dazzleCalendar.weekViewPager.setLayoutParams(tempWeek);
        //上下滑动 切换周月
        dazzleCalendar.setOnTouchListener(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, Menu.FIRST+1, 0, "周月切换");
        menu.add(Menu.NONE, Menu.FIRST+2, 0, "显示指定时间");
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //周月切换
        if(item.getItemId() == Menu.FIRST+1) {
            dazzleCalendar.calendarType = dazzleCalendar.calendarType == 1 ? 0 : 1;
            if(dazzleCalendar.calendarType == 0) {
                //让周视图显示当前用户选中的时间
                dazzleCalendar.setWeekSendCalendar(selectCalendar);
                //刷新一下界面
                //让周视图显示
                RelativeLayout.LayoutParams tempWeek = new RelativeLayout.LayoutParams(dazzleCalendar.weekViewPager.getLayoutParams());
                tempWeek.setMargins(0,0,0,0);
                dazzleCalendar.weekViewPager.setLayoutParams(tempWeek);
                //让月图隐藏
                RelativeLayout.LayoutParams tempMonth = new RelativeLayout.LayoutParams(dazzleCalendar.monthViewPager.getLayoutParams());
                tempMonth.setMargins(0,-DiviceInfo.dpToPx(DazzleCalendarActivity.this,210),0,0);
                dazzleCalendar.monthViewPager.setLayoutParams(tempMonth);
            } else {
                //让月视图显示当前用户选中的时间
                dazzleCalendar.setMonthSendCalendar(selectCalendar);
                //让周视图隐藏
                RelativeLayout.LayoutParams tempWeek = new RelativeLayout.LayoutParams(dazzleCalendar.weekViewPager.getLayoutParams());
                tempWeek.setMargins(0,-DiviceInfo.dpToPx(DazzleCalendarActivity.this,42),0,0);
                dazzleCalendar.weekViewPager.setLayoutParams(tempWeek);
                //让月图显示
                RelativeLayout.LayoutParams tempMonth = new RelativeLayout.LayoutParams(dazzleCalendar.monthViewPager.getLayoutParams());
                tempMonth.setMargins(0,0,0,0);
                dazzleCalendar.monthViewPager.setLayoutParams(tempMonth);
            }
        }
        //显示指定时间
        if(item.getItemId() == Menu.FIRST+2) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(DazzleCalendarActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                selectCalendar.clear();
                                selectCalendar.set(i,i1,i2);
                                //刷新日历
                                if(dazzleCalendar.calendarType == 1)
                                    dazzleCalendar.setMonthSendCalendar(selectCalendar);
                                else
                                    dazzleCalendar.setWeekSendCalendar(selectCalendar);
                                //设置头部的时间
                                setTitle(selectCalendar.get(Calendar.YEAR)+"年"+(selectCalendar.get(Calendar.MONTH)+1)+"月");
                            }
                        },
                        selectCalendar.get(Calendar.YEAR), selectCalendar.get(Calendar.MONTH),selectCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }
    //某一天被点击
    @Override
    public void onDayViewClick(CalendarDayView calendarDayView) {
        selectCalendar.setTime(calendarDayView.dayCalendar.getTime());
        //点击了一个月中不同的时间，需要翻页，不然就只需要刷新当前界面
        if(calendarDayView.dayCalendar.get(Calendar.MONTH) != calendarDayView.weekMonthCalendar.get(Calendar.MONTH)) {
            //刷新一下时间
            if(dazzleCalendar.calendarType == 0)
                //让周视图显示当前用户选中的时间
                dazzleCalendar.setWeekSendCalendar(selectCalendar);
            else
                //让月视图显示当前用户选中的时间
                dazzleCalendar.setMonthSendCalendar(selectCalendar);
        } else {
            //判断周月视图
            if(dazzleCalendar.calendarType == 1)
                dazzleCalendar.allMonthViews.get(dazzleCalendar.monthViewPager.getCurrentItem() % 3).reloadCurrMonth();
            else
                dazzleCalendar.allWeekViews.get(dazzleCalendar.weekViewPager.getCurrentItem() % 3).reloadCurrMonth();
        }
    }
    //某一天已经显示 我们在这里修饰该天
    @Override
    public void dayViewDidShow(CalendarDayView calendarDayView) {
        calendarDayView.dotNumberText.setVisibility(View.GONE);
        calendarDayView.dotView.setVisibility(View.GONE);
        calendarDayView.roundView.setVisibility(View.GONE);
        calendarDayView.centerTextView.setTextColor(getResources().getColor(R.color.black));

        //显示内容设置
        String currShowString = calendarDayView.dayCalendar.get(Calendar.DAY_OF_MONTH)+"";
        //今天是否是节气
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(calendarDayView.dayCalendar.getTime());
        String specialString = CalendarTool.specialString(calendar);
        if(!StringUtil.isBlank(specialString))
            currShowString = specialString;
        //是不是农历节日
        String lunarHolidayString = CalendarTool.lunarHolidayString(calendar);
        if(!StringUtil.isBlank(lunarHolidayString))
            currShowString = lunarHolidayString;
        //是不是公历节日
        String solarHolidayString = CalendarTool.solarHolidayString(calendar);
        if(!StringUtil.isBlank(solarHolidayString))
            currShowString = solarHolidayString;
        //今天是否是今天
        calendar.setTime(new Date());
        if(calendar.get(java.util.Calendar.YEAR) == calendarDayView.dayCalendar.get(Calendar.YEAR) &&
                calendar.get(java.util.Calendar.DAY_OF_MONTH) == calendarDayView.dayCalendar.get(Calendar.DAY_OF_MONTH) &&
                calendar.get(java.util.Calendar.MONTH) == calendarDayView.dayCalendar.get(Calendar.MONTH))
            currShowString = "今天";
        calendarDayView.centerTextView.setText(currShowString);

        //颜色设置
        //不是本月就显示灰色
        if(calendarDayView.calendarType == 1)
            if(calendarDayView.weekMonthCalendar.get(Calendar.MONTH) != calendarDayView.dayCalendar.get(Calendar.MONTH))
                calendarDayView.centerTextView.setTextColor(getResources().getColor(R.color.lightGray));
        //如果是今天就加上边框
        if(calendarDayView.dayCalendar.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) &&
                calendarDayView.dayCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                calendarDayView.dayCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
            calendarDayView.roundView.setStrokeLine(getResources().getColor(R.color.bangNavBarColor),1,21,21,21,21);
            calendarDayView.roundView.setVisibility(View.VISIBLE);
        }
        //是用户选中的天就加上一个背景
        if(calendarDayView.dayCalendar.get(Calendar.DAY_OF_MONTH) == selectCalendar.get(Calendar.DAY_OF_MONTH) &&
                calendarDayView.dayCalendar.get(Calendar.MONTH) == selectCalendar.get(Calendar.MONTH) &&
                calendarDayView.dayCalendar.get(Calendar.YEAR) == selectCalendar.get(Calendar.YEAR)) {
            calendarDayView.roundView.setBackground(getResources().getColor(R.color.bangNavBarColor),21,21,21,21);
            calendarDayView.roundView.setVisibility(View.VISIBLE);
            calendarDayView.centerTextView.setTextColor(getResources().getColor(R.color.white));
        }
        //模拟今天是否有未完成的事物
        int noFinishCount = 0;
        if(calendarDayView.dayCalendar.get(Calendar.DAY_OF_MONTH) == 1)
            noFinishCount = 1;
        if(calendarDayView.dayCalendar.get(Calendar.DAY_OF_MONTH) == 9)
            noFinishCount = 3;
        if(calendarDayView.dayCalendar.get(Calendar.DAY_OF_MONTH) == 14)
            noFinishCount = 6;
        if(calendarDayView.dayCalendar.get(Calendar.DAY_OF_MONTH) == 26)
            noFinishCount = 2;
        if(noFinishCount == 0)
            return;;
        if(calendarDayView.calendarType == 0) {//周视图显示数字
            calendarDayView.dotNumberText.setVisibility(View.VISIBLE);
            calendarDayView.dotNumberText.setText(noFinishCount+"");
        } else {//月视图显示红点
            calendarDayView.dotView.setVisibility(View.VISIBLE);
        }
    }
    //日历已经加载到了某一个种子时间，逻辑自己处理
    @Override
    public void calendarDidShow(Calendar calendar) {
        //时间换算到当前周、月对应的时间
        if(dazzleCalendar.calendarType == 1) {
            int selectDay = selectCalendar.get(Calendar.DAY_OF_MONTH);
            selectCalendar.clear();
            selectCalendar.set(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),selectDay);
        } else {
            int dayOfWeek = selectCalendar.get(Calendar.DAY_OF_WEEK);
            selectCalendar.clear();
            selectCalendar.set(Calendar.YEAR,calendar.get(Calendar.YEAR));
            selectCalendar.set(Calendar.WEEK_OF_YEAR,calendar.get(Calendar.WEEK_OF_YEAR));
            selectCalendar.set(Calendar.DAY_OF_WEEK,dayOfWeek);
        }
        //判断周月视图
        if(dazzleCalendar.calendarType == 1)
            dazzleCalendar.allMonthViews.get(dazzleCalendar.monthViewPager.getCurrentItem() % 3).reloadCurrMonth();
        else
            dazzleCalendar.allWeekViews.get(dazzleCalendar.weekViewPager.getCurrentItem() % 3).reloadCurrMonth();
        //设置头部的时间
        setTitle(selectCalendar.get(Calendar.YEAR)+"年"+(selectCalendar.get(Calendar.MONTH)+1)+"月");
    }
    //父view拦截消耗ACTION_DOWN和ACTION_MOVE，如果满足一定的条件就会进行拦截return true
    //然后setOnTouchListener里面会消耗ACTION_MOVE和ACTION_UP
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            //将点下的点的坐标保存（这里的事件被父view消耗了）
            case MotionEvent.ACTION_DOWN:
                Log.d("被外部消耗了","ACTION_DOWN");
                lastPointY = event.getRawY();
                Log.d("外面起点y:",lastPointY+"");
                break;
            //移动结束
            case MotionEvent.ACTION_UP:
                Log.d("被外部消耗了","ACTION_UP");
                lastPointY = 0;
                //看看当前应该显示哪个视图
                if(monthCalendarMovedPointY > 0) {//往下滑动的，显示月
                    dazzleCalendar.calendarType = 1;
                    ValueAnimator monthAnimator =  ValueAnimator.ofInt(monthLayoutParams.topMargin,0);
                    monthAnimator.setDuration(150);
                    monthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            Log.d("当前动画的值",(int)valueAnimator.getAnimatedValue()+"");
                            monthLayoutParams.setMargins(0,(int)valueAnimator.getAnimatedValue(),0,0);
                            dazzleCalendar.monthViewPager.setLayoutParams(monthLayoutParams);
                        }
                    });
                    monthAnimator.start();
                    weekLayoutParams.setMargins(0,-DiviceInfo.dpToPx(DazzleCalendarActivity.this,42),0,0);
                    dazzleCalendar.weekViewPager.setLayoutParams(weekLayoutParams);
                    calendarLayoutParams.height = DiviceInfo.dpToPx(DazzleCalendarActivity.this,252);
                    calendarLayoutParams.width = DiviceInfo.diviceWidth(DazzleCalendarActivity.this);
                    dazzleCalendar.setLayoutParams(calendarLayoutParams);
                } else {
                    dazzleCalendar.calendarType = 0;
                    ValueAnimator monthAnimator =  ValueAnimator.ofInt(monthLayoutParams.topMargin,-DiviceInfo.dpToPx(DazzleCalendarActivity.this,210));
                    monthAnimator.setDuration(150);
                    monthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            Log.d("当前动画的值",(int)valueAnimator.getAnimatedValue()+"");
                            monthLayoutParams.setMargins(0,(int)valueAnimator.getAnimatedValue(),0,0);
                            dazzleCalendar.monthViewPager.setLayoutParams(monthLayoutParams);
                        }
                    });
                    monthAnimator.start();
                    weekLayoutParams.setMargins(0,0,0,0);
                    dazzleCalendar.weekViewPager.setLayoutParams(weekLayoutParams);
                    calendarLayoutParams.height = DiviceInfo.dpToPx(DazzleCalendarActivity.this,42);
                    calendarLayoutParams.width = DiviceInfo.diviceWidth(DazzleCalendarActivity.this);
                    dazzleCalendar.setLayoutParams(calendarLayoutParams);
                }
                break;
            //计算出需要移动的距离
            case MotionEvent.ACTION_MOVE:
                Log.d("被外部消耗了","ACTION_MOVE");
                //取第一次的坐标，好来做动画
                //拦截到了事件，开始进行日历动画
                if(lastPointY == 0) {
                    lastPointY = event.getRawY();
                    //得到当前选中的时间是第几周
                    selectWeekOfMonth = selectCalendar.get(Calendar.WEEK_OF_MONTH);
                    monthLayoutParams = (RelativeLayout.LayoutParams)dazzleCalendar.monthViewPager.getLayoutParams();
                    weekLayoutParams = (RelativeLayout.LayoutParams)dazzleCalendar.weekViewPager.getLayoutParams();
                    calendarLayoutParams = dazzleCalendar.getLayoutParams();
                    monthCalendarStartMovePonitY = monthLayoutParams.topMargin;
                    //刷新一下时间
                    if(dazzleCalendar.calendarType == 1)
                        //让周视图显示当前用户选中的时间
                        dazzleCalendar.setWeekSendCalendar(selectCalendar);
                    else
                        //让月视图显示当前用户选中的时间
                        dazzleCalendar.setMonthSendCalendar(selectCalendar);
                    calendarLayoutParams.height = DiviceInfo.dpToPx(DazzleCalendarActivity.this,252);
                    calendarLayoutParams.width = DiviceInfo.diviceWidth(DazzleCalendarActivity.this);
                    dazzleCalendar.setLayoutParams(calendarLayoutParams);
                }
                monthCalendarMovedPointY = event.getRawY() - lastPointY;
                Log.d("外面移动了多少y:",monthCalendarMovedPointY+"");
                //移动月视图到哪个地方 处理边界问题
                int needMoveToY = (int)(monthCalendarMovedPointY + monthCalendarStartMovePonitY);
                if(needMoveToY > 0)
                    needMoveToY = 0;
                if(needMoveToY <- DiviceInfo.dpToPx(DazzleCalendarActivity.this,
                        210))
                    needMoveToY = -DiviceInfo.dpToPx(DazzleCalendarActivity.this,
                            210);
                Log.d("月视图应该在多少y:",needMoveToY+"");
                monthLayoutParams.setMargins(0,needMoveToY,0,0);
                dazzleCalendar.monthViewPager.setLayoutParams(monthLayoutParams);

                //让周视图在合适的时机显示出来
                if(dazzleCalendar.calendarType == 1) {//月视图变为周视图
                    if(monthCalendarMovedPointY <= (1 - selectWeekOfMonth) * DiviceInfo.dpToPx(DazzleCalendarActivity.this,42))
                        weekLayoutParams.setMargins(0,0,0,0);
                    else
                        weekLayoutParams.setMargins(0,-DiviceInfo.dpToPx(DazzleCalendarActivity.this,42),0,0);
                } else {//周视图变为月视图
                    if(monthCalendarMovedPointY >= (6 - selectWeekOfMonth) * DiviceInfo.dpToPx(DazzleCalendarActivity.this,42))
                        weekLayoutParams.setMargins(0,-DiviceInfo.dpToPx(DazzleCalendarActivity.this,42),0,0);
                    else
                        weekLayoutParams.setMargins(0,0,0,0);
                }
                dazzleCalendar.weekViewPager.setLayoutParams(weekLayoutParams);
                break;
        }
        return true;
    }
}
