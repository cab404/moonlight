package com.cab404.moonlight.framework;

import com.cab404.moonlight.parser.LevelAnalyzer;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * @author cab404
 */
public class ModularBlockParser implements LevelAnalyzer.BlockHandler {
    private AccessProfile profile;
    private ArrayList<Map.Entry<Module, Integer>> handlers;
    private ParsedObjectHandler object_handler;

    public ModularBlockParser(ParsedObjectHandler handler, AccessProfile profile) {
        this.object_handler = handler;
        handlers = new ArrayList<>();
        this.profile = profile;
    }

    /**
     * Binds new parsing module.
     *
     * @param id ID which will be used while returning object to handler.
     */
    public void bind(Module module, int id) {
        handlers.add(new AbstractMap.SimpleEntry<>(module, id));
    }

    @Override public void handleBlock(final LevelAnalyzer.BlockBuilder builder) {
        Iterator<Map.Entry<Module, Integer>> iterator = handlers.iterator();
        Map.Entry<Module, Integer> e;

        while (iterator.hasNext()) {
            e = iterator.next();
            final Module module = e.getKey();
            final int response_id = e.getValue();

            if (module.doYouLikeIt(builder.getHeaderTag())) {
                Object object = module.extractData(builder.assembleTree(), profile);
                if (object != null)
                    object_handler.handle(
                            object,
                            response_id
                    );
            }

            if (module.haveYouFinished())
                iterator.remove();

        }

    }

    public interface ParsedObjectHandler {
        public void handle(Object object, int key);
    }

}
