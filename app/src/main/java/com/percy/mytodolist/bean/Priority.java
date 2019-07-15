package com.percy.mytodolist.bean;

import android.graphics.Color;

import com.percy.mytodolist.R;

//用不同的颜色用以指代不同的优先级
public enum Priority {

    High(2, 0xFFD63931),
    Medium(1, 0xFFEE8E38),
    Low(0, Color.WHITE);

    public final int intValue;
    public final int color;

    Priority(int intValue, int color) {
        this.intValue = intValue;
        this.color = color;
    }

    public static Priority from(int intValue) {
        for (Priority priority : Priority.values()) {
            if (priority.intValue == intValue) {
                return priority;
            }
        }
        return Priority.Low; // default
    }
}
