package com.cab404.moonlight.facility;

import com.cab404.moonlight.util.U;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Показывает на экране те самые зелёные и красные окошки с Табуна. Почти.
 *
 * @author cab404
 */
public class MessageFactory {
    private static JSONParser parser = new JSONParser();

    public static MessageListener impl = new MessageListener() {
        @Override
        public void show(String title, String body, boolean isError) {
            if (!body.isEmpty()) {
                U.v(" :::: " + title.trim() + " :::: ");
                U.v(body);
                U.v("");
            }
        }
    };

    public static JSONObject processJSONwithMessage(String json) {
        try {
            JSONObject parsed = (JSONObject) parser.parse(json);

            impl.show("" + parsed.get("sMsgTitle"), "" + parsed.get("sMsg"), (boolean) parsed.get("bStateError"));

            return parsed;
        } catch (ParseException e) {
            U.w(e);
            U.w(json);
        }

        return null;
    }

    /**
     * Вся его работа - принимать строки о том,
     * что нельзя давать комментариям больше одного минуса
     * и заходить в Табун с неправильным паролем.
     */
    public static interface MessageListener {
        public void show(String title, String body, boolean isError);
    }
}
