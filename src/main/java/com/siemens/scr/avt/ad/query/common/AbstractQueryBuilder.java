package com.siemens.scr.avt.ad.query.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.siemens.scr.avt.ad.query.QueryBuilder;
import com.siemens.scr.avt.ad.query.model.MappingEntry;

/**
 * Build conjunctive queries without self-joins. An assumption employed is we
 * are performing natural joins.
 * 
 * @author Xiang Li
 * 
 */
public abstract class AbstractQueryBuilder implements QueryBuilder {
	public static final String AND = "AND";

	public static final String FROM = "FROM";

	private static Logger logger = Logger.getLogger(AbstractQueryBuilder.class);

	public static final String NEWLINE = "\r\n";

	public static final String SELECT = "SELECT";

	public static final String SEPARATOR = ",";

	public static final String SPACE = " ";

	public static final String WHERE = "WHERE";

	private static String conjunctionOf(List<String> conditions) {
		return StringUtils.join(conditions, SPACE + AND + SPACE);
	}

	private ModelManager modelManager;

	private Map<String, Object> params;
	private List<PathElement> aimParams = new ArrayList<PathElement>();

	private List<String> projectionList = new LinkedList<String>();


	private Set<String> tableSet = new HashSet<String>();

	protected String buildAtomicSelection(String key, Object value) {
		com.siemens.scr.avt.ad.query.model.MappingEntry entry = getModelManager()
				.getMappingEntry(key);
		
		return entry.buildSelectionCondition(this, value);
	}

	/**
	 * The impl. does not allow self join
	 */
	protected String buildCartesionProduct() {
		return FROM + SPACE + StringUtils.join(tableSet, SEPARATOR);
	}
	
	public String buildFullDicomQuery(Map<Integer, Object> intKeyedParams, Map<String, Object> params){
		ModelManager manager = getModelManager();
		Map<String, Object> allParams = new HashMap<String, Object>();
		allParams.putAll(manager.tag2Key(intKeyedParams));
		if(params != null){
			allParams.putAll(params);			
		}

		return buildFullDicomQuery(allParams);
	}

	
	public String buildFullDicomQuery(Map<String, Object> params){
		// set up
		this.params = params;
		projectionList.add("*");
		modelManager.expandTableSetToAllTables(tableSet);
		
		return buildQueryInternal();
	}
	
	protected String buildJoin() {
		return conjunctionOf(getModelManager().buildJoinConditions(tableSet));
	}
	
	protected String buildProjection() {
		return SELECT + SPACE + StringUtils.join(projectionList, SEPARATOR);
	}
	
	public String buildQuery(Map<Integer, Object> intKeyedParams, Map<String,Object> aimCriteria, List<String> out){
		ModelManager manager = getModelManager();
		Map<String, Object> dicomParams = new HashMap<String, Object>();
		dicomParams.putAll(manager.tag2Key(intKeyedParams));
		if(params != null){
			dicomParams.putAll(params);			
		}
		
		List<PathElement> aimParams = new ArrayList<PathElement>();
		if (aimCriteria != null){
			StringTokenizer tokenizer;
			for (String key : aimCriteria.keySet()){
				tokenizer = new StringTokenizer(key,".");
				aimParams.add(new PathElement(tokenizer.nextToken(),tokenizer.nextToken(),aimCriteria.get(key)));
			}
			if (aimParams.size()>0){
				tableSet.add("AD.IMAGE_ANNOTATION");
			}
			this.aimParams = aimParams;
		}
		return buildQuery(dicomParams, out);
	}

	public final String buildQuery(Map<String, Object> params, List<String> out) {
		preprocess(out, params);

		return buildQueryInternal();
	}
	

	private String buildQueryInternal(){
		String query = buildProjection() + SPACE + NEWLINE 
		+ buildCartesionProduct() + SPACE + NEWLINE + buildWhereClause();
		
		return query;
	}

	protected String buildSelection() {
		ArrayList<String> conditions = new ArrayList<String>();
		for (String key : params.keySet()) {
			conditions.add(buildAtomicSelection(key, params.get(key)));
		}
		return conjunctionOf(conditions);

	}
	
	protected String buildXMLSelection() {
		if(aimParams.isEmpty()) {
			return null;
		} 
		else {
			ArrayList<String> conditions = new ArrayList<String>();
			for (PathElement element : aimParams) {
				conditions.add(buildAtomicXMLSelection(element));
			}			
			return conjunctionOf(conditions);		
		}	
	}

	public abstract String buildAtomicXMLSelection(PathElement element);
	
	public abstract String buildAtomicSelection(Object value, SimpleEntry entry);
	
	public abstract String buildAtomicSelection(Object value,
			XPathEntry entry);
	
	protected String buildWhereClause() {
		ArrayList<String> components = new ArrayList<String>();
		String joinCondition = buildJoin();
		if(joinCondition != null && joinCondition.length() > 0){
			components.add(joinCondition);	
		}
		
		String selection = buildSelection();
		if(selection != null && selection.length() > 0){
			components.add(selection);	
		}
		
		String xmlSelection = buildXMLSelection();
		if(xmlSelection != null && xmlSelection.length() > 0){
			components.add(xmlSelection);	
		}
		
		String condition = conjunctionOf(components);
		if(condition == null || condition.length() == 0){
			return StringUtils.EMPTY;
		}
		
		String whereQueryString = WHERE + SPACE + condition;
		String wildCardQuery = supportWildcardSearch(whereQueryString);
		return wildCardQuery;
	}



	public ModelManager getModelManager() {
		return modelManager;
	}

	protected void preprocess(List<String> output, Map<String, Object> dicomParams){
		this.params = dicomParams;
		setUpProjectionList(output);
		setUpTableSet(output);
	}

	public void setModelManager(ModelManager modelManager) {
		this.modelManager = modelManager;
	}

	private void setUpProjectionList(List<String> output) {
		// TODO: extract XML fragments?
		for (String key : output) {
			MappingEntry entry = getModelManager().getMappingEntry(key);
			projectionList.add(entry.toProjectionExpression(this));
		}
	}

	private void setUpTableSet(List<String> out) {
		for (String key : params.keySet()) {
			MappingEntry entry = getModelManager().getMappingEntry(key);
			if (entry == null) {
				logger.error("Unknown Search Key:" + key + "!");
			} else {
				tableSet.add(entry.getTableName());
			}
		}
		for(String key : out){
			MappingEntry entry = getModelManager().getMappingEntry(key);
			if (entry == null) {
				logger.error("Unknown Search Key:" + key + "!");
			} else {
				tableSet.add(entry.getTableName());
			}
		}

		modelManager.expandTableSet(tableSet);
	}
	
	private String supportWildcardSearch(String whereQueryString) {
		if(whereQueryString.contains("*")) {
			String tmpquery = whereQueryString.replace("='", " like '");
			String replaceQuery = tmpquery.replace('*', '%');
			return replaceQuery;
		}
		else {
			return whereQueryString;
		}		
	}

}
