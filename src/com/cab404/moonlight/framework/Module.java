package com.cab404.moonlight.framework;

import com.cab404.moonlight.parser.HTMLTree;
import com.cab404.moonlight.parser.Tag;

/**
 * Represents raw-data -> data step. Should actually be named "extractor",
 * but who cares.
 *
 * @author cab404
 */
public interface Module<T> {
    T extractData(HTMLTree page, AccessProfile profile);
    boolean doYouLikeIt(Tag tag);
    boolean haveYouFinished();
}
