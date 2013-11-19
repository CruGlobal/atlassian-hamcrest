package com.atlassian.hamcrest;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.hamcrest.Description;
import org.hamcrest.SelfDescribing;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Avoids stack overflows when matchers use other (possibly cyclical) matchers in their description.
 *
 * @author Matt Drees
 */
public class CycleBreakingDescription extends QueueingDescription implements Description {

    public CycleBreakingDescription(Description description) {
        super(description);
    }

    Map<Object, PrimaryReference> instances = Maps.newIdentityHashMap();
    AtomicInteger counter = new AtomicInteger();
    Set<String> ids = Sets.newHashSet();

    @Override
    public Description appendDescriptionOf(final SelfDescribing value) {

        if (!(value instanceof ReflectivelyEqual)) {
            return super.appendDescriptionOf(value);
        } else {
            ReflectivelyEqual<?> matcher = (ReflectivelyEqual<?>) value;
            Object expected = matcher.getExpected();
            if (!instances.containsKey(expected))
            {
                PrimaryReference primaryReference = new PrimaryReference(value, expected);
                instances.put(expected, primaryReference);
                return super.appendDescriptionOf(primaryReference);
            }
            else
            {
                //avoid cycles
                PrimaryReference primaryReference = instances.get(expected);
                PrimaryReference.CyclicReference cyclicReference = primaryReference.newCyclicReference();
                return super.appendDescriptionOf(cyclicReference);
            }
        }

    }

    class PrimaryReference implements SelfDescribing
    {
        SelfDescribing value;
        Object expectedValue;

        String id;

        PrimaryReference(SelfDescribing value, Object expectedValue) {
            this.value = value;
            this.expectedValue = expectedValue;
        }

        @Override
        public void describeTo(final Description description) {
            CycleBreakingDescription.super.enqueue(
                new Runnable() {
                    @Override
                    public void run() {
                        if (id != null) {
                            description.appendText("&" + id);
                        }
                    }
                });
            value.describeTo(description);
        }

        public CyclicReference newCyclicReference() {
            if (id == null)
                makeUniqueId();
            return new CyclicReference();
        }

        private void makeUniqueId() {
            String potentialId = String.valueOf(expectedValue);
            while (ids.contains(potentialId))
            {
                potentialId = String.valueOf(expectedValue) + counter.getAndIncrement();
            }
            ids.add(potentialId);
            id = potentialId;
        }

        class CyclicReference implements SelfDescribing
        {

            @Override
            public void describeTo(Description description) {
                description.appendText("<reference to *" + id+">");
            }
        }
    }
}
