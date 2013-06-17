package com.search.manager.utility;

import java.io.IOException;
import java.io.InputStream;

public class CombinedInputStream extends InputStream {

	  /** The current Stream to read from */
	  private int current;

	  boolean isNextLineFeed = false;

	  /** The Streams to combine */
	  private final InputStream[] streams;

	  /**
	   * Constructs an combined InputStream, that reads from array stream per stream, till the last stream
	   * 
	   * @param streams All Streams that will be combined
	   */
	  public CombinedInputStream(final InputStream[] streams) {
	    current = 0;
	    this.streams = streams;
	  }

	  /**
	   * Reads from the list of streams, when the last stream is over, -1 will be returned (as usual)
	   */
	  public int read() throws IOException {
	    if (isNextLineFeed) {
	      isNextLineFeed = false;
	      return '\n';
	    }
	    int i = -1;
	    if (current < streams.length) {
	      i = streams[current].read();
	      if (i == -1) {
	        isNextLineFeed = true;
	        // the current stream has been finished, use the next one
	        current++;
	        return read();
	      }
	    }
	    return i;
	  }

	  /** Closes all streams */
	  public void close() throws IOException {
		IOException ex = null;  
	    for (int i = 0; i < streams.length; i++) {
	      try {
			streams[i].close();
	      } catch (IOException e) {
	    	  ex = e;
	      }
	    }
	    if (ex != null) {
	    	throw ex;
	    }
	  }

	  /** Is there more data to read */
	  public int available() throws IOException {
	    return current < streams.length ? streams[current].available() : 0;
	  }
	}