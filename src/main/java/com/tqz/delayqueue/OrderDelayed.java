package com.tqz.delayqueue;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Delayed类中的getDelay方法的返回值为当前任务距离执行任务还剩多久时间，小于0时表示该延迟任务需要执行了。
 * Comparable类中的compareTo方法将在队列中的任务按照执行时间进行排序，从而保证最先到延迟时间的任务排到队列的头部。
 *
 * @author <a href="https://github.com/tian-qingzhao">tianqingzhao</a>
 * @since 2024/9/11 9:36
 */
public class OrderDelayed implements Delayed {

    /**
     * 延迟任务的具体的执行内容.
     */
    private final String taskContent;

    /**
     * 延迟时间，秒为单位.
     */
    private final Long triggerTime;

    public OrderDelayed(String taskContent, Long delayTime) {
        this.taskContent = taskContent;
        this.triggerTime = System.currentTimeMillis() + delayTime * 1000;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(triggerTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return this.triggerTime.compareTo(((OrderDelayed) o).triggerTime);
    }

    public String getTaskContent() {
        return taskContent;
    }

    public Long getTriggerTime() {
        return triggerTime;
    }
}
