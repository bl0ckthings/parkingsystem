package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.concurrent.TimeUnit;

public class FareCalculatorService {


    TicketDAO ticketDAO = new TicketDAO();


    public double applyDiscount(double duration , double fareRate) {
        return (double) Math.round(((duration * fareRate) * (1 - 5 / 100.0)) * 100) / 100;
    }
    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }
        Instant inTime = ticket.getInTime().toInstant();
        Instant outTime = ticket.getOutTime().toInstant();

        Duration durationBtw = Duration.between(
                inTime,
                outTime
        );

        long durationMin = durationBtw.toMinutes();
        System.out.println(durationMin);
        double duration = (Math.floor((durationMin / 60.0) * 100) / 100);

        System.out.println(duration);
        if (duration < 0.5) {
            ticket.setPrice(Fare.FREE_PARKING);
            return;
        }

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                if (ticketDAO.isReccurentCustomer(ticket.getVehicleRegNumber())) {
                    ticket.setPrice(applyDiscount(duration, Fare.CAR_RATE_PER_HOUR));
                    System.out.println(applyDiscount(duration, Fare.CAR_RATE_PER_HOUR));
                } else {
                    ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                    System.out.println(duration * Fare.CAR_RATE_PER_HOUR);
                }

                break;
            }
            case BIKE: {
                if (ticketDAO.isReccurentCustomer(ticket.getVehicleRegNumber())) {
                    ticket.setPrice(applyDiscount(duration, Fare.BIKE_RATE_PER_HOUR));
                } else {
                    ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                }

                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}