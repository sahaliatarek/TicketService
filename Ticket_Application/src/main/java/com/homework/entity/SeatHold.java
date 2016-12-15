package com.homework.entity;

import java.util.Arrays;
import java.util.Date;

/**
 * SeatHold Entity
 * 
 * @author Tarek Sahalia
 *
 */
public class SeatHold {

	// Primary Key, will be used to hold and reserve seats
	private int seatHoldId;

	// the Actual seats locations held or reserved by the present SeatHold
	private Integer[] seatsLocations;
	private String reservationConfirmationCode;
	private String customerEmail;
	private Date expiration;

	public SeatHold(int seatHoldId, String customerEmail, Date expiration, Integer[] seatsLocations) {
		super();
		this.seatHoldId = seatHoldId;
		this.customerEmail = customerEmail;
		this.expiration = expiration;
		this.seatsLocations = seatsLocations;
	}
	// Getters And Setters

	public Integer[] getSeatsLocations() {
		return seatsLocations;
	}

	public int getSeatHoldId() {
		return seatHoldId;
	}

	public void setSeatHoldId(int seatHoldId) {
		this.seatHoldId = seatHoldId;
	}

	public void setSeatsLocations(Integer[] seatsLocations) {
		this.seatsLocations = seatsLocations;
	}

	public String getReservationConfirmationCode() {
		return reservationConfirmationCode;
	}

	public void setReservationConfirmationCode(String reservationConfirmationCode) {
		this.reservationConfirmationCode = reservationConfirmationCode;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public Date getExpiration() {
		return expiration;
	}

	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}

	@Override
	public String toString() {
		return getCustomerEmail() + " " + getSeatHoldId() + " " + getReservationConfirmationCode() + "\n"
				+ Arrays.toString(getSeatsLocations());
	}
}
