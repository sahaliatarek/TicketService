package com.homework.controller;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.homework.Constants;
import com.homework.entity.SeatHold;
import com.homework.service.TicketService;

@Controller
public class TicketController {
	private TicketService ticketService;
	private Pattern emailPattern;
	private Pattern seatNumberPattern;
	private Matcher seatNumbermatcher;
	private Matcher emailmatcher;

	@Autowired
	public TicketController(TicketService ticketService) {
		super();
		this.ticketService = ticketService;
	}

	/**
	 * index url for the application
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/")
	public String index(HttpServletRequest request) {
		// invalidating the session to put back all session variables to their
		// initial status
		request.getSession().invalidate();
		return "index";
	}
	
/**
 * 1- Validate the form prameters
 * 2- calls findAndHoldSeats service.
 * 3- return reservation page if seatHold, otherwise return the seatHold seats locations 
 * 
 * @param email
 * @param number
 * @param request
 * @return
 */
	@RequestMapping(value = "hold/{email}/{number}", method = RequestMethod.POST)
	public String holdSeats(@PathVariable("email") String email, @PathVariable("number") int number,
			HttpServletRequest request) {

		// The below lines are to validate the form from front_end. best
		// practice is to put this validation in separate location. However,
		// because having just 2 simple primitives to validate, putting them
		// here will not clutter the code much.
		emailPattern = Pattern.compile(Constants.EMAIL_PATTERN);
		emailmatcher = emailPattern.matcher(email);
		seatNumberPattern = Pattern.compile(Constants.SEAT_NUMBER__PATTERN);
		seatNumbermatcher = seatNumberPattern.matcher(String.valueOf(number));
		if (!emailmatcher.matches() || !seatNumbermatcher.matches() || number == 0) {
			request.getSession().setAttribute("NotValid", Constants.VALIDATION_MESSAGE);
			return "index";

		}

		// adding the new created SeatHold to the session, so the customer can
		// check the physical locations of seats and confirm the reservation. if
		// it was a Rest API. it will simply send back the resource SeatHold
		// (JSON, XML)

		SeatHold seatHold = ticketService.findAndHoldSeats(number, email);
		if (seatHold == null)
			return "noseatsavailble";
		request.getSession().setAttribute("seatLocations", Arrays.toString(seatHold.getSeatsLocations()));
		request.getSession().setAttribute("expirationTime", Constants.TIME_OF_SEAT_HOLD_VALIDITY);
		// the below line of code, save the attribute of the form action, when
		// the customer hit reserve button, the application will send the
		// request to this formAction link. this gives some abstraction to
		// the API. the client side does not care about the next step which is
		// reservation, since the next step will be provided by the API from the
		// server side, when
		// the client reaches to that step. If this was a restful API, this
		// approach will be implemented using HATEOAS (Hypermedia as the Engine
		// of Application State).

		request.getSession().setAttribute("formAction", "/reserve/" + email + "/" + seatHold.getSeatHoldId());

		return "reservation";
	}

	/**
	 * Based on  reserveSeats service. it returns expired page or the confirmation number.
	 * @param email
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "reserve/{email}/{id}", method = RequestMethod.POST)
	public String reserveSeats(@PathVariable("email") String email, @PathVariable("id") int id,
			HttpServletRequest request) {
		String confirmation = ticketService.reserveSeats(id, email);
		if (StringUtils.equals(Constants.RESERVATION_EXPIRED, confirmation))
			return "expired";
		request.getSession().setAttribute("confirmationNmber", confirmation);
		return "confirmation";

	}

}
