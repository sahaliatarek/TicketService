package com.homework.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.homework.Constants;
import com.homework.entity.Seat;
import com.homework.entity.SeatHold;

/**
 * TicketService Implementation.
 * 
 * @author Tarek Sahalia
 *
 */
@Service
public class TicketServiceImpl implements TicketService {

	private List<Seat> seats;
	private Map<Integer, SeatHold> seatHoldMap;
	private Map<Integer, SeatHold> reservedSeatHoldMap;
	private int numberSeatsAvailable;
	private int keyId;

	public TicketServiceImpl() {
		initializeOrRefreshApps();
	}

	/**
	 * return the shared available seats number across the whole application
	 */
	@Override
	public int numSeatsAvailable() {
		return numberSeatsAvailable;
	}

	/**
	 * add Or remove SeatHold seats numbers from numberSeatsAvailable. when
	 * SeatHold S is Expired, operation parameter is passed as false, and the
	 * method will add the number of seats of S to numberSeatsAvailable. on the
	 * other hand, when SeatHold S is held, operation parameter is passed as
	 * true, and the method will subtract the number of seats of S from
	 * numberSeatsAvailable
	 * 
	 * No need to declare the below method as synchronized, since it will be
	 * called always within a synchronized block.
	 * 
	 * @param numSeats
	 * @param operation
	 * @return
	 */
	private int setNumberSeatsAvailable(int numSeats, boolean operation) {
		return operation ? (numberSeatsAvailable += numSeats) : (numberSeatsAvailable -= numSeats);
	}

	/**
	 * AS Assumption; because there is no option for the customer to communicate
	 * his/her seats location preference when holding them, the application will
	 * communicate back to costumer the physical location of seats. so he/she
	 * can decide to reserve them.
	 * 
	 * The best available seats in this application for a customer are the
	 * closest to the stage starting from the right side (of the stage).
	 * 
	 * The algorithm for holding and reserving seats is FCFS (First Comes First
	 * Served).
	 * 
	 * 
	 */
	@Override
	public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {
		if (seatsAvailabilityCheck(numSeats)) {
			int id = keyGenerator();
			return holdTheBestAvailableSeats(numSeats, customerEmail,
					new SeatHold(id, customerEmail, new Date(), new Integer[numSeats]));
		}
		return null;
	}

	/**
	 * A simple incremental SeatHold id generator. Making this method
	 * synchronized against concurrent threads to assure the uniqueness of the
	 * id
	 * 
	 * @return
	 */
	private synchronized int keyGenerator() {
		return ++keyId;
	}

	/**
	 * Checking (Searching) The Availability of seats is in the heart of this
	 * application because the Application has a high-demand performance nature
	 * by definition. Therefore, the goal will be hoding the best available
	 * seats as soon as possible.
	 * 
	 * The Approach is to make this method as fast as possible, with of course
	 * keeping all the requirements valid.
	 * 
	 * Note that this method does not care about seats preference. it return as
	 * soon as the number of available seats reach or exceeds the number of
	 * seats required. As for the best availability, it will be calculated when
	 * physically the seats are held.
	 * 
	 * Also, note that this method is synchronized, so it is thread-safe against
	 * multi-processes which wants to access it concurrently. this is to Assure
	 * the logic of that the thread which comes first will hold the seats first.
	 * 
	 * @param numSeats
	 * @return
	 */
	private synchronized boolean seatsAvailabilityCheck(int numSeats) {
		try {
			// if there are seats available, return immediately.
			if (!(numSeats <= numSeatsAvailable()))
				for (int key : seatHoldMap.keySet()) {
					if (!(numSeats <= numSeatsAvailable())) {
						SeatHold seatHoldToCheck = seatHoldMap.get(key);
						if (isExpired(seatHoldToCheck.getExpiration())) {
							setNumberSeatsAvailable(seatHoldToCheck.getSeatsLocations().length, true);
							seatHoldMap.remove(key);
						}

					} else
						// if the current SeatHold is expired and its
						// number+umberSeatsAvailable >= numSeats required,
						// return immediately.
						return true;
				}
			// calculate the last SeatHold Node in the Map if it is expired.
			// return true if its number+umberSeatsAvailable >= numSeats
			return (numSeats <= numSeatsAvailable());
		} finally {
			// This block will run always.
			if (numSeats <= numSeatsAvailable())
				setNumberSeatsAvailable(numSeats, false);
		}
	}

