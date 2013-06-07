/*
Copyright (c) 2010, Siemens Corporate Research a Division of Siemens Corporation 
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.siemens.scr.avt.ad.acquisition;

import java.util.HashSet;
import java.util.Set;

import com.siemens.scr.avt.ad.dicom.GeneralSeries;

public class AVTCollection {
	private Set<GeneralSeries> series = new HashSet<GeneralSeries>();;
	
	private String UID;
	
	private String name;
	
	public String getUID() {
		return UID;
	}

	public void setUID(String UID) {
		this.UID = UID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addSeries(GeneralSeries series) {
		this.getSeries().add(series);
//		series.setAVTCollection(this);
	}

	public Set<GeneralSeries> getSeries(){
		return series;
	}


	public void setSeries(Set<GeneralSeries> series) {
		this.series = series;
	}
}
