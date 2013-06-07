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
import java.sql.PreparedStatement; 
import java.sql.ResultSet; 
import java.sql.SQLException; 
import java.sql.Types; 
import java.sql.Blob; 

import org.hibernate.Hibernate; 
import org.hibernate.HibernateException; 
import org.hibernate.usertype.UserType;

import org.apache.commons.lang.builder.HashCodeBuilder;


public class BinaryBlobType implements UserType 
{ 
  public int[] sqlTypes() 
  { 
    return new int[] { Types.BLOB }; 
  }

  public Class returnedClass() 
  { 
    return byte[].class; 
  } 

  public boolean equals(Object x, Object y) 
  { 
    return (x == y) 
      || (x != null 
        && y != null 
        && java.util.Arrays.equals((byte[]) x, (byte[]) y)); 
  } 

  public Object nullSafeGet(ResultSet rs, String[] names, Object owner) 
  throws HibernateException, SQLException 
  { 
    Blob blob = rs.getBlob(names[0]); 
    return blob.getBytes(1, (int) blob.length()); 
  } 

  public void nullSafeSet(PreparedStatement st, Object value, int index) 
  throws HibernateException, SQLException 
  { 
    st.setBlob(index, Hibernate.createBlob((byte[]) value)); 
  } 

  public Object deepCopy(Object value) 
  { 
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
		return (byte[]) value;
	}
	
	@Override
	public int hashCode(Object x) throws HibernateException {
		return new HashCodeBuilder().append( (byte[]) x).toHashCode();
		
		
	}
	
	@Override
	public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
		return original;
	} 

}

