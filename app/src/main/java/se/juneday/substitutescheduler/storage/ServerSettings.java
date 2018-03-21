package se.juneday.substitutescheduler.storage;


public class ServerSettings {

    private static String SERVER_PROTO = "http";
    private static String SERVER_HOST  = "10.0.2.2";
    private static String SERVER_PORT  = "8080";
    private static String SERVER_PATH  = "v1";

    private static String FORMAT = "format=json";

    private static String SERVER_PARAM_SUBSTITUTE = "substitute_id";
    private static String SERVER_PARAM_DAY        = "day";

    public static String serverUrl(String id, String date) {
        String idParam="";
        String dateParam="";

        if ( date!=null && (!date.equals("")) ) {
            dateParam = "&day=" + date;
        }

        if ( id!=null && (!id.equals("")) ) {
            idParam = "&substitute_id=" + id;
        }

        return SERVER_PROTO + "://" + SERVER_HOST + ":" + SERVER_PORT + "/" + SERVER_PATH
                + "?" + FORMAT
                + idParam
                + dateParam;
    }


}
