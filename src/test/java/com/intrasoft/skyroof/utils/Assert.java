package com.intrasoft.skyroof.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Assertion utility class that assists in validating arguments.
 * Useful for identifying programmer errors early and
 * clearly at runtime. Based on the Spring (and JUnit) respective class.
 * For use by utility modules for which we don't
 * want a spring dependency.
 */
public class Assert {

    private Assert() {
    }

    /**
     * Assert a boolean expression, throwing <code>IllegalArgumentException</code> if the test result is
     * <code>false</code>.
     * <p/>
     * <pre class="code">
     * Assert.isTrue(i &gt; 0, &quot;The value must be greater than zero&quot;);
     * </pre>
     *
     * @param expression a boolean expression
     * @param message    the exception message to use if the assertion fails
     * @throws IllegalArgumentException if expression is <code>false</code>
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert a boolean expression, throwing <code>IllegalArgumentException</code> if the test result is
     * <code>false</code>.
     * <p/>
     * <pre class="code">
     * Assert.isTrue(i &gt; 0);
     * </pre>
     *
     * @param expression a boolean expression
     * @throws IllegalArgumentException if expression is <code>false</code>
     */
    public static void isTrue(boolean expression) {
        isTrue(expression, "[Assertion failed] - this expression must be true");
    }

    /**
     * Assert that an object is <code>null</code> .
     * <p/>
     * <pre class="code">
     * Assert.isNull(value, &quot;The value must be null&quot;);
     * </pre>
     *
     * @param object  the object to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the object is not <code>null</code>
     */
    public static void isNull(Object object, String message) {
        if (object != null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that an object is <code>null</code> .
     * <p/>
     * <pre class="code">
     * Assert.isNull(value);
     * </pre>
     *
     * @param object the object to check
     * @throws IllegalArgumentException if the object is not <code>null</code>
     */
    public static void isNull(Object object) {
        isNull(object, "[Assertion failed] - the object argument must be null");
    }

    /**
     * Assert that an object is not <code>null</code> .
     * <p/>
     * <pre class="code">
     * Assert.notNull(clazz, &quot;The class must not be null&quot;);
     * </pre>
     *
     * @param object  the object to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the object is <code>null</code>
     */
    public static void notNull(Object object, String message, String... args) {
        if (object == null) {
            String decodedMessage = String.format(message, (Object[]) args);
            throw new IllegalArgumentException(decodedMessage);
        }
    }

    /**
     * Assert that an object is not <code>null</code> .
     * <p/>
     * <pre class="code">
     * Assert.notNull(clazz);
     * </pre>
     *
     * @param object the object to check
     * @throws IllegalArgumentException if the object is <code>null</code>
     */
    public static void notNull(Object object) {
        notNull(object, "[Assertion failed] - this argument is required; it must not be null");
    }

    /**
     * Assert that the given String is not empty; that is, it must not be <code>null</code> and not the empty String.
     * <p/>
     * <pre class="code">
     * Assert.hasLength(name, &quot;Name must not be empty&quot;);
     * </pre>
     *
     * @param text    the String to check
     * @param message the exception message to use if the assertion fails
     */
    public static void hasLength(String text, String message) {
        if (text == null || text.trim().length() == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that the given String is not empty; that is, it must not be <code>null</code> and not the empty String.
     * <p/>
     * <pre class="code">
     * Assert.hasLength(name);
     * </pre>
     *
     * @param text the String to check
     */
    public static void hasLength(String text) {
        hasLength(text, "[Assertion failed] - this String argument must have length; it must not be null or empty");
    }

    /**
     * Assert that the given Collection has at least one element
     */
    public static void hasElements(Collection<?> collection, String message) {
        Assert.isTrue(collection != null && collection.size() > 0, message);
    }

    /**
     * Assert that the given Collection has exactly one element
     */
    public static void exactlyOneElement(Collection<?> collection, String message) {
        Assert.isTrue(collection != null && collection.size() == 1, message);
    }

    /**
     * Assert that the given Collection has zero or one element
     */
    public static void atMostOneElement(Collection<?> collection, String message) {
        Assert.isTrue(collection != null && collection.size() <= 1, message);
    }

    /**
     * Assert that the given numbers are equal, allowing a deviation. Useful in cases where timestamps are compared
     */
    public static void equals(Long number1, Long number2, Long deviation, String message) {
        Boolean res = Math.abs(number1 - number2) <= deviation;
        Assert.isTrue(res, message);
    }

    /**
     * Assert that the given dates are equal, allowing a deviation. Useful in cases where timestamps are compared
     */
    public static void equals(LocalDateTime date1, LocalDateTime date2, Duration deviation, String message) {
        Duration duration = Duration.between(date1, date2);
        Assert.isTrue(Math.abs(duration.getSeconds()) < deviation.getSeconds(), message);
    }

    /**
     * Assert that two objects are equal
     */
    public static void equals(Object o1, Object o2, String message) {
        Assert.isTrue(o1 == o2 || o1 != null && o1.equals(o2), message);
    }

    /**
     * Assert that two objects are equal
     */
    public static void contains(List<?> list, Object obj, String message) {
        Assert.isTrue(obj != null && list != null && list.contains(obj), message);
    }

}
