package com.intellij.publictransportapi.implementation;

import org.apache.commons.lang.text.StrBuilder;
import org.onebusaway.gtfs.model.Frequency;
import org.onebusaway.gtfs.model.ShapePoint;
import org.onebusaway.gtfs.model.StopTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;

/**
 * Created by ruan0408 on 17/02/2016.
 */
public class Trip {

    private int internalId;
    private String shapeId = null;
    private Route route;
    private String destinationSign;

    //TODO we shouldnt have to set internalId
    public void setInternalId(int internalId) {this.internalId = internalId;}

    public void setDestinationSign(String destinationSign) {this.destinationSign = destinationSign;}

    public void setRoute(Route route) { this.route = route;}

    public int getInternalId() {return internalId;}

    public String getGtfsId() {
        if (route.getTripMTST().equals(this) && !route.isCircular())
            return route.fullNumberSign()+"-1";

        return route.fullNumberSign()+"-0";
    }

    public String getShapeId() {
        if (shapeId == null) {
            Predicate<org.onebusaway.gtfs.model.Trip> predicate;
            predicate = trip -> trip.getId().getId().equals(getGtfsId());

            org.onebusaway.gtfs.model.Trip trip =
                    API.filterGtfsToElement("getAllTrips", predicate);

            shapeId = trip.getShapeId().getId();
        }
        return shapeId;
    }

    public Route getRoute() {return route;}

    public String getDestinationSign() { return destinationSign;}

    public String getWorkingDays() {
        Predicate<org.onebusaway.gtfs.model.Trip> predicate;
        predicate = t -> t.getId().getId().equals(getGtfsId());

        org.onebusaway.gtfs.model.Trip trip =
                API.filterGtfsToElement("getAllTrips", predicate);

        return trip.getServiceId().getId();
    }

    public String getDetails() {
        return API.getTripDetails(internalId);
    }

    public Shape getShape() {
        Predicate<ShapePoint> predicate;
        predicate = point -> point.getShapeId().getId().equals(getShapeId());

        List<ShapePoint>shapes = API.filterGtfsToList("getAllShapePoints", predicate);
        Comparator<ShapePoint> comp = (p1, p2) -> {
            if (p1.getSequence() < p2.getSequence()) return -1;
            if (p1.getSequence() > p2.getSequence()) return 1;
            return 0;
        };
        shapes.sort(comp);
        Point[] points = new Point[shapes.size()];
        double[] distances = new double[shapes.size()];

        for (int i = 0; i < shapes.size(); i++) {
            ShapePoint p = shapes.get(i);
            points[i] = new Point(p.getLat(), p.getLon());
            distances[i] = p.getDistTraveled();
        }

        return new Shape(points, distances);
    }

//    public List<Stop> getAllStops() {
//        BusStop[] busStops = API.getBusStopsByTrip(internalId);
//        System.out.println(busStops.length);
//        List<Stop> stops = new ArrayList<>(busStops.length);
//
//        for (int i = 0; i < busStops.length; i++) {
//            BusStop s = busStops[i];
//            Point coord = new Point(s.getLatitude(), s.getLongitude());
//            stops.add(new Stop(s.getCode(), s.getName(), s.getAddress(), coord));
//        }
//        return stops;
//    }

    public List<Stop> getAllStops() {
        Predicate<StopTime> predicate;
        predicate = s -> s.getTrip().getId().getId().equals(getGtfsId());

        List<StopTime> filtered =
                API.filterGtfsToList("getAllStopTimes", predicate);

        Comparator<StopTime> comp = (s1, s2) -> {
            if (s1.getStopSequence() < s2.getStopSequence()) return -1;
            if (s1.getStopSequence() > s2.getStopSequence()) return 1;
            return 0;
        };

        filtered.sort(comp);

        List<org.onebusaway.gtfs.model.Stop> filtered2 =
                new ArrayList<>(filtered.size());

        Predicate<org.onebusaway.gtfs.model.Stop> predicate2;
        for (StopTime stop : filtered) {
            predicate2 = s -> s.getId().getId().equals(stop.getStop().getId().getId());
            filtered2.add(API.filterGtfsToElement("getAllStops", predicate2));
        }

        List<Stop> finalList = new ArrayList<>(filtered2.size());
        for (org.onebusaway.gtfs.model.Stop s: filtered2) {
            int id = Integer.parseInt(s.getId().getId());
            Point location = new Point(s.getLat(), s.getLon());
            //TODO use olhovivo to try and fill address
            finalList.add(new Stop(id, s.getName(),"", location));
        }
        return finalList;
    }

    public List<Bus> getAllBuses() {
        return null;
    }

    public List<PredictedBus> getPredictedBuses(Stop stop) {
        return null;
    }

    public Map<Stop, List<PredictedBus>> getAllPredictions() {
        return null;
    }

    public int getDepartureInterval(String hhmm) {
        try {
            Date date = new SimpleDateFormat("HH:mm").parse(hhmm);
            Date date0 = new SimpleDateFormat("HH:mm").parse("00:00");
            long time = (date.getTime() - date0.getTime())/1000;

            return getDepartureInterval(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getDepartureIntervalNow() {

        String time = new SimpleDateFormat("HH:mm").format(new Date());
        return getDepartureInterval(time);
    }

    private int getDepartureInterval(long time) {
        Predicate<Frequency> predicate;
        predicate = f -> f.getTrip().getId().getId().equals(getGtfsId()) &&
                (f.getStartTime() <= time) && (time < f.getEndTime());

        Frequency f = API.filterGtfsToElement("getAllFrequencies", predicate);
        return f.getHeadwaySecs();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Trip)) return false;

        Trip that = (Trip) obj;
        if (this.internalId != that.internalId) return false;

        return true;
    }

    @Override
    public String toString() {
        StrBuilder builder = new StrBuilder();
        builder.append(route.basicToString());
        builder.appendln("internalId: "+ internalId);
        builder.appendln("destinationSign: "+destinationSign);

        return builder.toString();
    }
}