package se.juneday.substitutescheduler.storage;


public class ServerSettings {

    private static String SERVER_PROTO = "http";
    private static String SERVER_HOST = "10.0.2.2";
    private static String SERVER_PORT = "8080";
    private static String SERVER_PATH = "v1";

    private static String FORMAT = "format=json";

    private static String SERVER_PARAM_SUBSTITUTE = "substitute_id";
    private static String SERVER_PARAM_DAY = "day";

    public static String serverUrl(String id, String date) {
        String idParam = "";
        String dateParam = "";
        StringBuilder url = new StringBuilder();

        url
                .append(SERVER_PROTO)
                .append("://")
                .append(SERVER_HOST)
                .append(":")
                .append(SERVER_PORT)
                .append("/")
                .append(SERVER_PATH)
                .append("?")
                .append(FORMAT);

        if (date != null && (!date.equals(""))) {
            url
                    .append("&day=")
                    .append(date);
        }

        if (id != null && (!id.equals(""))) {
            url
                    .append("&substitute_id=")
                    .append(id);
        }

        return url.toString();
    }


}
