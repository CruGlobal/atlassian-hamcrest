package com.atlassian.hamcrest;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import org.hamcrest.Description;
import org.hamcrest.SelfDescribing;

import java.util.Set;

/**
 *
 *
 * @author Matt Drees
 */
public class IndentingDescription implements Description {
    private Description description;

    private int level;
    boolean atNewline = true;

    private static final Set<String> openers = ImmutableSet.of("{", "[");
    private static final Set<String> closers = ImmutableSet.of("}", "]");
    private static final Set<String> separators = ImmutableSet.of(", ");
    private String newline = System.getProperty("line.separator");

    public IndentingDescription(Description description) {
        this.description = description;
    }

    @Override
    public Description appendText(String text) {
        if (openers.contains(text))
        {
            indentIfNecessary();
            description.appendText(text);
            newline();
            level++;
        }
        else if (closers.contains(text)) {
            level--;
            if (!atNewline)
                newline();
            indent();
            description.appendText(text);
//            newline();
        }
        else if (separators.contains(text))
        {
            indentIfNecessary();
            description.appendText(text);
            newline();
        }
        else
        {
            indentIfNecessary();
            description.appendText(indentNewlines(text));
        }

        return this;
    }

    private String indentNewlines(String text) {
        return text.replace("\\n", "\\n" + buildIndentation());
    }

    @Override
    public Description appendDescriptionOf(SelfDescribing value) {
        indentIfNecessary();
        value.describeTo(this);
        return this;
    }

    @Override
    public Description appendValue(Object value) {
        indentIfNecessary();
        description.appendValue(value);
        return this;
    }

    //TODO: perhaps lists should be separated by new lines

    @Override
    public <T> Description appendValueList(String start, String separator, String end, T... values) {
        indentIfNecessary();
        description.appendValueList(start, separator, end, values);
        return this;
    }

    @Override
    public <T> Description appendValueList(String start, String separator, String end, Iterable<T> values) {
        indentIfNecessary();
        description.appendValueList(start, separator, end, values);
        return this;
    }

    @Override
    public Description appendList(
        String start,
        String separator,
        String end,
        Iterable<? extends SelfDescribing> values) {
        indentIfNecessary();
        description.appendList(start, separator, end, values);
        return this;
    }

    private void indentIfNecessary() {
        if (atNewline)
            indent();
    }

    private void indent() {
        description.appendText(buildIndentation());
        atNewline = false;
    }

    private String buildIndentation() {
        return Strings.repeat("  ", level);
    }

    private void newline() {
        description.appendText(newline);
        atNewline = true;
    }

}
