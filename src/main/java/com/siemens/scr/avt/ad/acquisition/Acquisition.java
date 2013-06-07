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

import java.util.Set;

import com.siemens.scr.avt.ad.dicom.GeneralSeries;

public class Acquisition {
	private Set<AVTCollection> referencingCollections;
	
	private GeneralSeries series;

	private int acquisitionNumber;
	
	public int getAcquisitionNumber() {
		return acquisitionNumber;
	}

	public void setAcquisitionNumber(int acquisitionNumber) {
		this.acquisitionNumber = acquisitionNumber;
	}

	public Set<AVTCollection> getReferencingCollections() {
		return referencingCollections;
	}

	public void setReferencingCollections(Set<AVTCollection> referencingCollections) {
		this.referencingCollections = referencingCollections;
	}

	public GeneralSeries getSeries() {
		return series;
	}

	public void setSeries(GeneralSeries series) {
		this.series = series;
	}


	
	
}
