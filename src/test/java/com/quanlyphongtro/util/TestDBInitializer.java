package com.quanlyphongtro.util;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;
import javax.naming.spi.NamingManager;
import java.util.Hashtable;

public class TestDBInitializer {
    
    private static boolean initialized = false;

    public static synchronized void initJNDI() {
        if (initialized) return;

        try {
            // Setup Mock InitialContextFactoryBuilder
            NamingManager.setInitialContextFactoryBuilder(new InitialContextFactoryBuilder() {
                @Override
                public InitialContextFactory createInitialContextFactory(Hashtable<?, ?> environment) throws NamingException {
                    return new InitialContextFactory() {
                        @Override
                        public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
                            return new MockContext();
                        }
                    };
                }
            });
            initialized = true;
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    private static class MockContext extends InitialContext {
        public MockContext() throws NamingException {
            super();
        }

        @Override
        public Object lookup(String name) throws NamingException {
            return lookupString(name);
        }
        
        @Override
        public Object lookup(Name name) throws NamingException {
            throw new NamingException("Use String lookup");
        }

        // Mock implementation to return the local SQL Server DataSource
        public Object lookupString(String name) throws NamingException {
            if ("java:comp/env".equals(name)) {
                return this; // Return self so the next lookup works
            }
            if ("jdbc/HostelManagement".equals(name) || "java:comp/env/jdbc/HostelManagement".equals(name)) {
                SQLServerDataSource ds = new SQLServerDataSource();
                ds.setURL("jdbc:sqlserver://localhost:1433;databaseName=HostelManagement;encrypt=true;trustServerCertificate=true;");
                ds.setUser("sa");
                ds.setPassword("123");
                return ds;
            }
            throw new NamingException("Name not found: " + name);
        }

        // Trick to override lookup(String)
        @Override
        protected Context getDefaultInitCtx() throws NamingException {
            return new InitialContext() {
                @Override
                public Object lookup(String name) throws NamingException {
                    return lookupString(name);
                }
            };
        }
    }
}
