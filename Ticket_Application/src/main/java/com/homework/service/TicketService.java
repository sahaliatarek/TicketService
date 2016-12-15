package com.homework.service;

import java.util.List;
import java.util.Map;

import com.homework.entity.Seat;
import com.homework.entity.SeatHold;

public interface TicketService {
	/**
	 * The number of seats in the venue that are neither held nor reserved
	 *
	 * @return the number of tickets available in the venue
	 */
	int numSeatsAvailable();

	/**
	 * Find and hold the best available seats for a customer
	 *
	 * @param numSeats
	 *            the number of seats to find and hold
	 * @param customerEmail
	 *            unique identifier for the customer
	 * @return a SeatHold object identifying the specific seats and related
	 *         information
	 */
	SeatHold findAndHoldSeats(int numSeats, String customerEmail);

	/**
	 * Commit seats held for a specific customer
	 *
	 * @param seatHoldId
	 *            the seat hold identifier
	 * @param customerEmail
	 *            the email address of the customer to which the seat hold is
	 *            assigned
	 * @return a reservation confirmation code
	 */
	String reserveSeats(int seatHoldId, String customerEmail);

	/**
	 * The below method is a simple initializer for all Application Resources.
	 * it could be written directly into the service constructor, but we will need
	 * it to accomplish the Tests.
	 */
	public void initializeOrRefreshApps();
	
	/**
	 * Need this getter to complete the multi-threading Test
	 * @return
	 */
	public Map<Integer, SeatHold> getReservedSeatHoldMap();
	

	/**
	 * Need this getter to complete the multi-threading Test
	 * @return
	 */
	public List<Seat> getSeats();
}