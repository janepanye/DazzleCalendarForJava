package fuck.dazzlecalendar.CustemRoundView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.TextView;

import fuck.dazzlecalendar.MyApplication.MyApplication;
import fuck.dazzlecalendar.Tool.DiviceInfo;


/**
 * Created by mac on 2017/12/17.
 */
//可以设置圆角、边框、背景色的文本框
public class RoundTextView extends AppCompatTextView {
    private Paint linePaint = new Paint();//画边框的笔
    private Paint backgroundPaint = new Paint();//画背景的笔
    private float lineWidth;//边框的宽度
    private float leftTopRadius, leftBottomRadius, rightTopRadius, rightBottomRadius;//各个圆角大小

    public RoundTextView(Context context) {
        super(context);
    }
    public RoundTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    //设置背景圆角 单位是dp 简化代码  针对lineWidth大于圆角的情况，需要单独处理（目前只处理圆角为0的情况）
    public void setBackgroundAndLine(int backgroundColor,int lineColor,float lineWidth,float leftTopRadius,
                                   float leftBottomRadius,float rightTopRadius,float rightBottomRadius) {

        Context context = MyApplication.getInstance();

        this.leftTopRadius = DiviceInfo.dpToPx(context,leftTopRadius);
        this.leftBottomRadius = DiviceInfo.dpToPx(context,leftBottomRadius);
        this.rightTopRadius = DiviceInfo.dpToPx(context,rightTopRadius);
        this.rightBottomRadius = DiviceInfo.dpToPx(context,rightBottomRadius);

        this.lineWidth = DiviceInfo.dpToPx(context,lineWidth);

        linePaint.setColor(lineColor);
        backgroundPaint.setColor(backgroundColor);


        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.FILL);

        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setStyle(Paint.Style.FILL);
        invalidate();
    }
    //只设置边框线 背景为透明
    public void setStrokeLine(int lineColor,float lineWidth,float leftTopRadius,
                              float leftBottomRadius,float rightTopRadius,float rightBottomRadius) {
        Context context = MyApplication.getInstance();

        this.leftTopRadius = DiviceInfo.dpToPx(context,leftTopRadius);
        this.leftBottomRadius = DiviceInfo.dpToPx(context,leftBottomRadius);
        this.rightTopRadius = DiviceInfo.dpToPx(context,rightTopRadius);
        this.rightBottomRadius = DiviceInfo.dpToPx(context,rightBottomRadius);

        this.lineWidth = DiviceInfo.dpToPx(context,lineWidth);

        linePaint.setColor(lineColor);
        backgroundPaint.setColor(0);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeCap(Paint.Cap.SQUARE);
        linePaint.setStyle(Paint.Style.STROKE);
        invalidate();
    }
    //只画背景 边框为透明
    public void setBackground(int backgroundColor,float leftTopRadius,
                              float leftBottomRadius,float rightTopRadius,float rightBottomRadius) {
        Context context = MyApplication.getInstance();

        this.leftTopRadius = DiviceInfo.dpToPx(context,leftTopRadius);
        this.leftBottomRadius = DiviceInfo.dpToPx(context,leftBottomRadius);
        this.rightTopRadius = DiviceInfo.dpToPx(context,rightTopRadius);
        this.rightBottomRadius = DiviceInfo.dpToPx(context,rightBottomRadius);

        this.lineWidth = DiviceInfo.dpToPx(context,lineWidth);

        linePaint.setColor(0);
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setStyle(Paint.Style.FILL);
        invalidate();
    }
    //绘制圆角 一共要绘制8部分
    @Override
    protected void onDraw(Canvas canvas) {
        //只画边框线 背景透明
        if(backgroundPaint.getColor() == 0) {
            drawStrokeLine(canvas);
        } else if(linePaint.getColor() == 0){//只画背景
            drawBackground(canvas);
        } else {
            drawBackgroundAndLine(canvas);
        }
        super.onDraw(canvas);
    }
    //画背景
    private void drawBackground(Canvas canvas) {
        //如果四个圆角大小一样
        if(leftTopRadius == leftBottomRadius && leftBottomRadius == rightTopRadius && rightTopRadius == rightBottomRadius) {
            RectF rectF = new RectF(0,0,getWidth(),getHeight());
            canvas.drawRoundRect(rectF,leftTopRadius,leftTopRadius,backgroundPaint);
        }
        //如果上面两个圆角一样
        else if(leftTopRadius == rightTopRadius && leftBottomRadius == rightBottomRadius) {
            //上
            RectF rectF = new RectF(0,0,getWidth(),leftTopRadius * 2);
            canvas.drawRoundRect(rectF,leftTopRadius,leftTopRadius,backgroundPaint);
            //中
            rectF = new RectF(0,leftTopRadius,getWidth(),getHeight() - leftBottomRadius);
            canvas.drawRoundRect(rectF,0,0,backgroundPaint);
            //下
            rectF = new RectF(0,getHeight() - 2 * leftBottomRadius,getWidth(),getHeight());
            canvas.drawRoundRect(rectF,leftBottomRadius,leftBottomRadius,backgroundPaint);
        }
        //如果左边两个圆角一样
        else if(leftTopRadius == leftBottomRadius && rightTopRadius == rightBottomRadius) {
            //左
            RectF rectF = new RectF(0,0,leftTopRadius * 2,getHeight());
            canvas.drawRoundRect(rectF,leftTopRadius,leftTopRadius,backgroundPaint);
            //中
            rectF = new RectF(leftTopRadius,0,getWidth() - rightTopRadius,getHeight());
            canvas.drawRoundRect(rectF,0,0,backgroundPaint);
            //右
            rectF = new RectF(getWidth() - 2 * rightTopRadius,0,getWidth(),getHeight());
            canvas.drawRoundRect(rectF,rightTopRadius,rightTopRadius,backgroundPaint);
        }
    }
    //画边框
    private void drawStrokeLine(Canvas canvas) {
        //画出线条 1 左上
        RectF rectF = new RectF(lineWidth / 4,lineWidth / 4,leftTopRadius * 2+lineWidth / 4,leftTopRadius * 2+lineWidth / 4);
        linePaint.setStrokeWidth(lineWidth / 2);
        canvas.drawArc(rectF,180,90,false,linePaint);
        //画出线条 2 上
        linePaint.setStrokeWidth(lineWidth);
        canvas.drawLine(leftTopRadius+lineWidth / 2,0,getWidth() - rightTopRadius-lineWidth / 2,0,linePaint);
        //画出线条 3 右上
        rectF = new RectF(getWidth() - rightTopRadius * 2 - lineWidth / 4,lineWidth / 4,getWidth()-lineWidth / 4,rightTopRadius * 2+lineWidth / 4);
        linePaint.setStrokeWidth(lineWidth / 2);
        canvas.drawArc(rectF,270,90,false,linePaint);
        //画出线条 4 右
        linePaint.setStrokeWidth(lineWidth);
        canvas.drawLine(getWidth(),rightTopRadius+lineWidth / 2,getWidth(),getHeight() - rightBottomRadius-lineWidth / 2,linePaint);
        //画出线条 5 右下
        rectF = new RectF(getWidth() - rightBottomRadius * 2-lineWidth / 4,getHeight() - rightBottomRadius * 2-lineWidth / 4,getWidth()-lineWidth / 4,getHeight()-lineWidth / 4);
        linePaint.setStrokeWidth(lineWidth / 2);
        canvas.drawArc(rectF,0,90,false,linePaint);
        //画出线条 6 下
        linePaint.setStrokeWidth(lineWidth);
        canvas.drawLine(leftBottomRadius+lineWidth / 2,getHeight(),getWidth() - rightBottomRadius-lineWidth / 2,getHeight(),linePaint);
        //画出线条 7 左下
        rectF = new RectF(lineWidth / 4,getHeight() - leftBottomRadius * 2 - lineWidth / 4,leftBottomRadius * 2+lineWidth / 4,getHeight()-lineWidth / 4);
        linePaint.setStrokeWidth(lineWidth / 2);
        canvas.drawArc(rectF,90,90,false,linePaint);
        //画出线条 8 左
        linePaint.setStrokeWidth(lineWidth);
        canvas.drawLine(0,leftTopRadius+lineWidth / 2,0,getHeight() - leftBottomRadius-lineWidth / 2,linePaint);
    }
    //画线条
    private void drawBackgroundAndLine(Canvas canvas) {
        //如果四个圆角大小一样
        if(leftTopRadius == leftBottomRadius && leftBottomRadius == rightTopRadius && rightTopRadius == rightBottomRadius) {
            RectF rectF = new RectF(0,0,getWidth(),getHeight());
            canvas.drawRoundRect(rectF,leftTopRadius,leftTopRadius,linePaint);
        }
        //如果上面两个圆角一样
        else if(leftTopRadius == rightTopRadius && leftBottomRadius == rightBottomRadius) {
            //上
            RectF rectF = new RectF(0,0,getWidth(),leftTopRadius * 2);
            canvas.drawRoundRect(rectF,leftTopRadius,leftTopRadius,linePaint);
            //中
            rectF = new RectF(0,leftTopRadius,getWidth(),getHeight() - leftBottomRadius);
            canvas.drawRoundRect(rectF,0,0,linePaint);
            //下
            rectF = new RectF(0,getHeight() - 2 * leftBottomRadius,getWidth(),getHeight());
            canvas.drawRoundRect(rectF,leftBottomRadius,leftBottomRadius,linePaint);
        }
        //如果左边两个圆角一样
        else if(leftTopRadius == leftBottomRadius && rightTopRadius == rightBottomRadius) {
            //左
            RectF rectF = new RectF(0,0,leftTopRadius * 2,getHeight());
            canvas.drawRoundRect(rectF,leftTopRadius,leftTopRadius,linePaint);
            //中
            rectF = new RectF(leftTopRadius,0,getWidth() - rightTopRadius,getHeight());
            canvas.drawRoundRect(rectF,0,0,linePaint);
            //右
            rectF = new RectF(getWidth() - 2 * rightTopRadius,0,getWidth(),getHeight());
            canvas.drawRoundRect(rectF,rightTopRadius,rightTopRadius,linePaint);
        }

        //矩形要特别注意了 我们只处理以下几种情况
        //4个圆角大小一样
        if(leftTopRadius == leftBottomRadius && rightTopRadius == rightBottomRadius && leftTopRadius == rightTopRadius) {
            RectF rectF;
            if(leftTopRadius == 0) {
                rectF = new RectF(lineWidth,lineWidth,getWidth() - lineWidth,getHeight() - lineWidth);
                canvas.drawRoundRect(rectF,0,0,backgroundPaint);
            } else {
                rectF = new RectF(lineWidth,lineWidth,getWidth() - lineWidth,getHeight() - lineWidth);
                canvas.drawRoundRect(rectF,leftTopRadius - lineWidth,leftTopRadius - lineWidth,backgroundPaint);
            }
        } else if(leftTopRadius == rightTopRadius && leftBottomRadius == rightBottomRadius) {//上两个角相同 下两个角相同
            RectF rectF;
            //圆角为0需要单独处理
            if(leftTopRadius == 0) {
                //中
                rectF = new RectF(lineWidth,lineWidth,getWidth()-lineWidth,getHeight() - leftBottomRadius-lineWidth);
                canvas.drawRoundRect(rectF,0,0,backgroundPaint);
            } else {
                //上
                rectF = new RectF(lineWidth,lineWidth,getWidth()-lineWidth,leftTopRadius * 2-lineWidth);
                canvas.drawRoundRect(rectF,leftTopRadius-lineWidth,leftTopRadius-lineWidth,backgroundPaint);
                //中
                rectF = new RectF(lineWidth,leftTopRadius,getWidth()-lineWidth,getHeight() - leftBottomRadius-lineWidth);
                canvas.drawRoundRect(rectF,0,0,backgroundPaint);
            }
            //下
            rectF = new RectF(lineWidth,getHeight() - 2 * leftBottomRadius-lineWidth,getWidth()-lineWidth,getHeight()-lineWidth);
            canvas.drawRoundRect(rectF,leftBottomRadius-lineWidth,leftBottomRadius-lineWidth,backgroundPaint);
        } else if(leftTopRadius == leftBottomRadius && rightBottomRadius == rightTopRadius) {//左两个角相同 右两个角相同
            RectF rectF;
            //圆角为0需要单独处理
            if(leftTopRadius == 0) {
                //中
                rectF  = new RectF(lineWidth,lineWidth,getWidth() - rightTopRadius - lineWidth,getHeight() - lineWidth);
                canvas.drawRoundRect(rectF,0,0,backgroundPaint);
            } else {
                //左
                rectF  = new RectF(lineWidth,lineWidth,leftTopRadius * 2 - lineWidth,getHeight() - lineWidth);
                canvas.drawRoundRect(rectF,leftTopRadius - lineWidth,leftTopRadius - lineWidth,backgroundPaint);
                //中
                rectF = new RectF(leftTopRadius,lineWidth,getWidth() - rightTopRadius - lineWidth,getHeight() - lineWidth);
                canvas.drawRoundRect(rectF,0,0,backgroundPaint);
            }
            //右
            rectF = new RectF(getWidth() - 2 * rightTopRadius - lineWidth,lineWidth,getWidth()- lineWidth,getHeight() - lineWidth);
            canvas.drawRoundRect(rectF,rightTopRadius - lineWidth,rightTopRadius - lineWidth,backgroundPaint);
        }
    }
}
