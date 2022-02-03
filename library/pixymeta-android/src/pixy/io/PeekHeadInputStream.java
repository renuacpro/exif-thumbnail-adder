/*
 * Copyright (c) 2014-2021 by Wen Yu
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * or any later version.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 *
 * Change History - most recent changes go on top of previous changes
 *
 * PeekHeadInputStream.java
 *
 * Who   Date       Description
 * ====  =========  ==============================================================
 * WY    26Sep2015  Initial creation
 */

package pixy.io;

import java.io.*;

import pixy.util.ArrayUtils;

/**
 * Lightweight stream wrapper which allows to peek a
 * fixed length of bytes from the current stream head
 *
 */
public class PeekHeadInputStream extends InputStream {
	/** The source stream. */
    private InputStream src;

	/**
	 * Buffer to hold the peeked bytes.
	 */
	private byte[] buffer;

	/**
	 * Current buffer position.
	 */
	private int position;
	
	private boolean closed;

	/**
	 * @param bytesToPeek number of bytes to peek
	 * @param src The source InputStream to use
	 */
	public PeekHeadInputStream(InputStream src, int bytesToPeek) {
		this.src = src;
		this.buffer = new byte[bytesToPeek];
		try {
			IOUtils.readFully(src, buffer);
		} catch(IOException ex) {
			throw new RuntimeException("Error while reading bytes into buffer");
		}
	}
	
	public void close() throws IOException {
		if(closed) return;
		buffer = null;
		src.close();
		src = null;
		closed = true;
	}
	
	public void shallowClose() throws IOException {
		if(closed) return;
		buffer = null;
		closed = true;
	}
	
	/**
	 * Check to make sure that this stream has not been closed
	 */
    private  void ensureOpen() throws IOException {
    	if (closed)
    		throw new IOException("Stream closed");
    }
	
	public byte[] peek(int len) throws IOException {
		ensureOpen();
		if(len <= buffer.length) return ArrayUtils.subArray(buffer, 0, len);
		throw new IllegalArgumentException("Peek length larger than buffer");
	}

	@Override
	public int read() throws IOException {
		ensureOpen();
		if (position >= buffer.length)
			return src.read();
		else
			return (buffer[position++]&0xff);
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		ensureOpen();
		if (position >= buffer.length) {
			return src.read(b, off, len);
		} else if(position + len > buffer.length) {
			int bytesAvailable = buffer.length - position;
			System.arraycopy(buffer, position, b, off, bytesAvailable);
			position += bytesAvailable;			
			return bytesAvailable + src.read(b, off + bytesAvailable, len - bytesAvailable);
		}
		else {
			System.arraycopy(buffer, position, b, off, len);
			position += len;
			return len;
		}
	}
}
