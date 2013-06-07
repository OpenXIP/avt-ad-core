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
package com.siemens.scr.avt.ad.hibernate;

import java.io.Serializable;
import java.sql.SQLXML;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/**
 * Note: hibernate's built-in tool does not support XML type yet.
 * Specifying this customized type has the effect to disable automatic
 * schema update.
 * 
 * @author Xiang Li
 *
 */
public class StringXMLType implements UserType{

	 public int[] sqlTypes() 
	  { 
	    return new int[] { Types.SQLXML }; 
	  }

	  public Class returnedClass() 
	  { 
	    return String.class; 
	  } 

	  public boolean equals(Object x, Object y) 
	  { 
	    return (x == y); 
	       
	  } 

	  public Object nullSafeGet(ResultSet rs, String[] names, Object owner) 
	  throws HibernateException, SQLException 
	  { 
		  SQLXML xml = rs.getSQLXML(names[0]); 
			  
	    return xml.getString(); 
	  } 

	  public void nullSafeSet(PreparedStatement st, Object value, int index) 
	  throws HibernateException, SQLException 
	  { 
		  st.setString(index, (String)value);
	  } 

	  public Object deepCopy(Object value) 
	  { 
	    if (value == null) return null; 

	    return value;
	  } 

	  public boolean isMutable() 
	  { 
	    return false; 
	  }

		@Override
		public Object assemble(Serializable cached, Object owner)
				throws HibernateException {
			return cached;
		}

		@Override
		public Serializable disassemble(Object value) throws HibernateException {
			return (String) value;
		}
		
		@Override
		public int hashCode(Object x) throws HibernateException {
			return new HashCodeBuilder().append( (String) x).toHashCode();
			
			
		}
		
		@Override
		public Object replace(Object original, Object target, Object owner)
				throws HibernateException {
			return original;
		} 

}
