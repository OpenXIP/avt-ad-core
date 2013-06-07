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
package com.siemens.scr.avt.ad.io;

import org.hibernate.Session;

import com.siemens.scr.avt.ad.audit.AuditTrail;
import com.siemens.scr.avt.ad.util.HibernateUtil;



public class AuditTrailIO {
	
	public static void saveAuditTrail(AuditTrail audittrail) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		HibernateUtil.save(audittrail, session);
		session.close();
	}
}

