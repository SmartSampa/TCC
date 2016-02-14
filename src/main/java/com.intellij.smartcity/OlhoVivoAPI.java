package com.intellij.smartcity;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by ruan0408 on 10/02/2016.
 */
public class OlhoVivoAPI {

    private static final String BASE_URL = "http://api.olhovivo.sptrans.com.br/v0/";
//    private static Gson jsonParser = new GsonBuilder().serializeNulls().create();
    private static ObjectMapper jsonParser = new ObjectMapper();
    private String authKey;
    private HttpUrlConnector httpConnector;

    public OlhoVivoAPI(String key) {
        authKey = key;
        httpConnector = new HttpUrlConnector();
    }

    public boolean authenticate() {

        String url = BASE_URL +"Login/Autenticar?token="+authKey;
        String response = httpConnector.executePostWithoutForm(url);

        if (response.equalsIgnoreCase("true")) return true;
        return false;
    }

    public BusLine[] searchBusLines(String termosBusca) {
        String url = BASE_URL +"/Linha/Buscar?termosBusca="+termosBusca;
        String jsonResponse = httpConnector.executeGet(url);

        return jsonToObject(jsonResponse, BusLine[].class);
    }

    public String getBusLineDetails(int busLineCode) {
        String url = BASE_URL +"/Linha/CarregarDetalhes?codigoLinha="+busLineCode;
        String jsonResponse = httpConnector.executeGet(url);
        return jsonResponse;
    }
    //TODO getBusLineDetails
    //TODO write methods for all classes

    public BusStop[] searchBusStops(String searchTerms) {
        String url = BASE_URL +"/Parada/Buscar?termosBusca="+searchTerms;
        String jsonResponse = httpConnector.executeGet(url);
        return jsonToObject(jsonResponse, BusStop[].class);
//        return jsonParser.fromJson(jsonResponse, BusStop[].class);
    }

    public BusStop[] searchBusStopsByLine(int busLineCode) {
        String url = BASE_URL +"/Parada/BuscarParadasPorLinha?codigoLinha="+busLineCode;
        String jsonResponse = httpConnector.executeGet(url);
        return jsonToObject(jsonResponse, BusStop[].class);
//        return jsonParser.fromJson(jsonResponse, BusStop[].class);
    }

    public BusStop[] searchBusStopsByCorridor(int busCorridorCode) {
        String url = BASE_URL + "/Parada/BuscarParadasPorCorredor?codigoCorredor="+busCorridorCode;
        String jsonResponse = httpConnector.executeGet(url);
        return jsonToObject(jsonResponse, BusStop[].class);
//        return jsonParser.fromJson(jsonResponse, BusStop[].class);
    }

    public BusCorridor[] getAllBusCorridors() {
        String url = BASE_URL + "/Corredor";
        String jsonResponse = httpConnector.executeGet(url);
        return jsonToObject(jsonResponse, BusCorridor[].class);
//        return jsonParser.fromJson(jsonResponse, BusCorridor[].class);
    }

    public BusLinePositions searchBusPositionsByLine(int busLineCode) {
        String url = BASE_URL + "/Posicao?codigoLinha="+busLineCode;
        String jsonResponse = httpConnector.executeGet(url);
        return jsonToObject(jsonResponse, BusLinePositions.class);
//        return jsonParser.fromJson(jsonResponse, BusLinePositions.class);
    }

    public ForecastWithStopAndLine getForecastWithStopAndLine(int busStopCode, int busLineCode) {
        String url = BASE_URL + "/Previsao?codigoParada="+busStopCode+"&codigoLinha="+busLineCode;
        String jsonResponse = httpConnector.executeGet(url);
        return jsonToObject(jsonResponse, ForecastWithStopAndLine.class);
//        return jsonParser.fromJson(jsonResponse, ForecastWithStopAndLine.class);
    }

    public ForecastWithLine getForecastWithLine(int busLineCode) {
        String url = BASE_URL + "/Previsao/Linha?codigoLinha="+busLineCode;
        String jsonResponse = httpConnector.executeGet(url);
        return jsonToObject(jsonResponse, ForecastWithLine.class);
//        return jsonParser.fromJson(jsonResponse, ForecastWithLine.class);
    }

    public ForecastWithStop getForecastWithStop(int busStopCode) {
        String url = BASE_URL + "/Previsao/Parada?codigoParada="+busStopCode;
        String jsonResponse = httpConnector.executeGet(url);
        return jsonToObject(jsonResponse, ForecastWithStop.class);
//        return jsonParser.fromJson(jsonResponse, ForecastWithStop.class);
    }

    private <T> T jsonToObject(String jsonResponse, Class<T> tClass) {
        try {
            return jsonParser.readValue(jsonResponse, tClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
