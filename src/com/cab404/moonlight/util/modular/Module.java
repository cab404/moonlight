package com.cab404.moonlight.util.modular;

import com.cab404.moonlight.util.html_parser.HTMLTree;
import com.cab404.moonlight.util.html_parser.Tag;

/**
 * This will replace parsers.
 * Represents raw-data -> data step. Should actually be named "extractor",
 * but who cares.
 *
 * @author cab404
 */
public interface Module<T> {
    public abstract T extractData(HTMLTree page, AccessProfile profile);
    public abstract boolean doYouLikeIt(Tag tag);
    public abstract boolean haveYouFinished();

}
