package com.cab404.moonlight.parser;

import com.cab404.moonlight.facility.ResponseFactory;
import com.cab404.moonlight.framework.BlockProvider;

/**
 * Policy for working with parser threads.
 * Created at 4:51 on 03.01.15
 *
 * @author cab404
 */
public interface ParsingThreadPolicy {

    public void start();

    public void join();

    public void finished();

    public BlockProvider provider();

    public ResponseFactory.Parser reciever();

}
