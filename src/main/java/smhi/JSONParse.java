package smhi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

import dao.SmhiParameters;
import dao.Stations;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * Example class for the SMHI metobs API. Uses org.json for JSON parsing.
 *
 */
public class JSONParse {

    // Url for the metobs API
    private String metObsAPI = "https://opendata-download-metobs.smhi.se/api";



    /**
     * Print all available parameters.
     *
     * @return The key for the last parameter.
     * @throws IOException
     * @throws JSONException
     */
    public String getParameters() throws IOException, JSONException {

        JSONObject parameterObject = readJsonFromUrl(metObsAPI + "/version/latest.json");
        JSONArray parametersArray = parameterObject.getJSONArray("resource");

        int prmLen = parametersArray.length();

        HashMap<Integer,String> prmListV = new HashMap<>(prmLen);

        for (int i = 0; i < parametersArray.length(); i++) {
            int v = Integer.parseInt(parametersArray.getJSONObject(i).getString("key"));
            String s = parametersArray.getJSONObject(i).getString("title");

            prmListV.put(v, s);

        }

        String parameterKey = null;
        for (int i = 0; i < parametersArray.length(); i++) {

            JSONObject parameter = parametersArray.getJSONObject(i);
            parameterKey = parameter.getString("key");
            String parameterName = parameter.getString("title");

            System.out.println("ParamKey: " + parameterKey + ": " + parameterName );
        }

        return parameterKey;
    }

    public List<SmhiParameters> getConfiguredParameters() throws IOException {



        JSONObject parameterObject = readJsonFromUrl(metObsAPI + "/version/latest.json");
        JSONArray parametersArray = parameterObject.getJSONArray("resource");

        int prmLen = parametersArray.length();

        List<SmhiParameters> configuredParams = new ArrayList<>(prmLen);


        for (int i = 0; i < parametersArray.length(); i++) {
            int v = Integer.parseInt(parametersArray.getJSONObject(i).getString("key"));
            String t = parametersArray.getJSONObject(i).getString("title");
            String s = parametersArray.getJSONObject(i).getString("summary");

            SmhiParameters smhiParameter = new SmhiParameters();
            smhiParameter.setKey(v);
            smhiParameter.setTitle(t);
            smhiParameter.setSummary(s);
            configuredParams.add(smhiParameter);

        }

        return configuredParams;
    }

    /**
     * Print all available stations for the given parameter. Return the id for the last station.
     *
     * @param parameterKey The key for the wanted parameter
     * @return The id for the last station
     * @throws IOException
     * @throws JSONException
     */
    public String getStationNames(String parameterKey) throws IOException, JSONException {

        JSONObject stationsObject = readJsonFromUrl(metObsAPI + "/version/latest/parameter/" + parameterKey + ".json");
        JSONArray stationsArray = stationsObject.getJSONArray("station");
        String stationId = null;
        for (int i = 0; i < stationsArray.length(); i++) {
            String stationName = stationsArray.getJSONObject(i).getString("name");
            stationId = stationsArray.getJSONObject(i).getString("key");
            System.out.println("StationId " + stationId + ": " + stationName);
        }

        return stationId;
    }

    public List<Stations> getStations(String parameterKey) throws  IOException, JSONException{
        JSONObject stationsObject = readJsonFromUrl(metObsAPI + "/version/latest/parameter/" + parameterKey + ".json");
        JSONArray stationsArray = stationsObject.getJSONArray("station");


        List<Stations> myStations = new ArrayList<>(stationsArray.length());

        for (int i = 0; i < stationsArray.length(); i++) {

            String stationName = stationsArray.getJSONObject(i).getString("name");
            Integer stationId = Integer.parseInt(stationsArray.getJSONObject(i).getString("key"));
            Boolean active = stationsArray.getJSONObject(i).getBoolean("active");
            int iActive = active == true ? 1 : 0;
            Double longitud = stationsArray.getJSONObject(i).getDouble("longitude");
            Double latitud = stationsArray.getJSONObject(i).getDouble("latitude");
            Double height = stationsArray.getJSONObject(i).getDouble("height");
            long from = stationsArray.getJSONObject(i).getLong("from");
            long to = stationsArray.getJSONObject(i).getLong("to");

            Stations aStation = new Stations();

            aStation.setStationId(stationId);
            aStation.setStationName(stationName);
            aStation.setHeight(height);
            aStation.setLatitud(latitud);
            aStation.setLongitud(longitud);
            aStation.setFromDateTime(from);
            aStation.setToDateTime(to);
            aStation.setActive(iActive);

            myStations.add(aStation);

        }
        return myStations;
    }

    /**
     * Print all available periods for the given parameter and station. Return the key for the last period.
     *
     * @param parameterKey The key for the wanted parameter
     * @param stationKey   The key for the wanted station
     * @return The name for the last period
     * @throws IOException
     * @throws JSONException
     */
    public String getPeriodNames(String parameterKey, String stationKey) throws IOException, JSONException {

        JSONObject periodsObject = readJsonFromUrl(metObsAPI + "/version/latest/parameter/" + parameterKey + "/station/" + stationKey + ".json");
        JSONArray periodsArray = periodsObject.getJSONArray("period");

        String periodName = null;
        for (int i = 0; i < periodsArray.length(); i++) {
            periodName = periodsArray.getJSONObject(i).getString("key");
            System.out.println(periodName);
        }

        return periodName;
    }


    /**
     * Get the data for the given parameter, station and period.
     *
     * @param parameterKey The key for the wanted parameter
     * @param stationKey   The key for the wanted station
     * @param periodName   The name for the wanted period
     * @return The data
     * @throws IOException
     * @throws JSONException
     */
    public String getData(String parameterKey, String stationKey, String periodName)  {
        try{
            return readStringFromUrl(metObsAPI + "/version/latest/parameter/" + parameterKey + "/station/" + stationKey + "/period/" + periodName + "/data.csv");
        } catch(IOException e){
            System.out.println(e);
        } catch(Exception e){
            System.out.println(e);
        }

        return null;

    }


    private JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        String text = readStringFromUrl(url);
        return new JSONObject(text);
    }


    private String readStringFromUrl(String url) throws IOException {

        InputStream inputStream = new URL(url).openStream();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            StringBuilder stringBuilder = new StringBuilder();
            int cp;
            while ((cp = reader.read()) != -1) {
                stringBuilder.append((char) cp);
            }
            return stringBuilder.toString();
        } finally {
            inputStream.close();
        }
    }
}