	/**
	 * 
	 * Private Method hold physical seats and return the seatHold Object with
	 * its seat list locations. The Method below could be Asynchronous to make
	 * the program faster, so as soon as the application knows the number of
	 * Seats to Hold is available, it fires directly the acknowledgement to the
	 * client. But to give the Hold Operation More sense, it waits for the
	 * actual seat locations to show them to the customer in the front_end.
	 * 
	 * By definition, iterating the list of seats is the most expensive (Time)
	 * process in the application. That's why, there is no access to this list
	 * in the whole application but in this method.
	 * 
	 * Assumption: the application runs in a single Mono_Processor Server. if
	 * the application will run in a machine where it has more than one
	 * processor which share the same resources and run the application in
	 * shared parallel time between them, Or, if the application will run behind
	 * a load balancer in more than one server, then we must think about the
	 * thread safe and synchronization of the below method.
	 * 
	 * @param numSeats
	 * @param customerEmail
	 * @param seatHold
	 * @return
	 */
	private SeatHold holdTheBestAvailableSeats(int numSeats, String customerEmail, SeatHold seatHold) {
		seatHoldMap.put(seatHold.getSeatHoldId(), seatHold);

		// Counter used to add seat location into seatHold seat list
		int i = 0;
		// Note that this loop starts always from the very beginning of the
		// venue
		// Matrix until the number of seats required is held. This approach is
		// to make sure that the customer is getting the best
		// available seats at that frame of time (closest to the stage from
		// right to left). like that we eliminate the problematic of looking for
		// the best seat every time for every request. And we will focus more
		// on just the availability of seats.
		for (Seat seat : seats) {

			if (seat.getSeatHoldId() != 0) {
				if (!seat.isReserved()) {// if the seat is already reserved we
											// pass to the next best available
											// seat
					SeatHold seatToHold = reservedSeatHoldMap.get(seat.getSeatHoldId());
					if (seatToHold != null) {
						// if the seatHold is reserved, it marks the seat as
						// reserved as well, so next time,it will not access
						// reservedSeatHoldMap again.
						seat.setReserved(true);
					} else {
						if (isExpired(seat.getExpiration())) {
							seat.setSeatHoldId(seatHold.getSeatHoldId());
							seat.setExpiration(seatHold.getExpiration());
							seatHold.getSeatsLocations()[i] = seat.getSeatNumber();
							i++;
						}
					}
				}
			} else {// if the seat has never been held before, we hold it
					// immediately
				seat.setSeatHoldId(seatHold.getSeatHoldId());
				seat.setExpiration(seatHold.getExpiration());
				seatHold.getSeatsLocations()[i] = seat.getSeatNumber();
				i++;
			}

			// if the number of seats held is equal to the number of seats
			// wanted, then break from the loop and return the seatHold Object
			if (numSeats == i)
				break;
		}
		return seatHold;
	}

	/**
	 * In the below line of code, there is no checking of null value of
	 * expiration, looks like there is a risk of NullPointerException, however
	 * it will never been thrown because if expiration is null that means this
	 * seat was never held before, and the application will never reach to this
	 * method in this case.
	 */

	private boolean isExpired(Date expiration) {
		return !(((new Date().getTime() - expiration.getTime()) / 1000) < Constants.TIME_OF_SEAT_HOLD_VALIDITY);
	}

	/**
	 * reserveSeats is called when the customer reserve the held seats and it
	 * proceeds correctly when that seatHold is not expires. it generates a
	 * confirmation code for the customer, removes the seatHold from the
	 * temporary map, and adds it in the reserved map of SeatHold. Note: if the
	 * application was using a database to store its resources, these operations
	 * must be wrapped in transactional operation, so if one of them fails, it
	 * rolls back everything. Note Also that this method is surrounding the code
	 * within try ... catch block. This is to avoid a NullPointerException which
	 * might occurs in the case when a Customer tries to reserve an expired
	 * seatHold which is already reserved by another customer. this case is
	 * described in details in TicketApplicationTests.
	 * 
	 */

	@Override
	public String reserveSeats(int seatHoldId, String customerEmail) {
		try {
			String confirmation = Constants.CONFIRMATION_PREFIX + seatHoldId;
			SeatHold reservedSeatHold = seatHoldMap.get(seatHoldId);
			if (isExpired(reservedSeatHold.getExpiration()))
				return Constants.RESERVATION_EXPIRED;
			reservedSeatHold.setReservationConfirmationCode(confirmation);
			seatHoldMap.remove(seatHoldId, reservedSeatHold);
			reservedSeatHoldMap.put(seatHoldId, reservedSeatHold);
			return confirmation;
		} catch (NullPointerException e) {
			System.out.println("NullPointerException was thrown and catched.");
			return Constants.RESERVATION_EXPIRED;

		}
	}

	/**
	 * The below method is a simple initializer for all Application Resources.
	 * it could be written directly into the service constructor, but we will
	 * need it to accomplish the Tests.
	 */
	public void initializeOrRefreshApps() {
		seatHoldMap = new ConcurrentHashMap<>();
		reservedSeatHoldMap = new HashMap<>();
		numberSeatsAvailable = Constants.VENUE_SEAT_NUMBER;
		keyId = 0;
		seats = new ArrayList<>();
		for (int i = 0; i < Constants.VENUE_SEAT_NUMBER; i++) {
			// Note that it adds 1 to the seat location because the value of 0
			// has the meaning that the seat has never been held before. (look
			// at line 187 in holdTheBestAvailableSeats)
			seats.add(new Seat(i + 1));
		}

	}
}
