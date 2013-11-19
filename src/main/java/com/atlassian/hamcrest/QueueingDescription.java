package com.atlassian.hamcrest;

import com.google.common.collect.Lists;
import org.hamcrest.Description;
import org.hamcrest.SelfDescribing;

import java.util.Queue;

/**
 *
 *
 * @author Matt Drees
 */
public class QueueingDescription implements Description {
    private Description description;

    private boolean flushing;

    public QueueingDescription(Description description) {
        this.description = description;
    }

    Queue<Runnable> operations = Lists.newLinkedList();

    @Override
    public Description appendText(final String text) {
        enqueue(new Runnable() {
            @Override
            public void run() {
                description.appendText(text);
            }
        });
        return this;
    }

    @Override
    public Description appendDescriptionOf(final SelfDescribing value) {
        value.describeTo(this);
        return this;
    }

    @Override
    public Description appendValue(final Object value) {
        enqueue(
            new Runnable() {
                @Override
                public void run() {
                    description.appendValue(value);
                }
            });
        return this;
    }

    @Override
    public <T> Description appendValueList(final String start, final String separator, final String end, final T... values) {
        enqueue(new Runnable() {
            @Override
            public void run() {
                description.appendValueList(start, separator, end, values);
            }
        });
        return this;
    }

    @Override
    public <T> Description appendValueList(final String start, final String separator, final String end, final Iterable<T> values) {
        enqueue(
            new Runnable() {
                @Override
                public void run() {
                    description.appendValueList(start, separator, end, values);
                }
            });
        return this;
    }

    @Override
    public Description appendList(
        final String start, final String separator, final String end, final Iterable<? extends SelfDescribing> values) {
        enqueue(new Runnable() {
            @Override
            public void run() {
                description.appendList(start, separator, end, values);
            }
        });
        return this;
    }

    protected void enqueue(Runnable runnable) {
        if (flushing)
            runnable.run();
        else
            operations.add(runnable);
    }


    public void flushDescription() {
        flushing = true;
        try {
            while (!operations.isEmpty())
                operations.remove().run();
        } finally {
            flushing = false;
        }

    }
}
