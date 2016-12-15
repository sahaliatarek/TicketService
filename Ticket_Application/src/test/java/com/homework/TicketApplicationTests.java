package com.homework;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.homework.entity.SeatHold;
import com.homework.service.TicketService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TicketApplicationTests {

	@Autowired
	private TicketService ticketService;
	private String customerEmail = "customer@WTTS.com";

	@BeforeClass
	public static void printEstimatedTestTime() {
		System.out.println("Tests will take approximately 40 seconds to complete, please wait ...");
	}

	/**
	 * Refresh The Application the Autonomy of tests. That's because
	 * ticketService is a shared singleton object in spring context, and we want
	 * to run each test on a Reinitialized application. As for multi-threading,
	 * it will be implemented within the tests themselves.
	 */

	@Before
	public void beforeEachTest() {
		ticketService.initializeOrRefreshApps();
	}

	/**
	 * notify when each test is starting.
	 */
	@Rule
	public TestRule testWatcher = new TestWatcher() {
		protected void starting(Description description) {
			System.out.println("Starting test: " + description.getMethodName());
		}
	};

	/**
	 * Test if the numSeats wanted is greater than number of seats available,
	 * findAndHoldSeats returns null.
	 */
	@Test
	public void holdNumberOfSeatsUnavailable() {
		assertThat(ticketService.findAndHoldSeats(Constants.VENUE_SEAT_NUMBER + 1, customerEmail)).isEqualTo(null);
	}

	/**
	 * Test if findAndHoldSeats is holding the exact number of seats required.
	 */
	@Test
	public void isNumberWantedHeld() {
		int numSeats = 2;
		assertThat(ticketService.findAndHoldSeats(numSeats, customerEmail).getSeatsLocations().length)
				.isEqualTo(numSeats);
	}

	/**
	 * Hold, Reserve, and get the confirmation Number expected, which is WTTS1.
	 * where 1 is the SeatHoldId.
	 */
	@Test
	public void HoldReserveAndGetExpectedConfirmationCode() {
		int numSeats = 2;
		SeatHold seatHold = ticketService.findAndHoldSeats(numSeats, customerEmail);
		assertThat(ticketService.reserveSeats(seatHold.getSeatHoldId(), customerEmail)).isEqualTo("WTTS1");
	}

	/**
	 * Test if findAndHoldSeats is holding the best available seats.
	 */
	@Test
	public void holdBestAvailableSeats() {
		int numSeats = 2;
		Assert.assertArrayEquals(new Integer[] { 1, 2 },
				ticketService.findAndHoldSeats(numSeats, customerEmail).getSeatsLocations());
	}

	/**
	 * Check if after the expiration of a seatHold, its reservation will return
	 * expired.
	 */
	@Test
	public void reserveExpiredSeatHold() {
		int numSeats = 1;
		try {
			SeatHold seatHold = ticketService.findAndHoldSeats(numSeats, customerEmail);
			Thread.sleep(Constants.TIME_OF_SEAT_HOLD_VALIDITY * 1000 + 1);
			assertThat(ticketService.reserveSeats(seatHold.getSeatHoldId(), customerEmail))
					.isEqualTo(Constants.RESERVATION_EXPIRED);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * The below test is to Assure that the system is holding the best available
	 * seats, and its taking seat locations from SeatHold objects, if those
	 * seatHolds are expired.
	 * 
	 * 1- Hold 1 seat 2- let it expires. 3- hold another seat, 4- check if the
	 * second seatHold has the first seat held.
	 */
	@Test
	public void holdExpiredSeats() {
		int numSeats = 1;
		try {
			SeatHold seatHold = ticketService.findAndHoldSeats(numSeats, customerEmail);
			Thread.sleep(Constants.TIME_OF_SEAT_HOLD_VALIDITY * 1000 + 1);
			Assert.assertArrayEquals(seatHold.getSeatsLocations(),
					ticketService.findAndHoldSeats(numSeats, customerEmail).getSeatsLocations());

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	/**
	 * This Test is reserving an expired seatHold which its seats are reserved
	 * by another seatHold. This Test will throw a NullPointerException because
	 * when the expired SeatHold tries to finish the reservation, it will access
	 * seatHoldMap with its Id as key, however, it will throw a
	 * NullPointerException, because that key does not exist anymore because the
	 * second seatHold removes it when it reserves it.
	 * 
	 * Note That this exception will not occur if there is more available number
	 * of seats returned by the function numSeatsAvailable(). we can make sure
	 * if we run the same test with numSeat = 1 that NullPointerException is not
	 * thrown because the application is holding the seatHold without even
	 * iterating the seatHoldMap. As for the preference, it will be calculated
	 * automatically while holding the physical seats. 1- Hold seatHold1 2-let
	 * it expires 3-hold seatHold2 4- reserve SeatHold2 5- check that reserve
	 * seatHold1 is returning expired.
	 */
	@Test
	public void reserveExpiredSeatHoldAfterItsSeatsAreReserved() {
		int numSeats = Constants.VENUE_SEAT_NUMBER; // throws
													// NullPointerException
		// int numSeats = 1 // does not throw NullPointerException
		try {
			SeatHold seatHold1 = ticketService.findAndHoldSeats(numSeats, customerEmail);
			Thread.sleep(Constants.TIME_OF_SEAT_HOLD_VALIDITY * 1000 + 1);
			SeatHold seatHold2 = ticketService.findAndHoldSeats(numSeats, customerEmail);
			ticketService.reserveSeats(seatHold2.getSeatHoldId(), customerEmail);
			assertThat(ticketService.reserveSeats(seatHold1.getSeatHoldId(), customerEmail))
					.isEqualTo(Constants.RESERVATION_EXPIRED);

		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}

	}
}
