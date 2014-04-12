package com.cab404.moonlight.util.modular;

import com.cab404.moonlight.util.html_parser.LevelAnalyzer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author cab404
 */
public class ModularBlockParser implements LevelAnalyzer.BlockHandler {
    private AccessProfile profile;
    private HashMap<Module, Integer> handlers;
    private ParsedObjectHandler object_handler;

    public ModularBlockParser(ParsedObjectHandler handler, AccessProfile profile) {
        this.object_handler = handler;
        handlers = new HashMap<>();
        this.profile = profile;
    }

    public void bind(Module module, int id) {
        handlers.put(module, id);
    }

    @Override public void handleBlock(final LevelAnalyzer.BlockBuilder builder) {
        Iterator<Map.Entry<Module, Integer>> iterator = handlers.entrySet().iterator();
        Map.Entry<Module, Integer> e;

        while (iterator.hasNext()) {
            e = iterator.next();
            final Module module = e.getKey();
            final int response_id = e.getValue();

            if (module.doYouLikeIt(builder.getHeaderTag())) {
//                new Thread(response_id + " parser") {
//                    @Override public void run() {
                Object object = module.extractData(builder.assembleTree(), profile);
                if (object == null) return;
                object_handler.handle(
                        object,
                        response_id
                );
//                    }
//                }.start();
            }

            if (module.haveYouFinished())
                iterator.remove();

        }

    }

    public interface ParsedObjectHandler {
        public void handle(Object object, int key);
    }

}
