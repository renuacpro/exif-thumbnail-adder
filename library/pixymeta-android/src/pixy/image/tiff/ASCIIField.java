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
 */

package pixy.image.tiff;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import pixy.io.RandomAccessOutputStream;

/**
 * TIFF ASCII type field.
 * 
 * @author Wen Yu, yuwen_66@yahoo.com
 * @version 1.0 01/06/2013
 */
public final class ASCIIField extends TiffField<String> {

	public ASCIIField(short tag, String data) { // ASCII field is NUL- terminated ASCII string
		super(tag, FieldType.ASCII, getLength(data));
		this.data = data.trim() + '\0'; // Add NULL to the end of the string
	}
	
	private static int getLength(String data) {
		try {
			return data.trim().getBytes("UTF-8").length + 1;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Failed to create ASCIIField.");
		}
	}
	
	public String getDataAsString() {
		// ASCII field allows for multiple NUL separated strings
		return data.trim().replace("\0", "; ");
	}

	protected int writeData(RandomAccessOutputStream os, int toOffset) throws IOException {
		
		byte[] buf = data.getBytes("UTF-8");
        
		if (buf.length <= 4) {
			dataOffset = (int)os.getStreamPointer();
			byte[] tmp = new byte[4];
			System.arraycopy(buf, 0, tmp, 0, buf.length);
			os.write(tmp);
		} else {
			dataOffset = toOffset;
			os.writeInt(toOffset);
			os.seek(toOffset);
			os.write(buf);
			toOffset += buf.length; 
		}		
		return toOffset;
	}
}
