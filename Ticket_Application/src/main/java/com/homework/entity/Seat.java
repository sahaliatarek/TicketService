package com.homework.entity;

import java.util.Date;

/**
 * Seat Entity
 * @author Tarek Sahalia
 *
 */
public class Seat {

	// Actual Seat Number (Location)
	private int seatNumber;

	// A Foreign Key from SeatHold Entity set when the seat is held.
	private int seatHoldId;

	private Date expiration;
	
	// flag set to true when the seat is reserved.
	private boolean isReserved;
	
	// Getters And Setters

	public Seat(int seatNumber) {
		super();
		this.seatNumber = seatNumber;
	}

	public int getSeatNumber() {
		return seatNumber;
	}

	public void setSeatNumber(int seatNumber) {
		this.seatNumber = seatNumber;
	}

	public int getSeatHoldId() {
		return seatHoldId;
	}

	public void setSeatHoldId(int seatHoldId) {
		this.seatHoldId = seatHoldId;
	}

	public Date getExpiration() {
		return expiration;
	}

	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}

	public boolean isReserved() {
		return isReserved;
	}

	public void setReserved(boolean isReserved) {
		this.isReserved = isReserved;
	}

}
