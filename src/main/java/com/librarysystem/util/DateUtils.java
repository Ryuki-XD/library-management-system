package com.librarysystem.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for date operations and fine calculation.
 */
public final class DateUtils {

    /** Default loan period in days. */
    public static final int LOAN_PERIOD_DAYS = 14;

    /** Fine rate per overdue day in ₹. */
    public static final double FINE_PER_DAY = 2.0;

    /** Maximum books a student can borrow at once. */
    public static final int MAX_BOOKS_PER_STUDENT = 3;

    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final DateTimeFormatter SHORT_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private DateUtils() {
        // Utility class — prevent instantiation
    }

    /**
     * Calculates the due date from a given issue date.
     */
    public static LocalDate calculateDueDate(LocalDate issueDate) {
        return issueDate.plusDays(LOAN_PERIOD_DAYS);
    }

    /**
     * Calculates the fine amount for an overdue book.
     *
     * @param dueDate    the due date
     * @param returnDate the date the book was returned (or today if null)
     * @return fine amount, or 0.0 if not overdue
     */
    public static double calculateFine(LocalDate dueDate, LocalDate returnDate) {
        LocalDate effective = (returnDate != null) ? returnDate : LocalDate.now();
        long overdueDays = ChronoUnit.DAYS.between(dueDate, effective);
        return (overdueDays > 0) ? overdueDays * FINE_PER_DAY : 0.0;
    }

    /**
     * Checks if a book is overdue based on the due date.
     */
    public static boolean isOverdue(LocalDate dueDate) {
        return LocalDate.now().isAfter(dueDate);
    }

    /**
     * Formats a date for display (e.g., "12 Jul 2026").
     */
    public static String formatDisplay(LocalDate date) {
        return (date != null) ? date.format(DISPLAY_FORMAT) : "—";
    }

    /**
     * Formats a date in short form (e.g., "12/07/2026").
     */
    public static String formatShort(LocalDate date) {
        return (date != null) ? date.format(SHORT_FORMAT) : "—";
    }

    /**
     * Returns the number of overdue days (0 if not overdue).
     */
    public static long getOverdueDays(LocalDate dueDate) {
        long days = ChronoUnit.DAYS.between(dueDate, LocalDate.now());
        return Math.max(0, days);
    }
}
