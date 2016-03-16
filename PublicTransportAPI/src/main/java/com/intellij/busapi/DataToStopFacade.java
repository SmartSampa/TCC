package com.intellij.busapi;

import com.intellij.olhovivoapi.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.intellij.busapi.BusAPIManager.gtfs;
import static com.intellij.busapi.BusAPIManager.olhovivo;

/**
 * Created by ruan0408 on 13/03/2016.
 */
public class DataToStopFacade {

    protected static List<Stop> searchForStopsByTerm(String term) {
        BusStop[] busStops = olhovivo.searchBusStops(term);
        return DataToAPIConverter.busStopArrayToStops(busStops);
    }

    public List<Trip> getAllTrips(int stopId) {
        List<org.onebusaway.gtfs.model.Trip> allTrips = gtfs.getAllTrips(stopId);
        return DataToAPIConverter.gtfsTripToTrips(allTrips);
    }

    public Map<Trip, List<PredictedBus>> getAllPredictions(int stopId) {
        ForecastWithStop forecast = olhovivo.getForecastWithStop(stopId);
        BusLineNow[] busLineNowArray = forecast.getBusLines();

        Map<Trip, List<PredictedBus>> map = new HashMap<>(busLineNowArray.length);

        for (int i = 0; i < busLineNowArray.length; i++) {
            BusLineNow lineNow = busLineNowArray[i];
            map.put(DataToAPIConverter.busLineToTrip(lineNow.getBusLine()),
                    DataToAPIConverter.busNowArrayToPredictedBuses(lineNow.getVehicles()));
        }
        return map;
    }

    public List<PredictedBus> getPredictedBusesOfTrip(int stopId, int olhovivoTripId) {
        ForecastWithStopAndLine forecast = olhovivo
                .getForecastWithStopAndLine(stopId, olhovivoTripId);

        BusNow[] buses = forecast.getBuses();
        return DataToAPIConverter.busNowArrayToPredictedBuses(buses);
    }
}