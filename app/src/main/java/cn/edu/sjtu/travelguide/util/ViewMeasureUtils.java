package cn.edu.sjtu.travelguide.util;

import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;

public class ViewMeasureUtils {

    /**
     * 根据父 View 规则和子 View 的 LayoutParams，计算子类的宽度(width)测量规则
     *
     * @param view
     */
    public static int getChildWidthMeasureSpec(View view, int parentWidthMeasureSpec) {
        // 获取父 View 的测量模式
        int parentWidthMode = MeasureSpec.getMode(parentWidthMeasureSpec);
        // 获取父 View 的测量尺寸
        int parentWidthSize = MeasureSpec.getSize(parentWidthMeasureSpec);

        // 定义子 View 的测量规则
        int childWidthMeasureSpec = 0;

        // 获取子 View 的 LayoutParams
        ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams) view.getLayoutParams();


        if (parentWidthMode == MeasureSpec.EXACTLY || parentWidthMode == MeasureSpec.AT_MOST) {
            /* 这是当父类的模式是 dp 的情况 */
            if (layoutParams.width > 0) {
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(layoutParams.width, MeasureSpec.EXACTLY);
            } else if (layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(parentWidthSize, MeasureSpec.AT_MOST);
            } else if (layoutParams.width == ViewGroup.LayoutParams.MATCH_PARENT) {
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(parentWidthSize, MeasureSpec.EXACTLY);
            }
        } else if (parentWidthMode == MeasureSpec.UNSPECIFIED) {
            /* 这是当父类的模式是 MATCH_PARENT 的情况 */
            if (layoutParams.width > 0) {
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(layoutParams.width, MeasureSpec.EXACTLY);
            } else if (layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            } else if (layoutParams.width == ViewGroup.LayoutParams.MATCH_PARENT) {
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            }
        }

        // 返回子 View 的测量规则
        return childWidthMeasureSpec;
    }

    /**
     * 根据父 View 规则和子 View 的 LayoutParams，计算子类的宽度(width)测量规则
     *
     * @param view
     */
    public static int getChildHeightMeasureSpec(View view, int parentHeightMeasureSpec) {
        // 获取父 View 的测量模式
        int parentHeightMode = MeasureSpec.getMode(parentHeightMeasureSpec);
        // 获取父 View 的测量尺寸
        int parentHeightSize = MeasureSpec.getSize(parentHeightMeasureSpec);

        // 定义子 View 的测量规则
        int childHeightMeasureSpec = 0;

        // 获取子 View 的 LayoutParams
        ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams) view.getLayoutParams();


        if (parentHeightMode == MeasureSpec.EXACTLY || parentHeightMode == MeasureSpec.AT_MOST) {
            /* 这是当父类的模式是 dp 的情况 */
            if (layoutParams.height > 0) {
                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(layoutParams.height, MeasureSpec.EXACTLY);
            } else if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(parentHeightSize, MeasureSpec.AT_MOST);
            } else if (layoutParams.width == ViewGroup.LayoutParams.MATCH_PARENT) {
                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(parentHeightSize, MeasureSpec.EXACTLY);
            }
        } else if (parentHeightMode == MeasureSpec.UNSPECIFIED) {
            /* 这是当父类的模式是 MATCH_PARENT 的情况 */
            if (layoutParams.height > 0) {
                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(layoutParams.height, MeasureSpec.EXACTLY);
            } else if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            } else if (layoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT) {
                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            }
        }

        // 返回子 View 的测量规则
        return childHeightMeasureSpec;
    }
}