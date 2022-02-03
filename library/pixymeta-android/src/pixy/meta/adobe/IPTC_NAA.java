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
 * IPTC_NAA.java
 *
 * Who   Date       Description
 * ====  =========  ==================================================
 * WY    25Apr2015  Added addDataSets()
 * WY    25Apr2015  Renamed getDataSet(0 to getDataSets()
 * WY    13Apr2015  Changed write() to use ITPC.write()
 * WY    12Apr2015  Removed unnecessary read()
 */

package pixy.meta.adobe;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import pixy.meta.MetadataEntry;
import pixy.meta.adobe.ImageResourceID;
import pixy.meta.adobe._8BIM;
import pixy.meta.iptc.IPTC;
import pixy.meta.iptc.IPTCDataSet;
import pixy.meta.iptc.IPTCTag;
import pixy.string.StringUtils;

public class IPTC_NAA extends _8BIM {
	//
	private IPTC iptc;
		
	public IPTC_NAA() {
		this("IPTC_NAA");
	}
	
	public IPTC_NAA(String name) {
		super(ImageResourceID.IPTC_NAA, name, null);
		iptc = new IPTC();
	}

	public IPTC_NAA(String name, byte[] data) {
		super(ImageResourceID.IPTC_NAA, name, data);
		iptc = new IPTC(data);
	}
	
	public void addDataSet(IPTCDataSet dataSet) {
		iptc.addDataSet(dataSet);
	}
	
	public void addDataSets(Collection<? extends IPTCDataSet> dataSets) {
		iptc.addDataSets(dataSets);
	}
	
	/**
	 * Get all the IPTCDataSet as a map for this IPTC data
	 * 
	 * @return a map with the key for the IPTCDataSet name and a list of IPTCDataSet as the value
	 */
	public Map<IPTCTag, List<IPTCDataSet>> getDataSets() {
		return iptc.getDataSets();			
	}
	
	/**
	 * Get a list of IPTCDataSet associated with a key
	 * 
	 * @param key name of the data set
	 * @return a list of IPTCDataSet associated with the key
	 */
	public List<IPTCDataSet> getDataSet(IPTCTag key) {
		return iptc.getDataSet(key);
	}
	
	protected MetadataEntry getMetadataEntry() {
		//
		ImageResourceID eId  = ImageResourceID.fromShort(getID());
		MetadataEntry entry = new MetadataEntry(eId.name(), eId.getDescription(), true);
		
		Map<IPTCTag, List<IPTCDataSet>> datasetMap = this.getDataSets();
		
		if(datasetMap != null) {
			// Print multiple entry IPTCDataSet
			Set<Map.Entry<IPTCTag, List<IPTCDataSet>>> entries = datasetMap.entrySet();
			
			for(Entry<IPTCTag, List<IPTCDataSet>> entryMap : entries) {
				StringBuilder strBuilder = new StringBuilder();
				//
				for(IPTCDataSet item : entryMap.getValue())
					strBuilder.append(item.getDataAsString()).append(";");
				
				String key = entryMap.getKey().getName();				
				String value = StringUtils.replaceLast(strBuilder.toString(), ";", "");
				
				entry.addEntry(new MetadataEntry(key, value));
		    }
			
			return entry;
			
		} else 
			return super.getMetadataEntry();
	}
	
	public void write(OutputStream os) throws IOException {
		if(data == null) {			
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			iptc.write(bout);
			data = bout.toByteArray();
			size = data.length;
		}
		super.write(os);
	}
}
