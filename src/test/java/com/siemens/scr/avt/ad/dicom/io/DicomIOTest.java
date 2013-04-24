package com.siemens.scr.avt.ad.dicom.io;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.junit.Test;

import com.siemens.scr.avt.ad.io.DicomIO;

public class DicomIOTest {
	final static String SOPInstanceUID = "1.3.6.1.4.1.9328.50.1.10704";
	public final static String DICOM_FILE = "/RECIST_Data_rv_12/1.3.6.1.4.1.9328.50.1.10704.dcm";
	@Test
	public void testDump(){
		DicomIO.saveOrUpdateDicomFromFile(this.getClass(), DICOM_FILE);
		
		DicomObject dcmObj = DicomIO.dumpDicom(SOPInstanceUID);
		assertNotNull(dcmObj.get(Tag.PixelData));
		
		dcmObj = DicomIO.dumpDicom(SOPInstanceUID, true);
		assertNotNull(dcmObj.get(Tag.PixelData));
		
		dcmObj = DicomIO.dumpDicom(SOPInstanceUID, false);
		assertNull(dcmObj.get(Tag.PixelData));
	}
}
