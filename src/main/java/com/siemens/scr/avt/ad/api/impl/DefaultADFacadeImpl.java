package com.siemens.scr.avt.ad.api.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomObject;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.engine.SessionImplementor;

import com.siemens.scr.avt.ad.annotation.ImageAnnotation;
import com.siemens.scr.avt.ad.annotation.ImageAnnotationDescriptor;
import com.siemens.scr.avt.ad.api.ADFacade;
import com.siemens.scr.avt.ad.api.FacadeManager;
import com.siemens.scr.avt.ad.api.User;
import com.siemens.scr.avt.ad.audit.AuditTrail;
import com.siemens.scr.avt.ad.dicom.GeneralImage;
import com.siemens.scr.avt.ad.dicom.GeneralSeries;
import com.siemens.scr.avt.ad.dicom.GeneralStudy;
import com.siemens.scr.avt.ad.dicom.Patient;
import com.siemens.scr.avt.ad.io.AnnotationIO;
import com.siemens.scr.avt.ad.io.AuditTrailIO;
import com.siemens.scr.avt.ad.io.DicomIO;
import com.siemens.scr.avt.ad.io.UserIO;
import com.siemens.scr.avt.ad.query.Queries;
import com.siemens.scr.avt.ad.util.HibernateUtil;

public class DefaultADFacadeImpl implements ADFacade{
	private static Logger logger = Logger.getLogger(DefaultADFacadeImpl.class);
	
	static {
		FacadeManager.register(new DefaultADFacadeImpl());
	}

	@Override
	public List<String> findAnnotations(Map<Integer, Object> dicomCriteria,
			Map<String, Object> annotationCriteria) {
		return Queries.findbyCriteria(dicomCriteria, annotationCriteria, "com.siemens.scr.avt.ad.annotation.ImageAnnotation.descriptor.UID", String.class);
	}

	@Override
	public List<String> findDicomObjs(Map<Integer, Object> dicomCriteria,
			Map<String, Object> annotationCriteria) {
		return Queries.findbyCriteria(dicomCriteria, annotationCriteria, "com.siemens.scr.avt.ad.dicom.GeneralImage.SOPCommand.SOPInstanceUID", String.class);
	}

	@Override
	public ImageAnnotation getAnnotation(String annotationUID) {
		return Queries.findAnnotation(annotationUID);
	}

	@Override
	public DicomObject getDicomObject(String SOPInstanceUID) {
		return DicomIO.dumpDicom(SOPInstanceUID);
	}

