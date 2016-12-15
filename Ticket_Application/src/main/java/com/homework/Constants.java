package com.homework;

/**
 * 
 * Final Class, Holds all the constants of the application.
 * Best Practice: Most of the constants present in this class should be in an external 
 * properties file in real application.
 * 
 * 
 * @author Tarek Sahalia
 *
 */
public final class Constants {
	// Maximum number in the Venue
	public static final int VENUE_SEAT_NUMBER = 100;

	// Time for SeatHold to expire
	public static final long TIME_OF_SEAT_HOLD_VALIDITY = 10;

	// Confirmation Prefix
	public static final String CONFIRMATION_PREFIX = "WTTS";

	public static final String RESERVATION_EXPIRED = "expired";


	public static final String VALIDATION_MESSAGE = "EMAIL OR NUMBER INVALID";

	// email pattern to validate email parameter
	public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	// Positive numbers to validate seat numbers
	public static final String SEAT_NUMBER__PATTERN = "[0-9]+";

}
