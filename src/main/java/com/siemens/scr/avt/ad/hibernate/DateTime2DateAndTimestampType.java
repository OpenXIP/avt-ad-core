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

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/**
 * This type converts <code>java.util.Date</code> into
 * <code>java.sql.Date</code> and <code>java.sql.Time</code>.
 * 
 * @author Xiang Li
 * 
 */
public class DateTime2DateAndTimestampType implements UserType {

	public int[] sqlTypes() {
		return new int[] { Types.DATE, Types.TIMESTAMP };
	}

	public Class returnedClass() {
		return java.util.Date.class;
	}

	public boolean equals(Object x, Object y) {
		return (x == y) || (x != null && y != null && x.equals(y));

	}

	public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
			throws HibernateException, SQLException {
		java.sql.Date date = (java.sql.Date) Hibernate.DATE.nullSafeGet(rs,	names[0]);

		
		java.sql.Time time = (java.sql.Time) Hibernate.TIME.nullSafeGet(rs,	names[1]);

		if (date == null && time == null)
			return null;

		if (date == null)
			return new java.util.Date(time.getTime());

		return new java.util.Date(date.getTime() + time.getTime());
	}

	public void nullSafeSet(PreparedStatement st, Object value, int index)
			throws HibernateException, SQLException {
		java.util.Date dateTime = (value == null) ? new java.util.Date() : (java.util.Date) value;

		Hibernate.DATE.nullSafeSet(st, new java.sql.Date(dateTime.getTime()),
				index);
		Hibernate.TIME.nullSafeSet(st, new java.sql.Time(dateTime.getTime()),
				index + 1);

	}

	public Object deepCopy(Object value) {
		if (value == null)
			return null;

		return new java.util.Date(((java.util.Date) value).getTime());
	}

	public boolean isMutable() {
		return true;
	}

	@Override
	public Object assemble(Serializable cached, Object owner)
			throws HibernateException {
		return deepCopy(cached);
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable)deepCopy(value);
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	@Override
	public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
		return deepCopy(original);
	}

}