	@Override
	public List<ImageAnnotationDescriptor> listAnnotationsInSeries(String seriesInstanceUID) {
		throw new UnsupportedOperationException("not implemented for now.");
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.siemens.scr.avt.ad.api.ADFacade#retrieveAnnotationsInSeries(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ImageAnnotation> retrieveAnnotationsInSeries(String seriesInstanceUID) {
		String queryString = "SELECT i.* FROM AD.IMAGE_ANNOTATION i WHERE annotation_id IN ("
			+"SELECT DISTINCT ia.annotation_id FROM AD.IMAGE_ANNOTATION ia, AD.REFERENCE_IMAGES ri, AD.GENERAL_IMAGE gi, AD.GENERAL_SERIES gs "
			+"WHERE ia.annotation_id = ri.annotation_id AND ri.image_pk_id = gi.image_pk_id "
			+"AND gi.general_series_pk_id = gs.general_series_pk_id "
			+"AND gs.series_instance_uid = ?)";
		Session session = HibernateUtil.currentSession();
		
		SQLQuery query = session.createSQLQuery(queryString);
		query.setString(0, seriesInstanceUID);
		query.addEntity(ImageAnnotation.class);
		
		List<ImageAnnotation> queryResult = query.list();
		return queryResult;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ImageAnnotation> retrieveAnnotationsInSeries(String seriesInstanceUID, String roleInTrial) {
		String queryString = "SELECT i.* FROM AD.IMAGE_ANNOTATION i WHERE annotation_id IN ("
			+"SELECT DISTINCT ia.annotation_id FROM AD.IMAGE_ANNOTATION ia, AD.REFERENCE_IMAGES ri, AD.GENERAL_IMAGE gi, AD.GENERAL_SERIES gs "
			+"WHERE ia.annotation_id = ri.annotation_id AND ri.image_pk_id = gi.image_pk_id "
			+"AND gi.general_series_pk_id = gs.general_series_pk_id "
			+"AND gs.series_instance_uid = ? AND role_in_trial = ?)";
		Session session = HibernateUtil.currentSession();
		
		SQLQuery query = session.createSQLQuery(queryString);
		query.setString(0, seriesInstanceUID);
		query.setString(1, roleInTrial);
		query.addEntity(ImageAnnotation.class);
		
		List<ImageAnnotation> queryResult = query.list();
		return queryResult;
	}

	@Override
	public void saveDicom(DicomObject image) {
		if(image == null){
			logger.warn("DicomObject is null!");
			return;
		}
		DicomIO.saveOrUpdateDicom(image);
	}


	@Override
	//TODO: Handle pedigree information
	public boolean saveAnnotation(ImageAnnotation annotation, User user, String pedigree, String comment) {
		AnnotationIO.saveOrUpdateAnnotation(annotation);
		UserIO.saveUser(user);
		String annotationUID = annotation.getDescriptor().getUID();
		AuditTrail audittrail = new AuditTrail();
		audittrail.setUser(user);
		audittrail.setComment(comment);
		audittrail.setUid(annotationUID);
		audittrail.setTargetType("annotation");
		AuditTrailIO.saveAuditTrail(audittrail);	
		
		return true;// XL: we inherit the method signature from AD Phase I. It does not seem to make sense any more.
	}
	
	
	@Override
	//TODO: Handle pedigree information
	public boolean saveAnnotations(List<ImageAnnotation> annotations, User user, String pedigree, String comment) {
		boolean flag = true;
		for(ImageAnnotation annotation : annotations){
			flag &= saveAnnotation(annotation, user, pedigree, comment);
		}
		return flag;
	}


	@Override
	public void saveDicoms(List<DicomObject> images) {
		for(DicomObject dicom : images){
			saveDicom(dicom);
		}
	}

	@Override
	//TODO: Handle pedigree information
	public boolean updateAnnotation(ImageAnnotation annotation, User user, String pedigree, String comment,
			String referenceUID) {
		throw new UnsupportedOperationException("not implemented for now.");
	}

	@Override
	public List<Patient> findPatientByCriteria(Map<Integer, Object> dicomCriteria, Map<String, Object> annotationCriteria){
		return Queries.findEntityByCriteria(dicomCriteria, annotationCriteria, Patient.class);
	}

	@Override
	public Set<ImageAnnotation> retrieveAnnotationsOf(GeneralImage image) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		image = (GeneralImage) session.merge(image);
		Set<ImageAnnotation> result = image.getAnnotations();
		((SessionImplementor)session).initializeCollection((PersistentCollection) result, false);
		session.close();
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<GeneralImage> retrieveImagesOf(GeneralSeries series) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		series = (GeneralSeries) session.merge(series);
		Set<GeneralImage> result = series.getImages();
		((SessionImplementor)session).initializeCollection((PersistentCollection) result, false);
		session.close();
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<GeneralSeries> retrieveSeriesOf(GeneralStudy study) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		study = (GeneralStudy) session.merge(study);
		Set<GeneralSeries> result = study.getSeries();
		((SessionImplementor)session).initializeCollection((PersistentCollection) result, false);
		session.close();
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<GeneralStudy> retrieveStudiesOf(Patient patient) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		patient = (Patient) session.merge(patient);
		Set<GeneralStudy> result = patient.getStudies();
		((SessionImplementor)session).initializeCollection((PersistentCollection) result, false);
		session.close();
		return result;
	}

	@Override
	public List<DicomObject> retrieveDicomObjs(
			Map<Integer, Object> dicomCriteria,
			Map<String, Object> annotationCriteria) {
		return Queries.findDicomByCriteria(dicomCriteria, annotationCriteria);
	}

	@Override
	public List<ImageAnnotation> retrieveAnnotations(
			Map<Integer, Object> dicomCriteria,
			Map<String, Object> annotationCriteria) {
		return Queries.findEntityByCriteria(dicomCriteria, annotationCriteria, ImageAnnotation.class);
	}

	@Override
	public DicomObject getDicomObjectWithoutPixel(String SOPInstanceUID) {
		return DicomIO.dumpDicom(SOPInstanceUID, false);
	}

	@Override
	public List<DicomObject> retrieveDicomObjsWithoutPixel(Map<Integer, Object> dicomCriteria,
			Map<String, Object> annotationCriteria) {
		return Queries.findDicomByCriteria(dicomCriteria, annotationCriteria, false);
	}

