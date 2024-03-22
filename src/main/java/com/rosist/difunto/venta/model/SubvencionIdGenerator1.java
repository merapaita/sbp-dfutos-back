package com.rosist.difunto.venta.model;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

//public class SubvencionIdGenerator1 implements IdentifierGenerator {

//	@Override
//	public Object generate(SharedSessionContractImplementor session, Object object) throws HibernateException {

//	    String prefix = "cli";
//	    Connection connection = session.connection();
//
//	    try {
//	        Statement statement=connection.createStatement();
//
//	        ResultSet rs=statement.executeQuery("select count(client_id) as Id from Client");
//
//	        if(rs.next())
//	        {
//	            int id=rs.getInt(1)+101;
//	            String generatedId = prefix + new Integer(id).toString();
//	            return generatedId;
//	        }
//	    } catch (SQLException e) {
//	        // TODO Auto-generated catch block
//	        e.printStackTrace();
//	    }

//	    return null;
//	}

//	@Override
//	public Serializable generate(SessionImplementor session, Object object)
//	        throws HibernateException {
//
//	    String prefix = "cli";
//	    Connection connection = session.connection();
//
//	    try {
//	        Statement statement=connection.createStatement();
//
//	        ResultSet rs=statement.executeQuery("select count(client_id) as Id from Client");
//
//	        if(rs.next())
//	        {
//	            int id=rs.getInt(1)+101;
//	            String generatedId = prefix + new Integer(id).toString();
//	            return generatedId;
//	        }
//	    } catch (SQLException e) {
//	        // TODO Auto-generated catch block
//	        e.printStackTrace();
//	    }
//
//	    return null;
//	}	
//}