	@Override
	public List<GeneralImage> findImagesByCriteria(
			Map<Integer, Object> dicomCriteria,
			Map<String, Object> annotationCriteria) {
		return Queries.findEntityByCriteria(dicomCriteria, annotationCriteria, GeneralImage.class);
	}

	@Override
	public List<GeneralSeries> findSeriesByCriteria(
			Map<Integer, Object> dicomCriteria,
			Map<String, Object> annotationCriteria) {
		return Queries.findEntityByCriteria(dicomCriteria, annotationCriteria, GeneralSeries.class);
	}

	@Override
	public List<GeneralStudy> findStudiesByCriteria(
			Map<Integer, Object> dicomCriteria,
			Map<String, Object> annotationCriteria) {
		return  Queries.findEntityByCriteria(dicomCriteria, annotationCriteria, GeneralStudy.class);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<DicomObject> retrieveSegmentationObjects(String annotationUID) {
		Session session = HibernateUtil.currentSession();
		
		String queryString =  "SELECT * FROM AD.GENERAL_IMAGE i, AD.GENERAL_SERIES s, AD.STUDY st, AD.PATIENT p "
			+ "WHERE p.PATIENT_PK_ID = st.PATIENT_PK_ID "
			+ "AND st.STUDY_PK_ID = s.STUDY_PK_ID "
			+ "AND s.GENERAL_SERIES_PK_ID = i.GENERAL_SERIES_PK_ID "
			+ "AND SOP_INSTANCE_UID IN "
			+ "(SELECT X.SEGMENTATION_UID FROM AD.IMAGE_ANNOTATION a, "
			+ "	XMLTABLE ('declare default element namespace \"gme://caCORE.caCORE/3.2/edu.northwestern.radiology.AIM\"; "
			+ "$aim/imageAnnotation/probabilityMapCollection/ProbabilityMap' passing a.AIM_OBJECT as \"aim\" " 
			+ "COLUMNS SEGMENTATION_UID VARCHAR(64) PATH '@referencedInstanceUID') AS X WHERE a.ANNOTATION_UID = ?) "
			+ "UNION ALL "
			+ "SELECT * FROM AD.GENERAL_IMAGE i, AD.GENERAL_SERIES s, AD.STUDY st, AD.PATIENT p "
			+ "WHERE p.PATIENT_PK_ID = st.PATIENT_PK_ID "
			+ "AND st.STUDY_PK_ID = s.STUDY_PK_ID "
			+ "AND s.GENERAL_SERIES_PK_ID = i.GENERAL_SERIES_PK_ID "
			+ "AND SOP_INSTANCE_UID IN "
			+ "(SELECT X.SEGMENTATION_UID FROM AD.IMAGE_ANNOTATION a, "
			+ "	XMLTABLE ('declare default element namespace \"gme://caCORE.caCORE/3.2/edu.northwestern.radiology.AIM\"; "
			+ "$aim/ImageAnnotation/segmentationCollection/Segmentation' passing a.AIM_OBJECT as \"aim\" " 
			+ "COLUMNS SEGMENTATION_UID VARCHAR(64) PATH '@referencedSopInstanceUID') AS X WHERE a.ANNOTATION_UID = ?)" ;
		
		SQLQuery query = session.createSQLQuery(queryString);
		query.setString(0, annotationUID);
		query.setString(1, annotationUID);
		query.addEntity(GeneralImage.class)
			 .addEntity(GeneralSeries.class)
			 .addEntity(GeneralStudy.class)
			 .addEntity(Patient.class);
		
		List<Object[]> queryResult = new ArrayList<Object[]>();
		List<Object[]> tmpqueryResult = query.list();
		queryResult.addAll(tmpqueryResult);
		
		LinkedList<DicomObject> result = new LinkedList<DicomObject>();		
		for(Object[] tuple : queryResult){
			BasicDicomObject dcmObj = new BasicDicomObject();
			
			GeneralImage image = (GeneralImage) tuple[0];
			GeneralSeries series = (GeneralSeries) tuple[1];
			GeneralStudy study = (GeneralStudy) tuple[2];
			Patient patient = (Patient) tuple[3];
			
			image.getDicomObject().copyTo(dcmObj);
			series.getDicomObject().copyTo(dcmObj);
			study.getDicomObject().copyTo(dcmObj);
			patient.getDicomObject().copyTo(dcmObj);
			result.add(dcmObj);
		}		
		return result;		
	}
}
